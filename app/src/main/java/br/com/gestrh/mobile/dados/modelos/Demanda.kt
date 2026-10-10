package br.com.gestrh.mobile.dados.modelos

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "demandas")
data class Demanda(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val prazo: LocalDate,
    val observacao: String = "",
    val situacao: SituacaoDemanda = SituacaoDemanda.PENDENTE
)
