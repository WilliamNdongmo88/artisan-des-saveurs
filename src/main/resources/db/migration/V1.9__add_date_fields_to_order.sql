ALTER TABLE orders
ADD COLUMN delivered ENUM('pending', 'processing', 'shipped', 'delivered', 'cancelled') DEFAULT 'processing';

ALTER TABLE orders
ADD COLUMN created_at TIMESTAMP;

ALTER TABLE orders
ADD COLUMN updated_at TIMESTAMP;