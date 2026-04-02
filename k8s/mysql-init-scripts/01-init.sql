
-- Initial SQL script for MySQL used by rate-limit-api
-- Edit this file as needed; you can create a ConfigMap from this directory with:
-- kubectl create configmap mysql-initdb-config --from-file=k8s/mysql-init-scripts

CREATE DATABASE IF NOT EXISTS usage_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'usage_user'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON usage_db.* TO 'usage_user'@'%';
FLUSH PRIVILEGES;

CREATE DATABASE IF NOT EXISTS usage_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'usage_user'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON usage_db.* TO 'app'@'%';
FLUSH PRIVILEGES;


