package com.example.goals_app.util

import com.example.goals_app.data.model.ErroResponse
import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException

// Traduz qualquer falha de chamada à API em UMA mensagem pronta para mostrar ao usuário.
// "object" = Singleton, aqui usado como "classe utilitária" (métodos estáticos).
object ApiErro {

    private val gson = Gson()

    const val MSG_SEM_CONEXAO = "Não foi possível conectar ao servidor"
    private const val MSG_GENERICA = "Ocorreu um erro inesperado. Tente novamente."

    // Caso 1: o servidor respondeu, mas com erro (400, 401, 404, 409...).
    // O corpo do erro vem como JSON (ErroResponse) em response.errorBody().
    fun mensagem(response: Response<*>): String {
        val json = response.errorBody()?.string()          // "?." = só chama se não for null
        if (json.isNullOrBlank()) return MSG_GENERICA

        return try {
            val erro = gson.fromJson(json, ErroResponse::class.java)
            // Em erros de validação (400), "detalhes" lista cada campo inválido.
            if (!erro.detalhes.isNullOrEmpty()) {
                erro.detalhes.joinToString("\n")
            } else {
                erro.mensagem
            }
        } catch (e: Exception) {                           // corpo não era o JSON esperado
            MSG_GENERICA
        }
    }

    // Caso 2: nem chegou ao servidor (sem internet, API desligada, timeout).
    // O Retrofit lança IOException nesses casos.
    fun mensagem(erro: Throwable): String = when (erro) {
        is IOException -> MSG_SEM_CONEXAO
        else -> MSG_GENERICA
    }
}
