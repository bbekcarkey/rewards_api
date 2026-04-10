# Rewards API

Calculates reward points for customers based on their purchase transactions. Built with Spring Boot, JPA, and H2.

A customer earns **2 points** for every dollar over 100 and **1 point** for every dollar between 50 and 100 on each transaction. For example, a 120 purchase = 90 points (20 x 2 + 50 x 1).

## How to run

```
./mvnw spring-boot:run
```

App starts at http://localhost:8080 with sample data already loaded.

## Endpoints

**All customers:**
```
GET /api/v1/rewards?startDate=2026-01-01&endDate=2026-03-31
```

**Single customer:**
```
GET /api/v1/customers/1/rewards?startDate=2026-01-01&endDate=2026-03-31
```

**Example response:**
```json
{
  "customerId": 1,
  "customerName": "Customer 1",
  "startDate": "2026-01-01",
  "endDate": "2026-03-31",
  "monthlyRewards": [
    { "month": "2026-01", "points": 365, "transactionCount": 3, "totalSpend": 395.50 },
    { "month": "2026-02", "points": 150, "transactionCount": 2, "totalSpend": 200.00 }
  ],
  "totalPoints": 515
}
```

## Tests

```
./mvnw test
```

## Assumptions

- Points are calculated per transaction, not on combined monthly totals
- Only completed purchases count — refunds, pending, cancelled transactions are excluded
- Cents are truncated before calculating points (99.99 gives 49 points, not 50)
