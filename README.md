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

mvn clean install
```
Run locally:
```bash
mvn spring-boot:run
```
## 📁 API Endpoints (Examples)
```http
POST /coupons - Create a coupon
```
```http
GET /coupons - List all coupons
```
```http
GET /coupons/{id} - Fetch a coupon by ID
```
```http
POST /coupons/applicable-coupons - Get applicable coupons for a cart
```
```http
POST /coupons/apply-coupon/{id} - Apply a specific coupon to a cart
```
```http
DELETE /coupons/{id} - Delete a coupon
```
