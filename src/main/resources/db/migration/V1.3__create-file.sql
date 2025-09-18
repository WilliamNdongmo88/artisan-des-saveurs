CREATE TABLE files (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    temp VARCHAR(255),
    file_name VARCHAR(255),
    file_path VARCHAR(500),
    product_id BIGINT
);