# Sprint 02

Período: 12/10/2026 a 25/10/2026.

## Edição de demandas

A primeira etapa permite tocar em uma demanda na tela inicial e abrir seus dados no formulário. É possível alterar título, prazo e observação. Salvar alterações atualiza o mesmo registro; Cancelar ou voltar descarta o preenchimento. A situação pode ser alterada entre Pendente, Em andamento e Concluída no mesmo formulário. As alterações só são gravadas ao tocar em Salvar alterações. Cancelar mantém também a situação anterior.

A edição usa a estrutura existente do banco, sem mudança de versão nem exclusão de registros. Falhas de consulta permitem tentar novamente ou voltar. Falhas ao salvar mantêm o formulário preenchido.

## Atualização de situação

O segundo commit habilita a seleção de situação na edição. Título, prazo, observação e situação são atualizados em uma única operação, mantendo o ID. É possível retomar uma demanda concluída, selecionando Pendente ou Em andamento. A estrutura do banco permanece na versão existente.

Foram acrescentados testes para todas as situações após reabrir o banco, preservação de outros registros, rejeição de alterações com título vazio e cancelamento da troca de situação na interface. Esses testes Android ainda não foram executados neste ambiente.

## Cadastro e consulta de funcionários

O terceiro commit acrescenta o acesso Funcionários na tela inicial. A listagem mostra nome e observação quando preenchida, com estados de carregamento, lista vazia e erro com nova tentativa. Novo funcionário abre o formulário com nome obrigatório e observação opcional. Cancelar não grava; salvar apresenta confirmação e permite retornar à lista. O vínculo com demandas foi acrescentado na etapa seguinte.

Nesse terceiro commit, o banco passou da versão 1 para a versão 2. A migração `MIGRACAO_1_2` cria somente a tabela `funcionarios`, preservando a tabela e os IDs das demandas. Não há recriação destrutiva. Na próxima compilação, conferir e versionar os esquemas gerados pelo Room em `app/schemas`.

Foram escritos testes de cadastro, nome obrigatório, consulta, cancelamento, recuperação após reabertura e migração de um banco com estrutura da versão 1. Os testes de migração abrem a versão atual com Room para validar o esquema, consultam a demanda antiga e verificam novos cadastros. A execução Android continua pendente. A instrução SQL da migração foi exercitada em SQLite isolado, confirmando a preservação das demandas e dos IDs, o cadastro de funcionários e a recuperação após reabertura; essa conferência não substitui a validação do Room no Android.

## Vínculo opcional com funcionário

O quarto commit acrescenta Funcionário (opcional) ao cadastro e à edição das demandas. A seleção permite escolher uma pessoa cadastrada ou Sem funcionário. Remover vínculo altera somente o formulário até Salvar alterações; cancelar mantém o vínculo anterior. A tela inicial mostra o nome do funcionário vinculado. Demandas gerais continuam sem vínculo.

O banco agora usa a versão 3. A migração `MIGRACAO_2_3` acrescenta `funcionarioId` opcional e seu índice. A chave estrangeira rejeita funcionários inexistentes. Atualizações vindas da versão 1 percorrem as duas migrações. Nenhuma demanda ou funcionário é apagado; as demandas antigas começam sem vínculo.

Foram escritos testes Android de cadastro com vínculo, troca, remoção, cancelamento, recuperação após reabertura, rejeição de funcionário inexistente e migração das versões 1 e 2. A conferência em SQLite isolado passou para as duas rotas de migração, preservação dos dados, troca/remoção e integridade do vínculo. A compilação foi tentada novamente em 24/10/2026 e parou no download do Gradle por falta de acesso à rede. Os testes Android continuam sem execução neste ambiente.

## Verificação

Foram acrescentados testes Android para edição sem duplicação, preservação da situação e de outros registros, recuperação após reabrir o banco, rejeição de título vazio e ID inexistente, preenchimento do formulário e cancelamento. Os testes anteriores de cadastro permanecem no projeto.

Em 24/10/2026, a tentativa de compilar aplicativo e testes neste ambiente parou no download do Gradle com `Network is unreachable`. Não houve execução dos testes Android nem geração de APK. A conferência estática de XML, referências de texto e espaços passou. A consulta de atualização também foi exercitada em SQLite isolado; isso não substitui a compilação com Room.

No terminal PowerShell do Android Studio, com um emulador ou aparelho conectado:

```powershell
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest
.\gradlew.bat :app:connectedDebugAndroidTest
```

Conferir manualmente:

1. Abrir uma demanda e verificar os valores preenchidos.
2. Alterar título, prazo e observação; salvar e conferir na lista, sem duplicação.
3. Fechar e abrir o aplicativo; conferir os dados alterados.
4. Alterar os campos e cancelar; abrir novamente e conferir os valores anteriores.
5. Tentar salvar título vazio; verificar a mensagem e a ausência de alterações no banco.
6. Girar o aparelho durante o preenchimento; conferir que os campos são preservados.
7. Abrir um novo cadastro após uma edição; conferir que começa vazio.
8. Alterar uma demanda de Pendente para Em andamento e depois Concluída; salvar e conferir a lista a cada mudança.
9. Reabrir o aplicativo e conferir a situação; retornar uma demanda concluída para Pendente.
10. Selecionar outra situação e cancelar; conferir que a anterior foi mantida.
11. Abrir Funcionários, tentar salvar sem nome, cadastrar com e sem observação e conferir a lista.
12. Cancelar um cadastro preenchido e confirmar que não foi salvo; abrir novo cadastro e conferir os campos vazios.
13. Fechar e reabrir o aplicativo para conferir funcionários e demandas.
14. Instalar a atualização sobre a versão anterior, com a mesma assinatura e sem desinstalar: conferir a preservação das demandas e o cadastro de funcionários.
15. Conferir teclado, rolagem e rotação no formulário de funcionário.
16. Cadastrar demanda sem vínculo e outra com funcionário; conferir a identificação na lista.
17. Editar a demanda para trocar de funcionário, salvar e conferir após reabertura.
18. Remover o vínculo e cancelar; verificar que o anterior permanece. Repetir a remoção salvando e conferir que foi removido.
19. Atualizar instalações das versões de banco 1 e 2 sem desinstalar; conferir demandas e funcionários existentes e cadastrar novos vínculos.
20. Girar a tela após escolher funcionário; conferir a seleção antes de salvar.

Registrar dispositivo, Android, data, commit, resultados e capturas após a execução.

## Próximas etapas

- Testes integrados e publicação do APK de testes no GitHub Releases.
