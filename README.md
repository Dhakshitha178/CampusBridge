# CampusBridge 🎓 - Unified Campus Placement & Recruitment Management System

CampusBridge is a centralized, enterprise-grade campus placement and recruitment automation platform designed to bridge students, corporate recruiters, and college Training and Placement Officers (TPO).

---

## 🚀 Key Modules & Capabilities

### 1. 🎓 Student Placement Portal
- **Centralized Dashboard**: Live application status tracker, upcoming interview alerts, offer tracking, and profile completion.
- **Drive Browser & Eligibility Engine**: Real-time CGPA, backlog, and department-based eligibility calculation with one-click applications.
- **Unified Multi-Company Tracker**: Visual timeline tracking application progression from initial review to final offer across multiple companies.
- **Personalized Interview Calendar**: Live interview schedule with direct online meeting links/venues.
- **Self-Service Rescheduling**: Request interview time changes with automated reason tracking and conflict avoidance.
- **Offer Letter Management**: View CTC breakdown, offer letters, and submit Accept/Reject decisions.
- **Historical Placement Insights**: Review historical recruitment benchmarks, packages, and questions across past batches and peer colleges.

### 2. 🏛️ Training & Placement Officer (TPO) Portal
- **Central TPO Command Center**: Key institutional metrics (placement percentage, average/highest package, drive pipeline, pending reviews).
- **Drive Approval Pipeline**: Review and approve/reject campus drives posted by recruiters with feedback notes.
- **Live Drive Monitor**: Real-time funnel view of candidates in each drive stage (Aptitude -> Technical -> HR -> Offers).
- **Conflict-Aware Unified Master Calendar**: Conflict detection across overlapping corporate interviews.
- **Reschedule Management**: Review student interview reschedule requests and assign revised time slots.
- **Student & Company Management**: Filter and manage student records, resumes, placement statuses, and verified employer profiles.
- **Placement Analytics & Trends**: Interactive charts showing department-wise placement distributions and hiring trends.
- **Automated Weekly Reports**: One-click generation and instant download of PDF snapshots and Excel spreadsheets.
- **Broadcast Announcements**: Campus-wide announcements targeted to all, students, recruiters, or specific branches.

### 3. 💼 Corporate Recruiter Portal
- **Recruiter Dashboard**: Drive statistics, applicant counts, interview progress, and offer conversion rates.
- **Custom Campus Drive Creation**: Define dynamic multi-round selection processes, eligibility rules, and packages.
- **Applicant Funnel & Bulk Processing**: Filter eligible applicants and upload batch results with automatic status transitions.
- **Interview Scheduling Engine**: Schedule candidate interviews with specific panels, slots, and conflict prevention.
- **Panel & Evaluator Management**: Create interview panels, assign members, and record detailed rubric-based ratings.
- **Offer Release Pipeline**: Generate official placement offers with designated CTC and joining details.
- **Explainable College Recommendation Engine**: Institutional prioritization algorithm based on historical yield, talent pools, and skill alignment.

---

## 🛠️ Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **Frontend** | React 18, React Router v6, Axios, Bootstrap 5, Bootstrap Icons, Chart.js, React-ChartJS-2 |
| **Backend** | Java 21, Spring Boot 3, Spring Data JPA / Hibernate, Spring Security, JWT, Springdoc OpenAPI / Swagger |
| **Database** | MySQL 8.0+ (InnoDB, UTF-8mb4) |
| **Build Tools**| Maven 3.9+ (with Maven Wrapper `mvnw`), Node.js (npm) |

---

## 📂 Project Structure

```
CampusBridgeWebApp/
├── backend/
│   ├── .mvn/wrapper/
│   ├── src/main/java/com/campusbridge/placement/
│   │   ├── config/              # Security, CORS, Swagger configurations
│   │   ├── controller/          # REST endpoints (auth, student, tpo, recruiter)
│   │   ├── dto/                 # Request & Response Data Transfer Objects
│   │   ├── entity/              # JPA Domain Entities
│   │   ├── repository/          # Spring Data JPA Repositories
│   │   ├── security/            # JWT Token provider, Filter, UserDetailsService
│   │   ├── service/             # Business logic & algorithms
│   │   └── util/                # File storage and notification utilities
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── application-dev.properties
│   ├── mvnw.cmd
│   └── pom.xml
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/          # Reusable UI widgets & forms
│   │   ├── context/             # AuthContext, NotificationContext
│   │   ├── hooks/               # useAuth, useFetch
│   │   ├── pages/
│   │   │   ├── auth/            # LoginPage, RegisterPage
│   │   │   ├── student/         # Student portal pages
│   │   │   ├── tpo/             # TPO command center & tools
│   │   │   └── recruiter/       # Recruiter portal pages
│   │   ├── routes/              # Protected & RoleBased routing
│   │   └── services/            # Axios API integration clients
│   └── package.json
└── database/
    └── schema/
        └── 01_schema.sql        # Complete MySQL 8 database schema
```

---

## ⚙️ Getting Started

### Prerequisites
- **Java JDK 21** or later
- **Node.js** (v18+) and **npm**
- **MySQL Server 8.0+** running locally on port 3306

---

### Step 1: Database Setup
1. Log in to MySQL and execute the schema:
```sql
source database/schema/01_schema.sql;
```
2. Verify that `campus_bridge_db` is created.

---

### Step 2: Backend Configuration & Startup
1. Navigate to `backend/`:
```bash
cd backend
```
2. Check database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_bridge_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=Root@182
```
3. Run the Spring Boot application using the included Maven wrapper:
```powershell
.\mvnw.cmd spring-boot:run
```
Backend will start on: **`http://localhost:8080`**  
Swagger UI documentation: **`http://localhost:8080/swagger-ui.html`**

---

### Step 3: Frontend Setup & Startup
1. In a separate terminal, navigate to `frontend/`:
```bash
cd frontend
```
2. Run the React application:
```bash
npm start
```
Frontend will be accessible at: **`http://localhost:3000`**

---

## 🔐 User Roles & Permissions

CampusBridge provides 3 distinct role personas:
1. **`STUDENT`**:
   - URL: `/student/dashboard`
   - Access: Apply for drives, view status tracker, check interview calendar, request reschedules, accept/reject offers.
2. **`TPO`**:
   - URL: `/tpo/dashboard`
   - Access: Approve placement drives, live funnel monitoring, centralized conflict-free calendar, review reschedule requests, analytics, weekly PDF/Excel report generation.
3. **`RECRUITER`**:
   - URL: `/recruiter/dashboard`
   - Access: Create drives, manage stages, review applicants, schedule interviews, record evaluations, release offer letters, explainable college recommendations.

---

## 📄 License
CampusBridge is developed for campus recruitment management and educational institutions.
