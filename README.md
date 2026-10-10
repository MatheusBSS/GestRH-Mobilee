# GestRH Mobile

O GestRH Mobile está sendo desenvolvido para ajudar uma profissional de RH a acompanhar as tarefas que hoje organiza em planilhas. A ideia é reunir no celular o que precisa ser feito, o prazo de cada atividade e o que já foi concluído.

Uma demanda pode ser, por exemplo, conferir as férias de um funcionário ou acompanhar o envio da folha do mês. O aplicativo vai ajudar a organizar esse trabalho; os cálculos da folha continuam sendo feitos fora dele.

Este é um projeto da disciplina **Programação Mobile Aplicada**, do curso de Análise e Desenvolvimento de Sistemas da **UniEVANGÉLICA**, no semestre 2026/2.

## O que temos por enquanto

A tela inicial tem o botão **Nova demanda**, que abre o formulário com título, observação, prazo e situação. O título e o prazo são obrigatórios; a data é escolhida no calendário. Prazos anteriores ao dia atual também são permitidos para registrar atividades já atrasadas.

O botão **Salvar demanda** grava os dados no Room. Durante a gravação, os campos e botões ficam bloqueados. Após a confirmação, o formulário mostra **Demanda salva no celular** e permite voltar ao início. Se houver falha, os campos são mantidos para uma nova tentativa. Cancelar retorna sem salvar.

A tela inicial lista as demandas por prazo, mostrando título, data, situação e observação. A lista acompanha as alterações do banco: ao salvar e voltar ao início, o registro deve aparecer automaticamente. Há mensagens distintas para carregamento, lista vazia e falha de consulta, com opção de tentar novamente.

A compilação e os testes desta alteração ainda precisam ser confirmados no Android Studio.

## Como abrir o projeto

Vamos usar o **Android Studio**, com **Kotlin** para o código e **Jetpack Compose** para as telas.

1. No Android Studio, escolha **Get from VCS** ou **Clone Repository**.
2. Cole o endereço: `https://github.com/MatheusBSS/GestRH-Mobilee.git`.
3. Para acessar esta versão antes de ela entrar na `main`, selecione a branch `sprint-01` no menu do Git.
4. Abra a pasta que contém `settings.gradle.kts` e aguarde a sincronização.
5. No **SDK Manager**, instale o Android SDK Platform 35 e o Build-Tools 35.0.0. Nas configurações do Gradle, selecione um **JDK 17**.
6. Escolha um emulador ou celular Android e clique em **Run**.

Na primeira abertura, o Android Studio precisa de internet para baixar as dependências. Para testar em um celular, é necessário habilitar a depuração USB e autorizar a conexão com o computador.

A base usa Android Gradle Plugin 8.9.2, Gradle 8.11.1, Kotlin 2.1.20 e Compose BOM 2025.04.01. Use Android Studio compatível com AGP 8.9, como Meerkat 2024.3.1. O Android mínimo está configurado como 8.0 (API 26); ainda vamos confirmar a versão do aparelho usado na validação.

## Onde encontrar cada parte

Dentro de `app/src/main/java/br/com/gestrh/mobile/`:

| Arquivo | Para que serve |
| --- | --- |
| `AtividadePrincipal.kt` | Abre o aplicativo e carrega a tela inicial. |
| `apresentacao/telas/TelaInicial.kt` | Define o que aparece na primeira tela. |
| `apresentacao/tema/TemaAplicativo.kt` | Reúne as cores e a tipografia usadas nas telas. |

Os textos ficam em `app/src/main/res/values/strings.xml`. As configurações do aplicativo ficam no `AndroidManifest.xml`, e as bibliotecas usadas pelo projeto são declaradas em `app/build.gradle.kts`.

Os arquivos `gradlew`, `gradlew.bat` e a pasta `gradle/wrapper` permitem que os dois integrantes usem a mesma versão do Gradle. Eles fazem parte do projeto e devem permanecer no Git.

## Cadastro de demandas

| Arquivo em `apresentacao/` | Para que serve |
| --- | --- |
| `AplicativoGestRH.kt` | Alterna entre início e cadastro e conecta o formulário à lógica. |
| `telas/TelaCadastroDemanda.kt` | Apresenta os campos, calendário e validações. |
| `cadastro/CadastroDemandaViewModel.kt` | Salva no banco e acompanha sucesso, falha e gravação em andamento. |
| `listagem/ListaDemandasViewModel.kt` | Observa as demandas e controla carregamento e falha da consulta. |

