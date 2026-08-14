# Histórico consolidado do ScoutPlay

Este diretório registra decisões e artefatos úteis das branches antigas sem transformar código obsoleto em parte da entrega `ScoutPlay2.0`. A branch `historico` foi criada a partir da entrega e altera apenas documentação.

## Critério de consolidação

Foi preservado o que ajuda a explicar a visão do produto, a evolução técnica, o escopo acadêmico ou decisões futuras. Não foram copiados protótipos substituídos, arquivos de upload nem implementações experimentais que ainda exigem revisão.

## Inventário das branches

| Branch | Relação com `ScoutPlay2.0` | Conteúdo relevante | Decisão |
| --- | --- | --- | --- |
| `ScoutPlay2.0` | referência da entrega | aplicação React/Spring, documentação da Sprint 4 e testes | mantida intacta como fonte principal |
| `main` | linha da primeira versão | visão funcional, pitch, relatório final e diários de sprint | resumir e apontar para os originais |
| `frontpv` | ancestral totalmente absorvida | protótipo do front-end | não duplicar |
| `Maria-Clara-Br` | experimento antigo divergente | ajustes estáticos de interface e uma imagem enviada | não incorporar |
| `video` | experimento antigo divergente | ajustes estáticos relacionados a mídia | não incorporar |
| `feat/db-remodel` | duas alterações posteriores à entrega | experimento no contexto/serviço/controlador de IA | manter separada até revisão técnica |

## Linha do tempo

| Período | Marco |
| --- | --- |
| 2024 | primeira geração com páginas estáticas e modelos separados de atleta, olheiro e responsável |
| 2026 | remodelagem para `Usuario`, tipos de conta, detalhes de perfil em JSONB e front-end React/TanStack |
| Sprint 4 | entrega `ScoutPlay2.0`, com feed, perfis, busca, shortlist, avaliações, JWT e copiloto de IA |
| Pós-entrega | experimento `feat/db-remodel`, ainda não aceito na linha entregue |

## Artefatos originais importantes

Os links abaixo permanecem no histórico de `main`; eles não foram duplicados para evitar versões concorrentes:

- [Planejamento funcional original](https://github.com/vhdtv/ScoutPlay/blob/main/docs/produto/planejamento-funcional-scoutplay.md)
- [Relatório final acadêmico](https://github.com/vhdtv/ScoutPlay/blob/main/docs/Relat%C3%B3rio%20Final%20UC%20HUB.pdf)
- [Link do pitch registrado na primeira versão](https://github.com/vhdtv/ScoutPlay/blob/main/docs/LinkDoPitch.txt)
- [Status da primeira versão](https://github.com/vhdtv/ScoutPlay/blob/main/README_STATUS.md)
- [Semana 1](https://github.com/vhdtv/ScoutPlay/blob/main/SEMANA1.md) e [melhorias da Semana 1](https://github.com/vhdtv/ScoutPlay/blob/main/SEMANA1_MELHORIAS.md)
- [Semana 2 — JWT](https://github.com/vhdtv/ScoutPlay/blob/main/SEMANA2_JWT.md)
- [Roadmap da Semana 3](https://github.com/vhdtv/ScoutPlay/blob/main/SEMANA3_ROADMAP.md)

O pitch externo referenciado no arquivo histórico está em [Google Drive](https://drive.google.com/file/d/1aqnwmd-CNlyTCMiKtoYXOsj-z5DU1M7z/view).

## O que foi absorvido nesta consolidação

- a visão segura de descoberta de atletas por olheiros;
- os papéis originalmente imaginados para atleta, responsável, olheiro e administração;
- as regras de proteção a menores, privacidade, moderação e rastreabilidade;
- a separação entre escopo planejado e funcionalidade realmente entregue;
- um roadmap único para orientar Issues e GitHub Projects.

Consulte [PLANEJAMENTO_FUNCIONAL.md](PLANEJAMENTO_FUNCIONAL.md) e [ROADMAP.md](ROADMAP.md).

## O que foi deliberadamente deixado nas branches antigas

- código V1 substituído pela arquitetura da `ScoutPlay2.0`;
- páginas HTML/CSS/JavaScript estáticas já superadas pelo front-end React;
- uploads e fotos de usuários, que não são documentação do produto;
- alterações pós-entrega de IA da `feat/db-remodel`, porque ainda precisam de revisão de segurança, privacidade e compatibilidade;
- documentos semanais repetidos, preservados por links em vez de cópias.

Nenhuma branch original foi excluída e nenhum commit histórico foi reescrito.
