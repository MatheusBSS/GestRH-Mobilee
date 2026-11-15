package br.com.gestrh.mobile.apresentacao.listagem

import br.com.gestrh.mobile.dados.modelos.Demanda
import br.com.gestrh.mobile.dados.modelos.SituacaoDemanda
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class OrganizacaoDemandasTest {
    private val hoje = LocalDate.of(2026, 11, 15)
    private val demandas = listOf(
        Demanda(4, "Folha de pagamento", hoje.plusDays(1)),
        Demanda(2, "Férias João", hoje.minusDays(1)),
        Demanda(3, "Férias Maria", hoje.minusDays(1), situacao = SituacaoDemanda.EM_ANDAMENTO),
        Demanda(1, "Folha concluída", hoje.minusDays(2), situacao = SituacaoDemanda.CONCLUIDA),
        Demanda(5, "Décimo terceiro", hoje)
    )

    @Test fun atrasoRespeitaPrazoESituacao() {
        for (situacao in SituacaoDemanda.entries) {
            for (dias in -1L..1L) {
                val demanda = Demanda(titulo = "Teste", prazo = hoje.plusDays(dias), situacao = situacao)
                assertEquals(dias < 0 && situacao != SituacaoDemanda.CONCLUIDA, demanda.estaVencida(hoje))
            }
        }
    }

    @Test fun concluirEReabrirRecalculaAtraso() {
        val original = demandas[1]
        val concluida = original.copy(situacao = SituacaoDemanda.CONCLUIDA)
        assertFalse(concluida.estaVencida(hoje))
        assertTrue(concluida.copy(situacao = SituacaoDemanda.EM_ANDAMENTO).estaVencida(hoje))
        assertEquals(SituacaoDemanda.PENDENTE, original.situacao)
    }

    @Test fun passagemDoDiaAtualizaVencimento() {
        val demanda = demandas.last()
        assertFalse(demanda.estaVencida(hoje))
        assertTrue(demanda.estaVencida(hoje.plusDays(1)))
    }

    @Test fun resumoContaTodasESobrepoeVencidas() {
        assertEquals(ResumoDemandas(3, 1, 1, 2), resumirDemandas(demandas, hoje))
        assertEquals(ResumoDemandas(0, 0, 0, 0), resumirDemandas(emptyList(), hoje))
    }

    @Test fun buscaCombinaFiltroAcentosEMaiusculas() {
        assertEquals(listOf(2L), organizarDemandas(demandas, FiltroDemandas.VENCIDAS, "  FERIAS joao  ", hoje).map { it.id })
        assertTrue(organizarDemandas(demandas, FiltroDemandas.CONCLUIDAS, "férias", hoje).isEmpty())
    }

    @Test fun cadaFiltroMantemOsRegistrosCorretos() {
        val esperados = mapOf(
            FiltroDemandas.TODAS to listOf(1L, 2L, 3L, 5L, 4L),
            FiltroDemandas.PENDENTES to listOf(2L, 5L, 4L),
            FiltroDemandas.EM_ANDAMENTO to listOf(3L),
            FiltroDemandas.CONCLUIDAS to listOf(1L),
            FiltroDemandas.VENCIDAS to listOf(2L, 3L)
        )
        esperados.forEach { (filtro, ids) ->
            assertEquals(ids, organizarDemandas(demandas.reversed(), filtro, "", hoje).map { it.id })
        }
    }

    @Test fun editarPrazoReordenaERetiraDoFiltroVencidas() {
        val editadas = demandas.map { if (it.id == 2L) it.copy(prazo = hoje.plusDays(2)) else it }
        assertEquals(listOf(3L), organizarDemandas(editadas, FiltroDemandas.VENCIDAS, "", hoje).map { it.id })
        assertEquals(2L, organizarDemandas(editadas, FiltroDemandas.TODAS, "", hoje).last().id)
        assertEquals(1, resumirDemandas(editadas, hoje).vencidas)
    }

    @Test fun buscaSemCorrespondenciaNaoAlteraDados() {
        assertTrue(organizarDemandas(demandas, FiltroDemandas.TODAS, "inexistente", hoje).isEmpty())
        assertEquals(5, organizarDemandas(demandas, FiltroDemandas.TODAS, "   ", hoje).size)
        assertEquals(ResumoDemandas(3, 1, 1, 2), resumirDemandas(demandas, hoje))
    }
}
