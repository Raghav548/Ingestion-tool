# 📊 ClickHouse & Flat File Data Ingestion Tool

A full-stack web application providing bidirectional data ingestion between ClickHouse and flat files (CSV). Features secure JWT authentication, data preview, column mapping, and real-time progress tracking.

## 🛠️ Features

### ✅ Authentication & Connection
- JWT-based secure authentication
- Connection testing with detailed error feedback
- Visual status indicators and progress tracking

### 📂 Data Ingestion
- CSV upload with 100-record preview
- Column mapping between CSV and ClickHouse tables
- Success metrics and error handling
  - Mismatched columns
  - Invalid data types
  - Connection failures

### 🗃️ Table Management
- Fetch available ClickHouse tables
- On-demand column listing
- Checkbox-based column selection

## 📁 Project Structure

```bash
clickhouse-ingestion/
├── src/
│   ├── main/
│   │   ├── java/com/ingestion/
│   │   │   ├── controller/
│   │   │   │   ├── IngestionController.java
│   │   │   │   └── ClickHouseTestController.java
│   │   │   ├── dto/
│   │   │   │   └── ClickHouseRequestDTO.java
│   │   │   ├── service/
│   │   │   │   └── ClickHouseService.java
│   │   │   └── IngestionApplication.java
│   │   └── resources/
│   │       └── application.properties
├── frontend/
│   ├── public/
│   └── src/
│       ├── components/
│       ├── App.js
│       └── styles.css
└── README.md 
```

## 🚀 Getting Started
### 1. Clone the Repository
```bash
git clone https://github.com/your-username/clickhouse-ingestion.git
cd clickhouse-ingestion-app
```

### 2. Backend Setup (Spring Boot)
Prerequisites:
Java 17+
Maven

Commands:
```bash
cd clickhouse-ingestion-backend
mvn clean install
mvn spring-boot:run
Server starts at: http://localhost:8081
```

### 3. Frontend Setup (React)
Prerequisites:
Node.js & npm

Commands:
```bash
cd clickhouse-ingestion-frontend
npm install
npm start
```
App runs on: http://localhost:5173

## 🔐 Authentication Details
Requires a valid JWT token from ClickHouse Cloud.

Enter your ClickHouse JDBC URL (e.g., jdbc:clickhouse://your-url/default?ssl=true)

Provide username (usually default) and JWT token.

## ⚙️ Environment Configuration
application.properties

properties
```bash
server.port=8081
spring.application.name=clickhouse-ingestion
# Disable default DataSource configuration
server.port=8081

logging.level.com.clickhouse=DEBUG
logging.level.org.springframework.jdbc=DEBUG
logging.level.org.apache.http=DEBUG

spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
spring.datasource.driver-class-name=com.clickhouse.jdbc.ClickHouseDriver
```

## 📸 UI Highlights
🟢 Connected / 🔴 Failed status messages

✅ Record count preview after ingestion

📊 Progress bar during operations

🧾 CSV file preview and column matching

## 🧩 Tech Stack
Backend: Java 22, Spring Boot, ClickHouse JDBC

Frontend: React.js, Axios, HTML/CSS

Database: ClickHouse Cloud

Security: JWT Authentication

## 🧪 Example Use Cases
Upload a CSV file and ingest it into a selected ClickHouse table.

Select specific columns to control what gets ingested.

Validate connection settings before performing any ingestion.

📬 Contact
For issues or feature requests, please open a GitHub issue or contact [raghav698.be22@chitkara.edu.in].