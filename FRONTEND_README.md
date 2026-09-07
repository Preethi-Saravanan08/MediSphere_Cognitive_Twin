# MediSphere Cognitive Twin - Frontend (Angular 20)

## Patient Portal UI for Milestone 1

**Status:** ✅ Complete  
**Version:** 1.0.0-M1  
**Framework:** Angular 20  
**UI Library:** Bootstrap 5  
**Icons:** Font Awesome 6

---

## Features Implemented

### 📊 Dashboard
- Patient onboarding statistics
- Health twin count
- High-risk patient alerts
- FHIR resource synchronization status
- System component health checks
- Quick action buttons

### 👥 Patient Management
- List all active patients
- Search and filter patients
- Patient detail view
- Edit patient information
- Manage consent status
- HIPAA acknowledgment tracking

### ❤️ Health Twin Management
- Create digital health twins
- View 3D body model readiness
- Display current vital signs
- Risk score visualization
- Active alerts management
- Data completeness progress

### 📈 Vitals Tracking
- Record new vital signs
- Real-time vitals form
- Recent vitals history table
- Data quality indicators
- Anomaly detection indicators
- Validation status

### 📋 Consent Management
- HIPAA Privacy Notice
- Consent form with multiple types
- Audit trail of consents
- Consent acceptance workflow
- Download consent PDF
- Expiration date tracking

### 🔐 Authentication
- Login page with demo credentials
- SMART on FHIR OAuth2 integration
- User profile management
- Logout functionality

---

## Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   ├── navbar/
│   │   │   │   └── navbar.component.ts
│   │   │   └── sidebar/
│   │   │       └── sidebar.component.ts
│   │   ├── pages/
│   │   │   ├── dashboard/
│   │   │   │   └── dashboard.component.ts
│   │   │   ├── patients/
│   │   │   │   ├── patient-list.component.ts
│   │   │   │   └── patient-detail.component.ts
│   │   │   ├── health-twin/
│   │   │   │   └── health-twin.component.ts
│   │   │   ├── vitals/
│   │   │   │   └── vitals.component.ts
│   │   │   ├── consent/
│   │   │   │   └── consent.component.ts
│   │   │   └── login/
│   │   │       └── login.component.ts
│   │   ├── services/
│   │   │   ├── patient.service.ts
│   │   │   ├── health-twin.service.ts
│   │   │   └── vitals.service.ts
│   │   ├── app.component.ts
│   │   └── app.routes.ts
│   ├── styles.scss
│   ├── index.html
│   └── main.ts
├── package.json
├── tsconfig.json
└── angular.json
```

---

## Installation & Setup

### Prerequisites
- Node.js 20+ (LTS recommended)
- npm 10+
- Angular CLI 20

### Install Dependencies

```bash
cd frontend
npm install
```

### Development Server

```bash
npm start
```

Application will be available at `http://localhost:4200`

### Build for Production

```bash
npm run build
```

Output will be in `frontend/dist/medisphere/`

---

## API Integration

### Backend URL Configuration

The frontend connects to backend APIs at `http://localhost:8080/api/v1`

**Endpoints Used:**
- `GET/POST /patients` - Patient management
- `GET/POST /health-twins` - Digital twins
- `GET/POST /vitals` - Vital signs
- `GET/POST /consents` - Consent management
- `GET /health/status` - Application health
- `GET /health/milestone1` - Milestone 1 status

### API Services

**PatientService**
- `createPatient()`
- `getPatient()`
- `getAllActivePatients()`
- `updatePatient()`
- `updateConsent()`
- `updateHipaaStatus()`

**HealthTwinService**
- `createHealthTwin()`
- `getHealthTwin()`
- `updateHealthTwin()`
- `getHighRiskPatients()`
- `updateRiskScore()`
- `countHighRiskPatients()`

**VitalsService**
- `recordVitals()`
- `getLatestVitals()`
- `getVitalsByDateRange()`
- `getAnomalousVitals()`

---

## Routes

| Route | Component | Purpose |
|-------|-----------|---------|
| `/login` | LoginComponent | User authentication |
| `/dashboard` | DashboardComponent | Main dashboard |
| `/patients` | PatientListComponent | List all patients |
| `/patients/:id` | PatientDetailComponent | Patient detail view |
| `/health-twin/:patientId` | HealthTwinComponent | Digital health twin |
| `/vitals/:patientId` | VitalsComponent | Vitals recording & history |
| `/consent/:patientId` | ConsentComponent | Consent management |

