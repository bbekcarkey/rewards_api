-- Customers
INSERT INTO customers (id, name) VALUES (1, 'Customer 1');
INSERT INTO customers (id, name) VALUES (2, 'Customer 2');
INSERT INTO customers (id, name) VALUES (3, 'Customer 3');

-- Customer 1 (Jan-Mar 2026)
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (1, 120.00, '2026-01-05', 'COMPLETED', 'PURCHASE');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (1, 75.50, '2026-01-15', 'COMPLETED', 'PURCHASE');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (1, 200.00, '2026-01-25', 'COMPLETED', 'PURCHASE');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (1, 60.00, '2026-01-30', 'REVERSED', 'PURCHASE');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (1, 50.00, '2026-02-10', 'COMPLETED', 'PURCHASE');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (1, 150.00, '2026-02-20', 'COMPLETED', 'PURCHASE');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (1, 49.99, '2026-03-05', 'COMPLETED', 'PURCHASE');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (1, 101.00, '2026-03-18', 'COMPLETED', 'PURCHASE');

-- Customer 2
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (2, 25.00, '2026-01-08', 'COMPLETED', 'PURCHASE');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (2, 100.00, '2026-01-20', 'COMPLETED', 'PURCHASE');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (2, 51.00, '2026-02-05', 'COMPLETED', 'PURCHASE');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (2, 140.00, '2026-02-14', 'COMPLETED', 'PURCHASE');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (2, 75.00, '2026-03-01', 'COMPLETED', 'PURCHASE');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (2, 50.00, '2026-03-10', 'COMPLETED', 'PURCHASE');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (2, 110.00, '2026-03-25', 'COMPLETED', 'PURCHASE');

-- Customer 3 — includes some non-eligible transactions to test filtering
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (3, 30.00, '2026-01-12', 'COMPLETED', 'PURCHASE');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (3, 50.00, '2026-01-28', 'COMPLETED', 'PURCHASE');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (3, 99.99, '2026-02-15', 'COMPLETED', 'PURCHASE');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (3, 45.00, '2026-02-22', 'COMPLETED', 'REFUND');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (3, 120.00, '2026-03-08', 'PENDING', 'PURCHASE');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (3, 55.00, '2026-03-20', 'COMPLETED', 'PURCHASE');
INSERT INTO transactions (customer_id, amount, transaction_date, status, type) VALUES (3, 85.00, '2026-03-15', 'CANCELLED', 'PURCHASE');
