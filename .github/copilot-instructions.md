<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# Organix Backend - Copilot Instructions

This is a Java Spring Boot backend project for a content management and production application.

## Project Structure and Architecture

- **Framework**: Spring Boot 3.x with Java 17
- **Architecture**: REST API with multi-tenant design
- **Database**: MySQL (production) / H2 (development)
- **Security**: JWT-based authentication with role-based authorization
- **Documentation**: Swagger/OpenAPI 3

## Key Patterns and Conventions

### Package Structure
```
com.organixui.organixbackend/
├── config/       # Configuration classes (Security, Swagger)
├── controller/   # REST controllers
├── dto/          # Data Transfer Objects (request/response)
├── exception/    # Custom exceptions and global handler
├── model/        # JPA entities with relationships
├── repository/   # Spring Data JPA repositories
├── security/     # JWT and security utilities
└── service/      # Business logic layer
```

### Naming Conventions
- **Entities**: Use JPA annotations with UUID primary keys
- **DTOs**: Separate request/response DTOs with validation annotations
- **Controllers**: RESTful endpoints with Swagger documentation
- **Services**: Business logic with transaction management
- **Repositories**: Spring Data JPA with custom queries

### Security Implementation
- **Multi-tenancy**: All entities filtered by `companyId`
- **Role-based access**: ADMIN (full access) vs OPERATOR (limited access)
- **JWT tokens**: Stateless authentication with SecurityUtils helper
- **Resource ownership**: Users can only access their own resources (unless ADMIN)

### Key Business Rules
1. **Company Isolation**: All data operations must respect company boundaries
2. **Role Permissions**: 
   - ADMIN: Full CRUD on users, products, all drafts/content
   - OPERATOR: CRUD only on own drafts/content, read-only on products
3. **Draft to Content**: Only APPROVED drafts can be transformed to content
4. **Metrics**: Automatic creation of ContentMetrics for new content

### Exception Handling
- Use custom exceptions: `BusinessException`, `ResourceNotFoundException`, `UnauthorizedException`
- Global exception handler provides consistent error responses
- Include proper HTTP status codes and error messages

### Database Design
- **UUID primary keys** for all entities
- **Soft relationships** using foreign key columns
- **JSON columns** for flexible data (channels, metrics)
- **Audit fields** (createdAt, updatedAt where applicable)

### API Design Principles
- RESTful endpoints with proper HTTP methods
- Consistent request/response DTOs
- Comprehensive Swagger documentation
- Validation using Bean Validation annotations
- Filtering and pagination support where needed

## Development Guidelines

### When adding new features:
1. Create entity with proper JPA annotations and company isolation
2. Add corresponding DTOs with validation
3. Implement repository with company-filtered queries
4. Create service with business logic and security checks
5. Add REST controller with proper documentation
6. Include security annotations and role checks

### Security Considerations:
- Always validate company ownership using SecurityUtils
- Check user permissions before data access/modification
- Use @PreAuthorize annotations for role-based access
- Sanitize user inputs and validate DTOs

### Testing Approach:
- Unit tests for services and repositories
- Integration tests for controllers
- Security tests for authentication and authorization
- Use H2 database for test profile

### Code Quality:
- Use Lombok to reduce boilerplate code
- Follow Spring Boot best practices
- Maintain consistent error handling
- Document complex business logic
- Keep controllers thin, services focused
