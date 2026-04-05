-- Initial SQL script for MySQL used by rate-limit-api
-- Creates a database and a dedicated user for each microservice.
-- WARNING: these credentials are for local/dev only. Use secrets in production.

CREATE DATABASE IF NOT EXISTS billing_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'billing_user'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON billing_db.* TO 'billing_user'@'%';

CREATE DATABASE IF NOT EXISTS dunning_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'dunning_user'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON dunning_db.* TO 'dunning_user'@'%';

CREATE DATABASE IF NOT EXISTS subscription_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'subscription_user'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON subscription_db.* TO 'subscription_user'@'%';

CREATE DATABASE IF NOT EXISTS usage_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'usage_user'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON usage_db.* TO 'usage_user'@'%';

CREATE DATABASE IF NOT EXISTS user_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'user_user'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON user_db.* TO 'user_user'@'%';

FLUSH PRIVILEGES;


