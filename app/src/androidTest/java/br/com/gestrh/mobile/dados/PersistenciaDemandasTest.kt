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
    @Test
    fun editaSemDuplicarEPreservaSituacaoAposReabertura() = runBlocking {
        val id = repositorio.cadastrar("Original", LocalDate.of(2026, 10, 25), "Antes", SituacaoDemanda.EM_ANDAMENTO)
        val outroId = repositorio.cadastrar("Outra demanda", LocalDate.of(2026, 10, 24))
        val outra = repositorio.buscarDemanda(outroId)
        repositorio.editar(id, "  Revisada  ", LocalDate.of(2026, 10, 23), "  Depois  ")
        banco.close()
        abrirBanco()
        val atual = requireNotNull(repositorio.buscarDemanda(id))
        assertEquals("Revisada", atual.titulo)
        assertEquals("Depois", atual.observacao)
        assertEquals(LocalDate.of(2026, 10, 23), atual.prazo)
        assertEquals(SituacaoDemanda.EM_ANDAMENTO, atual.situacao)
        assertEquals(listOf(id, outroId), repositorio.observarDemandas().first().map { it.id })
        assertEquals(outra, repositorio.buscarDemanda(outroId))
    }

    @Test
    fun rejeitaEdicaoInvalidaSemAlterarRegistros() = runBlocking {
        val id = repositorio.cadastrar("Original", LocalDate.of(2026, 10, 25))
        val original = repositorio.buscarDemanda(id)
        try {
            repositorio.editar(id, "   ", LocalDate.now(), "Inválida")
            fail("Deveria rejeitar título vazio")
        } catch (esperado: IllegalArgumentException) {
            assertEquals(original, repositorio.buscarDemanda(id))
        }
        try {
            repositorio.editar(Long.MAX_VALUE, "Inexistente", LocalDate.now(), "")
            fail("Deveria rejeitar registro inexistente")
        } catch (esperado: IllegalStateException) {
            assertEquals(listOf(original), repositorio.observarDemandas().first())
        }
    }

    @Test
    fun alteraTodasAsSituacoesEPersisteDepoisDeReabrir() = runBlocking {
        val prazo = LocalDate.of(2026, 10, 25)
        val id = repositorio.cadastrar("Folha", prazo, "Conferir")
        val outroId = repositorio.cadastrar("Férias", prazo)
        val outra = repositorio.buscarDemanda(outroId)
        listOf(SituacaoDemanda.EM_ANDAMENTO, SituacaoDemanda.CONCLUIDA, SituacaoDemanda.PENDENTE).forEach { situacao ->
            repositorio.editar(id, "Folha", prazo, "Conferir", situacao)
            banco.close()
            abrirBanco()
            val demanda = requireNotNull(repositorio.buscarDemanda(id))
            assertEquals(situacao, demanda.situacao)
            assertEquals("Folha", demanda.titulo)
            assertEquals(prazo, demanda.prazo)
            assertEquals("Conferir", demanda.observacao)
            assertEquals(2, repositorio.observarDemandas().first().size)
            assertEquals(outra, repositorio.buscarDemanda(outroId))
        }
    }

    @Test
    fun tituloInvalidoNaoAlteraSituacao() = runBlocking {
        val id = repositorio.cadastrar("Original", LocalDate.of(2026, 10, 25))
        val original = repositorio.buscarDemanda(id)
        try {
            repositorio.editar(id, "  ", LocalDate.now(), "Inválida", SituacaoDemanda.CONCLUIDA)
            fail("Deveria rejeitar título vazio sem alterar a situação")
        } catch (esperado: IllegalArgumentException) {
            assertEquals(original, repositorio.buscarDemanda(id))
        }
    }

}
