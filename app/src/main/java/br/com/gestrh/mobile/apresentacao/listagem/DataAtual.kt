package br.com.gestrh.mobile.apresentacao.listagem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun lembrarDataAtual(): LocalDate {
    var hoje by remember { mutableStateOf(LocalDate.now()) }
    val cicloDeVida = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(cicloDeVida) {
        cicloDeVida.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (isActive) {
                val agora = ZonedDateTime.now()
                hoje = agora.toLocalDate()
                val proximoDia = hoje.plusDays(1).atStartOfDay(agora.zone)
                val espera = Duration.between(agora, proximoDia).toMillis().coerceIn(1L, 60_000L)
                delay(espera)
            }
        }
    }
    return hoje
}
