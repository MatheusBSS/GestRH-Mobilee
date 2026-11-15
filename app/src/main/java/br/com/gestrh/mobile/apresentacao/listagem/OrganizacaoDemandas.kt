package br.com.gestrh.mobile.apresentacao.listagem

import br.com.gestrh.mobile.dados.modelos.Demanda
import br.com.gestrh.mobile.dados.modelos.SituacaoDemanda
import java.text.Normalizer
import java.time.LocalDate
import java.util.Locale

enum class FiltroDemandas {
    TODAS, PENDENTES, EM_ANDAMENTO, CONCLUIDAS, VENCIDAS
}

data class ResumoDemandas(
    val pendentes: Int,
    val emAndamento: Int,
    val concluidas: Int,
    val vencidas: Int
)

fun Demanda.estaVencida(hoje: LocalDate): Boolean =
    prazo.isBefore(hoje) && situacao != SituacaoDemanda.CONCLUIDA

fun resumirDemandas(demandas: List<Demanda>, hoje: LocalDate) = ResumoDemandas(
    pendentes = demandas.count { it.situacao == SituacaoDemanda.PENDENTE },
    emAndamento = demandas.count { it.situacao == SituacaoDemanda.EM_ANDAMENTO },
    concluidas = demandas.count { it.situacao == SituacaoDemanda.CONCLUIDA },
    vencidas = demandas.count { it.estaVencida(hoje) }
)

fun organizarDemandas(
    demandas: List<Demanda>,
    filtro: FiltroDemandas,
    busca: String,
    hoje: LocalDate
): List<Demanda> {
    val termo = normalizarBusca(busca.trim())
    return demandas.asSequence().filter { demanda ->
        when (filtro) {
            FiltroDemandas.TODAS -> true
            FiltroDemandas.PENDENTES -> demanda.situacao == SituacaoDemanda.PENDENTE
            FiltroDemandas.EM_ANDAMENTO -> demanda.situacao == SituacaoDemanda.EM_ANDAMENTO
            FiltroDemandas.CONCLUIDAS -> demanda.situacao == SituacaoDemanda.CONCLUIDA
            FiltroDemandas.VENCIDAS -> demanda.estaVencida(hoje)
        }
    }.filter { termo.isEmpty() || normalizarBusca(it.titulo).contains(termo) }
        .sortedWith(compareBy<Demanda> { it.prazo }.thenBy { it.id })
        .toList()
}

private fun normalizarBusca(texto: String): String =
    Normalizer.normalize(texto, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .lowercase(Locale.ROOT)
