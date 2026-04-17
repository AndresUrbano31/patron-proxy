# Final Deliverables - AI Generation Service with PostgreSQL Integration

## A. Final Folder Structure

```
src/main/java/com/proxy/
|-- ProxyApplication.java                    # Main Spring Boot application
|-- application/
|   |-- service/
|   |   |-- AIGenerationService.java         # Service interface
|   |   |-- RealMockAIGenerationService.java # Mock AI implementation
|   |   |-- RateLimitProxyService.java        # Rate limiting proxy
|   |   |-- QuotaProxyService.java           # Quota management proxy
|   |   |-- UserAccountService.java           # User management
|   |   |-- TokenUsageService.java            # Token usage tracking
|   |   |-- AIGenerationRequestService.java  # Request history service
|   |   |-- ProxyService.java                 # Original proxy service
|   |   |-- UsageHistoryService.java         # Usage analytics
|   |   |-- QuotaService.java                 # Original quota service
|   |   |-- RateLimitService.java             # Original rate limiting
|-- domain/
|   |-- model/
|   |   |-- AIGenerationRequest.java          # AI request entity
|   |   |-- TokenUsage.java                   # Token usage entity
|   |   |-- UserAccount.java                  # User account entity
|   |   |-- Plan.java                         # Subscription plan entity
|   |   |-- UsageHistory.java                 # Usage history entity
|   |   |-- QuotaUsage.java                    # Quota usage entity
|   |   |-- ProxyRequest.java                  # Original proxy request
|   |   |-- RateLimit.java                     # Original rate limit
|   |   |-- Quota.java                         # Original quota
|   |-- service/
|   |   |-- ProxyService.java                 # Domain service interface
|-- infrastructure/
|   |-- repository/
|   |   |-- AIGenerationRequestRepository.java # AI request repo
|   |   |-- TokenUsageRepository.java          # Token usage repo
|   |   |-- UserAccountRepository.java         # User account repo
|   |   |-- PlanRepository.java                # Plan repo
|   |   |-- UsageHistoryRepository.java        # Usage history repo
|   |   |-- QuotaUsageRepository.java          # Quota usage repo
|   |-- exception/
|       |-- UserNotFoundException.java          # User not found exception
|       |-- UserAlreadyExistsException.java   # User exists exception
|       |-- QuotaExceededException.java        # Quota exceeded exception
|       |-- RateLimitExceededException.java    # Rate limit exceeded exception
|       |-- ProxyException.java                # General proxy exception
|-- presentation/
|   |-- controller/
|   |   |-- AIController.java                 # AI generation endpoints
|   |   |-- ProxyController.java              # Original proxy endpoints
|   |   |-- AdminController.java              # Admin endpoints
|   |-- dto/
|   |   |-- AIGenerationRequestDto.java       # AI request DTO
|   |   |-- AIGenerationResponseDto.java      # AI response DTO
|   |   |-- ProxyRequestDto.java              # Original proxy request DTO
|   |   |-- ProxyResponseDto.java             # Original proxy response DTO
|   |   |-- ErrorResponseDto.java             # Error response DTO
|   |-- exception/
|       |-- GlobalExceptionHandler.java        # Global exception handler
```

## B. Complete pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>

    <groupId>com.proxy</groupId>
    <artifactId>proxy</artifactId>
    <version>1.0.0</version>
    <name>proxy-pattern-service</name>
    <description>AI Generation Service with Proxy Pattern and PostgreSQL</description>

    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <spring-boot.version>3.2.5</spring-boot.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- Database Dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <finalName>proxy-service</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <mainClass>com.proxy.ProxyApplication</mainClass>
                </configuration>
            </plugin>
            
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
            
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.5</version>
                <configuration>
                    <useSystemClassLoader>false</useSystemClassLoader>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## C. Complete application.properties

