package br.com.metas.api.dto;

public enum PerfilDesempenho {

    INICIANTE("Cadastre sua primeira meta e comece a acompanhar sua evolução!"),
    EM_CONSTRUCAO("Todo começo é desafiador. Foque em uma meta por vez e conclua pequenos passos."),
    EM_EVOLUCAO("Você está evoluindo! Mantenha a constância para acelerar seus resultados."),
    COMPROMETIDO("Ótimo ritmo! Continue assim para alcançar todas as suas metas."),
    EXEMPLAR("Desempenho excelente! Que tal definir desafios ainda maiores?");

    private final String mensagem;

    PerfilDesempenho(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public static PerfilDesempenho porPontuacao(int pontuacao) {
        if (pontuacao >= 85) return EXEMPLAR;
        if (pontuacao >= 60) return COMPROMETIDO;
        if (pontuacao >= 35) return EM_EVOLUCAO;
        return EM_CONSTRUCAO;
    }
}
