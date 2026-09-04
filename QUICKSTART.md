# MediSphere Quickstart Guide

Get MediSphere running in 5 minutes with Docker Compose.

## Prerequisites

- Docker Desktop (includes Docker & Docker Compose)
- 4GB RAM available
- Ports 8080, 27017, 9092 available

## Option 1: Docker Compose (Fastest)

### 1. Start Services

```bash
# Clone and navigate to project
git clone <repo-url>
cd medisphere

# Start all services
docker-compose up -d

# Verify services are running
docker-compose ps
```

Expected output:
```
NAME                    STATUS
medisphere-mongodb      Up
medisphere-zookeeper    Up
medisphere-kafka        Up
medisphere-backend      Up
```

### 2. Test the API

```bash
# Health check
curl http://localhost:8080/api/health

# Get patient count
curl http://localhost:8080/api/v1/patients/count/active

# Expected response:
# { "success": true, "data": 0, "message": "..." }
```

### 3. Create a Patient

```bash
curl -X POST http://localhost:8080/api/v1/patients \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "PAT-001",
    "firstName": "John",
    "lastName": "Doe",
    "dateOfBirth": "1980-01-15",
    "gender": "M",
    "contact": "555-1234",
    "email": "john@example.com"
  }'
```

### 4. Grant Consent

```bash
curl -X POST http://localhost:8080/api/v1/consent/PAT-001/grant \
  -H "Content-Type: application/json" \
  -d '{"reason": "Patient provided written consent"}'
```

### 5. Monitor Logs

```bash
# Watch application logs
docker-compose logs -f medisphere-backend

# Watch MongoDB logs
docker-compose logs -f mongodb

# Watch Kafka logs
docker-compose logs -f kafka
```

### 6. Stop Services

```bash
docker-compose down

# To also remove volumes (data)
docker-compose down -v
```

---

## Option 2: Local Development

### Prerequisites

- Java 25+
- Maven 3.9+
- MongoDB 7.0+
- Apache Kafka 7.5+

### 1. Start Dependencies

```bash
# Start MongoDB
docker run -d --name mongodb -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=medisphere123 \
  mongo:7.0

# Start Kafka
docker run -d --name kafka -p 9092:9092 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  confluentinc/cp-kafka:7.5.0
```

### 2. Build & Run Application

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run

# Or run the JAR directly
java -jar target/medisphere-cognitive-twin-1.0.0.jar
```

The application will start on `http://localhost:8080/api`

---

## Common API Calls

### Patients

```bash
# Get all patients
curl http://localhost:8080/api/v1/patients

# Get specific patient
curl http://localhost:8080/api/v1/patients/{patientId}

# Get patients by status
curl http://localhost:8080/api/v1/patients/status/ACTIVE

# Update patient
curl -X PUT http://localhost:8080/api/v1/patients/{patientId} \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","lastName":"Smith"}'
```

### Health Twins

```bash
# Get twin for patient
curl http://localhost:8080/api/v1/twins/patient/{patientId}

# Get validated twins
curl http://localhost:8080/api/v1/twins/ready-for-prediction

# Get completeness stats
curl http://localhost:8080/api/v1/twins/completeness/stats
```

### Consent

```bash
# Grant consent
curl -X POST http://localhost:8080/api/v1/consent/{patientId}/grant \
  -H "Content-Type: application/json" \
  -d '{"reason":"Consent granted"}'

# Get consent status
curl http://localhost:8080/api/v1/consent/{patientId}/status

# Get consented count
curl http://localhost:8080/api/v1/consent/count/consented
```

---

## Database Access

### Connect to MongoDB

```bash
# Using Docker
docker exec -it medisphere-mongodb mongosh -u admin -p medisphere123

# In MongoDB shell
use medisphere
db.patients.find().limit(5)
db.health_twins.find().limit(5)
db.vitals.find().limit(5)
```

### MongoDB Collections

- `patients` - Patient records
- `health_twins` - Digital health twins
- `vitals` - Vital signs data
- `lab_results` - Laboratory results
- `fhir_resources` - FHIR resources cache

---

## File Locations

### Logs

```bash
# View logs in container
docker logs medisphere-backend

# Application logs
logs/medisphere.log

# HIPAA audit logs
logs/hipaa-audit.log
```

### Configuration

- `src/main/resources/application.yml` - Main configuration
- `.env.example` - Environment variables template
- `.gitignore` - Git ignore patterns

---

## Troubleshooting

### Services won't start

```bash
# Check port conflicts
lsof -i :8080
lsof -i :27017
lsof -i :9092

# Force stop conflicting processes (macOS)
killall java
```

### MongoDB connection error

```bash
# Check MongoDB is running
docker ps | grep mongodb

# Check MongoDB logs
docker logs medisphere-mongodb

# Verify credentials
docker exec medisphere-mongodb mongosh -u admin -p medisphere123 --eval "db.adminCommand('ping')"
```

### Kafka connection error

```bash
# Check Kafka is running
docker ps | grep kafka

# List topics
docker exec medisphere-kafka kafka-topics.sh --list --bootstrap-server localhost:9092

# Check topic details
docker exec medisphere-kafka kafka-topics.sh --describe --topic medisphere-vitals --bootstrap-server localhost:9092
```

### Application errors

```bash
# Check application logs
docker logs medisphere-backend

# Rebuild and restart
docker-compose down
docker-compose up -d --build

# Full reset
docker-compose down -v
docker-compose up -d
```

---

## Next Steps

1. **Read the full README**: `README.md` - Detailed documentation
2. **Review the specification**: `.kiro/specs/milestone-1-fhir-integration.md`
3. **Explore API**: Access Swagger UI at `/api/swagger-ui.html` (when implemented)
4. **Integrate FHIR**: Connect to your EHR system for real data
5. **Load test data**: Use provided scripts in `scripts/` directory

---

## Support & Issues

- Check logs for errors
- Review README.md for detailed troubleshooting
- Check Milestone 1 spec for requirements
- Monitor application health: `http://localhost:8080/api/health`

Happy developing! 🚀
