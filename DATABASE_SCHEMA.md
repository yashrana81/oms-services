# Database Schema - Order Management System

**Database:** `order-management`

---

## Order Service Tables

### 1. Users Table

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    mobile VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    token VARCHAR(500) UNIQUE,
    token_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Columns:**
- `id` - User identifier
- `name` - User full name
- `mobile` - Login mobile number
- `password` -  User password
- `token` - Authentication token
- `token_active` - Token validity flag
- `created_at` - User registration time
- `updated_at` - Last modification time

---

### 2. Order_Status Table

```sql
CREATE TABLE order_status (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed Data
INSERT IGNORE INTO order_status (id, name) VALUES 
    (1, 'ordered'),
    (2, 'cancelled'),
    (3, 'shipped'),
    (4, 'delivered');
```

**Columns:**
- `id` - Status identifier
- `name` - Status name

---

### 3. Orders Table

```sql
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    order_status_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (order_status_id) REFERENCES order_status(id)
);
```

**Columns:**
- `id` - Order identifier
- `user_id` - References users table
- `product_id` - References product
- `quantity` - Items ordered count
- `total_price` - Order total amount
- `order_status_id` - References order_status table
- `created_at` - Order creation time
- `updated_at` - Last modification time

---

## Inventory Service Tables

### 4. Products Table

```sql
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (price >= 0),
    CHECK (stock >= 0)
);

-- Seed Data
INSERT IGNORE INTO products (id, name, description, price, stock) VALUES
    (1, 'Laptop', 'High performance laptop', 999.99, 50),
    (2, 'Smartphone', 'Latest model smartphone', 699.99, 100),
    (3, 'Headphones', 'Noise cancelling headphones', 199.99, 200),
    (4, 'Keyboard', 'Mechanical gaming keyboard', 149.99, 150),
    (5, 'Mouse', 'Wireless ergonomic mouse', 79.99, 300);
```

**Columns:**
- `id` - Product identifier
- `name` - Product name
- `description` - Product details
- `price` - Product price
- `stock` - Available quantity
- `created_at` - Product creation time
- `updated_at` - Last modification time

---

## Relationships

- **users (1) → orders (N)** - One user, multiple orders
- **order_status (1) → orders (N)** - One status, multiple orders  
- **products (1) → orders (N)** - One product, multiple orders (cross-service)