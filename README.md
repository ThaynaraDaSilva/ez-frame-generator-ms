# 🎥 Generator Service (`ez-frame-generator-ms`)

## 📌 Contextualização

O **Generator Service** (também chamado de `Processing Service`) é um microserviço da solução `ez-frame`, responsável por **processar vídeos** enfileirados pelo `Ingestion Service`.

- Ele **escuta uma fila SQS**, processa os vídeos (ex.: extrai frames), e **salva os resultados** no Amazon S3.
- **Atualiza o status** no DynamoDB (tabela `ProcessedVideos`).
- Após o processamento, **atualiza o status** no `Ingestion Service` via HTTP.

---

## 🧩 Desenho de Arquitetura

O diagrama abaixo representa o fluxo do `Generator Service` dentro da solução `ez-frame`, focando no processamento de vídeos e atualização de status:

![image](https://github.com/user-attachments/assets/9bf71040-3403-4a75-8434-441b826df854)


> Para visualizar o diagrama, cole o script below in PlantText.

```
@startuml
!define RECTANGLE class
!includeurl https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml

Container(sqs, "SQS", "Fila de tarefas")
Container(processingService, "Generator Service", "Spring Boot (Java 21)", "Processa vídeos")
Container(ingestionService, "Ingestion Service", "Spring Boot (Java 21)", "Atualiza status")
Container(dynamodb, "DynamoDB", "Armazena metadados")
Container(processedVideosTable, "ProcessedVideos", "Tabela", "Metadados de vídeos processados")
Container(s3, "S3", "Armazena vídeos processados")

' Relacionamentos
dynamodb --> processedVideosTable
sqs --> processingService : "1. Escuta fila"
processingService --> s3 : "2. Salva vídeo processado"
processingService --> processedVideosTable : "3. Atualiza status (ProcessedVideos)"
processingService --> ingestionService : "4. Chama endpoint (POST /update-status)"

' Estilização
skinparam monochrome true
skinparam shadowing false
skinparam backgroundColor #FFFFFF

@enduml
```

---

## ✅ Pré-requisitos

- ☕ Java 21 instalado
- 📦 Maven instalado
- 🔐 Credenciais AWS configuradas (`AWS CLI` ou arquivo `~/.aws/credentials`)
- 🌐 Acesso a serviços AWS (SQS, S3, DynamoDB) com permissões adequadas
- 🔗 URL do endpoint do `Ingestion Service` (para chamadas HTTP)

---

## 📏 Limites Definidos com Relação a Upload de Vídeos

Embora o `Generator Service` não lide diretamente com uploads, ele processa vídeos que já passaram pelas políticas de upload definidas no `Ingestion Service`:

- **Tamanho Máximo por Arquivo**: 100 MB por vídeo
- **Limite Diário de Uploads por Usuário**: 10 vídeos por dia
- **Número Máximo de Arquivos por Requisição**: 5 vídeos por requisição
- **Tamanho Total por Requisição**: 300 MB no total por requisição

**Limite Interno do Generator Service**:

- **Máximo de Vídeos Processados Simultaneamente**: 20 vídeos, ajustável via configuração no EKS.

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
│   │   │       │   │   └── sqs/             # Listeners SQS
│   │   │       │   └── out/
│   │   │       │       └── repository/      # Repositórios (ex.: ProcessedVideosRepository)
│   │   │       ├── application/
│   │   │       │   ├── dto/                # DTOs
│   │   │       │   └── usecases/           # Casos de uso (ex.: ProcessVideoUseCase)
│   │   │       ├── domain/
│   │   │       │   └── model/              # Modelos de domínio (ex.: ProcessedVideo)
│   │   │       └── config/                 # Configurações (ex.: SQSConfig)
│   │   └── resources/
│   │       └── application.yml             # Configurações do Spring Boot
├── pom.xml                                    # Arquivo Maven com dependências
└── README.md                                  # Documentação do projeto
```

---

## 📊 Modelagem do Banco de Dados

O `Generator Service` utiliza o **DynamoDB** para armazenar metadados dos vídeos processados na tabela `ProcessedVideos`. Estrutura da tabela:

- **Nome da Tabela**: `ProcessedVideos`
- **Partition Key**: `videoId` (String, ex.: `vid123`)
- **Atributos**:
  - `filename`: Nome do arquivo processado (String, ex.: `video_processed.mp4`)
  - `status`: Status do processamento (String, ex.: `COMPLETED`, `FAILED`)
  - `errorMessage`: Mensagem de erro, se aplicável (String, ex.: `Erro no processamento`)
  - `timestamp`: Data/hora do processamento (String, ex.: `2025-04-19T10:10:00Z`)

---

## 🛠️ Como Compilar o Projeto

### 1️⃣ Clone o repositório

```bash
git clone https://github.com/ThaynaraDaSilva/ez-frame-generator-ms.git
cd ez-frame-generator-ms
```

### 2️⃣ Configure o arquivo `application.yml`

```yaml
aws:
  region: us-east-1
  sqs:
    queue-url: <URL_DA_FILA_SQS>
  s3:
    bucket: <NOME_DO_BUCKET>
  dynamodb:
    table-name: ProcessedVideos
ingestion-service:
  url: http://ingestion-service:8080/update-status
```

### 3️⃣ Compile e execute o projeto

```bash
mvn clean install
mvn spring-boot:run
```

O serviço começará a escutar a fila SQS e processar vídeos automaticamente.

---

## 🧱 Componentes da Solução Global ez-frame

| **Componente** | **Finalidade** | **Justificativa** |
| --- | --- | --- |
| **Clean Architecture** | Organização interna da solução | Foi escolhida para garantir uma estrutura modular, de fácil manutenção e testes. Essa separação clara entre regras de negócio e infraestrutura facilita a escalabilidade da solução ao longo do tempo, conforme o sistema evolui. |
| **Java 21** | Linguagem principal para implementação | A linguagem Java foi adotada em substituição ao .NET por uma decisão estratégica, considerando a expertise da equipe com o ecossistema Java. Essa escolha visa otimizar o desenvolvimento, reduzir a curva de aprendizado e garantir eficiência na evolução e manutenção da solução. |
| **DynamoDB** | Armazenamento dos metadados dos vídeos processados | Optamos pelo DynamoDB por ser altamente escalável e disponível, atendendo bem à necessidade de processar múltiplos vídeos em paralelo. Seu modelo NoSQL permite evoluir a estrutura dos dados sem migrações complexas, o que é útil caso futuramente a solução precise armazenar também os vídeos. |
| **Apache Maven** | Gerenciamento de dependências e build | Ferramenta amplamente utilizada no ecossistema Java, facilita a organização do projeto, o versionamento de dependências e o processo de build e deploy. |
| **Amazon SQS** | Gerenciamento da fila de processamento de vídeos | Utilizamos SQS para garantir que os vídeos sejam processados de forma assíncrona e segura, sem perda de requisições, mesmo em momentos de pico. Isso também ajuda a escalar o sistema com segurança. |
| **Amazon EKS** | Orquestração dos microsserviços da solução | Solução gerenciada baseada em Kubernetes, que facilita o deploy, a escalabilidade e o gerenciamento dos microsserviços (`generator`, `ingestion`, `notification`), mantendo a consistência da infraestrutura. |
| **GitHub Actions** | Automatização de build, testes e deploys | O GitHub Actions foi escolhido por estar amplamente consolidado no mercado e por oferecer uma integração direta com repositórios GitHub, simplificando pipelines de entrega contínua. Além disso, a equipe já possui familiaridade com a ferramenta, o que reduz tempo de configuração e acelera o processo de entrega contínua. |

---

## 🔗 Demais Projetos Relacionados

**ez-frame-ingestion-ms** — Microserviço que envia vídeos para a fila de processamento, consulta status, e chama o `Notification Service` para enviar e-mails em caso de falha.

**ez-frame-notification-ms** — Microserviço que envia notificações por e-mail em caso de falha no processamento, chamado pelo `Ingestion Service`.

---

## 👨‍💻 Desenvolvido por

@tchfer — RM357414

@ThaynaraDaSilva — RM357418
