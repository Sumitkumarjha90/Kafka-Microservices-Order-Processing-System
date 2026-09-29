# Delivery Service

This service implements the Delivery Service requirement from the Kafka Microservices
Order Processing System.

## Requirement implemented

Payment Service publishes:

`PAYMENT_SUCCESS` -> Kafka topic `payment-success`

Delivery Service:

1. Consumes successful payment events.
2. Creates a delivery.
3. Generates a tracking number.
4. Stores delivery information in MySQL.
5. Publishes `DELIVERY_CREATED` to `delivery-created`.
6. Does not create a delivery for failed payments.
7. Uses eventId and orderId checks for idempotency/duplicate protection.

The required delivery statuses are:

- CREATED
- IN_TRANSIT
- OUT_FOR_DELIVERY
- DELIVERED
- CANCELLED

This implementation creates the initial status as `CREATED`, as required by the
project specification.

## Dependencies

- Spring Boot
- Spring Web
- Spring Data JPA
- MySQL Driver
- Spring Kafka
- Jackson Databind

## Database

```sql
CREATE DATABASE delivery_db;
```

Update `src/main/resources/application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

## Kafka topics

The requirement specifies these minimum topics:

- order-created
- payment-success
- payment-failed
- delivery-created

This service consumes:

`payment-success`

This service publishes:

`delivery-created`

Create the input topic if it does not already exist:

```bash
kafka-topics --bootstrap-server localhost:9092   --create --topic payment-success   --partitions 3 --replication-factor 1
```

The application creates `delivery-created` with 3 partitions automatically.

## Consumer group

```text
delivery-service-group
```

The consumer uses `orderId` as the Kafka message key in the producer of
`delivery-created`, which supports partition-based ordering for the same order.

## Expected PAYMENT_SUCCESS input

```json
{
  "eventId": "PAY-10001",
  "eventType": "PAYMENT_SUCCESS",
  "orderId": 1001,
  "customerId": 101,
  "amount": 75000,
  "paymentId": "TXN-90001",
  "paymentMethod": "UPI",
  "paymentStatus": "SUCCESS",
  "deliveryAddress": "Bangalore"
}
```

Note: `deliveryAddress` is required by the Delivery Service, while the Payment
Service event shown in the requirement does not list it. Therefore this service's
event DTO supports it as an additional field. The upstream Payment Service must
include the address if Delivery Service is expected to populate it.

## Output DELIVERY_CREATED

```json
{
  "eventId": "DEL-1001",
  "eventType": "DELIVERY_CREATED",
  "orderId": 1001,
  "customerId": 101,
  "trackingNumber": "TRK-50001",
  "deliveryAddress": "Bangalore",
  "deliveryStatus": "CREATED"
}
```

## Run

```bash
mvn clean install
mvn spring-boot:run
```

## Important design note

The project requirement asks for retries and a DLT for the overall Kafka project.
This Delivery Service keeps the consumer simple and focused on its required
business flow. The global project can add Spring Kafka retry/DLT configuration
when the team's retry/DLT strategy is finalized.
