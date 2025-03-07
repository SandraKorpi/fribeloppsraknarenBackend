# Fribelopps-räknare App

## Project Description

**Fribelopps-räknare, Sandra Korpi JIN23**

This is my graduation project, a user-friendly web application designed to help students track their income during their studies. In Sweden, students receiving financial aid have an income limit, and exceeding it without reporting can lead to repayments. Many students struggle to monitor their earnings, which often results in unexpected repayment obligations.

The project consists of both a backend program and a frontend application. The backend handles data processing and business logic, while the frontend provides an intuitive and accessible interface for users.

As a student myself, I faced this challenge firsthand and found that there were no effective tools available to help with tracking income. This inspired me to develop this application, an essential solution to a real and pressing problem for students.

This backend is built using **Spring Boot**, **Java**, **MySQL**, **JWT tokens** for authentication, and **Swagger** for documenting and testing the API endpoints. The frontend component can be connected to this backend for a fully functioning user app.

## Technologies Used

- **Spring Boot (Java)**: For building the backend application and handling business logic.
- **MySQL**: For storing user data and worked hours.
- **JWT (JSON Web Tokens)**: For authentication and security.
- **Swagger**: For API documentation and testing.
- **Docker** (optional): For containerizing the application and facilitating deployment.

## Installation

### Prerequisites

- **Java 21.0.2** (or later version)
- **Maven**
- **MySQL database**
- **JWT secret key** for authentication

### Steps to Run the Project Locally

1. **Clone the repository**:
   ```bash
   git clone https://github.com/SandraKorpi/fribeloppsraknarenBackend.git
2. **Create a MySQL database**:
Create a new MySql database.

Note: Please create your own MySQL database and set up your database credentials in the .env file. Do not share sensitive credentials publicly.

3. **Configure environment variables: Create a .env file in the project root and add the following environment variables:**
.env
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/database-name
SPRING_DATASOURCE_USERNAME=your-database-username
SPRING_DATASOURCE_PASSWORD=your-database-password
JWT_SECRET=your-jwt-secret-key

4. **Build and run the application: Navigate to the project directory and run the following Maven commands:**

mvn clean install
mvn spring-boot:run

5. **Access Swagger UI: Once the application is running, you can access the Swagger documentation at:**
http://localhost:5000/swagger-ui.html

Access the app: You can now start using the app, which works together with the frontend for a fully functional user app.
You can access the frontend of the app here: https://github.com/SandraKorpi/fribeloppsraknarenFrontend.git

**License**
This project is proprietary. You are not allowed to modify, distribute, or copy the code without permission. All rights are reserved by the original author, Sandra Korpi.
