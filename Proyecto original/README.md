# Thymeleaf-Spring Boot Project

## 📌 Overview
This project is a Spring Boot web application that integrates **Thymeleaf** as the template engine for dynamic HTML rendering. It provides a user-friendly interface for managing products with fields like `quantity`.

## 🛠️ Tech Stack
- **Spring Boot** - Backend framework
- **Thymeleaf** - Templating engine
- **Spring MVC** - Web layer
- **JPA/Hibernate** - ORM for database interaction
- **Bootstrap** - Responsive UI design
- **MySQL** - Database
- **Tomcat 10** - Embedded server

## 🚀 Features
- **Dynamic UI** using Thymeleaf
- **CRUD operations** for Product entity
- **Spring MVC Controllers**
- **Form Handling & Validation**
- **Bootstrap for responsiveness**

## 📂 Project Structure
```
├── src
│   ├── main
│   │   ├── java/com/otz/controller/ProductController.java
│   │   ├── java/com/otz/entity/Product.java
│   │   ├── java/com/otz/repository/ProductRepository.java
│   │   ├── java/com/otz/service/ProductService.java
│   │   ├── java/com/otz/config/DatabaseConfig.java
│   │   ├── resources/templates/
│   │   │   ├── index.html
│   │   │   ├── products.html
│   │   ├── resources/application.properties
│   ├── test
├── pom.xml
├── README.md
```

## 🛠️ Setup & Installation
### 1️⃣ Clone the repository
```sh
git clone https://github.com/your-username/thymeleaf-springboot.git
cd thymeleaf-springboot
```
### 2️⃣ Configure Database (MySQL Example)
Modify `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```
### 3️⃣ Build & Run the Project
```sh
mvn clean install
mvn spring-boot:run
```

## 🌍 Accessing the Application
Open your browser and go to:
```
http://localhost:8080
```

## 🛠️ Troubleshooting
If you encounter the error:
```
Property or field 'quantity' cannot be found on object of type 'com.otz.entity.Product'
```
✔️ Ensure the `quantity` field exists in `Product.java` and has **getter and setter** methods.
✔️ Verify the **Thymeleaf syntax** in `products.html`:
```html
<td th:text="${product.quantity}"></td>
```
✔️ Restart the server after any changes.

## 📌 Responsive Design
This project integrates **Bootstrap** for responsiveness. To ensure a mobile-friendly UI:
- Use **Bootstrap Grid System** (`col-md-6`, `col-sm-12`, etc.)
- Apply **CSS media queries**
- Test with **Google Chrome DevTools** (Toggle device toolbar: `F12 -> Ctrl+Shift+M`)

### Example Responsive Table:
```html
<div class="table-responsive">
    <table class="table table-striped">
        <thead>
            <tr>
                <th>Product Name</th>
                <th>Quantity</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="product : ${products}">
                <td th:text="${product.name}"></td>
                <td th:text="${product.quantity}"></td>
            </tr>
        </tbody>
    </table>
</div>
```



