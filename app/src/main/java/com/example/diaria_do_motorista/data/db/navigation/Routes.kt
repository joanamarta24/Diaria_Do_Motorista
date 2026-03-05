package com.example.diaria_do_motorista.data.db.navigation

import okhttp3.Route

sealed class Routes(val route: String) {
object Login: Routes("login")
 object Home: Routes("home")
 object UsuariosList: Routes("usuarios")
 object UsuarioEdit: Routes("usuario/{usuarioId}"){
     fun passId(usuarioId: String ="new") = "usuario/$usuarioId"
 }
    object UsuarioDetails: Routes("usuario/detalhes/{usuarioId}"){
        fun passId(usuarioId: String) = "usuario/detalhes/$usuarioId"
    }
    object Relatorios : Routes("relatorios")
    object Transportadoras : Routes("transportadoras")
    object Veiculos : Routes("veiculos")
}