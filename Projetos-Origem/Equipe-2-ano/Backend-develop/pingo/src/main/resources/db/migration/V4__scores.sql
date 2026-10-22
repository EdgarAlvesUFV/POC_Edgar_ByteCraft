CREATE TABLE IF NOT EXISTS `Scores` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `points` INT NOT NULL,
  `stage_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
  `is_active` TINYINT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_scores_user_id_stage_id` (`user_id`, `stage_id`),  
  CONSTRAINT `fk_scores_stages`
    FOREIGN KEY (`stage_id`)
    REFERENCES `Stages` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_scores_users`
    FOREIGN KEY (`user_id`)
    REFERENCES `Users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
  );
