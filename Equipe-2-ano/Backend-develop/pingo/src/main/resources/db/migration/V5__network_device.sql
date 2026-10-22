CREATE TABLE IF NOT EXISTS `NetworkDevices` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(45) NOT NULL,
    `has_deffect` TINYINT NOT NULL,
    `type` TINYINT NOT NULL,
    `description` VARCHAR(45) NOT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    `is_active` TINYINT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_networkdevices_id` (`id`)
);