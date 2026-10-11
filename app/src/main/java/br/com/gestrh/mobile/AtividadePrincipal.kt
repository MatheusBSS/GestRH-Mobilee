package br.com.gestrh.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.gestrh.mobile.apresentacao.AplicativoGestRH
import br.com.gestrh.mobile.apresentacao.tema.TemaGestRH

class AtividadePrincipal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemaGestRH {
                AplicativoGestRH()
            }
        }
    }
}
