# Proxy Pattern Service

A Spring Boot application implementing the Proxy pattern with rate limiting and quota management, deployed using Docker on Render.

## Architecture

### Domain-Driven Design (DDD) Structure
- **Domain**: Core business logic and models (ProxyRequest, RateLimit, Quota)
- **Application**: Service implementations and business logic orchestration
- **Infrastructure**: External integrations, exceptions, and technical concerns
- **Presentation**: REST controllers, DTOs, and API layer

### Features
- **Proxy Pattern**: HTTP proxy with request forwarding
- **Rate Limiting**: Configurable request limits per client IP
- **Quota Management**: Byte-based quota limits with automatic reset
- **Global Exception Handling**: Centralized error handling
- **Health Checks**: Built-in health endpoints
- **Docker Ready**: Multi-stage Docker build for production

## Technology Stack

- **Java 21** with modern language features
- **Spring Boot 3.2.5** with Jakarta EE
- **Maven** for dependency management
- **Docker** for containerization
- **Render** for cloud deployment

## Configuration

### Application Properties
```properties
# Server Configuration
server.port=${PORT:8080}

# Rate Limit Configuration
proxy.rate-limit.max-requests=100
proxy.rate-limit.window-minutes=1

# Quota Configuration  
proxy.quota.max-bytes=10485760
proxy.quota.reset-hours=1
```

## API Endpoints

### GET /api/proxy
Returns service status and timestamp.

### POST /api/proxy
Proxy a request to a target URL.

**Request Body:**
```json
{
  "url": "https://example.com/api/data"
}
```

**Response:**
```json
{
  "content": "Response content from target URL",
  "statusCode": 200,
  "timestamp": "2026-04-17T17:40:00",
  "responseSize": 1024
}
```

**Error Responses:**
- **429 Too Many Requests**: Rate limit exceeded
- **429 Too Many Requests**: Quota exceeded  
- **502 Bad Gateway**: Proxy error
- **500 Internal Server Error**: Internal error

## Development Commands

### 1. Run Locally
```bash
mvn spring-boot:run -s settings.xml
```

### 2. Package Application
```bash
mvn clean package -DskipTests -s settings.xml
```

### 3. Run Tests
```bash
mvn test -s settings.xml
```

### 4. Build Docker Image
```bash
docker build -t proxy-service .
```

### 5. Run Docker Container
```bash
docker run -p 8080:8080 proxy-service
```

## Deployment

### GitHub Commands
```bash
# Add all changes
git add .

# Commit changes
git commit -m "Update project with Spring Boot and DDD architecture"

# Push to GitHub
git push origin master
```

### Render Deployment Steps

1. **Create New Web Service**
   - Go to Render Dashboard
   - Click "New +" > "Web Service"
   - Connect GitHub repository

2. **Configure Service**
   - **Name**: proxy-service
   - **Environment**: Docker
   - **Branch**: master
   - **Root Directory**: (leave empty)
   - **Dockerfile Path**: Dockerfile
   - **Region**: Oregon (US West) or preferred

3. **Environment Variables** (Optional)
   - `PORT`: 8080 (auto-set by Render)
   - `JAVA_OPTS`: "-Xmx512m -Xms256m"

4. **Advanced Settings**
   - **Health Check Path**: /api/proxy
   - **Auto-Deploy**: Enabled

5. **Create Service**
   - Click "Create Web Service"
   - Wait for build and deployment

## Java 21 Compatibility Checklist

### Requirements Met
- [x] **Java Version**: Using Java 21.0.10 LTS
- [x] **Maven Compiler**: Configured with release 21
- [x] **Spring Boot**: Version 3.2.5 (Java 21 compatible)
- [x] **Dependencies**: All using Jakarta EE namespace
- [x] **Compilation**: Successful with Java 21 features

### Maven Configuration
```xml
<properties>
    <java.version>21</java.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    <release>21</release>
</properties>
```

## Common Java 21 Issues & Solutions

### Issue: Maven doesn't recognize release 21
**Solution**: Ensure Maven 3.9+ and proper compiler plugin:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.12.1</version>
    <configuration>
        <source>21</source>
        <target>21</target>
        <release>21</release>
    </configuration>
</plugin>
```

### Issue: Spring Boot compatibility
**Solution**: Use Spring Boot 3.2.x+ for Java 21 support

### Issue: Jakarta EE namespace
**Solution**: Spring Boot 3.x automatically uses Jakarta EE

## Docker Configuration

### Multi-stage Build
- **Build Stage**: Maven with JDK 21 Alpine
- **Runtime Stage**: JRE 21 Alpine with security best practices
- **Health Check**: Built-in health endpoint monitoring
- **Security**: Non-root user execution

### Production Optimizations
- Layer caching for faster builds
- Minimal runtime image
- JVM tuning for containers
- Health checks for orchestration

## Monitoring

### Health Endpoints
- `/actuator/health` - Application health
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics

### Logging
Configured for production with structured logging and appropriate log levels.

## Security Considerations

- Non-root Docker user
- Rate limiting per IP
- Quota enforcement
- Input validation
- Error message sanitization

## Performance

- In-memory rate limiting and quota tracking
- HTTP client connection pooling
- Optimized JVM settings for containers
- Efficient exception handling
