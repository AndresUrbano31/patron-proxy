# Proxy Pattern Service - PostgreSQL Integration

## Database Integration Complete

### Architecture Overview

Your Spring Boot application now integrates with PostgreSQL following DDD pragmático architecture:

#### **Domain Layer (Entities)**
- **Plan**: Subscription plans with rate limits and quotas
- **UserAccount**: User accounts with associated plans
- **UsageHistory**: Request history tracking
- **QuotaUsage**: Persistent quota tracking per user

#### **Application Layer (Services)**
- **ProxyService**: Main proxy logic with database persistence
- **UserAccountService**: User management and plan upgrades
- **RateLimitService**: In-memory rate limiting (performance)
- **QuotaService**: Persistent quota management
- **UsageHistoryService**: Usage tracking and analytics

#### **Infrastructure Layer (Repositories)**
- **PlanRepository**: Spring Data JPA for plans
- **UserAccountRepository**: User account persistence
- **UsageHistoryRepository**: Usage history queries
- **QuotaUsageRepository**: Quota tracking persistence

#### **Presentation Layer (Controllers)**
- **ProxyController**: Main proxy endpoints
- **AdminController**: User management and analytics

---

## Database Schema

### Tables Created Automatically

```sql
-- Plans table
CREATE TABLE plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT NOT NULL,
    max_requests_per_minute INTEGER NOT NULL,
    max_bytes_per_hour BIGINT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- User accounts table
CREATE TABLE user_accounts (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    client_ip VARCHAR(255) NOT NULL,
    plan_id BIGINT NOT NULL REFERENCES plans(id),
    active BOOLEAN NOT NULL,
    plan_changed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Usage history table
CREATE TABLE usage_history (
    id BIGSERIAL PRIMARY KEY,
    user_account_id BIGINT NOT NULL REFERENCES user_accounts(id),
    target_url TEXT NOT NULL,
    status_code INTEGER NOT NULL,
    response_size BIGINT NOT NULL,
    response_time BIGINT NOT NULL,
    success BOOLEAN NOT NULL,
    request_time TIMESTAMP NOT NULL,
    error_message TEXT
);

-- Quota usage table
CREATE TABLE quota_usage (
    id BIGSERIAL PRIMARY KEY,
    user_account_id BIGINT NOT NULL REFERENCES user_accounts(id),
    bytes_used BIGINT NOT NULL,
    max_bytes BIGINT NOT NULL,
    window_start TIMESTAMP NOT NULL,
    window_end TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

---

## Render Environment Variables

### Required Environment Variables

Set these in your Render service:

```bash
# Database Connection
SPRING_DATASOURCE_URL=postgresql://patron_proxy_user:YOUR_PASSWORD@dpg-d7hbj5v7f7vs738fcso0-a.oregon-region.render.com:5432/patron_proxy
SPRING_DATASOURCE_USERNAME=patron_proxy_user
SPRING_DATASOURCE_PASSWORD=YOUR_ACTUAL_PASSWORD

# Optional: JVM tuning
JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"
```

### Database URL Format

```
postgresql://username:password@hostname:port/database
```

Replace `YOUR_PASSWORD` with your actual PostgreSQL password.

---

## API Endpoints

### Proxy Endpoints
- `GET /api/proxy` - Service status
- `POST /api/proxy` - Make proxy request

### Admin Endpoints
- `POST /api/admin/users` - Create user
- `GET /api/admin/users/{email}` - Get user info
- `PUT /api/admin/users/{email}/upgrade` - Upgrade user plan
- `GET /api/admin/users/{email}/usage` - Get user usage history
- `GET /api/admin/stats/summary` - Get system statistics

---

## Persistence Strategy

### What's Persisted in PostgreSQL:
- **User accounts** and their plans
- **Usage history** for analytics
- **Quota usage** for accurate tracking
- **Plan definitions** and pricing

### What Stays in Memory:
- **Rate limiting** for performance (1-minute windows)
- **HTTP client connections** for efficiency

### Why This Strategy:
- **Rate limiting**: High-frequency, short-lived data - better in memory
- **Quota tracking**: Longer-term (1-hour windows) - needs persistence
- **Usage history**: Important for analytics and billing - must persist
- **User data**: Critical business data - must persist

---

## Default Plans

Automatically created on startup:

| Plan | Requests/Min | Bytes/Hour | Price |
|------|-------------|-----------|-------|
| Basic | 10 | 1MB | $0.00 |
| Standard | 50 | 10MB | $9.99 |
| Premium | 200 | 50MB | $29.99 |
| Enterprise | 1000 | 1GB | $99.99 |

---

## Deployment Steps

### 1. Update Environment Variables in Render

```bash
# In Render Dashboard > Your Service > Environment
SPRING_DATASOURCE_URL=postgresql://patron_proxy_user:YOUR_PASSWORD@dpg-d7hbj5v7f7vs738fcso0-a.oregon-region.render.com:5432/patron_proxy
SPRING_DATASOURCE_USERNAME=patron_proxy_user
SPRING_DATASOURCE_PASSWORD=YOUR_ACTUAL_PASSWORD
```

### 2. Deploy

```bash
git add .
git commit -m "Add PostgreSQL integration with DDD architecture"
git push origin master
```

### 3. Verify Deployment

Check Render logs for:
- Database connection success
- Table creation
- Default plan insertion

---

## Testing the Integration

### Create a User
```bash
curl -X POST https://your-app.onrender.com/api/admin/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "clientIp": "192.168.1.100",
    "planName": "Standard"
  }'
```

### Make Proxy Request
```bash
curl -X POST https://your-app.onrender.com/api/proxy \
  -H "Content-Type: application/json" \
  -d '{"url": "https://httpbin.org/json"}'
```

### Check Usage
```bash
curl https://your-app.onrender.com/api/admin/users/test@example.com/usage
```

---

## Performance Considerations

### Database Indexes
- Primary keys automatically indexed
- Consider adding indexes on frequently queried fields:
  - `user_accounts.email`
  - `user_accounts.client_ip`
  - `usage_history.request_time`
  - `quota_usage.window_end`

### Connection Pooling
- HikariCP configured with 20 max connections
- Optimized for Render's connection limits

### Caching Strategy
- Rate limiting in memory for performance
- Database for persistent data only

---

## Monitoring

### Health Checks
- `/actuator/health` - Application health
- Database connectivity included

### Metrics
- `/actuator/metrics` - Performance metrics
- Database connection pool metrics available

---

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check environment variables
   - Verify database is running
   - Check network connectivity

2. **Table Creation Errors**
   - Check PostgreSQL version compatibility
   - Verify user permissions

3. **Performance Issues**
   - Monitor connection pool usage
   - Check slow queries
   - Consider adding indexes

### Debug Mode
Set in application.properties:
```properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

---

## Architecture Benefits

### DDD Pragmático
- Clear separation of concerns
- Business logic in domain layer
- Technical details in infrastructure
- Clean API boundaries

### Scalability
- Stateless application design
- Database handles persistence
- Memory for performance-critical operations

### Maintainability
- Easy to add new features
- Clear testing boundaries
- Well-structured codebase

---

## Next Steps

1. **Deploy to Render** with environment variables
2. **Test database connectivity**
3. **Verify proxy functionality**
4. **Monitor performance metrics**
5. **Scale as needed**

Your application is now ready for production deployment with full PostgreSQL integration!
