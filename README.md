# Fraud Detector Service

A high-performance enterprise microservice built with **Spring Boot 3.3.4** and **Java 21** for automated fraud detection, event streaming, document generation, and intelligent audit tracking.

---

## Tech Stack & Architecture

- **Core Framework:** Spring Boot `3.3.4` (Java 21)
- **Database (Audit Logs):** MongoDB 7.0
- **Cache Layer:** Redis 7 (Alpine)
- **Event Streaming:** Apache Kafka (Confluent 7.6.1) & Zookeeper
- **Document Storage:** MinIO (S3 Compatible API)
- **PDF Rendering:** OpenHTMLtoPDF Box
- **API Documentation:** SpringDoc OpenAPI 3 (Swagger UI) `http://${HOST_NAME}:8081/fraud-service/swagger-ui/index.html#`
- **Build System:** Maven wrapper (`mvnw`)
- **Containerization & CI/CD:** Docker, Docker Compose, Jenkins, Kubernetes

---

## Environment Profiles

The service supports two distinct runtime environments:
* **Local (Host):**
* **Profile:** `default` / `local`
* **Context Path:** `/fraud-engine`
* **Port:** `8081`
* **MongoDB:** `localhost:27017`
* **Redis Host:** `localhost:6379`
* **Kafka Bootstrap:** `localhost:9092`
* **MinIO Endpoint:** `localhost:9005`


* **Docker Stack:**
* **Profile:** `docker`
* **Context Path:** `/fraud-service`
* **Port:** `8081`
* **MongoDB:** `mongodb:27017`
* **Redis Host:** `redis-service:6379`
* **Kafka Bootstrap:** `kafka-service:29092`
* **MinIO Endpoint:** `minio:9005`
---

## Getting Started
 
### Prerequisites
- **Java 21 JDK** or higher
- **Maven 3.8+** (or use `./mvnw`)
- **Docker Desktop** & **Docker Compose**

---

## Running Locally (IDE / Host Machine)

Use this setup when running the application on your host machine while backing services run locally or via container ports.

### A. Prerequisites / Infrastructure Setup
Ensure MongoDB, Redis, Kafka, and MinIO are running locally or exposed to `localhost`.

### B. Build the Application
```
./mvnw clean package -DskipTests