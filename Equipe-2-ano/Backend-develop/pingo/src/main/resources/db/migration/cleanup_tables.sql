-- Script de limpeza de tabelas
-- Remove dados de: Users, UserProgress, Scores e Difficulties
-- Mantém as tabelas: Stages e NetworkDevices
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE `Scores`;
TRUNCATE TABLE `UserProgress`;
TRUNCATE TABLE `Users`;
TRUNCATE TABLE `Difficulties`;

SET FOREIGN_KEY_CHECKS = 1;
