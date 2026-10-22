CREATE TABLE IF NOT EXISTS `Difficulties` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `noise_amount` INT NOT NULL,
    `light_amount` INT NOT NULL,
    `speed` FLOAT NOT NULL,
    `level` TINYINT NOT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    `is_active` TINYINT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_difficulties_id` (`id`)
);