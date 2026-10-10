package br.com.gestrh.mobile.dados.banco

import androidx.room.TypeConverter
import br.com.gestrh.mobile.dados.modelos.SituacaoDemanda
import java.time.LocalDate

class ConversoresBanco {
    @TypeConverter
    fun dataParaNumero(data: LocalDate): Long = data.toEpochDay()

    @TypeConverter
    fun numeroParaData(dias: Long): LocalDate = LocalDate.ofEpochDay(dias)

    @TypeConverter
    fun situacaoParaTexto(situacao: SituacaoDemanda): String = situacao.name

    @TypeConverter
    fun textoParaSituacao(texto: String): SituacaoDemanda = SituacaoDemanda.valueOf(texto)
}
