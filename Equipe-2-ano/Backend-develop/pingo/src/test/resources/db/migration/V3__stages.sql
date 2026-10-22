CREATE TABLE IF NOT EXISTS `Stages` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `number` INT NOT NULL,
  `created_at` DATETIME NULL,
  `updated_at` DATETIME NULL,
  `is_active` TINYINT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_stages_id` (`id`),
  UNIQUE INDEX `id_stages_name` (`name`),
  UNIQUE INDEX `id_stages_number` (`number`)
);

