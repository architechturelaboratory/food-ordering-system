# Avro + Schema Registry Akışı

Güzel soru! Senin projenin koduna bakarak açıklayayım.

---

## 1. Avro schema tanımı (`kafka-model` modülü)

`infrastructure/kafka/kafka-model/src/main/resources/avro/payment_request.avsc` dosyasında şema tanımlı:

```json
{
  "type": "record",
  "name": "PaymentRequestAvroModel",
  "fields": [
    {"name": "orderId", "type": "string"},
    {"name": "price", "type": "string"}
  ]
}
```

Maven build sırasında `avro-maven-plugin` bu `.avsc` dosyasından Java class'ı üretiyor:

```text
PaymentRequestAvroModel.java
```

---

## 2. Producer schema'yı register eder (`kafka-producer` modülü)

`KafkaProducerConfig` içinde serializer olarak `KafkaAvroSerializer` tanımlı:

```java
props.put(
    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
    KafkaAvroSerializer.class
);

props.put("schema.registry.url", schemaRegistryUrl);
```

Producer ilk mesajı gönderdiğinde:

```text
KafkaAvroSerializer
```

otomatik olarak schema'yı Schema Registry'ye register eder:

```http
POST http://sr:8081/subjects/payment-request-value/versions
```

Body:

```json
{
  "schema": "..."
}
```

---

## 3. Consumer schema'yı okur (`kafka-consumer` modülü)

`KafkaConsumerConfig` içinde:

```java
props.put(
    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
    KafkaAvroDeserializer.class
);

props.put("schema.registry.url", schemaRegistryUrl);
```

Consumer mesajı alırken:

```text
KafkaAvroDeserializer
```

Schema Registry'den schema'yı çekip binary veriyi deserialize eder:

```http
GET http://sr:8081/subjects/payment-request-value/versions/latest
```

Sonrasında:

```text
Binary Kafka message
→ schema alınır
→ PaymentRequestAvroModel nesnesine dönüştürülür
```

---

# Genel Akış

```text
Order Service
  → PaymentRequestAvroModel oluştur
  → KafkaAvroSerializer
  → Schema Registry'ye register et (ilk seferinde)
  → Binary olarak Kafka'ya gönder

Payment Service
  → Kafka'dan binary mesaj al
  → KafkaAvroDeserializer
  → Schema Registry'den schema çek
  → PaymentRequestAvroModel nesnesine dönüştür
  → İşle
```

---

# Önemli Nokta

Yani sen aslında manuel olarak:

- schema upload etmiyorsun
- schema fetch etmiyorsun
- binary parse etmiyorsun

Bunları Confluent'ın serializer/deserializer class'ları otomatik yapıyor:

- `KafkaAvroSerializer`
- `KafkaAvroDeserializer`

Sen sadece:

```properties
schema.registry.url=http://sr:8081
```

ayarını doğru veriyorsun.

---

# Kubernetes Ortamında

Bizim K8s ortamında bu değer şöyle olacak:

```properties
schema.registry.url=http://sr:8081
```

Çünkü:

- `sr`
  → Kubernetes Service adı
- `8081`
  → Schema Registry portu

Bunu daha sonra:

- ConfigMap
- Deployment env variable
- Helm values

üzerinden vereceğiz.