package com.example.goals_app.data.model

// Cada enum espelha exatamente o enum do backend (mesmos nomes, em maiúsculas).
// O Gson converte o texto JSON "SAUDE" <-> Categoria.SAUDE automaticamente.
// "rotulo" é o texto amigável que aparece na tela.

enum class Categoria(val rotulo: String) {
    SAUDE("Saúde"),
    ESTUDOS("Estudos"),
    CARREIRA("Carreira"),
    FINANCAS("Finanças"),
    PESSOAL("Pessoal"),
    LAZER("Lazer"),
    OUTROS("Outros")
}

enum class Prioridade(val rotulo: String) {
    BAIXA("Baixa"),
    MEDIA("Média"),
    ALTA("Alta")
}

enum class StatusMeta(val rotulo: String) {
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada")
}

enum class PerfilDesempenho(val rotulo: String) {
    INICIANTE("Iniciante"),
    EM_CONSTRUCAO("Em construção"),
    EM_EVOLUCAO("Em evolução"),
    COMPROMETIDO("Comprometido"),
    EXEMPLAR("Exemplar")
}
