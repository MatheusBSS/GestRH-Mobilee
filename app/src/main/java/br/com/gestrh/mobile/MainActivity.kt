package br.com.gestrh.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.gestrh.mobile.ui.TelaInicial
import br.com.gestrh.mobile.ui.tema.TemaGestRH

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemaGestRH {
                TelaInicial()
            }
        }
    }
}
