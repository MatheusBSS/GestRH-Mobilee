# GestRH Mobile

O GestRH Mobile está sendo desenvolvido para ajudar uma profissional de RH a acompanhar as tarefas que hoje organiza em planilhas. A ideia é reunir no celular o que precisa ser feito, o prazo de cada atividade e o que já foi concluído.

Uma demanda pode ser, por exemplo, conferir as férias de um funcionário ou acompanhar o envio da folha do mês. O aplicativo vai ajudar a organizar esse trabalho; os cálculos da folha continuam sendo feitos fora dele.

Este é um projeto da disciplina **Programação Mobile Aplicada**, do curso de Análise e Desenvolvimento de Sistemas da **UniEVANGÉLICA**, no semestre 2026/2.

## O que temos por enquanto

Esta primeira versão traz a base do projeto e uma tela inicial com a mensagem “Nenhuma demanda cadastrada”. Ela ainda não cadastra nem salva informações.

O próximo passo é criar o cadastro e a lista de demandas, usando o Room para guardar os registros no próprio celular. Depois, vamos acrescentar a edição, o acompanhamento dos prazos e o vínculo com funcionários.

## Como abrir o projeto

Vamos usar o **Android Studio**, com **Kotlin** para o código e **Jetpack Compose** para as telas.

1. No Android Studio, escolha **Get from VCS** ou **Clone Repository**.
2. Cole o endereço: `https://github.com/MatheusBSS/GestRH-Mobilee.git`.
3. Para acessar esta versão antes de ela entrar na `main`, selecione a branch `estrutura-inicial` no menu do Git.
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

## Como vamos dividir o trabalho

**Matheus Martins Sena** ficará com as regras do aplicativo e o armazenamento local. **João Victor Duarte Santos** ficará com as telas. A integração e os testes serão feitos em conjunto.

Antes de desenvolver cada tela, vamos combinar os campos e as funções de que ela precisa. Assim, a interface e a parte de dados podem avançar sem depender de mudanças constantes uma na outra.

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
