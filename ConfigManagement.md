# Configuration Management

This document describes the configuration management approach used in this project for environment variables and application YAML files.

## Overview

The project uses a **Kubernetes-native configuration management** approach where:
- Configuration is externalized from application code
- Container images are environment-agnostic
- Environment variables are injected at runtime
- No environment-specific configuration files (test/staging/prod) exist



### 1. Environment Variable Externalization

All hardcoded values in `application.yml` files have been replaced with environment variable placeholders:

**Before:**
```yaml
server:
  port: 8181

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres?currentSchema=order
    username: postgres
    password: admin

kafka-config:
  bootstrap-servers: localhost:19092, localhost:29092, localhost:39092
  schema-registry-url: http://localhost:8081
```

**After:**
```yaml
server:
  port: ${ORDER_SERVICE_PORT}

spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

kafka-config:
  bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS}
  schema-registry-url: ${SCHEMA_REGISTRY_URL}
```

### 2. Local Environment Configuration

Created `local-environment/` directory with environment variable files:

```
local-environment/
├── global.env              # Shared configuration across all services
├── customer-service.env    # Customer service specific configuration
├── order-service.env       # Order service specific configuration
├── payment-service.env     # Payment service specific configuration
└── restaurant-service.env  # Restaurant service specific configuration
```

**global.env** contains service URLs and ports:

```bash
ORDER_SERVICE_PORT=8181
ORDER_SERVICE_URL=http://localhost:8181
PAYMENT_SERVICE_PORT=8182
PAYMENT_SERVICE_URL=http://localhost:8182
RESTAURANT_SERVICE_PORT=8183
RESTAURANT_SERVICE_URL=http://localhost:8183
CUSTOMER_SERVICE_PORT=8184
CUSTOMER_SERVICE_URL=http://localhost:8184
```

Example from `order-service.env`:
```bash
LOG_LEVEL_COM_FOOD_ORDERING_SYSTEM=DEBUG
DB_URL=jdbc:postgresql://localhost:5432/postgres?currentSchema=order&binaryTransfer=true&reWriteBatchedInserts=true&stringtype=unspecified
DB_USERNAME=postgres
DB_PASSWORD=admin
KAFKA_BOOTSTRAP_SERVERS=localhost:19092,localhost:29092,localhost:39092
SCHEMA_REGISTRY_URL=http://localhost:8081
SQL_INIT_MODE=ALWAYS
```

### 3. IDE Run Configurations

Created `.run/` directory with IntelliJ IDEA run configurations:

- **All Services.run.xml** - Compound configuration to run all services together
- **CustomerServiceApplication.run.xml** - Customer service with env file loading
- **OrderServiceApplication.run.xml** - Order service with env file loading
- **PaymentServiceApplication.run.xml** - Payment service with env file loading
- **RestaurantServiceApplication.run.xml** - Restaurant service with env file loading

Each service configuration loads environment files in this order:
1. `local-environment/global.env` (shared config)
2. `local-environment/{service-name}.env` (service-specific config)

## Why This Approach?

- **Container images remain environment-agnostic**: No hardcoded values, same image works everywhere
- **Runtime configuration injection**: Config is injected via environment variables at container startup
- **Kubernetes-native**: Production uses ConfigMap/Secret instead of Spring Cloud Config
- **Single environment strategy**: No test/staging/prod config files to prevent accidental deployments
- **12-Factor App compliant**: Configuration is externalized from code

## Important Notes

- `local-environment/` directory is **not** included in container builds (excluded via `.dockerignore`)
- These files exist only for local development
- In production, Kubernetes ConfigMap and Secret will provide the environment variables
- `.run/` directory contains IDE-specific configurations and should be gitignored