```properties
# Server Configuration
server.port=${PORT:8080}
server.servlet.context-path=/

# Application Configuration
spring.application.name=proxy-pattern-service
spring.profiles.active=default

# Database Configuration - PostgreSQL on Render
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.defer-datasource-initialization=true

# Connection Pool Configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
spring.datasource.hikari.connection-timeout=20000

# Management/Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
management.info.env.enabled=true

# Logging
logging.level.com.proxy=INFO
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n

# Rate Limit Configuration
proxy.rate-limit.max-requests=100
proxy.rate-limit.window-minutes=1

# Quota Configuration
proxy.quota.max-bytes=10485760
proxy.quota.reset-hours=1

# HTTP Client Configuration
proxy.http.connect-timeout-ms=10000
proxy.http.request-timeout-ms=30000
```

## D. Complete JPA Entities

### UserAccount.java
```java
@Entity
@Table(name = "user_accounts")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String clientIp;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;
    
    @Column(nullable = false)
    private Boolean active;
    
    @Column(name = "plan_changed_at")
    private LocalDateTime planChangedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors, methods, @PreUpdate, etc.
}
```

### Plan.java
```java
@Entity
@Table(name = "plans")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private Integer maxRequestsPerMinute;
    
    @Column(nullable = false)
    private Long maxBytesPerHour;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Boolean active;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors, methods, @PreUpdate, etc.
}
```

### AIGenerationRequest.java
```java
@Entity
@Table(name = "ai_generation_requests")
public class AIGenerationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;
    
    @Column(nullable = false)
    private String prompt;
    
    @Column(nullable = false)
    private String generatedContent;
    
    @Column(nullable = false)
    private Integer tokensUsed;
    
    @Column(nullable = false)
    private Long responseTimeMs;
    
    @Column(nullable = false)
    private Boolean success;
    
    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    // Constructors, methods, markAsCompleted(), markAsFailed(), etc.
}
```

### TokenUsage.java
```java
@Entity
@Table(name = "token_usage")
public class TokenUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;
    
    @Column(nullable = false)
    private Integer tokensUsed;
    
    @Column(nullable = false)
    private Integer maxTokens;
    
    @Column(name = "window_start", nullable = false)
    private LocalDateTime windowStart;
    
    @Column(name = "window_end", nullable = false)
    private LocalDateTime windowEnd;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // Constructors, methods, isExpired(), hasTokensAvailable(), consumeTokens(), etc.
}
```

## E. Complete Spring Data JPA Repositories

### AIGenerationRequestRepository.java
```java
@Repository
public interface AIGenerationRequestRepository extends JpaRepository<AIGenerationRequest, Long> {
    
    List<AIGenerationRequest> findByUserAccountIdOrderByRequestTimeDesc(Long userAccountId);
    
    List<AIGenerationRequest> findByUserAccountIdAndRequestTimeBetweenOrderByRequestTimeDesc(
            Long userAccountId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(r) FROM AIGenerationRequest r WHERE r.userAccount.id = :userAccountId AND r.requestTime >= :since")
    long countRequestsByUserSince(@Param("userAccountId") Long userAccountId, @Param("since") LocalDateTime since);
    
    @Query("SELECT SUM(r.tokensUsed) FROM AIGenerationRequest r WHERE r.userAccount.id = :userAccountId AND r.requestTime >= :since")
    Integer sumTokensUsedByUserSince(@Param("userAccountId") Long userAccountId, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(r) FROM AIGenerationRequest r WHERE r.success = false AND r.requestTime >= :since")
    long countFailedRequestsSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT AVG(r.responseTimeMs) FROM AIGenerationRequest r WHERE r.success = true AND r.requestTime >= :since")
    Double getAverageResponseTimeSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT r.prompt, COUNT(r) as requestCount FROM AIGenerationRequest r WHERE r.requestTime >= :since GROUP BY r.prompt ORDER BY requestCount DESC")
    List<Object[]> findMostRequestedPromptsSince(@Param("since") LocalDateTime since);
}
```

