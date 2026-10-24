package br.com.gestrh.mobile.dados

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
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

class FuncionariosTest {
    private lateinit var contexto: Context
    private lateinit var nomeBanco: String
    private var banco: BancoDeDados? = null

    @Before
    fun preparar() {
        contexto = ApplicationProvider.getApplicationContext()
        nomeBanco = "funcionarios-${UUID.randomUUID()}.db"
    }

    private fun abrirBanco(): BancoDeDados = Room.databaseBuilder(
        contexto, BancoDeDados::class.java, nomeBanco
    ).addMigrations(BancoDeDados.MIGRACAO_1_2, BancoDeDados.MIGRACAO_2_3).build().also { banco = it }

    @After
    fun limpar() {
        banco?.close()
        contexto.deleteDatabase(nomeBanco)
    }

    @Test
    fun cadastraOrdenaEPreservaAposReabrir() = runBlocking {
        val repositorio = RepositorioFuncionarios(abrirBanco().funcionarios())
        assertTrue(repositorio.observarFuncionarios().first().isEmpty())
        val bruno = repositorio.cadastrar("  Bruno  ", "  Administrativo  ")
        val ana = repositorio.cadastrar("ana")
        val registros = repositorio.observarFuncionarios().first()
        assertEquals(listOf(ana, bruno), registros.map { it.id })
        assertEquals("Bruno", registros.last().nome)
        assertEquals("Administrativo", registros.last().observacao)
        assertEquals("", registros.first().observacao)
        banco?.close()
        assertEquals(registros, RepositorioFuncionarios(abrirBanco().funcionarios()).observarFuncionarios().first())
    }

    @Test
    fun rejeitaNomeVazioSemGravar() = runBlocking {
        val repositorio = RepositorioFuncionarios(abrirBanco().funcionarios())
        try {
            repositorio.cadastrar("   ", "Observação")
            fail("Deveria rejeitar nome vazio")
        } catch (esperado: IllegalArgumentException) {
            assertTrue(repositorio.observarFuncionarios().first().isEmpty())
        }
    }

    @Test
    fun migraBancoAntigoSemPerderDemandas() = runBlocking {
        val arquivo = contexto.getDatabasePath(nomeBanco)
        arquivo.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(arquivo, null).use { antigo ->
            antigo.execSQL("CREATE TABLE demandas (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, titulo TEXT NOT NULL, prazo INTEGER NOT NULL, observacao TEXT NOT NULL, situacao TEXT NOT NULL)")
            antigo.execSQL("INSERT INTO demandas VALUES (7, 'Folha antiga', 20750, 'Conferir', 'EM_ANDAMENTO')")
            antigo.version = 1
        }
        val atualizado = abrirBanco()
        val demandas = RepositorioDemandas(atualizado.demandas())
        val antiga = requireNotNull(demandas.buscarDemanda(7))
        assertEquals("Folha antiga", antiga.titulo)
        assertEquals(LocalDate.ofEpochDay(20750), antiga.prazo)
        assertEquals("Conferir", antiga.observacao)
        assertEquals(SituacaoDemanda.EM_ANDAMENTO, antiga.situacao)
        val funcionarios = RepositorioFuncionarios(atualizado.funcionarios())
        funcionarios.cadastrar("Ana", "Exemplo")
        val novoId = demandas.cadastrar("Nova demanda", LocalDate.now())
        assertTrue(novoId > antiga.id)
        banco?.close()
        val reaberto = abrirBanco()
        assertEquals(antiga, RepositorioDemandas(reaberto.demandas()).buscarDemanda(7))
        assertEquals("Ana", RepositorioFuncionarios(reaberto.funcionarios()).observarFuncionarios().first().single().nome)
        assertEquals(2, RepositorioDemandas(reaberto.demandas()).observarDemandas().first().size)
    }
    @Test
    fun migraVersaoDoisPreservandoFuncionariosEDemandas() = runBlocking {
        val arquivo = contexto.getDatabasePath(nomeBanco)
        arquivo.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(arquivo, null).use { antigo ->
            antigo.execSQL("CREATE TABLE demandas (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, titulo TEXT NOT NULL, prazo INTEGER NOT NULL, observacao TEXT NOT NULL, situacao TEXT NOT NULL)")
            antigo.execSQL("CREATE TABLE funcionarios (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, nome TEXT NOT NULL, observacao TEXT NOT NULL)")
            antigo.execSQL("INSERT INTO demandas VALUES (7, 'Férias', 20750, 'Conferir', 'PENDENTE')")
            antigo.execSQL("INSERT INTO funcionarios VALUES (4, 'Ana', 'Administrativo')")
            antigo.version = 2
        }
        val atualizado = abrirBanco()
        val demandas = RepositorioDemandas(atualizado.demandas())
        val antiga = requireNotNull(demandas.buscarDemanda(7))
        assertNull(antiga.funcionarioId)
        assertEquals("Férias", antiga.titulo)
        assertEquals(LocalDate.ofEpochDay(20750), antiga.prazo)
        assertEquals("Conferir", antiga.observacao)
        assertEquals(SituacaoDemanda.PENDENTE, antiga.situacao)
        val funcionario = RepositorioFuncionarios(atualizado.funcionarios()).observarFuncionarios().first().single()
        assertEquals(4L, funcionario.id)
        assertEquals("Ana", funcionario.nome)
        demandas.editar(7, antiga.titulo, antiga.prazo, antiga.observacao, funcionarioId = 4, atualizarVinculo = true)
        banco?.close()
        assertEquals(4L, RepositorioDemandas(abrirBanco().demandas()).buscarDemanda(7)?.funcionarioId)
    }

