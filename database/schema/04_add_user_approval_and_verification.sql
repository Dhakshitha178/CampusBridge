-- CampusBridge Database Migration: 04_add_user_approval_and_verification.sql
-- Description: Add designation, approval_status, is_email_verified, and verification_code to users table.

USE campus_bridge_db;

ALTER TABLE users 
    ADD COLUMN IF NOT EXISTS designation VARCHAR(150) NULL AFTER phone_number,
    ADD COLUMN IF NOT EXISTS approval_status VARCHAR(50) NOT NULL DEFAULT 'APPROVED' AFTER is_active,
    ADD COLUMN IF NOT EXISTS is_email_verified BOOLEAN NOT NULL DEFAULT TRUE AFTER approval_status,
    ADD COLUMN IF NOT EXISTS verification_code VARCHAR(50) NULL AFTER is_email_verified;
