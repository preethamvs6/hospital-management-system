# 🏥 MedCore — Hospital Management System

A full-stack Hospital Management System built with **Java Spring Boot** (backend) and **HTML5/CSS3/JavaScript** (frontend), using **MySQL** database and **Maven** build tool.

## 🚀 Features

- **Admin Module**: Manage doctors, patients, view reports, handle appointments
- **Doctor Module**: View appointments, update prescriptions, patient details
- **Patient Module**: Register/login, book appointments, view prescriptions, payment status
- **Appointment Module**: Book, cancel, complete appointments, history tracking
- **Billing Module**: Generate bills, payment tracking, revenue reports
- **Authentication**: Session-based auth with role-based access (ADMIN, DOCTOR, PATIENT)
- **Responsive UI**: Modern dark glassmorphism design, mobile-friendly

## 🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
| Backend | Java 17, Spring Boot 3.5.14 |
| Frontend | HTML5, CSS3, Vanilla JavaScript |
| Database | MySQL 8.0 |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security 6.x |
| Build | Maven |
| DevOps | Docker, Jenkins, AWS EC2 |

## 📋 Prerequisites

- **Java 17+** (JDK)
- **Maven 3.9+**
- **MySQL 8.0+**
- **Docker** (optional, for containerized deployment)

## ⚡ Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd java-full-stack
```

### 2. Setup MySQL Database
```sql
CREATE DATABASE hospital_db;
```
Update credentials in `backend/hospital-management/src/main/resources/application-dev.properties` if needed:
```properties
spring.datasource.username=root
spring.datasource.password=password
```

### 3. Build & Run
```bash
cd backend/hospital-management
mvn clean install -DskipTests
mvn spring-boot:run
```

### 4. Access the Application
Open your browser and navigate to: **http://localhost:8080**

### Default Admin Credentials
- **Username**: `admin`
- **Password**: `admin123`

## 🐳 Docker Deployment

### Using Docker Compose (Recommended)
```bash
# From root directory
docker-compose up -d

# Check logs
docker-compose logs -f app
```
This starts both MySQL and the application. Access at **http://localhost:8080**

### Manual Docker Build
```bash
cd backend/hospital-management
docker build -t hospital-management .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host:3306/hospital_db \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  hospital-management
```

## 🔗 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login |
| POST | `/api/auth/register` | Register |
| POST | `/api/auth/logout` | Logout |
| GET | `/api/auth/me` | Current user |

### Doctors
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/doctors` | Get all doctors |
| GET | `/api/doctors/{id}` | Get doctor by ID |
| PUT | `/api/doctors/{id}` | Update doctor |
| DELETE | `/api/doctors/{id}` | Delete doctor |
| GET | `/api/doctors/search?keyword=` | Search doctors |

### Patients
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/patients` | Get all patients |
| GET | `/api/patients/{id}` | Get patient by ID |
| PUT | `/api/patients/{id}` | Update patient |
| DELETE | `/api/patients/{id}` | Delete patient |
| GET | `/api/patients/search?keyword=` | Search patients |

### Appointments
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/appointments` | Get all |
| POST | `/api/appointments` | Book appointment |
| PUT | `/api/appointments/{id}/cancel` | Cancel |
| PUT | `/api/appointments/{id}/complete` | Complete |
| GET | `/api/appointments/patient/{id}` | By patient |
| GET | `/api/appointments/doctor/{id}` | By doctor |

### Billing
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/bills` | Get all bills |
| POST | `/api/bills` | Create bill |
| PUT | `/api/bills/{id}/pay` | Record payment |
| GET | `/api/bills/revenue` | Revenue stats |

### Dashboard
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard/stats` | Get statistics |

## 📁 Project Structure

```
java-full-stack/
├── backend/hospital-management/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/
│       ├── java/com/hospital/management/
│       │   ├── config/          # Security, CORS, DataInitializer
│       │   ├── controller/      # REST API controllers
│       │   ├── dto/             # Data Transfer Objects
│       │   ├── entity/          # JPA entities
│       │   ├── enums/           # Enumerations
│       │   ├── exception/       # Global exception handling
│       │   ├── repository/      # Spring Data JPA repositories
│       │   └── service/         # Business logic
│       └── resources/
│           ├── application*.properties
│           └── static/          # Frontend files
│               ├── css/style.css
│               ├── js/*.js
│               └── *.html
├── docker-compose.yml
├── Jenkinsfile
└── README.md
```

## 🌐 AWS EC2 Deployment

1. **Launch EC2 instance** (Ubuntu 22.04, t2.medium recommended)
2. **Install Docker & Docker Compose**:
   ```bash
   sudo apt update && sudo apt install -y docker.io docker-compose
   sudo usermod -aG docker $USER
   ```
3. **Clone & Deploy**:
   ```bash
   git clone <repo-url> && cd java-full-stack
   docker-compose up -d
   ```
4. **Open port 8080** in EC2 Security Group (inbound rules)
5. **Access**: `http://<ec2-public-ip>:8080`

## 🔧 Jenkins CI/CD Setup

1. Install Jenkins with Docker plugin
2. Create a Pipeline job pointing to this repository
3. Configure GitHub webhook: `http://<jenkins-url>/github-webhook/`
4. The `Jenkinsfile` handles: Checkout → Build → Test → Docker Build → Deploy

## 📄 License

This project is for educational purposes.
