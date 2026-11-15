-- Additive migration: uploaded originals and text stay together in MySQL.
CREATE TABLE IF NOT EXISTS zk_smart_entry (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tenant_id BIGINT NOT NULL,
  request_id CHAR(36) NOT NULL,
  title VARCHAR(160) NOT NULL,
  content MEDIUMTEXT NOT NULL,
  creator BIGINT NOT NULL,
  create_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  UNIQUE KEY uk_smart_entry_request (tenant_id, request_id),
  KEY idx_smart_entry_tenant_time (tenant_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS zk_smart_entry_image (
  id BIGINT NOT NULL AUTO_INCREMENT,
  entry_id BIGINT NOT NULL,
  tenant_id BIGINT NOT NULL,
  file_name VARCHAR(255) NOT NULL,
  mime_type VARCHAR(40) NOT NULL,
  byte_size INT NOT NULL,
  sha256 CHAR(64) NOT NULL,
  image_data LONGBLOB NOT NULL,
  PRIMARY KEY (id),
  KEY idx_smart_entry_image_owner (tenant_id, entry_id),
  CONSTRAINT fk_smart_entry_image FOREIGN KEY (entry_id) REFERENCES zk_smart_entry(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
