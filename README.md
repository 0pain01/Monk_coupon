# 📋 Coupon API

A Spring Boot-based RESTful service to manage and apply different types of coupons to a shopping cart. This project supports coupon creation, validation, and application logic for scenarios such as product-based discounts, cart-wide offers, and Buy-X-Get-Y-Free deals.

---

## ✅ Implemented Cases

### 1. Product-Wise Discount
- Applies a percentage discount on a specific product.
- Example: `20% off on Product A`
- ✅ Discount reflected in individual items and totals.

### 2. Buy X Get Y Free (BxGy)
- Buy a certain number of products from a list and get others free.
- Supports:
  - Repetition limits
  - Free items only if present in cart
- Example: `Buy 3 of Product 1 or 2, get 1 of Product 3 free`

### 3. Cart-Wise Discount
- Applies discount based on cart total exceeding a threshold.
- Example: `10% off on orders above $500`

### 4. Coupon Expiry & Active Status
- Coupons can have an expiration date.
- Expired coupons are automatically deactivated.
- Expired coupons throw a descriptive exception if used.

### 5. Validation & Exception Handling
- Proper handling for scenarios like:
  - Non-existent coupon
  - Expired or inactive coupon
  - Insufficient product quantity

### 6. Unit Testing
- Tested layers:
  - Controllers
  - Services
  - Strategies
- Includes negative test cases and edge conditions.

---

## ❌ Unimplemented Cases

| Case                  | Reason                                      |
|-----------------------|---------------------------------------------|
| Stackable Coupons     | Complexity in combining discounts logically |
| Category-Based Coupons| Lack of product category model              |
| Region/User-Specific Coupons | No user or region context in current setup |

---

## ⚠️ Limitations

- Only one coupon can be applied at a time.
- Discounts are flat-rate percentages or fixed deals only.
- BxGy free items must exist in the cart to calculate discount.
- Product prices are provided directly in cart request (no central product DB).
- No authentication/authorization in place.

---

## 🛠️ Assumptions

- Coupons are globally applicable to any user/cart.
- Coupon type is defined explicitly and determines the required details.
- In BxGy, quantity combinations across all buy-products count toward eligibility.
- Discount logic is handled using the Strategy Pattern.
- Cart is passed with product ID, quantity, and price manually.

---

## 📂 Deployment

> Requires Java 17+

### Build with Maven:

```bash
mvn clean install
```

Run locally:
```bash
mvn spring-boot:run
```

## 📁 API Endpoints (Examples)

1. POST /coupons - Create a coupon
```http
POST  http://localhost:8080/coupons
```
Request:

```json
{
  "type": "CART_WISE",
  "name": "10% Off Above 300",
  "description": "10% off if total > 300",
  "active": true,
  "expiresAt": "2025-12-01T23:59:59",
  "details": {
    "threshold": 300,
    "discount": 10
  }
}
```
```json
{
  "type": "PRODUCT_WISE",
  "name": "20% off Product A",
  "description": "Flat 20% off",
  "active": true,
  "expiresAt": "2025-12-01T23:59:59",
  "details": {
    "product_id": 1,
    "discount": 20
  }
}
```
```json
{
  "name": "Buy 2 Get 1 Free",
  "description": "Buy 6 of  [1,2] get 2 of [3] free",
  "type": "BXGY",
  "active": true,
  "expiresAt": "2025-12-31T23:59:59",
  "details": {
    "repetition_limit": 2,
    "buy_products": [
      { "product_id": 1, "quantity": 3 },
      { "product_id": 2, "quantity": 3 }
    ],
    "get_products": [
      { "product_id": 3, "quantity": 1 }
    ]
  }
}
```

