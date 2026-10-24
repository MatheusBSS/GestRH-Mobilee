package br.com.gestrh.mobile.dados

import br.com.gestrh.mobile.dados.banco.FuncionarioDao
import br.com.gestrh.mobile.dados.modelos.Funcionario
import kotlinx.coroutines.flow.Flow

class RepositorioFuncionarios(private val consultas: FuncionarioDao) {
    fun observarFuncionarios(): Flow<List<Funcionario>> = consultas.observarTodos()

    suspend fun cadastrar(nome: String, observacao: String = ""): Long {
        val nomeAjustado = nome.trim()
        require(nomeAjustado.isNotEmpty()) { "Informe o nome do funcionário." }
        return consultas.inserir(Funcionario(nome = nomeAjustado, observacao = observacao.trim()))
    }
}
