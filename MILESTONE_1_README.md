# MediSphere Cognitive Twin - Milestone 1
## FHIR Integration & Twin Foundation

**Timeline:** Weeks 1-2  
**Status:** Development  
**Version:** 1.0.0-M1

---

## Project Overview

This milestone implements the core foundation for MediSphere:
- <cite index="1-19,1-20,1-21">Implements FHIR API integration with EHR systems, creates digital health twins in MongoDB, and builds a patient 360 dashboard. Establishes Kafka streaming for real-time vitals from wearables. Implements HIPAA-compliant consent management.</cite>

**Expected Outcomes:**
- <cite index="1-22,1-23,1-24">Patient 360 Dashboard: 1,247 patients onboarded. 2.4M FHIR resources synced. Digital twin shows 3D body with risk heatmap.</cite>

---

## Technology Stack

- **Backend:** Java 25, Spring Boot 4
- **Database:** MongoDB (Time-Series Store)
- **Message Queue:** Apache Kafka
- **Healthcare:** FHIR R4 APIs, SMART on FHIR
- **Security:** OAuth2, HIPAA Vault
- **Deployment:** Docker, Kubernetes

---

## Project Structure

```
medisphere-cognitive-twin/
├── src/main/java/com/medisphere/
│   ├── domain/                    # Core entities
│   │   ├── Patient.java
│   │   ├── HealthTwin.java
│   │   ├── Vitals.java
│   │   ├── LabResult.java
│   │   ├── Consent.java
│   │   └── FHIRResource.java
│   ├── repository/                # Data access layer
│   │   ├── PatientRepository.java
│   │   ├── HealthTwinRepository.java
│   │   ├── VitalsRepository.java
│   │   ├── LabResultRepository.java
│   │   ├── ConsentRepository.java
│   │   └── FHIRResourceRepository.java
│   ├── service/                   # Business logic
│   │   ├── PatientService.java
│   │   ├── HealthTwinService.java
│   │   ├── VitalsService.java
│   │   ├── ConsentService.java
│   │   ├── FhirIntegrationService.java
│   │   └── AuditService.java
│   ├── controller/                # REST API endpoints
│   │   ├── PatientController.java
│   │   ├── HealthTwinController.java
│   │   ├── VitalsController.java
│   │   ├── ConsentController.java
│   │   └── HealthController.java
│   ├── config/                    # Configuration
│   │   ├── KafkaConfig.java
│   │   └── RestTemplateConfig.java
│   ├── kafka/                     # Message consumers
│   │   └── VitalsConsumer.java
│   └── MediSphereApplication.java # Entry point
├── resources/
│   └── application.yml            # Application configuration
├── docker-compose.yml             # Local development stack
├── pom.xml                        # Maven dependencies
└── .kiro/specs/
    └── milestone-1-fhir-integration.md
```

---

## Core Entities

<cite index="1-17">Core entities: Patient, HealthTwin, Vitals, LabResult, RiskPrediction, Careplan, Alert, Provider, FHIRResource, FLModel.</cite>

### 1. **Patient**
- Patient demographics and contact information
- FHIR integration tracking
- Consent and HIPAA acknowledgment
- Status management (ONBOARDING, ACTIVE, INACTIVE, ARCHIVED)

### 2. **HealthTwin**
- Digital representation of patient's health state
- Current vitals and recent lab results
- Risk scores for various conditions
- Active alerts and care information
- Data completeness tracking

### 3. **Vitals**
- Real-time vital signs from wearables
- Heart rate, BP, SpO2, temperature, glucose, etc.
- Data quality assessment and validation
- Sync status tracking
- Anomaly detection

### 4. **LabResult**
- Laboratory test results
- Normal/abnormal/critical interpretation
- FHIR Observation mapping
- Historical tracking

### 5. **Consent**
- Patient consent records (HIPAA, DATA_PROCESSING, AI_PREDICTION)
- Consent audit trail
- Expiration and revocation tracking
- Status management

### 6. **FHIRResource**
- Raw FHIR resource storage
- Sync status and validation tracking
- Mapping to patient entities
- Retry logic for failed syncs

---

## Key Modules

<cite index="1-22">Key Modules: FHIR R4 API integration, MongoDB patient twin store, SMART on FHIR authentication, Kafka vitals streaming, Patient 360 UI, Consent management</cite>

### 1. **FHIR R4 API Integration**
- `FhirIntegrationService`: Handles FHIR resource ingestion and synchronization
- Validates FHIR resources against R4 standards
- Manages retry logic for failed syncs
- Maps FHIR resources to patient entities

### 2. **MongoDB Patient Twin Store**
- `HealthTwinService`: Creates and manages digital health twins
- `PatientRepository`, `HealthTwinRepository`: Data persistence
- Stores complete patient health snapshots
- Enables historical analysis and trend detection

### 3. **SMART on FHIR Authentication**
- OAuth2 integration for EHR system access
- Secure token management
- HIPAA-compliant access control

