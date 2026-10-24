package br.com.gestrh.mobile.dados.modelos

import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "demandas",
    foreignKeys = [ForeignKey(
        entity = Funcionario::class,
        parentColumns = ["id"], childColumns = ["funcionarioId"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index("funcionarioId")]
)
data class Demanda(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val prazo: LocalDate,
    val observacao: String = "",
    val situacao: SituacaoDemanda = SituacaoDemanda.PENDENTE,
    val funcionarioId: Long? = null
)
