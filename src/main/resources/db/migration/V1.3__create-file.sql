CREATE TABLE files (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    temp VARCHAR(255),
    product_id BIGINT,
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES product(id)
);