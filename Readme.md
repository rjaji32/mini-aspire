# Getting Started

1. If you haven't already, install a database server on your local machine or use a remote database server. For MySQL, you can download and install it from the official MySQL website. - https://www.mysql.com/downloads/
2. After the database server is installed and running, use a database client (e.g., MySQL Workbench, phpMyAdmin, or command-line tools) to create a new database that your Spring Boot application will use. You can create a database using SQL commands like: CREATE DATABASE your_database_name;
3. Writing db properties in application.properties file- 
   spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
4. Install maven on your system
5. Build the project using Maven's mvn command. This will compile the code, resolve dependencies, and package the application: mvn clean install command.
6. Once the build is successful, you can run your Spring Boot application using the spring-boot:run goal: mvn spring-boot:run