    @Test
    fun cadastraTrocaRemoveEPreservaVinculoAposReabrir() = runBlocking {
        var atual = abrirBanco()
        val funcionarios = RepositorioFuncionarios(atual.funcionarios())
        val ana = funcionarios.cadastrar("Ana")
        val bruno = funcionarios.cadastrar("Bruno")
        var demandas = RepositorioDemandas(atual.demandas())
        val prazo = LocalDate.of(2026, 10, 25)
        val id = demandas.cadastrar("Férias", prazo, "Conferir", funcionarioId = ana)
        val geral = demandas.cadastrar("Folha geral", prazo)
        assertNull(demandas.buscarDemanda(geral)?.funcionarioId)
        banco?.close()
        atual = abrirBanco()
        demandas = RepositorioDemandas(atual.demandas())
        assertEquals(ana, demandas.buscarDemanda(id)?.funcionarioId)
        demandas.editar(id, "Férias revisadas", prazo, "Conferir")
        assertEquals(ana, demandas.buscarDemanda(id)?.funcionarioId)
        for (vinculo in listOf(bruno, null)) {
            demandas.editar(id, "Férias revisadas", prazo, "Conferir", SituacaoDemanda.EM_ANDAMENTO,
                funcionarioId = vinculo, atualizarVinculo = true)
            banco?.close()
            demandas = RepositorioDemandas(abrirBanco().demandas())
            val registro = requireNotNull(demandas.buscarDemanda(id))
            assertEquals(vinculo, registro.funcionarioId)
            assertEquals("Férias revisadas", registro.titulo)
            assertEquals(prazo, registro.prazo)
            assertEquals(SituacaoDemanda.EM_ANDAMENTO, registro.situacao)
            assertEquals(2, demandas.observarDemandas().first().size)
        }
    }

    @Test
    fun rejeitaFuncionarioInexistenteSemAlterarDemanda() = runBlocking {
        val demandas = RepositorioDemandas(abrirBanco().demandas())
        val prazo = LocalDate.of(2026, 10, 25)
        val id = demandas.cadastrar("Original", prazo)
        val original = demandas.buscarDemanda(id)
        try {
            demandas.editar(id, "Não salvar", prazo, "", funcionarioId = Long.MAX_VALUE, atualizarVinculo = true)
            fail("Deveria rejeitar funcionário inexistente")
        } catch (esperado: android.database.sqlite.SQLiteConstraintException) {
            assertEquals(original, demandas.buscarDemanda(id))
        }
        try {
            demandas.cadastrar("Não inserir", prazo, funcionarioId = Long.MAX_VALUE)
            fail("Deveria rejeitar funcionário inexistente")
        } catch (esperado: android.database.sqlite.SQLiteConstraintException) {
            assertEquals(listOf(original), demandas.observarDemandas().first())
        }
    }

}
