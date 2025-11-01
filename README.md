# 📚 Bookstore Management System

> **Spring Boot Practical Task Implementation**  
> A RESTful web application for managing a bookstore system with role-based authentication, validation, and testing.

---

### 🌐 Live Project Access

You can directly **check and test the project live** using the following links:

- **Frontend (UI):** [https://bookstore-project-ui.netlify.app/](https://bookstore-project-ui.netlify.app/)
- **Backend (API):** [https://bookstore-prod.onrender.com](https://bookstore-prod.onrender.com)
- **GitHub Repository:** [https://github.com/dhanrajdhasure7143/bookstore](https://github.com/dhanrajdhasure7143/bookstore)

### 👥 User Credentials

| Role  | Username | Password   | Access      |
|-------|-----------|------------|--------------|
| Admin | admin     | admin   | Full CRUD    |
| User  | user      | user    | Read-only    |

---

## 🎯 Project Overview

This project is implementation of a **Book Management System** as per the Java, Spring Boot Practical Task requirements. It showcases proficiency in:

- ✅ **REST API Design** using Spring Boot
- ✅ **Logical Validation** with custom ISBN validator
- ✅ **Authentication & Authorization** with JWT and role-based access
- ✅ **Comprehensive Testing** with JUnit 5 and MockMvc
- ✅ **Error Handling** with global exception management
- ✅ **Database Integration** with Spring Data JPA and H2

## 🏗️ Architecture & Design

### **Technology Stack**
- **Backend**: Java 17, Spring Boot 3.3.5, Spring Security, Spring Data JPA
- **Database**: H2 In-Memory Database
- **Authentication**: JWT (JSON Web Tokens)
- **Testing**: JUnit 5, MockMvc, Spring Boot Test
- **Build Tool**: Maven
- **Frontend**: HTML5/CSS3/JavaScript UI

### **Project Structure**
```
src/
├── main/java/com/closedigit/bookstore/
│   ├── config/          # Security, CORS, Data initialization
│   ├── controller/      # REST API endpoints
│   ├── dto/            # Data Transfer Objects (Java Records)
│   ├── entity/         # JPA entities
│   ├── exception/      # Custom exceptions & global handler
│   ├── mapper/         # Entity-DTO mapping
│   ├── repository/     # Data access layer
│   ├── security/       # JWT utilities & filters
│   ├── service/        # Business logic layer
│   └── validator/      # Custom validation logic
└── test/java/          # Comprehensive test suite
```

## 🚀 Quick Start

### **Prerequisites**
- Java 17 or higher
- Maven 3.6+ (or use included Maven wrapper)

### **Setup & Run**

1. **Navigate to backend directory**
   ```bash
   cd "Close Digit/bookstore"
   ```

2. **Build and run**
   ```bash
   # Build the project
   mvn clean install
   
   # Run tests
   mvn test
   
   # Start the application
   mvn spring-boot:run
   ```

   **Alternative (if Maven not installed):**
   ```bash
   # Windows
   mvnw.cmd clean install
   mvnw.cmd test
   mvnw.cmd spring-boot:run
   
   # Linux/Mac
   ./mvnw clean install
   ./mvnw test
   ./mvnw spring-boot:run
   ```

3. **Access the application**
   - **API Base URL**: `http://localhost:8080/api`
   - **H2 Console**: `http://localhost:8080/h2-console`
   - **Web UI**: Open `Close Digit/bookstore-ui/index.html` in your browser

### **Default Credentia
| Role  | Username | Password | Permissions |
|-------|----------|----------|-------------|
| Admin | `admin`  | `admin` | Full CRUD access |
| User  | `user`   | `user`  | Read-only access |

## 📋 API Documentation

### **Core Endpoints**

#### **Authentication**
```http
POST /api/auth/login     # User login
POST /api/auth/register  # User registration
```

#### **Book Management**
```http
GET    /api/books           # Get all books (paginated & sortable)
GET    /ap/{id}      # Get book by ID
POST   /api/books           # Create book (Admin only)
PUT    /api/books/{id}      # Update book (Admin only)
DELETE /api/books/{id}      # Delete book (Admin only)
```

### **Advanced Features**

#### **Pagination & Sorting**
```http
GET /api/books?page=0&size=10&sortBy=publishedDate&sortDir=desc
GET /api/books?sortBy=price&sortDir=asc
GET /api/books?sortBy=author&sortDir=desc
```

**Supported Sort Fields**: `id`, `title`, `author`, `publis`, `genre`, `price`, `isbn`

## 🔧 Implementatiails

### **1. Book Entity Specification**
```java
@Entity
public class Book {
    @Id @GeneratedValue
    private Long id;                    // Auto-generated
    
    @Size(min = 1, max = 100)
    private String title;               // Required, 1-100 chars
    
    @Size(min = 1, max = 50)
    private String author;              // Required, 1-50 chars
    
    @NotNull
    private LocalDate publishedDate;    // Required
    
    private String genre;               // Optional
    
    @DecimalMin("0.0")
    private BigDecimal price;           // Required, positive
    
    @NotBlank
    private String isbn;                // Required, validated
}
```

### **2. ISBN Validation Logic**
```java
@Component
public class IsbnValidator {
    public boolean isValidIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) return false;
        
        String cleanIsbn = isbn.trim();
        
        // Valid formats: 10-digit or 13-digit numeric only
        return (cleanIsbn.length() == 10 && cleanIsbn.matches("^[0-9]{10}$")) ||
               (cleanIsbn.length() == 13 && cleanIsbn.matches("^[0-9]{13}$"));
    }
}
```

### **3. Security Implementation**
- **JWT-based authentication** with configurable expiration
- **Role-based authorization** using `@PreAuthorize`
- **Method-level security** for fine-grained access control
- **Password encryption** using BCrypt

### **4. Exception Handling**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFound(BookNotFoundException ex);
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex);
    
}
```

## 🧪 Testing Strategy

### **Test Coverage**
- ✅ **Unit Tests**: Service layer, validators, mappers
- ✅ **Integration Tests**: Controller endpoints with MockMvc
- ✅ **Security Tests**: Authentication and authorization
- ✅ **Validation Tests**: ISBN validation with edge cases
- ✅ **End-to-End Tests**: Complete workflow scenarios

### **Running Tests**
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=IsbnValidatorTest

# Run with coverage
./mvnw test jacoco:report
```

### **Key Test Scenarios**
- ISBN validation with valid/invalid formats
- Book CRUD operations with proper authorization
- Pagination and sorting functionality
- Error handling for various edge cases
- Security access control (admin vs user permissions)

## 📊 Sample API Usage

### **1. User Registration & Login**
```bash
# Register new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "securepass123"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### **2. Book Operations**
```bash
# Get books with sorting (newest first)
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  "http://localhost:8080/api/books?sortBy=publishedDate&sortDir=desc&size=5"

# Create new book (Admin only)
curl -X POST http://localhost:8080/api/books \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "publishedDate": "2008-08-01",
    "genre": "Programming",
    "price": 45.99,
    "isbn": "9780132350884"
  }'

