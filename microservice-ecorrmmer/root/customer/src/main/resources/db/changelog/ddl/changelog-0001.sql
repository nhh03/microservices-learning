CREATE TABLE user_address (
    id BIGINT AUTO_INCREMENT NOT NULL,
    created_by VARCHAR(255),
    created_on DATETIME(6),
    last_modified_by VARCHAR(255),
    last_modified_on DATETIME(6),
    user_id VARCHAR(255) NOT NULL,
    address_id BIGINT NOT NULL,
    is_active BOOLEAN,
    PRIMARY KEY (id)
);