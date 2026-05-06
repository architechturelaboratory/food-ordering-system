# Food Ordering System

## Docker Compose

Tüm altyapıyı ayağa kaldırmak için:

```bash
docker compose -f common.yml -f kafka_cluster.yml -f postgres.yml up -d
```

Altyapıyı kapatmak için:

```bash
docker compose -f common.yml -f kafka_cluster.yml -f postgres.yml down
```

> Komutlar `infrastructure/docker-compose/` dizininden çalıştırılmalıdır.

## PostgreSQL

- **Order Service:** `jdbc:postgresql://localhost:5432/postgres?currentSchema=order&binaryTransfer=true&reWriteBatchedInserts=true&stringtype=unspecified`
- **Payment Service:** `jdbc:postgresql://localhost:5432/postgres?currentSchema=payment&binaryTransfer=true&reWriteBatchedInserts=true&stringtype=unspecified`
- **Customer Service:** `jdbc:postgresql://localhost:5432/postgres?currentSchema=customer&binaryTransfer=true&reWriteBatchedInserts=true`
- **Restaurant Service:** `jdbc:postgresql://localhost:5432/postgres?currentSchema=restaurant&binaryTransfer=true&reWriteBatchedInserts=true&stringtype=unspecified`

> Username: `postgres` / Password: `admin`

## Kafka

- **Bootstrap Servers:** `localhost:19092,localhost:29092,localhost:39092`
- **Schema Registry:** `http://localhost:8081`
- **Kafka UI:** [http://localhost:9000](http://localhost:9000)

### Kafka Topics

- `payment-request`
- `payment-response`
- `restaurant-approval-request`
- `restaurant-approval-response`
- `customer`

## Service Endpoints

| Service | Port | Base URL |
|---------|------|----------|
| Order Service | 8181 | http://localhost:8181 |
| Payment Service | 8182 | http://localhost:8182 |
| Restaurant Service | 8183 | http://localhost:8183 |
| Customer Service | 8184 | http://localhost:8184 |

### Swagger UI

- **Customer Service:** http://localhost:8184/swagger-ui/index.html
- **Order Service:** http://localhost:8181/swagger-ui/index.html

## Docker Images

Servis Docker image'larını listelemek için:

```bash
docker images | grep "com.food.ordering.system"
```

Image naming convention: `${project.groupId}/${service.name}:${project.version}`

Örnek: `com.food.ordering.system/customer.service:1.0-SNAPSHOT`

Image build etmek için (proje root'undan):

```bash
mvn clean install -Pbuild-docker-image
```

## Environment Variables

Her servis için env dosyaları `local-environment/` dizininde bulunur:

- `global.env` — Ortak port ve URL tanımları
- `order-service.env`
- `payment-service.env`
- `customer-service.env`
- `restaurant-service.env`