# Get cheapest books
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  "http://localhost:8080/api/books?sortBy=price&sortDir=asc"
```

## 🎨 Web Interface

The project includes a responsive web interface featuring:

- **Clean Authentication UI** with login/register tabs
- **Professional Data Table** with sorting and pagination
- **Role-based UI Elements** (admin sees additional controls)
- **Real-time Validation** and user feedback
- **Mobile-responsive Design** for all screen sizes

### **UI Features**
- 🔐 Secure login/logout functionality
- 📊 Dynamic book listing with sorting options
- ➕ Book creation/editing (admin only)
- 🗑️ Book deletion with confirmation (admin only)
- 📱 Fully responsive design
- 🎯 Toast notifications for user feedback

## 🔍 Data Models

### **Book Response**
```json
{
  "id": 1,
  "title": "The Great Gatsby",
  "author": "F. Scott Fitzgerald",
  "publishedDate": "1925-04-10",
  "genre": "Fiction",
  "price": 12.99,
  "isbn": "9780743273565"
}
```

### **Authentication Response**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@bookstore.com",
    "role": "ADMIN",
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

## 🛡️ Security Features

- **JWT Token Authentication** with configurable expiration
- **Role-based Access Control** (ADMIN/USER)
- **Method-level Security** using `@PreAuthorize`
- **Input Validation** preventing SQL injection
- **CORS Configuration** for cross-origin requests
- **Password Encryption** using BCrypt

## 📈 Performance & Scalability

- **Pagination Support** for large datasets
- **Efficient Sorting** with database-level operations
- **Connection Pooling** with HikariCP
- **Lazy Loading** for optimal memory usage
- **Caching-ready Architecture** for future enhancements

## 🔧 Configuration

### **Application Properties**
```properties
# Database Configuration
spring.datasource.url=jdbc:h2:mem:bookstore
spring.datasource.username=sa
spring.datasource.password=password

# JWT Configuration  
jwt.secret=mySecretKey123456789012345678901234567890
jwt.expiration=86400000

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
```

### **Development vs Production**
- **Development**: H2 in-memory database with sample data
- **Production Ready**: Easy migration to PostgreSQL/MySQL
- **Environment Profiles**: Separate configurations for different environments

## 📝 Development Notes

### **Design Patterns Used**
- **Repository Pattern** for data access abstraction
- **DTO Pattern** with Java Records for clean data transfer
- **Builder Pattern** for complex object creation
- **Strategy Pattern** for validation logic

### **Best Practices Implemented**
- **Clean Code Principles** with meaningful names and small methods
- **SOLID Principles** for maintainable architecture
- **Comprehensive Logging** for debugging and monitoring
- **Exception Handling** with proper HTTP status codes
- **Input Validation** at multiple layers

---

**Developed by**: Dhanraj D. Hasure 

**Contact**: [dhanrajdhasure@gmail.com]
