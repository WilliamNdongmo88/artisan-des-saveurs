CREATE TABLE product_items (
    id SERIAL PRIMARY KEY,
    quantity INT NOT NULL,
    order_id BIGINT,
    product_id BIGINT,
    CONSTRAINT fk_product_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);
