-- Add status column if it doesn't exist
ALTER TABLE students 
ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE; 