package br.com.gestrh.mobile.dados

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gestrh.mobile.dados.banco.BancoDeDados
import br.com.gestrh.mobile.dados.modelos.SituacaoDemanda
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersistenciaDemandasTest {
    private lateinit var contexto: Context
    private lateinit var nomeBanco: String
    private lateinit var banco: BancoDeDados
    private lateinit var repositorio: RepositorioDemandas

    @Before
    fun preparar() {
        contexto = ApplicationProvider.getApplicationContext()
        nomeBanco = "teste-${UUID.randomUUID()}.db"
        abrirBanco()
    }

    private fun abrirBanco() {
        banco = Room.databaseBuilder(contexto, BancoDeDados::class.java, nomeBanco).build()
        repositorio = RepositorioDemandas(banco.demandas())
    }

    @After
    fun limpar() {
        banco.close()
        contexto.deleteDatabase(nomeBanco)
    }

    @Test
    fun recuperaDadosESituacoesDepoisDeReabrir() = runBlocking {
        val prazo = LocalDate.of(2028, 2, 29)
        val registros = SituacaoDemanda.entries.map { situacao ->
            val id = repositorio.cadastrar("  Férias de exemplo  ", prazo, " Conferir datas ", situacao)
            requireNotNull(repositorio.buscarDemanda(id))
        }
        assertEquals(3, registros.map { it.id }.distinct().size)
        banco.close()
        abrirBanco()
        assertEquals(registros, repositorio.observarDemandas().first())
        registros.forEach {
            assertEquals("Férias de exemplo", it.titulo)
            assertEquals("Conferir datas", it.observacao)
            assertEquals(prazo, it.prazo)
            assertEquals(it, repositorio.buscarDemanda(it.id))
        }
        assertNull(repositorio.buscarDemanda(Long.MAX_VALUE))
    }

    @Test
    fun listaDemandasPorPrazo() = runBlocking {
        assertTrue(repositorio.observarDemandas().first().isEmpty())
        val depois = repositorio.cadastrar("Folha de exemplo", LocalDate.of(2026, 11, 1))
        val antes = repositorio.cadastrar("Férias de exemplo", LocalDate.of(2026, 10, 15))
        assertEquals(listOf(antes, depois), repositorio.observarDemandas().first().map { it.id })
        assertEquals(SituacaoDemanda.PENDENTE, repositorio.buscarDemanda(antes)?.situacao)
    }

    @Test
    fun rejeitaTituloVazioSemGravar() = runBlocking {
        var rejeitou = false
        try {
            repositorio.cadastrar("   ", LocalDate.of(2026, 10, 15))
        } catch (erro: IllegalArgumentException) {
            rejeitou = true
        }
        assertTrue(rejeitou)
        assertTrue(repositorio.observarDemandas().first().isEmpty())
    }
}