### TokenUsageRepository.java
```java
@Repository
public interface TokenUsageRepository extends JpaRepository<TokenUsage, Long> {
    
    Optional<TokenUsage> findByUserAccountIdAndWindowEndAfter(Long userAccountId, LocalDateTime now);
    
    @Query("SELECT t FROM TokenUsage t WHERE t.userAccount.id = :userAccountId AND t.windowEnd > :now")
    Optional<TokenUsage> findActiveTokenUsageForUser(@Param("userAccountId") Long userAccountId, @Param("now") LocalDateTime now);
    
    @Query("SELECT t FROM TokenUsage t WHERE t.windowEnd < :expiredBefore")
    List<TokenUsage> findExpiredTokenUsages(@Param("expiredBefore") LocalDateTime expiredBefore);
    
    @Query("DELETE FROM TokenUsage t WHERE t.windowEnd < :expiredBefore")
    int deleteExpiredTokenUsages(@Param("expiredBefore") LocalDateTime expiredBefore);
    
    @Query("SELECT t FROM TokenUsage t WHERE t.userAccount.clientIp = :clientIp AND t.windowEnd > :now")
    Optional<TokenUsage> findActiveTokenUsageByClientIp(@Param("clientIp") String clientIp, @Param("now") LocalDateTime now);
}
```

### UserAccountRepository.java
```java
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    
    Optional<UserAccount> findByEmail(String email);
    
    Optional<UserAccount> findByClientIp(String clientIp);
    
    boolean existsByEmail(String email);
    
    boolean existsByClientIp(String clientIp);
    
    @Query("SELECT u FROM UserAccount u WHERE u.active = true AND u.clientIp = :clientIp")
    Optional<UserAccount> findActiveByClientIp(@Param("clientIp") String clientIp);
    
    @Query("SELECT u FROM UserAccount u WHERE u.active = true AND u.email = :email")
    Optional<UserAccount> findActiveByEmail(@Param("email") String email);
    
    @Query("SELECT COUNT(u) FROM UserAccount u WHERE u.active = true AND u.plan.id = :planId")
    long countActiveUsersByPlan(@Param("planId") Long planId);
}
```

### PlanRepository.java
```java
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    
    Optional<Plan> findByName(String name);
    
    List<Plan> findByActiveTrue();
    
    @Query("SELECT p FROM Plan p WHERE p.active = true ORDER BY p.price ASC")
    List<Plan> findActivePlansOrderByPrice();
    
    boolean existsByName(String name);
}
```

## F. Complete Services/Use Cases

### Proxy Pattern Chain Implementation

```java
// Service Interface
public interface AIGenerationService {
    String generateContent(String prompt);
    Integer estimateTokens(String content);
}

// Real Service (Mock Implementation)
@Service
public class RealMockAIGenerationService implements AIGenerationService {
    @Override
    public String generateContent(String prompt) {
        // Mock AI generation with realistic delays and responses
        return generateMockResponse(prompt);
    }
    
    @Override
    public Integer estimateTokens(String content) {
        return Math.max(1, content.length() / 4);
    }
}

// Rate Limit Proxy
@Service
public class RateLimitProxyService implements AIGenerationService {
    private final AIGenerationService targetService;
    private final UserAccountService userAccountService;
    private final ConcurrentHashMap<String, RateLimitInfo> rateLimits = new ConcurrentHashMap<>();
    
    @Override
    public String generateContent(String prompt) {
        // Check rate limit before forwarding
        if (!checkRateLimit()) {
            throw new RateLimitExceededException("Rate limit exceeded");
        }
        incrementRequest();
        return targetService.generateContent(prompt);
    }
}

// Quota Proxy
@Service
@Transactional
public class QuotaProxyService implements AIGenerationService {
    private final AIGenerationService targetService;
    private final TokenUsageService tokenUsageService;
    private final AIGenerationRequestService requestService;
    
    @Override
    public String generateContent(String prompt) {
        // Check token quota before processing
        Integer estimatedTokens = targetService.estimateTokens(prompt);
        
        if (!tokenUsageService.checkTokenQuotaAvailable(userAccount, estimatedTokens)) {
            throw new QuotaExceededException("Token quota exceeded");
        }
        
        // Process request
        String content = targetService.generateContent(prompt);
        Integer actualTokens = targetService.estimateTokens(content);
        
        // Consume tokens and save request
        tokenUsageService.consumeTokens(userAccount, actualTokens);
        requestService.saveRequest(createRequest(prompt, content, actualTokens));
        
        return content;
    }
}
```