Os campos preenchidos são preservados ao girar a tela. A gravação usa o ciclo de vida do ViewModel para continuar durante essa mudança. Toques repetidos são bloqueados durante o salvamento e após a confirmação; um novo cadastro libera o próximo registro.

## Armazenamento local

Os arquivos ficam em `app/src/main/java/br/com/gestrh/mobile/dados/`:

| Arquivo | Para que serve |
| --- | --- |
| `modelos/Demanda.kt` | Define as informações de uma demanda. |
| `modelos/SituacaoDemanda.kt` | Define as três situações possíveis. |
| `banco/BancoDeDados.kt` | Abre o banco `gestrh.db` no armazenamento interno. |
| `banco/DemandaDao.kt` | Contém as operações de gravação e consulta. |
| `banco/ConversoresBanco.kt` | Converte datas e situações para armazenamento. |
| `RepositorioDemandas.kt` | Valida o título e oferece as operações para as próximas telas. |

O projeto usa Room 2.7.2 e KSP 2.1.20-1.0.32. Os prazos são datas sem horário. Para acessar os dados, crie um `RepositorioDemandas(BancoDeDados.obter(contexto).demandas())`. O cadastro deve ser chamado em uma corrotina; a consulta da lista retorna um `Flow` que acompanha as alterações.

Há testes em `app/src/androidTest/` para gravação em arquivo, reabertura do banco, situações, ordem dos prazos e rejeição de título vazio. Eles usam um banco exclusivo de teste, removido ao terminar. Para executá-los com um celular ou emulador conectado:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

Depois da primeira compilação, o Room exporta a estrutura do banco em `app/schemas/`. Versione o JSON gerado para apoiar futuras migrações. Não há migração destrutiva configurada.

## Responsabilidade pelo desenvolvimento

Matheus ficará responsável pela implementação das telas, regras, armazenamento e integração, organizando cada etapa em um commit separado.

## O que falta conferir nesta base

A compilação foi tentada no ambiente de preparação, mas parou no download do Gradle por falta de acesso à rede. Por isso, ainda precisamos confirmar no Android Studio se o projeto sincroniza, compila e abre corretamente no celular ou emulador.

Ao executar, confira se o título e a mensagem inicial aparecem sem cortes, inclusive com a fonte do celular ampliada. Depois, guarde uma captura da tela e o link do commit para documentar o resultado no relatório.

Para gerar um APK de teste pelo terminal do Android Studio, use:

```powershell
.\gradlew.bat :app:assembleDebug
```

No Linux ou macOS, use `./gradlew :app:assembleDebug`. O APK será gerado em `app/build/outputs/apk/debug/app-debug.apk`.

## Cuidados com os arquivos

O repositório é público, então a professora pode consultá-lo pelo link, sem convite. Nos testes e nas capturas, vamos usar dados fictícios.

O `.gitignore` já exclui arquivos gerados e configurações do computador, como `local.properties` e as pastas de build. Não envie senhas, chaves de assinatura ou dados reais de funcionários.

## Conferir o cadastro

1. Toque em Nova demanda e tente salvar sem preencher: o formulário deve indicar título e prazo obrigatórios.
2. Preencha uma atividade fictícia, escolha prazo e situação e salve. Aguarde a confirmação.
3. Volte ao início e abra outro cadastro: os campos devem estar vazios.
4. Preencha e cancele: nada deve ser gravado.
5. Gire a tela durante o preenchimento e confira os campos.
6. Confira na tela inicial o título, prazo, situação e observação salvos; cadastre uma segunda demanda com prazo anterior e confira a ordem.
7. Feche e reabra o aplicativo e confira se os registros continuam na lista.

Os testes de interface verificam campos obrigatórios, cancelamento e o fluxo integrado de cadastro e consulta usando Room. Os testes de banco existentes verificam persistência e consultas. Eles ainda não foram executados no ambiente de preparação; não registre aprovação antes de executá-los no dispositivo ou emulador.

## Acompanhar a Sprint 01

Consulte [o registro da sprint](documentacao/Sprint-01.md) e [o roteiro de testes](documentacao/Testes-Sprint-01.md). O código está preparado; os testes Android permanecem pendentes até execução no dispositivo ou emulador.
