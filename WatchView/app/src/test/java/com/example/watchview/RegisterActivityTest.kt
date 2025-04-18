package com.example.watchview

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import android.content.Context

import org.junit.jupiter.api.Test

class RegisterActivityTest {

    private fun verificarCorreo(correo: String): Boolean {
        val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        return regex.matches(correo)
    }

    private fun empiezaConMayuscula(texto: String): Boolean {
        return texto.isNotEmpty() && texto.first().isUpperCase()
    }

    private fun validarContrasenas(context: Context, pass: String, repPass: String): Boolean {
        if (pass != repPass) {
            return false
        }

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
    fun verificarCorreo_validoEInvalido() {
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
    fun validarContrasenas() {
        val mockContext = mock(Context::class.java)

        // Caso correcto
        assertTrue(validarContrasenas(mockContext, "Password1!", "Password1!"))

        // Contraseñas diferentes
        assertFalse(validarContrasenas(mockContext, "Password1!", "Password2!"))

        // Muy corta
        assertFalse(validarContrasenas(mockContext, "Pas1!", "Pass1!"))

        // No empieza con mayúscula
        assertFalse(validarContrasenas(mockContext, "password1!", "password1!"))

        // No tiene carácter especial
        assertFalse(validarContrasenas(mockContext, "Password1", "Password1"))

        // No tiene número
        assertFalse(validarContrasenas(mockContext, "Password!", "Password!"))

        // No tiene mayúscula
        assertFalse(validarContrasenas(mockContext, "password1!", "password1!"))
    }
}