### 4. **Kafka Vitals Streaming**
- `VitalsConsumer`: Consumes real-time vitals from wearables
- `KafkaConfig`: Topics, producers, and consumer configuration
- Scalable real-time data ingestion
- Event-driven architecture

### 5. **Consent Management**
- `ConsentService`: Manages patient consents
- HIPAA acknowledgment tracking
- Consent audit trail for compliance
- Status tracking (ACTIVE, REVOKED, EXPIRED)

### 6. **HIPAA Audit Logging**
- `AuditService`: Centralized audit logging
- Records all data access and modifications
- Compliance tracking
- Tamper-proof audit trail

---

## REST API Endpoints

### Patient Management
```
POST   /api/v1/patients                           # Create patient
GET    /api/v1/patients/{id}                      # Get patient
GET    /api/v1/patients/search/by-patient-id/{patientId}
PUT    /api/v1/patients/{id}                      # Update patient
GET    /api/v1/patients/list/active               # List active patients
GET    /api/v1/patients/list/with-consent         # List patients with consent
GET    /api/v1/patients/stats/count               # Count active patients
POST   /api/v1/patients/{id}/consent              # Update consent status
POST   /api/v1/patients/{id}/hipaa-acknowledgment # Update HIPAA status
```

### Health Twin Management
```
POST   /api/v1/health-twins/patient/{patientId}  # Create health twin
GET    /api/v1/health-twins/patient/{patientId}  # Get health twin
PUT    /api/v1/health-twins/{twinId}             # Update health twin
GET    /api/v1/health-twins/list/high-risk       # Get high-risk patients
GET    /api/v1/health-twins/list/completeness/{minCompleteness}
GET    /api/v1/health-twins/list/alerts          # Get twins with alerts
POST   /api/v1/health-twins/{twinId}/risk-score  # Update risk score
POST   /api/v1/health-twins/{twinId}/alert       # Add alert
GET    /api/v1/health-twins/stats/high-risk-count
GET    /api/v1/health-twins/stats/total-count
```

### Vitals Management
```
POST   /api/v1/vitals                            # Record vitals
GET    /api/v1/vitals/{id}                       # Get vitals
GET    /api/v1/vitals/patient/{patientId}/latest # Get latest vitals
GET    /api/v1/vitals/patient/{patientId}/range  # Get vitals by date range
GET    /api/v1/vitals/list/invalid               # Get invalid vitals
GET    /api/v1/vitals/list/pending-sync          # Get pending vitals
GET    /api/v1/vitals/patient/{patientId}/anomalous
GET    /api/v1/vitals/list/low-oxygen
POST   /api/v1/vitals/{id}/validate              # Validate vitals
GET    /api/v1/vitals/stats/count/{patientId}
GET    /api/v1/vitals/stats/pending-count
```

### Consent Management
```
POST   /api/v1/consents                          # Create consent
GET    /api/v1/consents/patient/{patientId}/type/{consentType}
GET    /api/v1/consents/patient/{patientId}      # Get all consents
GET    /api/v1/consents/list/active              # Get active consents
GET    /api/v1/consents/list/expired             # Get expired consents
GET    /api/v1/consents/check/active/{patientId}/{consentType}
POST   /api/v1/consents/{consentId}/revoke       # Revoke consent
POST   /api/v1/consents/{consentId}/verify       # Verify consent
POST   /api/v1/consents/{consentId}/hipaa-acknowledge
GET    /api/v1/consents/stats/active-count
GET    /api/v1/consents/stats/hipaa-acknowledged-count
```

### Health & Status
```
GET    /api/v1/health/status                    # Application health
GET    /api/v1/health/milestone1                # Milestone 1 status
```

---

## Configuration

### Application Configuration (`application.yml`)

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/medisphere
  
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: medisphere-vitals-consumer

audit:
  enabled: true
  log-file: logs/hipaa-audit.log
  retention-days: 2555

fhir:
  server-url: http://localhost:8081/fhir
  auth-enabled: true
```

### Environment Variables

```
SPRING_DATA_MONGODB_URI=mongodb://admin:admin123@mongodb:27017/medisphere?authSource=admin
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:29092
FHIR_SERVER_URL=http://fhir-server:8081/fhir
JWT_ISSUER_URI=http://localhost:8080
AUDIT_ENABLED=true
```

---

## Running Locally

### Option 1: Docker Compose (Recommended)

```bash
cd d:\MediSphere_Cognitive

# Start all services (MongoDB, Kafka, Zookeeper, MediSphere)
docker-compose up -d

# View logs
docker-compose logs -f medisphere

# Stop services
docker-compose down
```

### Option 2: Manual Setup

#### Prerequisites
- Java 25 or later
- Maven 3.9+
- MongoDB running locally
- Kafka running locally

#### Steps

```bash
# 1. Start MongoDB
docker run -d -p 27017:27017 --name mongodb mongo:7.0

# 2. Start Kafka and Zookeeper
docker-compose up -d zookeeper kafka

# 3. Build the application
mvn clean package

# 4. Run the application
java -jar target/medisphere-cognitive-twin-1.0.0.jar

