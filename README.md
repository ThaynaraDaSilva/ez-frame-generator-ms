
# Contextualização - ez-frame-generator-ms

# Desenho de Arquitetura

# Pré requisitos

## Limites definidos com relação a upload de videos

# Estrutura de diretórios do projeto

# Modelagem do banco de dados

# Como compilar o projeto

# Componentes da solução global ez-frame

| **Componente**               | **Finalidade**                                                                 | **Justificativa**                                                                                                                                                                                                                                                                                      |
|------------------------------|--------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Clean Architecture**       | Organização interna da solução                                                 | Foi escolhida para garantir uma estrutura modular, de fácil manutenção e testes. Essa separação clara entre regras de negócio e infraestrutura facilita a escalabilidade da solução ao longo do tempo, conforme o sistema evolui.                                                                     |
| **Java 21**                  | Linguagem principal para implementação                                          | A linguagem Java foi adotada em substituição ao .NET por uma decisão estratégica, considerando a expertise da equipe com o ecossistema Java. Essa escolha visa otimizar o desenvolvimento, reduzir a curva de aprendizado e garantir eficiência na evolução e manutenção da solução.                   |
| **DynamoDB**                 | Armazenamento dos metadados e arquivos gerados (como ZIPs de frames)           | Optamos pelo DynamoDB por ser altamente escalável e disponível, atendendo bem à necessidade de processar múltiplos vídeos em paralelo. Seu modelo NoSQL permite evoluir a estrutura dos dados sem migrações complexas, o que é útil caso futuramente a solução precise armazenar também os vídeos.     |
| **Apache Maven**             | Gerenciamento de dependências e build                                          | Ferramenta amplamente utilizada no ecossistema Java, facilita a organização do projeto, o versionamento de dependências e o processo de build e deploy.                                                                                                                                                |
| **Amazon Cognito**           | Autenticação e segurança no microsserviço de usuários                          | Solução gerenciada que facilita a implementação de autenticação com usuário e senha, atendendo ao requisito de proteger o sistema e controlando o acesso de forma segura e padronizada.                                                                                                               |
| **Amazon SQS**               | Gerenciamento da fila de processamento de vídeos                               | Utilizamos SQS para garantir que os vídeos sejam processados de forma assíncrona e segura, sem perda de requisições, mesmo em momentos de pico. Isso também ajuda a escalar o sistema com segurança.                                                                                                   |
| **Amazon EKS**               | Orquestração dos microsserviços da solução                                     | Solução gerenciada baseada em Kubernetes, que facilita o deploy, a escalabilidade e o gerenciamento dos microsserviços (`generator`, `user`, `notification`), mantendo a consistência da infraestrutura.                                                                                                |
| **AWS Fargate (via EKS)**    | Execução do `ez-frame-generator-ms`                                           | O Fargate permite rodar o processamento dos vídeos de forma escalável e sem a necessidade de gerenciar servidores, otimizando recursos e custos, além de se adaptar bem ao processamento paralelo exigido pela solução.                                                                                |
| **Amazon SES**               | Envio de e-mails de notificação em caso de erro                                | Atende ao requisito de notificação automática para o usuário em caso de falha no processamento. É um serviço simples, eficiente e com baixo custo, ideal para esse tipo de comunicação.                                                                                                                 |
| **GitHub Actions** | Automatização de build, testes e deploys | O GitHub Actions foi escolhido por estar amplamente consolidado no mercado e por oferecer uma integração direta com repositórios GitHub, simplificando pipelines de entrega contínua. Além disso, a equipe já possui familiaridade com a ferramenta, o que reduz tempo de configuração e acelera o processo de entrega contínua. Essa escolha também reflete uma tendência atual de muitas empresas que estão migrando de soluções como Jenkins e Azure DevOps para plataformas mais leves e integradas, como o próprio GitHub Actions. |

# Demais projetos relacionados a esta solução

# Desenvolvido por:
@tchfer : RM357414<br>
@ThaynaraDaSilva : RM357418<br>