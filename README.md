# Organix Backend

Backend RESTful completo para aplicativo de gerenciamento de conteúdo e produção, desenvolvido com Java Spring Boot.

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Data JPA**
- **Spring Security** com JWT
- **MySQL** (produção) / **H2** (desenvolvimento)
- **Maven**
- **Swagger/OpenAPI 3** para documentação
- **Lombok** para redução de boilerplate

## 📋 Funcionalidades

### Autenticação e Autorização
- ✅ Sistema de autenticação baseado em JWT
- ✅ Multi-tenancy (segregação de dados por empresa)
- ✅ Controle de acesso baseado em roles (ADMIN, OPERATOR)
- ✅ Registro de nova empresa com usuário admin inicial

### Gerenciamento de Entidades
- ✅ **Empresas (Companies)**: Gerenciamento de organizações
- ✅ **Usuários (Users)**: Controle de usuários com diferentes tipos de acesso
- ✅ **Produtos (Products)**: Catálogo de produtos da empresa
- ✅ **Rascunhos (Drafts)**: Sistema de rascunhos com aprovação
- ✅ **Conteúdo (Content)**: Gerenciamento completo de conteúdo com métricas
- ✅ **Métricas (Metrics)**: Sistema de métricas de performance por canal

### APIs Disponíveis
- ✅ **Autenticação** (`/auth`): Login e registro
- ✅ **Usuários** (`/users`): CRUD de usuários (somente ADMIN)
- ✅ **Produtos** (`/products`): CRUD de produtos (somente ADMIN)
- ✅ **Rascunhos** (`/drafts`): CRUD de rascunhos com controle de acesso
- ✅ **Conteúdo** (`/content`): CRUD de conteúdo com filtros avançados
- ✅ **Performance** (`/performance`): Analytics e métricas de performance

### Recursos Especiais
- ✅ Transformação de rascunhos aprovados em conteúdo
- ✅ Sistema de métricas por canal com dados JSON
- ✅ Filtros avançados para conteúdo (data, status, usuário, produto)
- ✅ Analytics de performance com agregações
- ✅ Tratamento de exceções padronizado
- ✅ Validação de entrada com Bean Validation

## 🏗️ Arquitetura

```
src/main/java/com/organixui/organixbackend/
├── config/              # Configurações (Security, Swagger)
├── controller/          # Controladores REST
├── dto/                 # Data Transfer Objects
├── exception/           # Exceções personalizadas e handlers
├── model/               # Entidades JPA
├── repository/          # Repositórios Spring Data
├── security/            # Classes de segurança JWT
└── service/             # Camada de negócio
```

## 🗄️ Modelo de Dados

### Entidades Principais
- **Company**: Empresas com admin
- **User**: Usuários com roles (ADMIN/OPERATOR)
- **Product**: Produtos da empresa
- **Draft**: Rascunhos com status de aprovação
- **Content**: Conteúdo final com métricas
- **ContentMetrics**: Métricas detalhadas por canal

### Relacionamentos
- Todas as entidades são segregadas por `companyId`
- Usuários pertencem a uma empresa
- Conteúdo está vinculado a produtos e usuários
- Métricas são vinculadas ao conteúdo (OneToOne)

## 🚀 Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.6+
- MySQL 8.0+ (ou usar H2 para desenvolvimento)

### Configuração do Banco de Dados

#### Desenvolvimento (H2)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Produção (MySQL)
1. Configure o MySQL e crie o banco:
```sql
CREATE DATABASE organix_db;
```

2. Atualize as configurações em `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/organix_db
    username: seu_usuario
    password: sua_senha
```

### Executando a Aplicação
```bash
# Clonar o repositório
git clone <repository-url>
cd organix-backend

# Compilar e executar
mvn clean install
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080/api`

## 📚 Documentação da API

### Swagger UI
Acesse a documentação interativa em: `http://localhost:8080/api/swagger-ui.html`

### Endpoints Principais

#### Autenticação
```http
POST /api/auth/signup     # Registro de empresa e admin
POST /api/auth/login      # Login de usuário
```

#### Usuários (ADMIN apenas)
```http
GET    /api/users         # Listar usuários
POST   /api/users         # Criar usuário
PUT    /api/users/{id}    # Atualizar usuário
DELETE /api/users/{id}    # Excluir usuário
```

#### Produtos (ADMIN apenas)
```http
GET    /api/products      # Listar produtos
POST   /api/products      # Criar produto
PUT    /api/products/{id} # Atualizar produto
DELETE /api/products/{id} # Excluir produto
```

