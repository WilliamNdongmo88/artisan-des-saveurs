CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    price NUMERIC(10,2) NOT NULL CHECK (price > 0),
    category VARCHAR(50),
    files_id BIGINT,
    is_available BOOLEAN DEFAULT TRUE,
    stock_quantity INTEGER DEFAULT 0,
    unit VARCHAR(50),
    is_featured BOOLEAN DEFAULT FALSE,
    origin VARCHAR(50),
    preparation VARCHAR(3000),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    user_id BIGINT,
    CONSTRAINT fk_product_files FOREIGN KEY (files_id) REFERENCES files(id)
);
