// Declaração do pacote da aplicação, agrupando as classes relacionadas.
package com.example.google_books_project

// Importação de classes necessárias para testes em dispositivos Android.
import androidx.test.platform.app.InstrumentationRegistry

// Fornece acesso ao Instrumentation para testes.
import androidx.test.ext.junit.runners.AndroidJUnit4

// Runner usado para executar testes JUnit em Android.
import org.junit.Test

// Anotação para marcar métodos como testes.
import org.junit.runner.RunWith

// Permite especificar um Runner para o teste.
import org.junit.Assert.*

// Fornece métodos estáticos para asserções em testes, como assertEquals.

/**
 * Classe para testes instrumentados, executados em dispositivos Android.
 *
 * A documentação oficial do Android oferece mais detalhes:
 * [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
// Define que esta classe de testes usará o Runner AndroidJUnit4.
class ExampleInstrumentedTest {

    // Método de teste: verifica se o contexto da aplicação tem o nome do pacote correto.
    @Test
    fun useAppContext() {
        // Obtém o contexto da aplicação sob teste usando InstrumentationRegistry.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // Verifica se o nome do pacote do contexto é igual ao esperado.
        assertEquals("com.example.google_books_project", appContext.packageName)
    }
}
