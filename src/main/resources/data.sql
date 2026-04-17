-- Insert default plans for AI Generation Service
INSERT INTO plans (name, description, max_requests_per_minute, max_bytes_per_hour, price, active, created_at) VALUES
('Basic', 'Basic plan for casual AI users - 10 requests/min', 10, 1048576, 0.00, true, CURRENT_TIMESTAMP),
('Standard', 'Standard plan for regular AI users - 50 requests/min', 50, 10485760, 9.99, true, CURRENT_TIMESTAMP),
('Premium', 'Premium plan for power AI users - 200 requests/min', 200, 52428800, 29.99, true, CURRENT_TIMESTAMP),
('Enterprise', 'Enterprise plan for businesses - 1000 requests/min', 1000, 1073741824, 99.99, true, CURRENT_TIMESTAMP);
