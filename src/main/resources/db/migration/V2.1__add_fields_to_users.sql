ALTER TABLE users
    ADD COLUMN receive_promotional_offers BOOLEAN DEFAULT FALSE,
    ADD COLUMN receive_order_updates BOOLEAN DEFAULT FALSE,
    ADD COLUMN be_notified_of_new_products BOOLEAN DEFAULT FALSE,
    ADD COLUMN language VARCHAR(10),
    ADD COLUMN currency VARCHAR(10);
