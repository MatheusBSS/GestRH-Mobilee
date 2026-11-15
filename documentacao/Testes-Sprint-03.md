# Conferência da Sprint 03

Esta sprint acrescenta filtros, busca e resumo à tela inicial e identifica demandas vencidas. A ordenação existente por prazo foi mantida e integrada às novas consultas. O banco Room e os registros das sprints anteriores permanecem com a mesma estrutura.

## Roteiro manual

Use dados fictícios e anote a versão do APK, data do teste, aparelho, Android, resultado observado e evidência de cada cenário. A lista abaixo é um roteiro; não representa testes já executados.

| Verificação | Resultado esperado |
| --- | --- |
| Cadastrar tarefas com prazo ontem, hoje e amanhã, em cada situação | Apenas as de ontem, pendentes ou em andamento, mostram Vencida. |
| Concluir uma tarefa vencida e depois reabri-la | O aviso desaparece ao concluir e reaparece ao reabrir. |
| Deixar a tela aberta até a mudança do dia e retornar do segundo plano | Avisos e contadores acompanham a data local do aparelho. |
| Usar cada filtro | Todas, Pendentes, Em andamento, Concluídas e Vencidas apresentam os registros correspondentes. |
| Buscar FERIAS JOAO quando o título é Férias João | A busca encontra o título, ignorando acentos e maiúsculas. |
| Combinar busca e filtro, depois limpar | A lista respeita ambos; limpar busca e filtros restaura Todas. |
| Buscar um título inexistente | Aparece Nenhuma demanda encontrada, com opção de limpar. |
| Cadastrar vários prazos, inclusive iguais | A lista ordena por prazo crescente e desempata pelo ID. |
| Editar título, prazo ou situação | Resultados, ordem, avisos e contadores se atualizam. |
| Observar o resumo com um filtro ativo | Os totais continuam abrangendo todas as demandas. Vencidas também pertencem a Pendentes ou Em andamento. |
| Abrir edição e voltar; girar a tela | Busca e filtro continuam selecionados. |
| Cadastrar, vincular, trocar e remover funcionário | Os fluxos anteriores continuam funcionando. |
| Fechar e reabrir, sem internet | Os dados permanecem disponíveis. |
| Aumentar a fonte e usar uma tela pequena | Filtros quebram em linhas e o conteúdo permite rolagem, sem esconder ações. |
| Instalar sobre a versão anterior | Conferir compatibilidade da assinatura e preservação dos dados; não desinstalar uma versão que contenha dados importantes. |

A avaliação com Samara deve registrar suas próprias observações após uso da versão. Compilação e testes de regras não substituem essa avaliação.

## Verificação técnica

O GitHub Actions executa `:app:testDebugUnitTest`, compila aplicativo e testes Android e executa Android Lint. Os testes unitários cobrem limites de data, situações, busca combinada, ordenação e contadores. Os testes Android são compilados neste fluxo, mas precisam de dispositivo ou emulador para execução.

Para executar no Android Studio, use JDK 17 e SDK 35. No terminal:

```sh
bash gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest :app:lintDebug
bash gradlew :app:connectedDebugAndroidTest
```

O segundo comando exige dispositivo ou emulador conectado. Registre os resultados reais antes de concluir os cards de validação.