---

## Components Overview

### Dashboard Component
- Real-time statistics from backend
- Milestone 1 completion checklist
- System status indicators
- Quick action buttons

### Patient List Component
- Searchable patient table
- Filter by status
- Patient detail/health twin navigation
- Responsive design

### Patient Detail Component
- Edit personal information
- Manage consent status
- HIPAA acknowledgment
- Patient status tracking

### Health Twin Component
- Data completeness progress
- Risk level indicator
- Current vital signs display
- Risk score visualization
- Active alerts display
- Twin metadata information

### Vitals Component
- New vitals recording form
- Recent vitals history table
- Data quality indicators
- Validation status
- Anomaly detection indicators

### Consent Component
- HIPAA Privacy Notice display
- Multi-type consent form
- Audit trail of consents
- Consent history table
- PDF download capability

### Login Component
- Username/password form
- Demo credentials display
- Milestone 1 status alert
- Responsive design

---

## Styling & Theme

### Color Scheme
- Primary: `#007bff` (Blue)
- Success: `#28a745` (Green)
- Warning: `#ffc107` (Yellow)
- Danger: `#dc3545` (Red)
- Info: `#17a2b8` (Cyan)

### Fonts
- Primary Font: Segoe UI, Tahoma, Geneva, Verdana, sans-serif
- Bootstrap Icons: Font Awesome 6

### Responsive Breakpoints
- Mobile: < 768px
- Tablet: 768px - 1024px
- Desktop: > 1024px

---

## Features & Capabilities

### Milestone 1 Implementation
✅ Patient 360 Dashboard  
✅ Patient onboarding interface  
✅ Health twin creation and visualization  
✅ Real-time vitals recording  
✅ Consent management interface  
✅ HIPAA compliance UI  
✅ Responsive design  
✅ API integration with backend  

### Data Display
✅ Real-time statistics  
✅ Risk score visualization  
✅ Alert management  
✅ Historical data tables  
✅ Progress indicators  
✅ Status badges  

### User Interactions
✅ Patient search and filter  
✅ Form validation  
✅ Consent acknowledgment  
✅ Data recording  
✅ Navigation  
✅ Quick actions  

---

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

---

## Performance Optimization

- Lazy loading for modules
- OnPush change detection
- Standalone components
- Tree-shakable services
- Production build optimization

---

## Testing

### Unit Tests
```bash
npm test
```

### Build Test
```bash
npm run build -- --configuration production
```

---

## Deployment

### Docker Deployment

```dockerfile
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist/medisphere /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### Build and Run

```bash
docker build -t medisphere-frontend .
docker run -p 3000:80 medisphere-frontend
```

---

## Environment Configuration

Create `.env` file for environment-specific configuration:

```
API_URL=http://localhost:8080/api/v1
AUTH_ENABLED=true
ENVIRONMENT=development
LOG_LEVEL=debug
```

---

## Troubleshooting

### Port Already in Use
```bash
ng serve --port 4201
```

### CORS Issues
Ensure backend has CORS enabled for `http://localhost:4200`

### API Connection Failed
Check backend is running at `http://localhost:8080`

### Build Errors
```bash
rm -rf node_modules
npm install
npm run build
```

---

## Next Steps (Milestone 2)

- AI Risk Prediction visualization
- Advanced 3D body model rendering
- Real-time alert notifications
- Chart.js integration for trends
- Care plan management UI
- Provider collaboration features
- Mobile app (React Native)

---

## File Manifest

**Components:** 7 files  
**Services:** 3 files  
**Pages:** 6 files  
**Configuration:** 3 files  

**Total:** 19 Angular TypeScript files + HTML templates + SCSS

---

## Documentation

- `FRONTEND_README.md` (this file)
- Component inline documentation
- Service method documentation
- Type definitions with JSDoc

---

## Version History

- **1.0.0-M1** (2025-09-06) - Initial Milestone 1 release
  - Patient Portal UI
  - API integration
  - Dashboard and statistics
  - Patient management
  - Vitals tracking
  - Consent management
  - HIPAA compliance UI

---

## Support & Contact

For issues or questions about the frontend:
1. Check component documentation
2. Review service implementations
3. Check API integration in services
4. Review error logs in browser console

---

**Status:** ✅ Milestone 1 Frontend Complete  
**Ready for:** Integration testing with backend  
**Next Release:** Milestone 2 (Weeks 3-4)