# Application will be available at http://localhost:8080/api
```

---

## Testing the API

### 1. Create a Patient

```bash
curl -X POST http://localhost:8080/api/v1/patients \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "dateOfBirth": "1980-05-15",
    "gender": "MALE",
    "email": "john.doe@example.com",
    "contact": "+1-555-0123"
  }'
```

### 2. Create a Health Twin

```bash
curl -X POST http://localhost:8080/api/v1/health-twins/patient/{patientId}
```

### 3. Record Vitals

```bash
curl -X POST http://localhost:8080/api/v1/vitals \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "{patientId}",
    "timestamp": "2025-09-06T14:30:00",
    "sourceDevice": "wearable-001",
    "heartRate": 72,
    "systolicBP": 120,
    "diastolicBP": 80,
    "oxygenSaturation": 98,
    "temperature": 36.8,
    "bloodGlucose": 95
  }'
```

### 4. Create Consent

```bash
curl -X POST http://localhost:8080/api/v1/consents \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "{patientId}",
    "consentType": "HIPAA",
    "status": "ACTIVE",
    "dataCategories": ["VITALS", "LABS", "MEDICATIONS"],
    "purposes": ["TREATMENT", "CARE_COORDINATION"],
    "createdBy": "clinic-admin"
  }'
```

### 5. Check Application Health

```bash
curl http://localhost:8080/api/v1/health/status
curl http://localhost:8080/api/v1/health/milestone1
```

---

## Data Pipeline

<cite index="1-14">Data pipeline: Wearables + EHR + Labs → FHIR API → Kafka → MongoDB → TensorFlow Federated → Clinician Dashboard → Preventive Intervention.</cite>

```
┌─────────────────────────────────────────────────────────────────┐
│  Wearables + EHR + Labs                                         │
└────────────────┬────────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────────┐
│  FHIR API (FHIR R4 Resources)                                   │
└────────────────┬────────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────────┐
│  Apache Kafka (Event Streaming)                                 │
│  - medisphere-vitals (3 partitions)                              │
│  - medisphere-fhir-sync (2 partitions)                           │
│  - medisphere-vitals-alerts (2 partitions)                       │
└────────────────┬────────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────────┐
│  MongoDB (Twin Store & Time-Series)                             │
│  - patients collection                                           │
│  - health_twins collection                                       │
│  - vitals collection (time-series)                               │
│  - lab_results collection                                        │
│  - consents collection                                           │
│  - fhir_resources collection                                     │
└────────────────┬────────────────────────────────────────────────┘
                 │
                 ▼
    ┌─────────────────────────────────┐
    │  TensorFlow Federated (M2)      │
    │  Privacy-Preserving ML          │
    └─────────────────────────────────┘
                 │
                 ▼
    ┌─────────────────────────────────┐
    │  Clinician Dashboard (UI)       │
    │  Patient 360 View               │
    └─────────────────────────────────┘
                 │
                 ▼
    ┌─────────────────────────────────┐
    │  Preventive Intervention        │
    │  Care Plans & Alerts            │
    └─────────────────────────────────┘
```

---

## Validation Checklist

<cite index="1-25,1-26">Validation Screens: FHIR resource validation, HIPAA audit logging, Patient consent verification, Twin data completeness >95%, Vitals range validation, RBAC by provider/patient</cite>

- [ ] 1,247 patients successfully onboarded
- [ ] 2.4M FHIR resources synced
- [ ] Digital twin data completeness > 95%
- [ ] Patient 360 dashboard displays 3D body with risk heatmap
- [ ] FHIR resource validation passing
- [ ] HIPAA audit logging operational
- [ ] Patient consent verification working
- [ ] Vitals range validation in place
- [ ] RBAC functioning for all provider/patient roles
- [ ] All wearable data streaming through Kafka
- [ ] MongoDB storing patient twins correctly
- [ ] Consent audit trails complete

---

## Troubleshooting

### MongoDB Connection Issues
```
Error: MongoAuthenticationException
Solution: Ensure MongoDB credentials in application.yml match docker-compose.yml
```

### Kafka Connection Issues
```
Error: KafkaTimeoutException
Solution: Ensure Kafka and Zookeeper are running: docker-compose ps
```

### FHIR Integration Issues
```
Error: FHIR resource validation failed
Solution: Check FHIR server URL in application.yml
```

### Port Already in Use
```
Error: Address already in use
Solution: Change port in application.yml or kill process using the port
```

---

## Next Steps (Milestone 2)

- Deploy TensorFlow Federated for risk prediction
- Implement 91.4% accuracy CVD risk model
- Add SHAP explainability
- Set up federated learning across hospitals

---

## References

- FHIR R4 Specification: https://www.hl7.org/fhir/r4/
- SMART on FHIR: https://docs.smarthealthit.org/
- Apache Kafka: https://kafka.apache.org/
- MongoDB: https://www.mongodb.com/
- Spring Boot: https://spring.io/projects/spring-boot

---

**Status:** ✅ Milestone 1 Code Complete  
**Next Review:** After initial testing and validation
