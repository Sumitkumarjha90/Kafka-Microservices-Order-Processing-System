# E-Commerce Order Processing System

A Spring Boot microservices-based e-commerce order processing system using **Java, Spring Boot, MySQL, Apache Kafka, and REST APIs**.

The system is divided into four main services:

1. **Order Service**
2. **Payment Service**
3. **Delivery Service**
4. **Notification Service**

Kafka is used for asynchronous communication between services.

---

## 1. Architecture

```text
                         +------------------+
                         |   Client / API   |
                         +--------+---------+
                                  |
                                  v
                         +------------------+
                         |   Order Service  |
                         |    Port: 8081    |
                         +--------+---------+
                                  |
                                  | ORDER_CREATED
                                  v
                         +------------------+
                         |      Kafka       |
                         +------------------+
                           |              |
                           |              |
                           v              v
                    +-------------+  +----------------+
                    |   Payment   |  | Notification   |
                    |   Service   |  |    Service     |
                    +------+------+  +----------------+
                           |
                           | PAYMENT_PROCESSED
                           v
                    +-------------+
                    |    Kafka    |
                    +------+------+
                           |
                           v
                    +-------------+
                    |  Delivery   |
                    |   Service   |
                    +------+------+
                           |
                           | DELIVERY_CREATED
                           v
                    +-------------+
                    |    Kafka    |
                    +------+------+
                           |
                           v
                    +----------------+
                    | Notification   |
                    |    Service     |
                    +----------------+
```

---

# 2. Services

## Order Service

Responsible for:

- Creating orders
- Storing order information
- Publishing `ORDER_CREATED` events
- Starting the order-processing flow

Typical responsibilities:

```text
Client
  |
  v
Order Service
  |
  +---- Save Order
  |
  +---- Publish ORDER_CREATED
```

Example event:

```json
{
  "eventId": "ORD-10001",
  "eventType": "ORDER_CREATED",
  "orderId": 1001,
  "customerId": 101,
  "amount": 2500.00,
  "deliveryAddress": "Bangalore"
}
```

---

# 3. Payment Service

The Payment Service consumes order events and processes payment.

Flow:

```text
ORDER_CREATED
      |
      v
Payment Service
      |
      +---- Process Payment
      |
      +---- SUCCESS / FAILED / PENDING
      |
      v
PAYMENT_PROCESSED
```

Example successful payment event:

```json
{
  "eventId": "PAY-10001",
  "eventType": "PAYMENT_PROCESSED",
  "orderId": 1001,
  "customerId": 101,
  "amount": 2500.00,
  "paymentId": "PAY-50001",
  "paymentMethod": "UPI",
  "paymentStatus": "SUCCESS",
  "reason": null
}
```

Payment statuses:

```text
SUCCESS
FAILED
PENDING
```

The Delivery Service should create a delivery only when the payment status is `SUCCESS`.

---

# 4. Delivery Service

The Delivery Service consumes successful payment events.

Requirement:

> Consume successful payment events, create a delivery, generate a tracking number, store delivery information and publish `DELIVERY_CREATED`.

Flow:

```text
PAYMENT_PROCESSED
        |
        v
Delivery Service
        |
        +---- Check payment status
        |
        +---- If SUCCESS
        |
        +---- Create Delivery
        |
        +---- Generate Tracking Number
        |
        +---- Save Delivery
        |
        +---- Publish DELIVERY_CREATED
```

Example:

```json
{
  "eventId": "DEL-10001",
  "eventType": "DELIVERY_CREATED",
  "orderId": 1001,
  "customerId": 101,
  "trackingNumber": "TRK-50001",
  "deliveryAddress": "Bangalore",
  "deliveryStatus": "CREATED"
}
```

Delivery statuses:

```text
CREATED
IN_TRANSIT
OUT_FOR_DELIVERY
DELIVERED
CANCELLED
```

Example tracking number:

```text
TRK-50001
```

---

# 5. Notification Service

The Notification Service listens to events from other services and prints/sends notifications.

It consumes:

```text
order-created
payment-processed
delivery-created
```

The Notification Service does not own the order, payment, or delivery business logic.

Its responsibility is to react to events and create notifications.

---

# 6. Complete End-to-End Flow

The complete application flow is:

