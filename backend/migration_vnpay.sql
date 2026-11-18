-- Migration for VNPAY Integration

-- Remove old columns from payments table
ALTER TABLE payments
DROP COLUMN provider,
DROP COLUMN transaction_id;

-- Add new columns for VNPAY
ALTER TABLE payments
ADD COLUMN payment_code VARCHAR(255),
ADD COLUMN gateway_transaction_id VARCHAR(255),
ADD COLUMN payment_method VARCHAR(255),
ADD COLUMN response_code VARCHAR(255),
ADD COLUMN bank_code VARCHAR(255),
ADD COLUMN transaction_time TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP;
