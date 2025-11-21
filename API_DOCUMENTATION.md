# Order Management System - API Documentation

## Service Endpoints
- **Inventory Service:** http://15.206.89.28:8081
- **Order Service:** http://15.206.89.28:8082

---

## System Flows

### 1. User Authentication Flow
- User signs up with mobile and password → receives authentication token
- User logs in with credentials → receives new active token
- User logs out → token becomes inactive, must login again for new token

### 2. Product Browsing Flow
- Browse all available products with stock information
- View specific product details including price and availability

### 3. Order Placement Flow
- User places order with product ID and quantity
- System checks inventory stock availability
- If stock available: reduces stock, creates order with ORDERED status
- If insufficient stock: returns error

### 4. Order Management Flow
- User can view all their orders or specific order details
- User can update order status: ORDERED → SHIPPED → DELIVERED or CANCELLED
- When order is CANCELLED: message sent to SQS queue → inventory service restores stock automatically

### 5. Stock Restoration Flow (Async)
- Order service publishes CANCEL_ORDER event to AWS SQS
- Inventory service listens to SQS queue
- Stock automatically restored for cancelled orders

---

## Authentication APIs

### 1. User Signup
**POST** `/api/auth/signup`

**Description:** Register a new user account

**Headers:** None

**Request Body:**
```json
{
  "name": "Test User",
  "mobile": "9876543210",
  "password": "password123"
}
```

**Returns:**
```json
{
  "userId": 1,
  "name": "Test User",
  "token": "generated-auth-token"
}
```

---

### 2. User Login
**POST** `/api/auth/login`

**Description:** Login with existing credentials

**Headers:** None

**Request Body:**
```json
{
  "mobile": "9876543210",
  "password": "password123"
}
```

**Returns:**
```json
{
  "userId": 1,
  "name": "Test User",
  "token": "generated-auth-token"
}
```

---

### 3. User Logout
**POST** `/api/auth/logout`

**Description:** Logout user by deactivating their authentication token

**Headers:**
```
Authorization: your-token-here
```

**Request Body:** None

**Returns:**
```json
"Logged out successfully"
```

---

## Product APIs

### 4. Get All Products
**GET** `/api/products`

**Description:** Retrieve list of all available products

**Headers:** None

**Request Body:** None

**Returns:**
```json
[
  {
    "id": 1,
    "name": "Laptop",
    "description": "High performance laptop",
    "price": 999.99,
    "stock": 50
  }
]
```

---

### 5. Get Product by ID
**GET** `/api/products/{id}`

**Description:** Get details of a specific product

**Headers:** None

**Request Body:** None

**Returns:**
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "High performance laptop",
  "price": 999.99,
  "stock": 50
}
```

---

## Order APIs

### 6. Place Order
**POST** `/api/orders`

**Description:** Create a new order for a product

**Headers:**
```
Authorization: your-token-here
```

**Request Body:**
```json
{
  "productId": 1,
  "quantity": 2
}
```

**Returns:**
```json
{
  "orderId": 1,
  "userId": 1,
  "productId": 1,
  "quantity": 2,
  "totalPrice": 1999.98,
  "status": "ORDERED",
  "createdAt": "2024-11-21T12:00:00"
}
```

---

### 7. Get User Orders
**GET** `/api/orders`

**Description:** Retrieve all orders for logged-in user

**Headers:**
```
Authorization: your-token-here
```

**Request Body:** None

**Returns:**
```json
[
  {
    "orderId": 1,
    "userId": 1,
    "productId": 1,
    "quantity": 2,
    "totalPrice": 1999.98,
    "status": "ORDERED",
    "createdAt": "2024-11-21T12:00:00"
  }
]
```

---

### 8. Get Order by ID
**GET** `/api/orders/{orderId}`

**Description:** Get details of a specific order

**Headers:**
```
Authorization: your-token-here
```

**Request Body:** None

**Returns:**
```json
{
  "orderId": 1,
  "userId": 1,
  "productId": 1,
  "quantity": 2,
  "totalPrice": 1999.98,
  "status": "ORDERED",
  "createdAt": "2024-11-21T12:00:00"
}
```

---

### 9. Update Order Status
**PUT** `/api/orders/{orderId}/status`

**Description:** Change order status (ORDERED → SHIPPED → DELIVERED or CANCELLED)

**Headers:**
```
Authorization: your-token-here
```

**Request Body:**
```json
{
  "status": "SHIPPED"
}
```

**Returns:**
```json
{
  "orderId": 1,
  "userId": 1,
  "productId": 1,
  "quantity": 2,
  "totalPrice": 1999.98,
  "status": "SHIPPED",
  "createdAt": "2024-11-21T12:00:00"
}
```

---

## Health Check APIs

### 10. Inventory Service Health
**GET** `/api/health`

**Description:** Check if inventory service is running

**Headers:** None

**Request Body:** None

**Returns:**
```json
"Inventory Service is up and running!"
```

---

### 11. Order Service Health
**GET** `/api/health`

**Description:** Check if order service is running

**Headers:** None

**Request Body:** None

**Returns:**
```json
"Order Service is up and running!"
```

---

## Notes
- All authenticated endpoints require `Authorization` header with token from login/signup
- When order is CANCELLED, stock is automatically restored via SQS messaging
- Valid order statuses: ORDERED, SHIPPED, DELIVERED, CANCELLED
- Token becomes inactive after logout; login again to get new active token