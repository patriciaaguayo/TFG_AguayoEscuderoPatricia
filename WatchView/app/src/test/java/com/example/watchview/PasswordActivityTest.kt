package com.example.watchview

import android.content.Context
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class PasswordActivityTest {

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
    fun validarContrasenas() {
        val mockContext = mock(Context::class.java)

        // Caso correcto
        assertTrue(validarContrasenas(mockContext, "Password1!", "Password1!"))

        // Contraseñas diferentes
        assertFalse(validarContrasenas(mockContext, "Password1!", "Password2!"))

        // Muy corta
        assertFalse(validarContrasenas(mockContext, "Pass1!", "Pas1!"))

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