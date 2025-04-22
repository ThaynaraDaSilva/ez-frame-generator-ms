# 🎥 ez-frame-generator-ms

## 📌 Contextualização

O microsserviço `ez-frame-generator-ms` é responsável pelo processamento assíncrono de vídeos da solução **ez-frame**. Ele consome mensagens da fila SQS (`video-processing-queue`), faz o download de vídeos do bucket S3 (`ez-frame-video-storage`), gera frames em memória, compacta-os em um arquivo ZIP, e salva o ZIP no S3. Após o processamento, atualiza o status do vídeo no `ez-video-ingestion-ms` via endpoint `http://host:8080/v1/ms/videos/update-status`.

---

## 🧩 Desenho de Arquitetura

![image](https://github.com/user-attachments/assets/da998aa9-deb2-48fc-9025-06d3e1dfb0d1)

---

## 🧱 Componentes da Solução Global ez-frame

| **Componente** | **Finalidade** | **Justificativa** |
| --- | --- | --- |
| **Clean Architecture** | Organização interna da solução | Foi escolhida para garantir uma estrutura modular, de fácil manutenção e testes. Essa separação clara entre regras de negócio e infraestrutura facilita a escalabilidade da solução ao longo do tempo, conforme o sistema evolui. |
| **Java 21** | Linguagem principal para implementação | A linguagem Java foi adotada em substituição ao .NET por uma decisão estratégica, considerando a expertise da equipe com o ecossistema Java. Essa escolha visa otimizar o desenvolvimento, reduzir a curva de aprendizado e garantir eficiência na evolução e manutenção da solução. |
| **Apache Maven** | Gerenciamento de dependências e build | Ferramenta amplamente utilizada no ecossistema Java, facilita a organização do projeto, o versionamento de dependências e o processo de build e deploy. |
| **Amazon EKS** | Orquestração dos microsserviços da solução | Solução gerenciada baseada em Kubernetes, que facilita o deploy, a escalabilidade e o gerenciamento dos microsserviços (`generator`, `ingestion`, `notification`), mantendo a consistência da infraestrutura. |
| **Amazon SES** | Envio de e-mails de notificação em caso de erro | Atende ao requisito de notificação automática para o usuário em caso de falha no processamento. É um serviço simples, eficiente e com baixo custo, ideal para esse tipo de comunicação. |
| **GitHub Actions** | Automatização de build, testes e deploys | O GitHub Actions foi escolhido por estar amplamente consolidado no mercado e por oferecer uma integração direta com repositórios GitHub, simplificando pipelines de entrega contínua. Além disso, a equipe já possui familiaridade com a ferramenta, o que reduz tempo de configuração e acelera o processo de entrega contínua. |
| **Amazon Cognito** | Autenticação e segurança no microsserviço de usuários | Solução gerenciada que facilita a implementação de autenticação com usuário e senha, atendendo ao requisito de proteger o sistema e controlando o acesso de forma segura e padronizada. |
| **Amazon SQS** | Gerenciamento da fila de processamento de vídeos | Utilizamos SQS para garantir que os vídeos sejam processados de forma assíncrona e segura, sem perda de requisições, mesmo em momentos de pico. Isso também ajuda a escalar o sistema com segurança. |
| **DynamoDB** | Armazenamento dos metadados e arquivos gerados (como ZIPs of frames) | Optamos pelo DynamoDB por ser altamente escalável e disponível, atendendo bem à necessidade de processar múltiplos vídeos em paralelo. Seu modelo NoSQL permite evoluir a estrutura dos dados sem migrações complexas, o que é útil caso futuramente a solução precise armazenar também os vídeos. |
| **Amazon S3** | Armazenamento de vídeos e arquivos ZIP gerados | O S3 foi adotado por ser um serviço de armazenamento de objetos altamente durável, escalável e econômico, perfeito para armazenar vídeos enviados pelos usuários e arquivos ZIP gerados pelo `ez-frame-generator-ms` (bucket `ez-frame-video-storage`). Permite o compartilhamento seguro dos arquivos gerados via presigned URLs e suporta vídeos grandes e múltiplos uploads com facilidade. |

---

## 🧩 Fluxo de Interação entre Serviços

O diagrama abaixo ilustra o fluxo do `ez-frame-generator-ms` (em verde) e suas interações com outros componentes do sistema.

![image](https://github.com/user-attachments/assets/8081bc86-2c7a-4041-affb-ba3841e22d92)

---

## ✅ Pré-requisitos

- ☕ Java 21 instalado
- 📦 Maven instalado
- 🔐 Credenciais AWS configuradas (`AWS CLI` ou arquivo `~/.aws/credentials`)
- 🌐 Acesso a serviços AWS (SQS, S3, DynamoDB) com permissões adequadas

---

## 📏 Limites Definidos com Relação a Upload de Vídeos

Embora o `ez-frame-generator-ms` não lide diretamente com uploads, ele processa vídeos que já passaram pelas políticas de upload definidas no `ez-video-ingestion-ms`:

- **Tamanho Máximo por Arquivo**: 100 MB por vídeo
- **Limite Diário de Uploads por Usuário**: 10 vídeos por dia
- **Número Máximo de Arquivos por Requisição**: 3 vídeos por requisição
- **Tamanho Total por Requisição**: 300 MB no total por requisição

**Limite Interno do Generator Service**:

- **Máximo de Vídeos Processados Simultaneamente**: 20 vídeos, ajustável via configuração no EKS.

---

## ✅ Requisito para execução da solução

### 🚀 Criar ambiente e realizar deploy na seguinte ordem:
1. [Infra](https://github.com/ThaynaraDaSilva/ez-frame-infrastructure)
2. [Ingestion](https://github.com/ThaynaraDaSilva/ez-video-ingestion-ms)
3. [Generator](https://github.com/ThaynaraDaSilva/ez-frame-generator-ms)
4. [Notification](https://github.com/ThaynaraDaSilva/ez-frame-notification-ms)

---

## 🗂️ Estrutura de Diretórios do Projeto

A estrutura segue o padrão **Clean Architecture**:

```
ez-frame-generator-ms/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/duosilva/tech/solutions/ez/frame/generator/ms/
│   │   │       ├── adapters/
│   │   │       │   ├── in/
│   │   │       │   │   └── queue/           # Integração com SQS
│   │   │       │   └── out/
│   │   │       │       ├── storage/         # Integração com S3
│   │   │       │       └── http/            # Chamadas HTTP ao ingestion-ms
│   │   │       ├── application/
│   │   │       │   ├── dto/                 # DTOs
│   │   │       │   └── usecases/            # Casos de uso
│   │   │       ├── domain/
│   │   │       │   └── model/               # Modelos de domínio
│   │   │       └── config/                  # Configurações
│   │   └── resources/
│   │       └── application.yml              # Configurações do Spring Boot
├── pom.xml                                     # Arquivo Maven com dependências
└── README.md                                   # Documentação do projeto
```

---

## 🎥 Vídeos de apresentação

[📐 Desenho de Arquitetura](https://youtu.be/ry-GS9WqmaU)

[🔧 Github Rulesets, Pipelines e Sonarqube](https://youtu.be/jqO4ldizBwY)

[🔐 Jornada de Login e Upload de Vídeo](https://youtu.be/sk-AvQ9TnIw)

[📧 Jornada de Envio de Notificação](https://youtu.be/mE9PhuUo4Co)

[🖼️ Jornada de Geração de Frames](https://youtu.be/bfRUG1w-S8w)

---

## 👨‍💻 Desenvolvido por

@tchfer — RM357414

@ThaynaraDaSilva — RM357418
