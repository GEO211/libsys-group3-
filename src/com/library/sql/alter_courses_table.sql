-- Add active column if it doesn't exist
ALTER TABLE courses 
ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE; 