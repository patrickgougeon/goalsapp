package com.example.goals_app.data.api

import com.example.goals_app.data.model.*
import retrofit2.Response
import retrofit2.http.*

// Interface do Retrofit: cada função = um endpoint. É como um @FeignClient (Spring Cloud) / um Controller "ao contrário".
// "suspend" = a chamada roda em coroutine (não trava a tela). Response<T> dá acesso ao código HTTP e ao erro.
interface MetasApi {

    // ---------- Usuários ----------
    @POST("api/usuarios")
    suspend fun cadastrar(@Body body: UsuarioRequest): Response<UsuarioResponse>

    @POST("api/usuarios/login")
    suspend fun login(@Body body: LoginRequest): Response<UsuarioResponse>

    @GET("api/usuarios/{id}")
    suspend fun buscarUsuario(@Path("id") id: Long): Response<UsuarioResponse>

    @PUT("api/usuarios/{id}")
    suspend fun atualizarUsuario(@Path("id") id: Long, @Body body: UsuarioRequest): Response<UsuarioResponse>

    @DELETE("api/usuarios/{id}")
    suspend fun excluirUsuario(@Path("id") id: Long): Response<Unit>

    @GET("api/usuarios/{id}/desempenho")
    suspend fun desempenho(@Path("id") id: Long): Response<DesempenhoResponse>

    // ---------- Metas ----------
    // @Query vira ?status=...&categoria=... ; se for null, o Retrofit simplesmente omite o parâmetro.
    @GET("api/usuarios/{usuarioId}/metas")
    suspend fun listarMetas(
        @Path("usuarioId") usuarioId: Long,
        @Query("status") status: StatusMeta? = null,
        @Query("categoria") categoria: Categoria? = null
    ): Response<List<MetaResponse>>

    @POST("api/usuarios/{usuarioId}/metas")
    suspend fun criarMeta(@Path("usuarioId") usuarioId: Long, @Body body: MetaRequest): Response<MetaResponse>

    @GET("api/metas/{id}")
    suspend fun buscarMeta(@Path("id") id: Long): Response<MetaResponse>

    @PUT("api/metas/{id}")
    suspend fun atualizarMeta(@Path("id") id: Long, @Body body: MetaRequest): Response<MetaResponse>

    @PATCH("api/metas/{id}/status")
    suspend fun alterarStatus(@Path("id") id: Long, @Body body: StatusRequest): Response<MetaResponse>

    @DELETE("api/metas/{id}")
    suspend fun excluirMeta(@Path("id") id: Long): Response<Unit>

    // ---------- Itens do checklist ----------
    @GET("api/metas/{metaId}/itens")
    suspend fun listarItens(@Path("metaId") metaId: Long): Response<List<ItemChecklistResponse>>

    @POST("api/metas/{metaId}/itens")
    suspend fun criarItem(@Path("metaId") metaId: Long, @Body body: ItemChecklistRequest): Response<ItemChecklistResponse>

    @PUT("api/itens/{id}")
    suspend fun atualizarItem(@Path("id") id: Long, @Body body: ItemChecklistRequest): Response<ItemChecklistResponse>

    @PATCH("api/itens/{id}/alternar")
    suspend fun alternarItem(@Path("id") id: Long): Response<ItemChecklistResponse>

    @DELETE("api/itens/{id}")
    suspend fun excluirItem(@Path("id") id: Long): Response<Unit>
}
