## Brokage Backend API

This project is a backend API for a brokage firm that allows employees and customers to manage stock orders, deposits,
and withdrawals. It supports **Basic Authentication** for admin user and **JWT-based authentication** for customer
users.

### Technologies Used

- Java 21
- Spring Boot 3
- H2 Database
- Spring Security
- JWT Authentication
- Mockito

### Running the Application

1. Clone the repository.
2. Run the application: `mvn spring-boot:run`
3. The application runs on http://localhost:8080.
4. Postman Request Collection at [brokage.postman_collection.json](./docs/brokage.postman_collection.json) 

#### H2 Database Console

Access the H2 in-memory database console at http://localhost:8080/h2-console. Use the following details:

- **JDBC URL** : jdbc:h2:mem:testdb
- **Username** : sa
- **Password** : (leave blank)

### Authentication

- **Admin Authentication** : Admin users are authenticated using Basic Authentication (username: admin, password: admin)
- **JWT Token** : Customers authenticate via JWT tokens. They first login via the **/api/login** endpoint to receive a JWT token, which must be included in the Authorization header for subsequent requests. JWT tokens should be included in the Authorization header of the request with the Bearer prefix. Authorization: Bearer

### Features

- **Admin Features** : Admin users can manage all customer orders.
- **Customer Features** : Customers can view, create, and manage their own stock orders.
- **Authentication** :
    - Admin users : Basic Authentication 
      - username=admin, password=admin
    - Customers : JWT-based login via /api/login endpoint.
      - username=customer1, password=password1
      - username=customer2, password=password2

### API Endpoints

#### 1. Deposit Money

- **Endpoint** : POST /api/order/deposit
- **Description** : Deposit money into the customer’s account.
- **Request Header** : Authorization: Basic (admin)
- **Request Body:**

```
{
    "customerId": 1,
    "amount": 200
}
```

#### 2. Withdraw Money

- **Endpoint** : POST /api/order/withdraw
- **Description** : Deposit money into the customer’s account.
- **Request Header** : Authorization: Basic (admin)
- **Request Body:**

```
{
    "customerId": 1,
    "amount": 100
}
```

#### 3. Create a New Order

- **Endpoint** :  POST /api/customer/orders
- **Description** : Create a new order for a customer.
- **Request Header** : Authorization: Basic (admin)
- **Request Body** :

```
{
    "side": "BUY",
    "customer": 1,
    "asset": "ING",
    "size": 1,
    "price": 50
}
```
- **Response:**

```
{
    "id": 1,
    "customerId": 1,
    "assetName": "ING",
    "orderSide": "BUY",
    "size": 1.0,
    "price": 50.0,
    "status": "PENDING",
    "createdDate": "2024-10-13T00:15:49.37983"
}
```

#### 4. Cancel a Pending Order

- **Endpoint** : DELETE /api/order/orders/{id}
- **Description** : Cancel a pending order by order ID. Only orders with PENDING status can be cancelled.
- **Request Header** : Authorization: Basic (admin)
- **Response** : HTTP 200 OK

#### 5. Match a Pending Order

- **Endpoint** : POST /api/order/match?orderId={}
- **Description** : Match a pending order by order ID.
- **Request Header** : Authorization: Basic (admin)
- **Request** : `/api/order/match?orderId=1`
- **Response** : `Order matched successfully.`

#### 6. List Customer Assets

- **Endpoint** : GET /api/order/assets/{customerId}
- **Description** : List all assets of given customer.
- **Request Header** : Authorization: Basic (admin)
- **Response:**

```
[
    {
        "id": 1,
        "customerId": 1,
        "assetName": "TRY",
        "size": 100.0,
        "usableSize": 100.0
    }
]
```

#### 7. List Customer Orders

- **Endpoint** : GET /api/order/orders?customerId={}&startDate={}&endDate={}
- **Description** :  List all orders of given customer between given date range.
- **Request Header** : Authorization: Basic (admin)
- **Request** : `/api/order/orders?customerId=1&startDate=2024-10-12T12:40:30&endDate=2024-10-15T18:40:30`
- **Response:**

```
[
    {
        "id": 1,
        "side": "BUY",
        "customer": 1,
        "asset": "ING",
        "size": 1.0,
        "price": 50.0,
        "status": "PENDING",
        "createdDate": "2024-10-13T00:15:49.37983"
    }
]
```

#### 8. Customer Login (JWT Authentication)

- **Endpoint** : POST /api/login
- **Description** : Customer login endpoint. Returns a JWT token for authenticated users.
- **Request Body** :

```
{
	"username": "customer1",
	"password": "password1"
}
```
- **Response** :

```
{
	"token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### 9. List Customer Orders

- **Endpoint** : GET /api/customer/orders
- **Description** : List all orders for the authenticated customer.
- **Request Header** : Authorization: Bearer
- **Response** :

```
[
    {
        "id": 1,
        "side": "BUY",
        "customer": 1,
        "asset": "ING",
        "size": 1.0,
        "price": 50.0,
        "status": "PENDING",
        "createdDate": "2024-10-13T00:15:49.37983"
    }
]
```

#### 10. Update Customer Information

- **Endpoint** : POST /api/customer/username
- **Description** : Update username or password of authenticated customer.
- **Request Header** : Authorization: Bearer
- **Request Body** :

```
{
	"username": "customer1a"
}
```
- **Response** : HTTP 200 OK
