package br.com.metas.api.service;

import br.com.metas.api.dto.DesempenhoResponse;
import br.com.metas.api.dto.PerfilDesempenho;
import br.com.metas.api.entity.Categoria;
import br.com.metas.api.entity.ItemChecklist;
import br.com.metas.api.entity.Meta;
import br.com.metas.api.entity.Prioridade;
import br.com.metas.api.entity.StatusMeta;
import br.com.metas.api.entity.Usuario;
import br.com.metas.api.repository.MetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Processa as metas do usuário e gera um resultado personalizado: pontuação, perfil e recomendações.
 */
@Service
@RequiredArgsConstructor
public class DesempenhoService {

    private static final double PESO_CONCLUSAO = 0.4;
    private static final double PESO_PROGRESSO = 0.4;
    private static final double PESO_PONTUALIDADE = 0.2;
    private static final int DIAS_ALERTA_PRAZO = 7;
    private static final int LIMITE_METAS_SIMULTANEAS = 5;

    private final MetaRepository metaRepository;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public DesempenhoResponse gerar(Long usuarioId) {
        Usuario usuario = usuarioService.buscarEntidade(usuarioId);
        List<Meta> metas = metaRepository.buscarPorUsuario(usuarioId, null, null);

        List<Meta> ativas = metas.stream().filter(m -> m.getStatus() != StatusMeta.CANCELADA).toList();
        int concluidas = contarPorStatus(metas, StatusMeta.CONCLUIDA);
        int emAndamento = contarPorStatus(metas, StatusMeta.EM_ANDAMENTO);
        int canceladas = contarPorStatus(metas, StatusMeta.CANCELADA);
        int atrasadas = (int) metas.stream().filter(Meta::isAtrasada).count();

        List<ItemChecklist> itens = ativas.stream().flatMap(m -> m.getItens().stream()).toList();
        int itensConcluidos = (int) itens.stream().filter(ItemChecklist::isConcluido).count();
        int progressoGeral = itens.isEmpty() ? 0 : (int) Math.round(itensConcluidos * 100.0 / itens.size());

        Map<Categoria, Integer> progressoPorCategoria = calcularProgressoPorCategoria(ativas);
        Categoria destaque = progressoPorCategoria.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        int pontuacao = calcularPontuacao(ativas, concluidas, atrasadas);
        PerfilDesempenho perfil = metas.isEmpty() ? PerfilDesempenho.INICIANTE : PerfilDesempenho.porPontuacao(pontuacao);

        return new DesempenhoResponse(
                usuario.getId(),
                usuario.getNome(),
                metas.size(),
                concluidas,
                emAndamento,
                canceladas,
                atrasadas,
                itens.size(),
                itensConcluidos,
                progressoGeral,
                pontuacao,
                perfil,
                perfil.getMensagem(),
                destaque,
                progressoPorCategoria,
                gerarRecomendacoes(metas, atrasadas, emAndamento, progressoPorCategoria)
        );
    }

    private int contarPorStatus(List<Meta> metas, StatusMeta status) {
        return (int) metas.stream().filter(m -> m.getStatus() == status).count();
    }

    private Map<Categoria, Integer> calcularProgressoPorCategoria(List<Meta> metas) {
        Map<Categoria, Double> medias = metas.stream().collect(Collectors.groupingBy(
                Meta::getCategoria,
                () -> new EnumMap<>(Categoria.class),
                Collectors.averagingInt(Meta::calcularProgresso)));

        Map<Categoria, Integer> resultado = new EnumMap<>(Categoria.class);
        medias.forEach((categoria, media) -> resultado.put(categoria, (int) Math.round(media)));
        return resultado;
    }

    private int calcularPontuacao(List<Meta> ativas, int concluidas, int atrasadas) {
        if (ativas.isEmpty()) {
            return 0;
        }
        double taxaConclusao = concluidas * 100.0 / ativas.size();
        double progressoMedio = ativas.stream().mapToInt(Meta::calcularProgresso).average().orElse(0);
        double pontualidade = 100.0 - atrasadas * 100.0 / ativas.size();

        return (int) Math.round(taxaConclusao * PESO_CONCLUSAO
                + progressoMedio * PESO_PROGRESSO
                + pontualidade * PESO_PONTUALIDADE);
    }

    private List<String> gerarRecomendacoes(List<Meta> metas, int atrasadas, int emAndamento,
                                            Map<Categoria, Integer> progressoPorCategoria) {
        List<String> recomendacoes = new ArrayList<>();
        if (metas.isEmpty()) {
            recomendacoes.add("Comece cadastrando uma meta simples e divida-a em pequenos itens de checklist.");
            return recomendacoes;
        }

        List<Meta> andamento = metas.stream().filter(m -> m.getStatus() == StatusMeta.EM_ANDAMENTO).toList();
        LocalDate hoje = LocalDate.now();

        if (atrasadas > 0) {
            recomendacoes.add("Você tem " + atrasadas + " meta(s) atrasada(s). Revise os prazos ou divida-as em etapas menores.");
        }
        if (emAndamento > LIMITE_METAS_SIMULTANEAS) {
            recomendacoes.add("Há muitas metas em andamento ao mesmo tempo. Priorize as de prioridade alta.");
        }
        andamento.stream()
                .filter(m -> m.getDataLimite() != null && !m.isAtrasada())
                .filter(m -> ChronoUnit.DAYS.between(hoje, m.getDataLimite()) <= DIAS_ALERTA_PRAZO)
                .forEach(m -> recomendacoes.add("A meta \"" + m.getTitulo() + "\" vence em "
                        + ChronoUnit.DAYS.between(hoje, m.getDataLimite()) + " dia(s). Concentre seus esforços nela!"));
        andamento.stream()
                .filter(m -> m.getPrioridade() == Prioridade.ALTA && m.calcularProgresso() < 30)
                .forEach(m -> recomendacoes.add("A meta \"" + m.getTitulo()
                        + "\" é de prioridade alta, mas ainda tem pouco progresso."));
        if (andamento.stream().anyMatch(m -> m.getItens().isEmpty())) {
            recomendacoes.add("Adicione itens de checklist às metas sem etapas definidas para acompanhar melhor seu progresso.");
        }
        if (progressoPorCategoria.size() > 1) {
            progressoPorCategoria.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .ifPresent(e -> recomendacoes.add("Dedique mais atenção à categoria " + e.getKey()
                            + ", que está com o menor progresso (" + e.getValue() + "%)."));
        }
        if (recomendacoes.isEmpty()) {
            recomendacoes.add("Nenhum ponto de atenção no momento. Continue assim!");
        }
        return recomendacoes;
    }
}
