package br.com.gestrh.mobile.dados.modelos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "funcionarios")
data class Funcionario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val observacao: String = ""
)
