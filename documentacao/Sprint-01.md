# Sprint 01

Período oficial: 28/09/2026 a 11/10/2026. Atualização em 10/10/2026.

A sprint estabelece o fluxo inicial de cadastro e consulta de demandas com armazenamento local. Matheus conduz a implementação. O aplicativo acompanha tarefas de RH, prazos, observações e situações; os cálculos de folha de pagamento ficam fora do escopo.

## Implementação

- Tela inicial com lista ordenada por prazo e atualização a partir do Room.
- Formulário com título e prazo obrigatórios, observação opcional e situação.
- Calendário para escolha da data, mensagens de validação, confirmação e tratamento de falha.
- Armazenamento interno, banco Room e testes de reabertura.
- Estados distintos para carregamento, lista vazia e erro de consulta, com nova tentativa.

O código foi preparado e versionado; compilação e testes em Android ainda precisam ser confirmados. Consulte [o roteiro de testes](Testes-Sprint-01.md).

## Rastreabilidade

| Requisito | Entrega no código | Limite nesta sprint |
| --- | --- | --- |
| RF01 / CA01 | Cadastro integrado ao Room | Execução Android pendente |
| RF02 / CA02 | Consulta na tela inicial | Execução Android pendente |
| RF04 / CA04 | Escolha da situação no cadastro e exibição na lista | Alteração de registro existente ficará para Sprint 02 |
| RF03 | Planejado | Edição ainda não implementada |
| RF05 | Planejado | Destaque de vencidas na Sprint 03 |
| RF06 / RF07 | Planejado | Funcionários e vínculo na Sprint 02 |

## Commits principais

- ac377c5: estrutura Android e instruções para abrir o projeto.
- 932cc34: entidades, banco Room, repositório e testes de persistência.
- 02445a8: formulário de cadastro.
- b30b5f1: gravação do formulário no Room.
- 29565f6: consulta de demandas na tela inicial.
- 37faf71: testes do fluxo integrado.

## Kanban

[Quadro do projeto](https://trello.com/b/MvJdwu40/gestrh-mobile-controle-de-demandas-de-rh).

As cinco colunas são Backlog do Projeto, Sprint Atual — A Fazer, Em andamento, Validação e Concluído. Os três cards implementados permanecem em Validação. O teste no Android permanece A Fazer; a revisão do relatório aguarda os resultados e a confirmação da data da primeira entrega formal.

- [Persistência](https://trello.com/c/rfYo5r4t).
- [Teste do fluxo](https://trello.com/c/OsB9S5mA).
- [Relatório e evidências](https://trello.com/c/tRLjQqGU).

## Próximos ciclos

| Sprint | Período | Prioridade |
| --- | --- | --- |
| 02 | 12/10/2026 a 25/10/2026 | Edição, situação, funcionários e vínculo opcional |
| 03 | 26/10/2026 a 08/11/2026 | Demandas vencidas, testes e validação com a parceira |
| 04 | 09/11/2026 a 22/11/2026 | Correções, regressão, versão instalável e entrega |
