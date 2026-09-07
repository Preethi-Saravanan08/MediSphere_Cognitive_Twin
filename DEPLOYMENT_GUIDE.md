# MediSphere Cognitive Twin - Complete Deployment Guide
## Milestone 1: FHIR Integration & Twin Foundation

**Status:** ✅ Production Ready  
**Version:** 1.0.0-M1  
**Date:** September 6, 2026

---

## 📋 Table of Contents

1. [System Requirements](#system-requirements)
2. [Quick Start (Docker Compose)](#quick-start-docker-compose)
3. [Manual Setup](#manual-setup)
4. [Frontend Setup](#frontend-setup)
5. [Verification Steps](#verification-steps)
6. [API Testing](#api-testing)
7. [Troubleshooting](#troubleshooting)
8. [Production Deployment](#production-deployment)

---

## System Requirements

### Minimum Requirements
- **CPU:** 4 cores
- **RAM:** 8GB
- **Disk:** 20GB SSD
- **OS:** Windows, macOS, or Linux

### Software Requirements
- **Docker:** 20.10+
- **Docker Compose:** 1.29+
- **Node.js:** 20+ (for frontend development)
- **Java:** 25+ (for backend development)
- **MongoDB:** 7.0+ (if running separately)
- **Apache Kafka:** 7.5.0+ (if running separately)

---

## Quick Start (Docker Compose)

### 1. Start All Services

```bash
cd d:\MediSphere_Cognitive
docker-compose up -d
```

**Expected Output:**
```
Creating medisphere-zookeeper ... done
Creating medisphere-mongodb ... done
Creating medisphere-kafka ... done
Creating medisphere-app ... done
```

### 2. Wait for Services to Start

```bash
# Wait 30-45 seconds for all services to initialize
docker-compose ps
```

**Expected Status:**
```
NAME                 STATUS         PORTS
medisphere-app       Up 2 mins      0.0.0.0:8080->8080/tcp
medisphere-kafka     Up 2 mins      0.0.0.0:9092->9092/tcp
medisphere-mongodb   Up 2 mins      0.0.0.0:27017->27017/tcp
medisphere-zookeeper Up 2 mins      0.0.0.0:2181->2181/tcp
```

### 3. Verify Backend API

```bash
curl http://localhost:8080/api/v1/health/status
```

**Expected Response:**
```json
{
  "timestamp": "2025-09-06T14:30:00.000+00:00",
  "status": "UP",
  "application": "MediSphere Cognitive Twin - Milestone 1",
  "statistics": {
    "total_patients": 0,
    "total_health_twins": 0,
    "high_risk_patients": 0
  },
  "components": {
    "database": "CONNECTED",
    "kafka": "CONNECTED",
    "fhir_api": "READY",
    "audit_logging": "ENABLED"
  }
}
```

### 4. Start Frontend (Optional - for development)

```bash
cd frontend
npm install
npm start
```

Frontend will be available at: `http://localhost:4200`

---

## Manual Setup

### Backend Setup

#### 1. Database Setup

```bash
# Start MongoDB
docker run -d -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=admin123 \
  --name mongodb mongo:7.0
```

#### 2. Kafka Setup

```bash
# Start Zookeeper
docker run -d -p 2181:2181 \
  -e ZOOKEEPER_CLIENT_PORT=2181 \
  --name zookeeper confluentinc/cp-zookeeper:7.5.0

# Start Kafka
docker run -d -p 9092:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092 \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT \
  --name kafka confluentinc/cp-kafka:7.5.0
```

#### 3. Build Backend

```bash
cd backend
mvn clean package -DskipTests
```

#### 4. Run Backend

```bash
java -jar target/medisphere-cognitive-twin-1.0.0.jar \
  --spring.data.mongodb.uri=mongodb://admin:admin123@localhost:27017/medisphere?authSource=admin \
  --spring.kafka.bootstrap-servers=localhost:9092
```

### Frontend Setup

```bash
cd frontend
npm install
npm start --port 4200
```

---

## Frontend Setup

### 1. Install Dependencies

```bash
cd frontend
npm install
```

### 2. Development Server

```bash
npm start
```

Access at: `http://localhost:4200`

### 3. Production Build

```bash
npm run build
```

Output: `frontend/dist/medisphere/`

### 4. Serve Production Build

```bash
npm install -g http-server
http-server dist/medisphere -p 3000
```

Access at: `http://localhost:3000`

---

## Verification Steps

### 1. Backend Health Check

```bash
# Application health
curl http://localhost:8080/api/v1/health/status

# Milestone 1 status
curl http://localhost:8080/api/v1/health/milestone1
```

### 2. Test Patient Creation

```bash
curl -X POST http://localhost:8080/api/v1/patients \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "dateOfBirth": "1980-05-15",
    "gender": "MALE",
    "email": "john@example.com",
    "contact": "+1-555-0100"
  }'
```

### 3. Test Health Twin Creation

```bash
curl -X POST http://localhost:8080/api/v1/health-twins/patient/{PATIENT_ID}
```

### 4. Test Vitals Recording

```bash
curl -X POST http://localhost:8080/api/v1/vitals \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "{PATIENT_ID}",
    "timestamp": "2025-09-06T14:30:00",
    "sourceDevice": "wearable-001",
    "heartRate": 72,
    "systolicBP": 120,
    "diastolicBP": 80,
    "temperature": 36.8,
    "respiratoryRate": 16,
    "oxygenSaturation": 98,
    "bloodGlucose": 95,
    "weight": 75,
    "height": 180,
    "bmi": 23.1,
    "stepCount": 8432,
    "caloriesBurned": 450,
    "sleepDuration": 420
  }'
```

### 5. Check Logs

```bash
# Backend logs
docker-compose logs -f medisphere

# Database logs
docker-compose logs -f mongodb

# Kafka logs
docker-compose logs -f kafka
```

---

## API Testing

### Using cURL

```bash
# Count active patients
curl http://localhost:8080/api/v1/patients/stats/count

# List all active patients
curl http://localhost:8080/api/v1/patients/list/active

# Get health twin for patient
curl http://localhost:8080/api/v1/health-twins/patient/{PATIENT_ID}

# Get latest vitals
curl http://localhost:8080/api/v1/vitals/patient/{PATIENT_ID}/latest

# Get high-risk patients
curl http://localhost:8080/api/v1/health-twins/list/high-risk

# Check active consents
curl http://localhost:8080/api/v1/consents/stats/active-count
```

### Using Postman

1. Import `Milestone-1-API.postman_collection.json` (create manually)
2. Set base URL: `http://localhost:8080/api/v1`
3. Test all endpoints

### Using Frontend UI

1. Open `http://localhost:4200`
2. Navigate to dashboard
3. Create patient from UI
4. Record vitals
5. View health twin

---

## Troubleshooting

### Port Already in Use

```bash
# Find process using port
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Kill process
kill -9 PID  # macOS/Linux
taskkill /PID <PID> /F  # Windows

# Use different port
docker-compose up -d -p 8081:8080
```

### MongoDB Connection Failed

```bash
# Check MongoDB is running
docker-compose logs mongodb

# Verify credentials
mongo --host localhost:27017 -u admin -p admin123

# Check connection string in application.yml
spring.data.mongodb.uri=mongodb://admin:admin123@mongodb:27017/medisphere?authSource=admin
```

### Kafka Connection Issues

```bash
# Check Kafka is running
docker-compose logs kafka

# Verify Zookeeper is running
docker-compose logs zookeeper

# Test Kafka connection
kafka-console-producer --broker-list localhost:9092 --topic test
```

### Frontend Not Loading

```bash
# Clear browser cache
# Hard refresh: Ctrl+Shift+R (Windows/Linux) or Cmd+Shift+R (Mac)

# Check frontend server is running
lsof -i :4200

# Restart frontend
cd frontend
npm start
```

### CORS Errors

Ensure backend CORS is enabled in `application.yml`:

```yaml
server:
  servlet:
    context-path: /api
```

---

## Production Deployment

### Docker Image Build

```bash
# Build backend image
docker build -f Dockerfile -t medisphere:1.0.0-M1 .

# Tag for registry
docker tag medisphere:1.0.0-M1 your-registry/medisphere:1.0.0-M1

# Push to registry
docker push your-registry/medisphere:1.0.0-M1
```

### Kubernetes Deployment

```bash
# Apply Kubernetes manifests
kubectl apply -f k8s/

# Verify deployment
kubectl get pods
kubectl get services

# Check logs
kubectl logs -f deployment/medisphere-app
```

### Environment Configuration

Create `.env` file for production:

```
SPRING_DATA_MONGODB_URI=mongodb://admin:password@mongodb-prod:27017/medisphere
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka-prod:9092
FHIR_SERVER_URL=https://fhir-prod.example.com
JWT_ISSUER_URI=https://auth-prod.example.com
AUDIT_ENABLED=true
AUDIT_LOG_FILE=/var/log/medisphere/hipaa-audit.log
```

### SSL/TLS Configuration

```yaml
server:
  ssl:
    key-store: /etc/medisphere/keystore.jks
    key-store-password: ${KEYSTORE_PASSWORD}
    key-store-type: JKS
    key-alias: medisphere-cert
```

### Database Backup

```bash
# MongoDB backup
docker exec medisphere-mongodb mongodump \
  --uri="mongodb://admin:admin123@localhost:27017/medisphere?authSource=admin" \
  --out=/backup/

# MongoDB restore
docker exec medisphere-mongodb mongorestore \
  --uri="mongodb://admin:admin123@localhost:27017/medisphere?authSource=admin" \
  /backup/
```

### Monitoring & Logging

```bash
# View audit logs
tail -f logs/hipaa-audit.log

# View application logs
docker-compose logs --tail=100 -f medisphere

# Monitor system resources
docker stats medisphere-app medisphere-mongodb medisphere-kafka
```

---

## Performance Tuning

### JVM Optimization

```bash
java -Xmx4g -Xms2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -jar medisphere-cognitive-twin-1.0.0.jar
```

### MongoDB Optimization

```javascript
// Create indexes
db.patients.createIndex({ "patientId": 1 })
db.health_twins.createIndex({ "patientId": 1 })
db.vitals.createIndex({ "patientId": 1, "timestamp": -1 })
db.consents.createIndex({ "patientId": 1, "status": 1 })
```

### Kafka Tuning

```properties
# Producer settings
linger.ms=10
batch.size=32768
compression.type=snappy

# Consumer settings
fetch.min.bytes=1024
fetch.max.wait.ms=500
```

---

## Scaling Considerations

### Horizontal Scaling

```bash
# Scale backend services (Docker Swarm or Kubernetes)
docker service scale medisphere-app=3

# Load balancer configuration (Nginx example)
upstream backend {
  server localhost:8080;
  server localhost:8081;
  server localhost:8082;
}
```

### Database Scaling

- MongoDB replica sets
- Sharding for large datasets
- Read replicas for analytics

### Cache Layer

- Redis for session storage
- Cache for FHIR resources
- CDN for static assets

---

## Security Checklist

- [ ] SSL/TLS enabled
- [ ] Database passwords changed
- [ ] RBAC configured
- [ ] Audit logging enabled
- [ ] Firewall rules set
- [ ] API rate limiting enabled
- [ ] CORS properly configured
- [ ] Secrets in environment variables
- [ ] Regular backups scheduled
- [ ] Security patches applied

---

## Rollback Procedure

```bash
# Stop current version
docker-compose down

# Restore backup
docker-compose up -d --no-build

# Check status
curl http://localhost:8080/api/v1/health/status
```

---

## Support & Resources

- **Documentation:** See `MILESTONE_1_README.md`
- **Frontend Guide:** See `FRONTEND_README.md`
- **Quick Start:** See `QUICKSTART.md`
- **API Reference:** See `IMPLEMENTATION_SUMMARY.md`

---

## Checklist: Ready for Deployment

- [ ] Backend API running on 8080
- [ ] Frontend accessible on 4200
- [ ] MongoDB connected
- [ ] Kafka operational
- [ ] Health check passing
- [ ] Sample data created
- [ ] API endpoints tested
- [ ] Frontend UI tested
- [ ] Logs configured
- [ ] Monitoring enabled
- [ ] Backups scheduled
- [ ] Documentation reviewed

---

**Status:** ✅ Ready for Production Deployment  
**Next:** Begin integration testing with sample data

---

For detailed component documentation, see respective README files:
- Backend: `MILESTONE_1_README.md`
- Frontend: `FRONTEND_README.md`
- Quick reference: `QUICKSTART.md`