2. GET /coupons - List all coupons
```http
GET  http://localhost:8080/coupons
```
Response:
```json
[
    {
        "type": "PRODUCT_WISE",
        "name": "20% off Product A",
        "description": "Flat 20% off",
        "expiresAt": "2025-12-31T23:59:59",
        "active": true,
        "productId": 1,
        "discountPercentage": 20,
        "id": 1
    },
    {
        "type": "BXGY",
        "name": "Buy 2 X(3) Get Y(2) Free",
        "description": "Buy 6 of  [1,2] get 2 of [3] free",
        "expiresAt": "2025-12-31T23:59:59",
        "active": true,
        "repetitionLimit": 2,
        "buyProducts": [
            {
                "id": 4,
                "productId": 1,
                "quantityRequired": 3
            },
            {
                "id": 5,
                "productId": 2,
                "quantityRequired": 3
            }
        ],
        "getProducts": [
            {
                "id": 6,
                "productId": 3,
                "quantityFree": 1
            }
        ],
        "id": 2
    },
    {
        "type": "CART_WISE",
        "name": "10% Off Above 300",
        "description": "10% off if total > 300",
        "expiresAt": "2025-12-01T23:59:59",
        "active": true,
        "threshold": 300.00,
        "discountPercentage": 10,
        "id": 3
    }
]
```

3.  GET /coupons/{id} - Fetch a coupon by ID

Response:
```http
GET  http://localhost:8080/coupons/1
```
```json
{
        "type": "CART_WISE",
        "name": "10% Off Above 300",
        "description": "10% off if total > 300",
        "expiresAt": "2025-12-01T23:59:59",
        "active": true,
        "threshold": 300.00,
        "discountPercentage": 10,
        "id": 3
    }
```


4. POST /coupons/applicable-coupons - Get applicable coupons for a cart
```http
POST  http://localhost:8080/coupons/applicable-coupons
```
Request:
```json
{
  "cart": {
    "items": [
      { "product_id": 1, "quantity": 6, "price": 50 },
      { "product_id": 2, "quantity": 3, "price": 30 },
      { "product_id": 3, "quantity": 2, "price": 25 }
    ]
  }
}
```
Response:
```json
{
    "applicable_coupons": [
        {
            "coupon_id": 2,
            "type": "product-wise",
            "discount": 60
        },
        {
            "coupon_id": 8,
            "type": "bxgy",
            "discount": 50
        },
        {
            "coupon_id": 9,
            "type": "cart-wise",
            "discount": 44.00
        }
    ]
}
```

5. POST /coupons/apply-coupon/{id} - Apply a specific coupon to a cart
```http
POST  http://localhost:8080/coupons/applyc-coupon/1
```
Request:
```json
{
  "cart": {
    "items": [
      { "product_id": 1, "quantity": 6, "price": 50 },
      { "product_id": 2, "quantity": 3, "price": 30 },
      { "product_id": 3, "quantity": 2, "price": 25 }
    ]
  }
}
```
Response:
```json
{
    "updated_cart": {
        "items": [
            {
                "product_id": 1,
                "quantity": 6,
                "price": 50,
                "total_discount": 60.00
            },
            {
                "product_id": 2,
                "quantity": 3,
                "price": 30,
                "total_discount": 0
            },
            {
                "product_id": 3,
                "quantity": 2,
                "price": 25,
                "total_discount": 0
            }
        ],
        "total_price": 440.00,
        "total_discount": 60.00,
        "final_price": 380.00
    }
}
```

6. DELETE /coupons/{id} - Delete a coupon
```http
DELETE  http://localhost:8080/coupons/1
```
response:
```json
{
    "message": "Deleted coupon: 10% Off Above 300 (ID: 9)"
}
```

7. PUT /coupons/{id} - Update a coupon
request:
```http
PUT  http://localhost:8080/coupons/1
```
```json
{
        "type": "CART_WISE",
        "name": "20% Off Above 300",
        "description": "20% off if total > 300",
        "expiresAt": "2025-12-01T23:59:59",
        "active": true,
        "threshold": 300.00,
        "discountPercentage": 20,
        "id": 3
    }
```
