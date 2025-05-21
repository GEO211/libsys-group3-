-- Add active column if it doesn't exist
ALTER TABLE books 
ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE; 