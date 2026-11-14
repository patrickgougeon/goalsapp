package com.example.goals_app.util

import retrofit2.Response

// Resultado de uma chamada à API: ou deu certo (com o dado), ou deu erro (com a mensagem).
// "sealed class" = conjunto FECHADO de subtipos (parecido com enum, mas cada caso pode carregar dados).
// "<out T>" = genérico, como Resultado<T> em Java.
sealed class Resultado<out T> {
    data class Sucesso<T>(val dado: T) : Resultado<T>()
    data class Erro(val mensagem: String) : Resultado<Nothing>()
}

// Executa a chamada de rede e já converte tudo em Resultado, para as telas não repetirem try/catch.
// "suspend" porque chama funções suspend; "inline"/lambda: recebe a chamada como parâmetro.
//
// Uso numa Activity:
//   when (val r = chamarApi { ApiClient.api.login(...) }) {
//       is Resultado.Sucesso -> usar(r.dado)
//       is Resultado.Erro    -> mostrar(r.mensagem)
//   }
suspend fun <T> chamarApi(chamada: suspend () -> Response<T>): Resultado<T> {
    return try {
        val response = chamada()
        val corpo = response.body()
        when {
            response.isSuccessful && corpo != null -> Resultado.Sucesso(corpo)
            // DELETE devolve 204 sem corpo: sucesso, mas body() é null. Só é seguro para Response<Unit>.
            response.isSuccessful -> @Suppress("UNCHECKED_CAST") Resultado.Sucesso(Unit as T)
            else -> Resultado.Erro(ApiErro.mensagem(response))
        }
    } catch (e: Exception) {
        Resultado.Erro(ApiErro.mensagem(e))
    }
}
