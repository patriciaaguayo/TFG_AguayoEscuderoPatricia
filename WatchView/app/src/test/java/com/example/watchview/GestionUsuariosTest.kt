package com.example.watchview

import android.content.Context
import android.widget.Toast
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class GestionUsuariosTest {

    private fun verificarCorreo(correo: String): Boolean {
        val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        return regex.matches(correo)
    }

    private fun empiezaConMayuscula(texto: String): Boolean {
        return texto.isNotEmpty() && texto.first().isUpperCase()
    }

    fun validarContrasena(context: Context, pass: String): Boolean {
        if (pass.length < 6) {
            return false
        }

        if (!pass.first().isUpperCase()) {
            return false
        }

        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{6,}$")
        if (!regex.containsMatchIn(pass)) {
            return false
        }

        return true
    }

    @Test
    fun verificarCorreo() {
        assertTrue(verificarCorreo("test@example.com"))
        assertTrue(verificarCorreo("correo.valido_123@dominio.org"))
        assertFalse(verificarCorreo("correoInvalido.com"))
        assertFalse(verificarCorreo("invalido@com"))
        assertFalse(verificarCorreo("otro@.com"))
    }

    @Test
    fun empiezaConMayuscula() {
        assertTrue(empiezaConMayuscula("Hola"))
        assertFalse(empiezaConMayuscula("hola"))
        assertFalse(empiezaConMayuscula(""))
        assertTrue(empiezaConMayuscula("A"))  // Un solo caracter válido
    }

    @Test
    fun validarContrasena() {
        val mockContext = mock(Context::class.java)

        // Caso correcto
        assertTrue(validarContrasena(mockContext, "Password1!"))

        // Muy corta
        assertFalse(validarContrasena(mockContext, "Pas1!"))

        // No empieza con mayúscula
        assertFalse(validarContrasena(mockContext, "password1!"))

        // No tiene carácter especial
        assertFalse(validarContrasena(mockContext, "Password1"))

        // No tiene número
        assertFalse(validarContrasena(mockContext, "Password!"))

        // No tiene mayúscula
        assertFalse(validarContrasena(mockContext, "password1!"))
    }
}