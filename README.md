# Spring Boot Application

This is a basic Spring Boot application.

## 🚀 How to Run

### 1. Build the Project (Skipping Tests)

Since this project does not include a configured data source by default, tests are disabled during build:

```bash
mvn clean install -DskipTests
```

### 2. Add a Data Source (Optional)

If you want to run the application with a database, configure the `application.properties` or `application.yml` file located in `src/main/resources/properties`:

```properties
# Example for PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Run the Application

After building, run the application using:

```bash
mvn spring-boot:run
```

Or run the generated JAR file directly:

```bash
java -jar target/your-app-name.jar
```

## 🛠 Requirements

- Java 17+ (or your project's target version)
- Maven 3.6+
