package com.example.goals_app.util

import android.content.Context

// Guarda quem está logado. SharedPreferences = um "localStorage" do Android:
// pares chave/valor salvos em um arquivo XML privado do app, que sobrevive ao fechar o app.
//
// "Context" é o objeto que dá acesso aos recursos do Android (arquivos, preferências, strings...).
// Toda Activity É um Context, então basta passar "this" ao criar: SessionManager(this).
class SessionManager(context: Context) {

    // "private val" no construtor-corpo: só esta classe enxerga as preferências.
    private val prefs = context.getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE)

    // Salva o login. apply() grava em segundo plano (commit() grava na hora e trava a tela).
    fun salvarUsuario(id: Long, nome: String) {
        prefs.edit()
            .putLong(CHAVE_ID, id)
            .putString(CHAVE_NOME, nome)
            .apply()
    }

    // Propriedades com get(): parecem campos, mas executam código ao serem lidas.
    val usuarioId: Long
        get() = prefs.getLong(CHAVE_ID, SEM_USUARIO)

    val nomeUsuario: String
        get() = prefs.getString(CHAVE_NOME, "") ?: ""   // "?:" (Elvis) = valor padrão se vier null

    val estaLogado: Boolean
        get() = usuarioId != SEM_USUARIO

    // Logout: apaga tudo.
    fun limpar() {
        prefs.edit().clear().apply()
    }

    // "companion object" = onde ficam os membros estáticos (static) da classe.
    private companion object {
        const val ARQUIVO = "sessao_metas"
        const val CHAVE_ID = "usuario_id"
        const val CHAVE_NOME = "usuario_nome"
        const val SEM_USUARIO = -1L
    }
}
