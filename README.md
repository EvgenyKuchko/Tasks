# TaskNest App
Tasks - is a web application for task management with authorization, editing and deleting tasks. Users have 2 roles: User and Administrator.
Administrator has access to the list of all users and tasks. Can edit any tasks and users, as well as create new tasks by assigning them to existing users or create new users, also grant administrator rights to users.
The application implements task search by expressions.

## Technologies
- Java 17
- Spring Boot 3
- Spring Security
- Spring Data Jpa
- Lombok
- MySQL 8
- Flyway
- Thymeleaf
- HTML/CSS/JS

## Setup and launch
To start the project you need to configure the database (MySQL) in the application.properties file.
spring.datasource.url=jdbc:mysql://localhost:3306/yourBD
spring.datasource.username=yourusername
spring.datasource.password=yourpassword
Next, start the project via TasksApplication.java
The application will be available at the following url http://localhost:8080/register from registration page

## Additional information
The entire project is covered in tests. 
There is also an example of a database migration with two users and an administrator (access to the administrator with the following data).
username: admin
password: admin
