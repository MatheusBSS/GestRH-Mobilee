# Sprint 02

Período: 12/10/2026 a 25/10/2026.

## Edição de demandas

A primeira etapa permite tocar em uma demanda na tela inicial e abrir seus dados no formulário. É possível alterar título, prazo e observação. Salvar alterações atualiza o mesmo registro; Cancelar ou voltar descarta o preenchimento. A situação atual é preservada nesta etapa.

A edição usa a estrutura existente do banco, sem mudança de versão nem exclusão de registros. Falhas de consulta permitem tentar novamente ou voltar. Falhas ao salvar mantêm o formulário preenchido.

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

Registrar dispositivo, Android, data, commit, resultados e capturas após a execução.

## Próximas etapas

- Alteração da situação das demandas.
- Cadastro e consulta de funcionários.
- Vínculo opcional entre demanda e funcionário, com migração que preserve os dados.
- Testes integrados e publicação do APK de testes no GitHub Releases.
