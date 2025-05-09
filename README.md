# 🎥 ez-frame-generator-ms

## 📌 Contextualização

O microsserviço `ez-frame-generator-ms` é responsável pelo processamento assíncrono de vídeos da solução **ez-frame**. Ele consome mensagens da fila SQS (`video-processing-queue`), faz o download de vídeos do bucket S3 (`ez-frame-video-storage`), gera frames em memória, compacta-os em um arquivo ZIP, e salva o ZIP no S3. Após o processamento, solicita a atualização do status do vídeo via endpoint `http://host:8080/v1/ms/videos/update-status` (`ez-video-ingestion-ms`).

---

## 🧩 Desenho de Arquitetura

![image](https://github.com/user-attachments/assets/da998aa9-deb2-48fc-9025-06d3e1dfb0d1)

---

## 🛡️ Políticas de Upload de Vídeos

Embora o `ez-frame-generator-ms` não lide diretamente com uploads, ele processa vídeos que já passaram pelas políticas de upload definidas no `ez-video-ingestion-ms`. O projeto foi estruturado com suporte à implementação de múltiplas **políticas configuráveis**, facilitando sua evolução para diferentes regras de negócio e, se necessário, a expansão para um serviço com diferentes planos e maior flexibilidade de regras. **Para esta entrega, definimos a implementação de apenas duas políticas**:

- `validateMaxFilesPerRequest`
- `validateTotalSizePerRequest`

Essas regras estão centralizadas na classe `VideoUploadPolicy` (pacote `br.duosilva.tech.solutions.ez.video.ingestion.ms.domain.policy`), permitindo fácil manutenção e extensibilidade.

O `ez-frame-generator-ms` está configurado para verificar a fila a cada **500 milissegundos** `(@Scheduled(fixedRate = 500))`, permitindo uma alta frequência de varredura da fila. Em cada execução, ele tenta buscar **até 10 mensagens** por vez `(maxNumberOfMessages(10))`, que é o limite máximo permitido pelo Amazon SQS por chamada. Cada mensagem representa um vídeo que precisa ser processado.

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
| **DynamoDB** | Armazenamento dos metadados | Optamos pelo DynamoDB por ser altamente escalável e disponível, atendendo bem à necessidade de processar múltiplos vídeos em paralelo. Seu modelo NoSQL permite evoluir a estrutura dos dados sem migrações complexas, o que é útil caso futuramente a solução precise armazenar também os vídeos. |
| **Amazon S3** | Armazenamento de vídeos e arquivos ZIP gerados | O S3 foi adotado por ser um serviço de armazenamento de objetos altamente durável, escalável e econômico, perfeito para armazenar vídeos enviados pelos usuários e arquivos ZIP gerados pelo `ez-frame-generator-ms` (bucket `ez-frame-video-storage`). Permite o compartilhamento seguro dos arquivos gerados via presigned URLs e suporta vídeos grandes e múltiplos uploads com facilidade. |

---

## 🧩 Fluxo de Interação entre Serviços

O diagrama abaixo ilustra o fluxo do `ez-frame-generator-ms` ***(em verde)*** e suas interações com outros componentes do sistema.

![image](https://github.com/user-attachments/assets/8081bc86-2c7a-4041-affb-ba3841e22d92)

---

## ✅ Pré-requisitos para solução ez-frame (Todos os Microserviços)

- ☕ **Java 21**
- 📦 **Maven**
- 🔐 **Credenciais AWS configuradas no repositório como GitHub Secrets**  
  - `AWS_ACCESS_KEY_ID`  
  - `AWS_SECRET_ACCESS_KEY`
- 🔐 **Credenciais do SonarQube configuradas no repositório como GitHub Secrets**  
  - `SONAR_TOKEN`
  - `PROJECT_KEY`
- 🔐 **Credenciais do Dockerhub configuradas no repositório como GitHub Secrets**  
  - `DOCKER_PASSWORD`
  - `DOCKER_USERNAME`
- 👤 **Criar UserPool e AppClient no Amazon Cognito**
- 📄 **Configurar as filas**:
  - `video-processing-queue`
  - `video-processing-queue-dlq`
- 📧 **Criar Entity (e-mail verificado) no Amazon SES**
- 🛡️ **Criar usuário IAM com política SES para envio de e-mails**  
  - Permissões necessárias: `ses:SendEmail` e `ses:SendRawEmail`
  - Exemplo de **policy JSON** para colar na criação da política no IAM:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "ses:SendEmail",
                "ses:SendRawEmail"
            ],
            "Resource": "*"
        }
    ]
}
```

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