#### Rascunhos
```http
GET    /api/drafts                    # Listar rascunhos (com filtros)
POST   /api/drafts                    # Criar rascunho
PUT    /api/drafts/{id}               # Atualizar rascunho
DELETE /api/drafts/{id}               # Excluir rascunho
```

#### Conteúdo
```http
GET    /api/content                           # Listar conteúdo (com filtros)
POST   /api/content                           # Criar conteúdo
PUT    /api/content/{id}                      # Atualizar conteúdo
DELETE /api/content/{id}                      # Excluir conteúdo
POST   /api/content/transform-draft/{draftId} # Transformar rascunho em conteúdo
```

#### Performance Analytics
```http
GET /api/performance/summary          # Resumo de métricas
GET /api/performance/channel-performance # Performance por canal
GET /api/performance/top-content      # Conteúdo com melhor performance
```

## 🔐 Autenticação

### Processo de Registro
1. `POST /api/auth/signup` - Cria empresa e usuário admin
2. Retorna JWT token para autenticação

### Processo de Login
1. `POST /api/auth/login` - Autentica usuário
2. Retorna JWT token e informações do usuário

### Usando o Token
Inclua o token JWT no header das requisições:
```http
Authorization: Bearer <jwt-token>
```

## 🛡️ Segurança e Autorização

### Roles de Usuário
- **ADMIN**: Acesso completo a todos os recursos da empresa
- **OPERATOR**: Acesso limitado aos próprios rascunhos e conteúdo

### Multi-tenancy
- Todos os dados são segregados por `companyId`
- Usuários só acessam dados da própria empresa
- Validação automática de company em todas as operações

### Controle de Acesso
- Usuários OPERATOR só podem acessar/modificar recursos próprios
- Usuários ADMIN têm acesso completo aos dados da empresa
- Validação de propriedade de recursos em tempo de execução

## 📊 Sistema de Métricas

### Métricas Globais
- Views, Likes, Comments, Shares
- Reach, Engagement

### Métricas por Canal
- Dados específicos por canal (Instagram, Facebook, etc.)
- Site visits, New accounts, Post clicks

### Analytics Disponíveis
- Resumo de performance com filtros por período
- Performance detalhada por canal
- Top content por diferentes métricas

## 🧪 Profiles de Execução

### Development (dev)
- Banco H2 em memória
- Console H2 habilitado
- Logs detalhados

### Test (test)
- Banco H2 em memória para testes
- Configuração isolada

### Production (default)
- MySQL como banco principal
- Configurações de produção

## 🔧 Configurações Principais

### JWT
```yaml
jwt:
  secret: mySecretKey123456789012345678901234567890
  expiration: 86400000 # 24 horas
```

### Banco de Dados
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/organix_db
    username: root
    password: root
```

### CORS
- Configurado para aceitar todas as origens em desenvolvimento
- Personalizável para produção

## 🚨 Tratamento de Erros

### Exceções Personalizadas
- `BusinessException`: Erros de regra de negócio
- `ResourceNotFoundException`: Recursos não encontrados
- `UnauthorizedException`: Acesso negado

### Respostas de Erro Padronizadas
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found: 123e4567-e89b-12d3-a456-426614174000",
  "path": "/api/users/123e4567-e89b-12d3-a456-426614174000",
  "errorCode": "RESOURCE_NOT_FOUND"
}
```

## 📝 Exemplo de Uso

### 1. Registrar Empresa e Admin
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "companyName": "Minha Empresa",
    "name": "João Admin",
    "email": "admin@empresa.com",
    "password": "senha123"
  }'
```

### 2. Fazer Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@empresa.com",
    "password": "senha123"
  }'
```

### 3. Criar Produto (usando token)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt-token>" \
  -d '{
    "name": "Produto Teste"
  }'
```

## 🔄 Roadmap

### Próximas Funcionalidades
- [ ] Upload de imagens para conteúdo
- [ ] Sistema de notificações
- [ ] Relatórios em PDF
- [ ] Integração com redes sociais
- [ ] Dashboard com gráficos
- [ ] Backup automático

### Melhorias Técnicas
- [ ] Cache com Redis
- [ ] Testes automatizados
- [ ] CI/CD Pipeline
- [ ] Monitoramento com Actuator
- [ ] Documentação com AsciiDoc

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## 📞 Contato

- **Email**: dev@organix.com
- **Documentação**: http://localhost:8080/api/swagger-ui.html
- **API**: http://localhost:8080/api
