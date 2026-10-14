-- CampusBridge Database Migration: 02_fix_academic_year_length.sql
-- Description: Expand academic_year column length in student_profiles and company_placement_history
--              to accommodate standard academic year formats (e.g. '2026-2027') as well as descriptive batch labels without truncation.

USE campus_bridge_db;

ALTER TABLE student_profiles MODIFY COLUMN academic_year VARCHAR(50) NOT NULL;
ALTER TABLE company_placement_history MODIFY COLUMN academic_year VARCHAR(50) NOT NULL;
