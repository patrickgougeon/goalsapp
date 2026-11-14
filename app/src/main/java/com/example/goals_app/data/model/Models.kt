package com.example.goals_app.data.model

// DTOs: classes que apenas carregam dados entre o app e a API (equivalem aos "records" do backend).
// Os nomes dos campos DEVEM ser iguais aos do JSON. Datas ficam como String e são formatadas na tela.

// ---------- Usuário ----------
data class UsuarioRequest(val nome: String, val email: String, val senha: String)

data class LoginRequest(val email: String, val senha: String)

data class UsuarioResponse(
    val id: Long,
    val nome: String,
    val email: String,
    val criadoEm: String
)

// ---------- Checklist ----------
data class ItemChecklistRequest(
    val descricao: String,
    val ordem: Int? = null,        // null = backend coloca no fim da lista
    val concluido: Boolean? = null
)

data class ItemChecklistResponse(
    val id: Long,
    val metaId: Long,
    val descricao: String,
    val concluido: Boolean,
    val ordem: Int,
    val concluidoEm: String?       // "?" = pode ser null
)

// ---------- Meta ----------
data class MetaRequest(
    val titulo: String,
    val descricao: String?,
    val categoria: Categoria,
    val prioridade: Prioridade,
    val dataLimite: String?,       // formato "2026-12-31"
    val itens: List<ItemChecklistRequest>? = null
)

data class StatusRequest(val status: StatusMeta)

data class MetaResponse(
    val id: Long,
    val usuarioId: Long,
    val titulo: String,
    val descricao: String?,
    val categoria: Categoria,
    val prioridade: Prioridade,
    val status: StatusMeta,
    val dataLimite: String?,
    val criadaEm: String,
    val concluidaEm: String?,
    val progresso: Int,            // 0..100, direto na ProgressBar
    val atrasada: Boolean,
    val totalItens: Int,
    val itensConcluidos: Int,
    val itens: List<ItemChecklistResponse>
)

// ---------- Desempenho ----------
data class DesempenhoResponse(
    val usuarioId: Long,
    val nomeUsuario: String,
    val totalMetas: Int,
    val metasConcluidas: Int,
    val metasEmAndamento: Int,
    val metasCanceladas: Int,
    val metasAtrasadas: Int,
    val totalItens: Int,
    val itensConcluidos: Int,
    val progressoGeral: Int,
    val pontuacao: Int,
    val perfil: PerfilDesempenho,
    val mensagem: String,
    val categoriaDestaque: Categoria?,
    val progressoPorCategoria: Map<Categoria, Int>,
    val recomendacoes: List<String>
)

// ---------- Erro (corpo de qualquer resposta 4xx) ----------
data class ErroResponse(
    val status: Int,
    val erro: String,
    val mensagem: String,
    val detalhes: List<String>?
)
