-- Use this database
USE customer_db;

-- Insert test account
INSERT INTO accounts (
    id,
    account_number,
    customer_id,
    account_type,
    balance,
    primary_account,
    status,
    password_hash,
    requires_password_change,
    opening_date
) VALUES (
             UUID(),
             '1234567890',
             UUID(),
             'SAVINGS',
             50000.00,
             true,
             'ACTIVE',
             '$2a$10$eImiTXuWVxfM37uY4JANjOqKKGvVZ3d3.t.oqKpRBTgGZWjG8f7nC', -- password: password123
             false,
             NOW()
         );

-- Insert test debit card
INSERT INTO debit_cards (
    id,
    account_number,
    card_number,
    expiry_date,
    daily_limit,
    status,
    issued_date
) VALUES (
             UUID(),
             '1234567890',
             '4532123456789012',
             '2028-12-31',
             50000,
             'ACTIVE',
             NOW()
         );

-- Verify data
SELECT * FROM accounts;
SELECT * FROM debit_cards;