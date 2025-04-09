[![codecov](https://codecov.io/gh/Christabll/awesome-challenge-online-marketplace-api/branch/main/graph/badge.svg)](https://codecov.io/gh/Christabll/awesome-challenge-online-marketplace-api)


# Awesome Challenge: Online Marketplace API

## Overview

This is a comprehensive RESTful API for an online marketplace. It allows users to buy and sell products, manage their inventory, and process orders. The API supports user authentication via JWT, and features role-based access control for different user types (admin, buyer, seller). In addition, it includes Swagger documentation, Docker containerization
## Features

### Entities and Endpoints

- **Users**
  - **Register & Login:**  
    ***Note on User Registration and Roles***:
    Upon application startup, a default admin account is automatically created with the following credentials:

        Email: christa.bellaishi@gmail.com

        Password: Admin123!

This default admin is the only account authorized to register additional admin users. All self-registered users are assigned the buyer role by default, and only an admin can promote a buyer to a seller.
Shoppers receive email verification on registration. They can log in and manage their profiles.
- **Endpoints:**
  - `POST /api/auth/register`
  - `POST /api/auth/login`
  - `PUT /api/v1/users/profile` (update profile)

- **Products**
  - **CRUD Operations:**  
    Admins can create, update, delete products.
  - **Featured Products:**  
    Admins can mark products as featured.
  - **Browsing:**  
    Shoppers can browse products by category, tags, and search.
  - **Endpoints:**
    - `POST /api/v1/admin/products` (create product)
    - `PUT /api/v1/admin/products/{productId}` (update product)
    - `DELETE /api/v1/admin/products/{productId}` (delete product)
    - `GET /api/v1/products` (browse products)
    - `GET /api/v1/products/{productId}` (view product details)

- **Categories**
  - **CRUD Operations:**  
    Admins can create, update, delete, and list categories.
  - **Endpoints:**
    - `POST /api/v1/admin/categories`
    - `PUT /api/v1/admin/categories/{categoryId}`
    - `DELETE /api/v1/admin/categories/{categoryId}`
    - `GET /api/v1/categories` (public endpoint)
    - `GET /api/v1/categories/{categoryId}` (public endpoint)

- **Orders**
  - **Shopper Actions:**  
    Shoppers can place orders, view their order history, track order status, and cancel orders if permitted.
  - **Admin Actions:**  
    Admins can manage orders (view, update status, delete).
  - **Email Notifications:**  
    Shoppers receive email notifications when order status updates.
  - **Endpoints:**
    - **For Shoppers:**
      - `POST /api/v1/orders` (place order)
      - `GET /api/v1/orders/history` (order history)
      - `GET /api/v1/orders/{orderId}` (track order – only accessible to the owner)
      - `PATCH /api/v1/orders/{orderId}/cancel` (cancel order)
    - **For Admins:**
      - `GET /api/v1/admin/orders` (get all orders)
      - `GET /api/v1/admin/orders/{orderId}` (get order by ID)
      - `PATCH /api/v1/admin/orders/{orderId}/status` (update order status)
      - `DELETE /api/v1/admin/orders/{orderId}` (delete order)

- **Reviews**
  - **Review and Rate Products:**  
    Shoppers can leave a review and rate products they have purchased.
  - **Endpoints:**
    - `POST /api/v1/reviews/{productId}` (post review)  
      *Request body contains: `rating` and `comment`.*
    - `GET /api/v1/reviews/{productId}` (get reviews for a product)

### Database Integration and Relationships

- **Normalized Schema:**  
  Each entity (Users, Products, Orders, Categories, Reviews, OrderItems) is stored in a separate table with foreign key relationships.
- **Key Relationships:**
  - A **Category** has many **Products**.
  - A **User** can have many **Orders**.
  - An **Order** has one **User** and multiple **OrderItems**.
  - An **OrderItem** links an **Order** with a **Product**.
  - A **Review** is linked to both a **Product** and a **User**.

### Security

- **JWT Authentication:**  
  Users must log in to receive a token. Endpoints are secured using role-based access control.
- **Roles:**
  - **Admins** can manage users, products, orders, and categories.
  - **Shoppers** (buyers) can place orders, review products, and manage their own profiles.

### Documentation

- **Swagger/OpenAPI:**  
  Swagger documentation is available (e.g., at `/swagger-ui.html`) for exploring all API endpoints.

### Containerization

- **Docker:**  
  The application is containerized with Docker.
- **Docker Compose:**  
  A `docker-compose.yml` file manages the API server and a PostgreSQL database service.
  - To run the stack, execute:
    ```bash
    docker-compose up --build
    ```

### Testing

- **Unit and Integration Tests:**  
  The project includes unit tests (using JUnit 5 and Mockito) and integration tests to cover the core endpoints. The target is to achieve at least 60% code coverage.
- **Jacoco:**  
  Jacoco is integrated to generate a coverage report:
  - Run tests:
    ```bash
    mvn clean test
    ```
  - Generate the report:
    ```bash
    mvn jacoco:report
    ```
  - Open the report at `target/site/jacoco/index.html` in your browser.

## Setup Instructions

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/Christabll/awesome-challenge-online-marketplace-api.git
   cd awesome-challenge-online-marketplace-api

2. **Configure the Environment**:

   Update the application.properties file with your database credentials and other settings.

   Ensure Docker is installed if you plan to run the application in containers.

3. **Build the Application**:
   ```bash
      mvn clean package
4. **Run Tests**:
    ```bash
     mvn clean test
     mvn jacoco:report

   Open target/site/jacoco/index.html to view coverage.
5. **Run the Application Locally**:
   ```bash
     mvn spring-boot:run
    The API will be available on port 8081 (or as configured).

6. **Run with Docker Compose**:
   ```bash
     docker-compose up --build

     This will start both the API server and the PostgreSQL database.

### API Documentation

Once the application is running, navigate to:

          http://localhost:8081/swagger-ui.html
This page provides a detailed list of all endpoints, request/response schemas, and allows you to interact with the API directly.
![image](https://github.com/user-attachments/assets/e41f5124-7cf8-490a-8f2b-d53b32e341ce)


### System Design

High-Level Components:

        API Server: Runs the Spring Boot application.

        Database: PostgreSQL stores data in a normalized schema.

        Authentication: JWT-based authentication secures endpoints.

        Containerization: Docker and Docker Compose manage services.

![SystemDesign](https://github.com/user-attachments/assets/6f7b1170-c204-4fb1-a28d-12e285eb8136)


Interaction:
The API server interacts with the database using JPA/Hibernate. It processes requests (CRUD operations on Users, Products, Orders, Categories, Reviews),
applies business logic, and returns JSON responses. Authentication and authorization are enforced by Spring Security with JWT tokens.

Additional Features

      Email Notifications:
       Shoppers receive email notifications when order statuses are updated.

      Role-Based Access:
       Different endpoints are protected for Admins and Shoppers.

      Scalability Consideration (Optional Bonus):
       For high load scenarios, consider integrating a message queue (like RabbitMQ or Kafka) for asynchronous order processing.

Running the Application

Ensure you have Java 17, Maven, and Docker installed. Follow the setup instructions above,
and then use Docker Compose or run locally using Maven.

