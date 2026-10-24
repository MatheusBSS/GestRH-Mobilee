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
    ).addMigrations(BancoDeDados.MIGRACAO_1_2).build().also { banco = it }

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
}
