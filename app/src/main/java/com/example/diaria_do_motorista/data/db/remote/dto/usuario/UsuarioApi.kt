import com.example.diaria_do_motorista.data.db.remote.dto.usuario.UsuarioAlterarSenhaDto
import com.example.diaria_do_motorista.data.db.remote.dto.usuario.UsuarioCreateDto
import com.example.diaria_do_motorista.data.db.remote.dto.usuario.UsuarioResponseDto
import com.example.diaria_do_motorista.data.db.remote.dto.usuario.UsuarioUpdateDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UsuarioApi {

    companion object {
        private const val BASE_ENDPOINT = "usuarios"
    }

    // GET ALL ou GET com filtros
    @GET("$BASE_ENDPOINT")
    @Headers("Content-Type: application/json")
    suspend fun listarUsuarios(
        @Header("Authorization") token: String,
        @Query("transportadoraId") transportadoraId: String? = null,
        @Query("tipo") tipo: String? = null,
        @Query("ativo") ativo: Boolean? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String? = "nome,asc"
    ): Response<List<UsuarioResponseDto>>

    // GET por ID
    @GET("$BASE_ENDPOINT/{id}")
    @Headers("Content-Type: application/json")
    suspend fun obterUsuarioPorId(
        @Header("Authorization") token: String,
        @Path("id") id: Long // Ou String se realmente for string
    ): Response<UsuarioResponseDto>

    // GET por email
    @GET("$BASE_ENDPOINT/email/{email}")
    @Headers("Content-Type: application/json")
    suspend fun obterUsuarioPorEmail(
        @Header("Authorization") token: String,
        @Path("email") email: String
    ): Response<UsuarioResponseDto>

    // POST - Criar
    @POST(BASE_ENDPOINT)
    @Headers("Content-Type: application/json")
    suspend fun criarUsuario(
        @Header("Authorization") token: String,
        @Body usuarioCreateDto: UsuarioCreateDto
    ): Response<UsuarioResponseDto> // Normalmente retorna o recurso criado

    // PUT - Atualizar completo
    @PUT("$BASE_ENDPOINT/{id}")
    @Headers("Content-Type: application/json")
    suspend fun atualizarUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body usuarioUpdateDto: UsuarioUpdateDto
    ): Response<UsuarioResponseDto> // Retorna o recurso atualizado

    // PATCH - Atualizar parcial (senha)
    @PATCH("$BASE_ENDPOINT/{id}/senha") // Usar PATCH para atualização parcial
    @Headers("Content-Type: application/json")
    suspend fun alterarSenha(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body alterarSenhaDto: UsuarioAlterarSenhaDto
    ): Response<Void> // Ou Unit, mas Void é mais claro para "sem retorno"

    // PATCH - Status
    @PATCH("$BASE_ENDPOINT/{id}/status")
    @Headers("Content-Type: application/json")
    suspend fun alterarStatus(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body statusRequest: StatusRequest// Melhor que Query para PATCH
    ): Response<Void>

    // DELETE
    @DELETE("$BASE_ENDPOINT/{id}")
    @Headers("Content-Type: application/json")
    suspend fun excluirUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Void>

    // GET por transportadora
    @GET("$BASE_ENDPOINT/transportadora/{transportadoraId}")
    @Headers("Content-Type: application/json")
    suspend fun listarPorTransportadora(
        @Header("Authorization") token: String,
        @Path("transportadoraId") transportadoraId: Long,
        @Query("ativo") ativo: Boolean? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): Response<List<UsuarioResponseDto>>

    // GET motoristas disponíveis
    @GET("$BASE_ENDPOINT/motoristas/disponiveis")
    @Headers("Content-Type: application/json")
    suspend fun listarMotoristasDisponiveis(
        @Header("Authorization") token: String,
        @Query("data") data: String, // Usar formato ISO: "2024-01-30"
        @Query("transportadoraId") transportadoraId: Long? = null,
        @Query("turno") turno: String? = null // Adicionar turno se aplicável
    ): Response<List<UsuarioResponseDto>>

    // GET perfil do usuário logado
    @GET("$BASE_ENDPOINT/me")
    @Headers("Content-Type: application/json")
    suspend fun obterMeuPerfil(
        @Header("Authorization") token: String
    ): Response<UsuarioResponseDto>

    // NOVO: Busca com múltiplos filtros
    @GET("$BASE_ENDPOINT/busca")
    @Headers("Content-Type: application/json")
    suspend fun buscarUsuarios(
        @Header("Authorization") token: String,
        @Query("nome") nome: String? = null,
        @Query("email") email: String? = null,
        @Query("tipo") tipo: String? = null,
        @Query("transportadoraId") transportadoraId: Long? = null,
        @Query("ativo") ativo: Boolean? = null
    ): Response<List<UsuarioResponseDto>>
}

