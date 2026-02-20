package com.example.diaria_do_motorista.ui.theme.feature.login.usuario.edit

sealed class UsuarioEditEvent {
    data class OnNomeChange(val nome: String) : UsuarioEditEvent()
    data class OnEmailChange(val email: String) : UsuarioEditEvent()
    data class OnTelefoneChange(val telefone: String) : UsuarioEditEvent()
    data class OnDataNascimentoChange(val dataNascimento: String) : UsuarioEditEvent()
    data class OnTransportadoraSelected(val transportadoraId: String) : UsuarioEditEvent()
    data class OnVeiculoSelected(val matricula: String) : UsuarioEditEvent()
    data class OnSenhaChange(val senha: String) : UsuarioEditEvent()
    data class OnConfirmarSenhaChange(val confirmarSenha: String) : UsuarioEditEvent()
    object OnSave : UsuarioEditEvent()
    object OnLoadData : UsuarioEditEvent()
    object ClearMessages : UsuarioEditEvent()
}