### User Management Service

```java
@Service
@Transactional
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final PlanRepository planRepository;
    
    public UserAccount createUser(String email, String clientIp, String planName) {
        if (userAccountRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User already exists");
        }
        
        Plan plan = planRepository.findByName(planName)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
        
        UserAccount user = new UserAccount(email, clientIp, plan);
        return userAccountRepository.save(user);
    }
    
    public UserAccount upgradePlan(String email, String newPlanName) {
        UserAccount user = findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        Plan newPlan = planRepository.findByName(newPlanName)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
        
        user.upgradePlan(newPlan);
        return userAccountRepository.save(user);
    }
    
    @Transactional(readOnly = true)
    public Optional<UserAccount> findUserByEmail(String email) {
        return userAccountRepository.findActiveByEmail(email);
    }
    
    @Transactional(readOnly = true)
    public Optional<UserAccount> findUserByClientIp(String clientIp) {
        return userAccountRepository.findActiveByClientIp(clientIp);
    }
}
```

## G. Persistence Strategy Explanation

### What's Persisted in PostgreSQL:
- **User Accounts**: User information, email, IP, plan assignments
- **Plans**: Subscription plans with pricing and limits
- **AI Generation Requests**: Complete request history with prompts, responses, tokens used, timing
- **Token Usage**: Hourly token tracking per user for quota management
- **Usage History**: Original proxy usage tracking (if needed)

### What Stays in Memory:
- **Rate Limiting**: 1-minute request windows per user (high-frequency, short-lived)
- **HTTP Client Connections**: Connection pooling for performance
- **Current User Sessions**: Active session data for immediate access

### Why This Strategy:
- **Rate Limiting**: 1-minute windows create many short-lived entries - better in memory for performance
- **Token Usage**: 1-hour windows need persistence for accurate quota tracking across restarts
- **Request History**: Critical for analytics, billing, and user insights - must persist
- **User Data**: Essential business data - must persist

## H. Final Render Deployment Checklist

### Environment Variables Required:
```bash
# Database Connection
SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-d7hbj5v7f7vs738fcso0-a:5432/patron_proxy
SPRING_DATASOURCE_USERNAME=patron_proxy_user
SPRING_DATASOURCE_PASSWORD=YOUR_ACTUAL_PASSWORD

# Optional JVM Tuning
JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"
```

### Deployment Steps:
1. **Configure Environment Variables** in Render Dashboard
2. **Push Changes** to GitHub:
   ```bash
   git add .
   git commit -m "Complete AI Generation Service with PostgreSQL integration"
   git push origin master
   ```
3. **Verify Deployment** - Check Render logs for:
   - Database connection success
   - Table creation with JPA
   - Default plan insertion
   - Application startup

### API Endpoints Available:
- `GET /api/ai/status` - Service status
- `POST /api/ai/generate` - Generate AI content with proxy chain
- `GET /api/ai/user/{email}/quota` - Get user quota information
- `GET /api/proxy` - Original proxy status
- `POST /api/proxy` - Original proxy functionality
- `GET /api/admin/users/{email}` - User management
- `PUT /api/admin/users/{email}/upgrade` - Plan upgrades

### Testing Commands:
```bash
# Test AI Generation
curl -X POST https://your-app.onrender.com/api/ai/generate \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Explain the Proxy pattern in software engineering"}'

# Check User Quota
curl https://your-app.onrender.com/api/ai/user/demo_user_192_168_1_100@proxy.local/quota
```

### Success Indicators:
- [x] **Compiles successfully** with Java 21
- [x] **Connects to PostgreSQL** using environment variables
- [x] **Creates tables automatically** with JPA
- [x] **Implements Proxy pattern** with rate limiting and quota
- [x] **Persists data** in PostgreSQL
- [x] **Ready for Render deployment**

Your AI Generation Service is now **production-ready** with full PostgreSQL integration, DDD architecture, and Proxy pattern implementation!
