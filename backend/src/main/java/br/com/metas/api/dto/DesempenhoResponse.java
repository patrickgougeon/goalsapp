package br.com.metas.api.dto;

import br.com.metas.api.entity.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "Resultado personalizado com a análise de desempenho do usuário")
public record DesempenhoResponse(
        Long usuarioId,
        String nomeUsuario,
        int totalMetas,
        int metasConcluidas,
        int metasEmAndamento,
        int metasCanceladas,
        int metasAtrasadas,
        int totalItens,
        int itensConcluidos,
        @Schema(description = "Percentual geral de itens concluídos (0 a 100)") int progressoGeral,
        @Schema(description = "Pontuação de 0 a 100 que combina conclusão, progresso e pontualidade") int pontuacao,
        @Schema(example = "COMPROMETIDO") PerfilDesempenho perfil,
        @Schema(example = "Ótimo ritmo! Continue assim para alcançar todas as suas metas.") String mensagem,
        @Schema(description = "Categoria com maior progresso médio (nulo se não houver metas)") Categoria categoriaDestaque,
        @Schema(description = "Progresso médio (0 a 100) das metas de cada categoria") Map<Categoria, Integer> progressoPorCategoria,
        List<String> recomendacoes
) {
}
