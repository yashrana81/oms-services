# Order Management System

Microservices-based order management system with inventory tracking and async messaging via AWS SQS.

## Quick Start

### Option 1: Using Docker Images (No Clone Required)
```bash
# Run Inventory Service
docker run -d --name inventory-service -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://<host>:3306/order-management \
  -e SPRING_DATASOURCE_USERNAME=<username> \
  -e SPRING_DATASOURCE_PASSWORD=<password> \
  -e AWS_REGION=ap-south-1 \
  -e SQS_QUEUE_NAME=<your-sqs-queue-url> \
  yashrana81/inventory-service:latest

# Run Order Service
docker run -d --name order-service -p 8082:8082 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://<host>:3306/order-management \
  -e SPRING_DATASOURCE_USERNAME=<username> \
  -e SPRING_DATASOURCE_PASSWORD=<password> \
  -e AWS_REGION=ap-south-1 \
  -e SQS_QUEUE_NAME=<your-sqs-queue-url> \
  -e INVENTORY_SERVICE_URL=http://<inventory-host>:8081 \
  yashrana81/order-service:latest
```

### Option 2: Build from Source
1. **Setup Database:** Create MySQL database `order-management` and update credentials in `application.yml`
2. **Build Services:** `mvn clean package` in both `order-service` and `inventory-service`
3. **Run Services:** `java -jar target/<service-name>.jar` or build Docker images with provided Dockerfiles
4. **Access APIs:** Order Service on port 8082, Inventory Service on port 8081

## Documentation

- [API Documentation](API_DOCUMENTATION.md) - All endpoints with request/response examples
- [Database Schema](DATABASE_SCHEMA.md) - Complete DDL and table structure

