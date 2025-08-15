CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    subtotal NUMERIC(10, 2) NOT NULL,
    discount NUMERIC(10, 2) NOT NULL,
    total NUMERIC(10, 2) NOT NULL,
    free_shipping BOOLEAN NOT NULL,
    user_id BIGINT,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
);

