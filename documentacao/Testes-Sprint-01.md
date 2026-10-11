# Testes da Sprint 01

Registro de 10/10/2026. Responsável pela execução no Android: Matheus Martins Sena.

## O que foi verificado aqui

- XML de textos e manifesto: leitura válida.
- Referências `R.string` do código principal: todos os textos existem, sem nomes duplicados.
- `git diff --check`: sem erros de espaço.
- Revisão do caminho formulário → ViewModel → repositório → Room → lista.

Essas verificações não comprovam compilação nem funcionamento no Android.

## Bloqueio de execução

A tentativa `./gradlew :app:assembleDebug :app:assembleDebugAndroidTest --console=plain` parou no download do Gradle 8.11.1 com `java.net.SocketException: Network is unreachable`. O ambiente de preparação também não tem Android SDK, adb ou emulador. Nenhum teste Android foi executado aqui e nenhum APK foi gerado.

## Executar no Android Studio

1. Atualize o Git e selecione `sprint-01`, ou a `main` depois do merge dessa branch.
2. Sincronize o Gradle com internet, JDK 17 e SDK 35 instalados.
3. Inicie um emulador API 26 ou superior, ou conecte um celular com depuração USB.
4. No terminal PowerShell do Android Studio, execute:

```powershell
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest
.\gradlew.bat :app:connectedDebugAndroidTest
```

O relatório dos testes fica em `app/build/reports/androidTests/connected/debug/index.html` quando a execução terminar. O APK de teste do aplicativo fica em `app/build/outputs/apk/debug/app-debug.apk`.

## Cobertura automatizada escrita

| Classe | O que verifica | Resultado atual |
| --- | --- | --- |
| PersistenciaDemandasTest | Reabertura do banco em arquivo; preservação dos campos e situações; ordem por prazo; título vazio | Não executado |
| CadastroDemandaTest | Campos obrigatórios e cancelar sem enviar | Não executado |
| FluxoDemandasTest | Formulário real com calendário, gravação Room, retorno à lista e atualização automática; cancelamento preenchido | Não executado |

Os testes usam bancos exclusivos de teste, sem apagar o banco normal do aplicativo. A reabertura do banco está coberta no teste de persistência; encerrar e abrir o processo inteiro deve ser conferido também pelo roteiro manual.

## Roteiro manual

Use apenas dados fictícios. Anote o aparelho, a versão do Android, o commit, a data e o resultado de cada caso.

| Caso | Procedimento | Resultado esperado | Resultado obtido |
| --- | --- | --- | --- |
| T01 | Abrir uma instalação sem registros | Mensagem de lista vazia; botão Nova demanda | Pendente |
| T02 | Tentar salvar título e prazo vazios | Mensagens nos campos; nenhum registro criado | Pendente |
| T03 | Cadastrar “Conferir férias de exemplo”, prazo 11/10/2026, observação “Verificar datas” e situação Em andamento | Confirmação; lista com os quatro campos corretos | Pendente |
| T04 | Fechar o aplicativo pela tela de recentes e abrir novamente; repetir com Forçar parada, sem limpar armazenamento | Mesmo registro permanece visível | Pendente |
| T05 | Abrir cadastro, preencher e cancelar | Lista e quantidade de registros permanecem iguais | Pendente |
| T06 | Criar outro registro com prazo anterior | Ordem crescente de prazo; nenhum item perdido | Pendente |
| T07 | Salvar e tocar repetidamente; depois iniciar novo cadastro | Um registro por envio; formulário novo vazio | Pendente |
| T08 | Girar durante preenchimento e salvamento | Campos preservados; ausência de duplicidade | Pendente |
| T09 | Desativar a internet depois da instalação e cadastrar/consultar | Fluxo funciona localmente | Pendente |
| T10 | Aumentar fonte e usar teclado/rolagem | Conteúdo legível e ações alcançáveis | Pendente |

## Evidências a guardar

- Tela vazia, formulário preenchido, confirmação e lista após reabertura.
- Resultado do comando de compilação e relatório de testes.
- Dispositivo e versão do Android usados, data e commit testado.

Somente depois da execução, substitua “Pendente” pelo resultado real e mova os cards aprovados para Concluído. Se houver falha, registre os passos e a mensagem antes de corrigir.
