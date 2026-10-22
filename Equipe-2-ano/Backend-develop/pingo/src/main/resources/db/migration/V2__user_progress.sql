CREATE TABLE IF NOT EXISTS `UserProgress` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `current_stage` INT NOT NULL,
  `created_at` DATETIME NULL,
  `updated_at` DATETIME NULL,
  `is_active` TINYINT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_userprogress_user_id` (`user_id`),
  CONSTRAINT `fk_userprogress_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `Users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);