```text
             Client
               |
               v
        +--------------+
        | Order Service|
        +------+-------+
               |
               | ORDER_CREATED
               v
             Kafka
               |
               +----------------------+
               |                      |
               v                      v
       +---------------+      +----------------+
       | Payment       |      | Notification   |
       | Service       |      | Service        |
       +-------+-------+      +----------------+
               |
               | PAYMENT_PROCESSED
               |
               v
             Kafka
               |
               v
       +---------------+
       | Delivery      |
       | Service       |
       +-------+-------+
               |
               | DELIVERY_CREATED
               |
               v
             Kafka
               |
               v
       +----------------+
       | Notification   |
       | Service        |
       +----------------+
```

---

# 7. Detailed Flow

## Step 1: Create Order

The client sends a request to the Order Service.

Example:

```http
POST /orders
```

Request:

```json
{
  "customerId": 101,
  "customerName": "Rahul",
  "productId": 501,
  "productName": "Laptop",
  "quantity": 1,
  "amount": 2500.00,
  "deliveryAddress": "Bangalore"
}
```

Order Service:

1. Receives request
2. Validates data
3. Saves order in database
4. Creates `ORDER_CREATED` event
5. Publishes event to Kafka

Topic:

```text
order-created
```

---

# 8. Payment Flow

Payment Service consumes:

```text
order-created
```

It receives the order information and processes payment.

If payment succeeds:

```text
paymentStatus = SUCCESS
```

Payment Service publishes:

```text
payment-processed
```

Example:

```json
{
  "eventId": "PAY-10001",
  "eventType": "PAYMENT_PROCESSED",
  "orderId": 1001,
  "customerId": 101,
  "amount": 2500.00,
  "paymentId": "PAY-50001",
  "paymentMethod": "UPI",
  "paymentStatus": "SUCCESS"
}
```

---

# 9. Delivery Flow

Delivery Service consumes:

```text
payment-processed
```

It checks:

```java
if ("SUCCESS".equalsIgnoreCase(event.getPaymentStatus())) {
    // create delivery
}
```

If payment is successful:

1. Create delivery
2. Generate tracking number
3. Set delivery status to `CREATED`
4. Store delivery in database
5. Create `DELIVERY_CREATED` event
6. Publish event

Topic:

```text
delivery-created
```

Example:

```json
{
  "eventId": "DEL-10001",
  "eventType": "DELIVERY_CREATED",
  "orderId": 1001,
  "customerId": 101,
  "trackingNumber": "TRK-50001",
  "deliveryAddress": "Bangalore",
  "deliveryStatus": "CREATED"
}
```

---

# 10. Notification Flow

Notification Service consumes three Kafka topics.

## Order notification

```text
order-created
```

It prints:

```text
Your order #1001 has been successfully created.
```

## Payment notification

```text
payment-processed
```

For successful payment:

```text
Payment successful for order #1001
```

For pending payment:

```text
Payment is pending for order #1001
```

For failed payment:

```text
Payment failed for order #1001
```

## Delivery notification

```text
delivery-created
```

It prints:

```text
Your order #1001 has been assigned for delivery.
Tracking Number: TRK-50001
```

---

# 11. Kafka Topics

The system uses these main Kafka topics:

| Topic | Producer | Consumers |
|---|---|---|
| `order-created` | Order Service | Payment Service, Notification Service |
| `payment-processed` | Payment Service | Delivery Service, Notification Service |
| `delivery-created` | Delivery Service | Notification Service |

---

# 12. Kafka Producer and Consumer

## Producer

A producer sends messages to Kafka.

Example:

```java
kafkaTemplate.send("payment-processed", event);
```

The service producing the event is the producer.

Example:

```text
Payment Service
      |
      | produce
      v
payment-processed
```

---

## Consumer

A consumer receives messages from Kafka.

Example:

```java
@KafkaListener(
    topics = "payment-processed",
    groupId = "delivery-group"
)
public void consumePaymentProcessed(String message) {
    // process message
}
```

The Delivery Service is a consumer of:

```text
payment-processed
```

---

# 13. Consumer Groups

Consumer groups allow Kafka to distribute messages among multiple instances.

Example:

```text
delivery-group
```

The Delivery Service uses:

```java
groupId = "delivery-group"
```

Notification Service uses:

```java
groupId = "notification-group"
```

Because they have different group IDs, both services can consume the same event independently.

Example:

```text
payment-processed
        |
        +------ delivery-group ------> Delivery Service
        |
        +------ notification-group --> Notification Service
```

---

# 14. Why Notification Service Uses ObjectMapper

The Kafka message is received as JSON text.

Example:

```json
{
  "orderId": 1001,
  "customerId": 101
}
```

