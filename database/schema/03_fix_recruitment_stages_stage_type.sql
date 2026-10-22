-- CampusBridge Database Migration: 03_fix_recruitment_stages_stage_type.sql
-- Description: Convert stage_type column in recruitment_stages table to VARCHAR(50)
--              to accommodate all StageType enum values (including ASSESSMENT) without truncation errors.

USE campus_bridge_db;

ALTER TABLE recruitment_stages MODIFY COLUMN stage_type VARCHAR(50) NOT NULL;
