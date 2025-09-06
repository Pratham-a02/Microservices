-- Insert into orders
INSERT INTO orders (price, order_status) VALUES
(100.50, 'PENDING'),
(200.75, 'CONFIRMED'),
(300.00, 'DELIVERED'),
(150.25, 'PENDING'),
(120.50, 'CANCELLED'),
(210.50, 'PENDING'),
(350.75, 'DELIVERED'),
(110.00, 'CONFIRMED'),
(180.20, 'PENDING'),
(250.40, 'CANCELLED');

-- Insert into order_item
INSERT INTO order_item (order_id, product_id, quantity) VALUES
(1, 101, 2),
(1, 102, 1),
(2, 103, 1),
(2, 104, 3),
(3, 105, 1),
(3, 106, 2),
(4, 107, 2),
(5, 108, 3),
(6, 109, 2),
(7, 110, 2),
(8, 111, 2),
(9, 112, 3),
(10, 113, 2);
