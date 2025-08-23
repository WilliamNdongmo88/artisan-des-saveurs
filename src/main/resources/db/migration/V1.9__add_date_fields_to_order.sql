ALTER TABLE orders
ALTER COLUMN delivered TYPE VARCHAR(20)
USING CASE
    WHEN delivered = TRUE THEN 'processing'
    WHEN delivered = FALSE THEN 'delivered'
    ELSE NULL
END;

ALTER TABLE orders
ADD COLUMN created_at TIMESTAMP;

ALTER TABLE orders
ADD COLUMN updated_at TIMESTAMP;