Java needs to convert this JSON into a Java object.

Jackson `ObjectMapper` performs this conversion.

```java
PaymentProcessedEvent event =
        objectMapper.readValue(
            message,
            PaymentProcessedEvent.class
        );
```

The process is:

```text
Kafka JSON String
       |
       v
ObjectMapper
       |
       v
PaymentProcessedEvent Java Object
```

---

# 15. Database Responsibilities

Each service should normally own its own database/schema.

Example:

```text
Order Service
    |
    +---- order_db

Payment Service
    |
    +---- payment_db

Delivery Service
    |
    +---- delivery_db
```

This follows the microservice principle of keeping service data ownership separate.

---

# 16. Delivery Database

Example delivery information:

```text
delivery
--------------------------------
id
order_id
customer_id
tracking_number
delivery_address
delivery_status
created_at
```

Example record:

```text
1
1001
101
TRK-50001
Bangalore
CREATED
```

---

# 17. Delivery Status Lifecycle

A delivery can move through these states:

```text
CREATED
   |
   v
IN_TRANSIT
   |
   v
OUT_FOR_DELIVERY
   |
   v
DELIVERED
```

A delivery may also be:

```text
CREATED
   |
   v
CANCELLED
```

---

# 18. Important Microservice Concepts Used

This project demonstrates:

- Spring Boot
- Spring Kafka
- Kafka Producer
- Kafka Consumer
- Kafka Topics
- Consumer Groups
- Event-driven architecture
- Asynchronous communication
- REST APIs
- JSON
- Jackson ObjectMapper
- JPA/Hibernate
- MySQL
- Repository pattern
- Service layer
- Entity classes
- Event classes
- DTO/Event-based communication
- Microservice architecture

---

# 19. Why Kafka Is Used

Without Kafka, services could communicate directly:

```text
Order Service ---> Payment Service
Payment Service ---> Delivery Service
```

This creates tighter coupling.

With Kafka:

```text
Order Service
      |
      v
    Kafka
      |
      v
Payment Service
```

The services communicate through events.

Advantages include:

- Loose coupling
- Asynchronous communication
- Independent services
- Event-driven processing
- Better scalability
- Multiple consumers can receive the same event

---

# 20. Synchronous vs Asynchronous Communication

## Synchronous

Example:

```text
Order Service
      |
      | HTTP request
      v
Payment Service
      |
      | response
      v
Order Service
```

The caller waits for the response.

## Asynchronous

Example:

```text
Order Service
      |
      | publish event
      v
Kafka
      |
      v
Payment Service
```

The producer does not need to directly call the consumer.

---

# 21. Error Handling

Kafka consumers can encounter serialization/deserialization errors.

For example:

```text
SerializationException
RecordDeserializationException
```

Spring Kafka can use:

```text
ErrorHandlingDeserializer
```

for handling deserialization errors.

When using JSON events, the producer and consumer must also agree on the event structure and JSON serialization/deserialization configuration.

---

# 22. Common Kafka Event Flow

```text
EVENT PRODUCER
      |
      v
    Kafka
      |
      v
EVENT CONSUMER
      |
      v
Business Logic
      |
      v
Database / New Event
```

For this project:

```text
Order Service
    |
    | ORDER_CREATED
    v
Kafka
    |
    v
Payment Service
    |
    | PAYMENT_PROCESSED
    v
Kafka
    |
    v
Delivery Service
    |
    | DELIVERY_CREATED
    v
Kafka
    |
    v
Notification Service
```

---

# 23. Interview Explanation

A simple interview explanation:

> "I worked on an e-commerce order processing system based on microservices. We have Order, Payment, Delivery and Notification services. The Order Service creates an order and publishes an ORDER_CREATED event to Kafka. The Payment Service consumes that event, processes the payment and publishes a PAYMENT_PROCESSED event. The Delivery Service consumes successful payment events, creates a delivery, generates a tracking number, stores the delivery information and publishes a DELIVERY_CREATED event. The Notification Service consumes order, payment and delivery events and generates notifications. Kafka provides asynchronous communication and decouples the services."

---

# 24. Important Interview Questions

## Q1. Why did you use Kafka?

Kafka is used for asynchronous, event-driven communication between microservices.

---

## Q2. What is a Kafka topic?

A topic is a logical channel where Kafka stores events/messages.

Examples:

```text
order-created
payment-processed
delivery-created
```

---

## Q3. What is a producer?

A producer publishes messages/events to Kafka.

Example:

```text
Payment Service -> payment-processed
```

---

## Q4. What is a consumer?

A consumer reads messages/events from Kafka.

Example:

```text
Delivery Service <- payment-processed
```

---

## Q5. Why are consumer groups used?

Consumer groups allow Kafka to distribute messages among consumers and provide independent consumption between different services.

---

## Q6. What happens when payment fails?

The Payment Service publishes a payment event with:

```text
paymentStatus = FAILED
```

The Delivery Service should not create a delivery for a failed payment.

---

## Q7. How does Delivery Service know payment succeeded?

It consumes the `payment-processed` event and checks:

```java
"SUCCESS".equalsIgnoreCase(event.getPaymentStatus())
```

---

## Q8. How is the tracking number generated?

The Delivery Service generates a unique tracking number, for example:

```text
TRK-50001
```

and stores it with the delivery.

---

## Q9. Why does Notification Service use ObjectMapper?

Kafka provides the event as JSON. `ObjectMapper` converts the JSON message into the corresponding Java event object.

---

## Q10. What is event-driven architecture?

Instead of services directly calling each other, one service publishes an event and other services react to that event.

---

## Q11. What happens if Notification Service is down?

Kafka retains the event according to its configuration. When the Notification Service becomes available again, its consumer can continue consuming events according to its consumer-group offset.

---

## Q12. What happens if Delivery Service receives a failed payment?

It checks the payment status and does not create a delivery.

---

## Q13. What is loose coupling?

A service does not need to know the internal implementation of another service. It communicates through defined events.

---

# 25. Example Complete Scenario

Customer places an order:

```text
Order ID = 1001
Customer ID = 101
Amount = 2500
Address = Bangalore
```

### Step 1

Order Service stores:

```text
Order 1001
```

and publishes:

```text
ORDER_CREATED
```

### Step 2

Payment Service receives:

```text
ORDER_CREATED
```

and processes payment.

Result:

```text
SUCCESS
```

It publishes:

```text
PAYMENT_PROCESSED
```

### Step 3

Delivery Service receives:

```text
PAYMENT_PROCESSED
```

Checks:

```text
SUCCESS
```

Creates:

```text
Tracking Number = TRK-50001
Status = CREATED
```

Stores delivery and publishes:

```text
DELIVERY_CREATED
```

### Step 4

Notification Service receives:

```text
DELIVERY_CREATED
```

and produces:

```text
Your order #1001 has been assigned for delivery.
Tracking Number: TRK-50001
```

---

# 26. Project Summary

```text
                    E-COMMERCE SYSTEM
                           |
       +-------------------+-------------------+
       |                   |                   |
       v                   v                   v
     Order              Payment             Delivery
    Service             Service              Service
       |                   |                   |
       +-------- Kafka ----+-------- Kafka ----+
                           |
                           v
                    Notification
                       Service
```

The main business flow is:

```text
CREATE ORDER
     ↓
ORDER_CREATED
     ↓
PROCESS PAYMENT
     ↓
PAYMENT_PROCESSED
     ↓
CHECK PAYMENT SUCCESS
     ↓
CREATE DELIVERY
     ↓
GENERATE TRACKING NUMBER
     ↓
SAVE DELIVERY
     ↓
DELIVERY_CREATED
     ↓
SEND NOTIFICATION
```

---

## Technology Stack

- Java
- Spring Boot
- Spring Kafka
- Apache Kafka
- MySQL
- Spring Data JPA
- Hibernate
- Jackson
- Maven
- REST API
- Microservices Architecture
- Event-Driven Architecture

---

## Services Summary

| Service | Main Responsibility | Produces | Consumes |
|---|---|---|---|
| Order Service | Create and store orders | `order-created` | REST request |
| Payment Service | Process payments | `payment-processed` | `order-created` |
| Delivery Service | Create delivery and tracking number | `delivery-created` | `payment-processed` |
| Notification Service | Generate notifications | None | `order-created`, `payment-processed`, `delivery-created` |

---

## Final Business Flow

```text
Client
  |
  v
Order Service
  |
  | order-created
  v
Kafka
  |
  +----------------------+
  |                      |
  v                      v
Payment Service      Notification Service
  |
  | payment-processed
  v
Kafka
  |
  +----------------------+
  |                      |
  v                      v
Delivery Service     Notification Service
  |
  | delivery-created
  v
Kafka
  |
  v
Notification Service
```

This represents the complete event-driven order processing flow of the project.
