CREATE TABLE IF NOT EXISTS `<playerNames>` (
    `player_id` INT UNSIGNED NOT NULL,
    `name` VARCHAR(16) NOT NULL,
    PRIMARY KEY (`name`),
    UNIQUE KEY (`player_id`, `name`),
    CONSTRAINT `fk_<playerNames>_<players>`
        FOREIGN KEY (`player_id`)
        REFERENCES `<players>` (`player_id`)
        ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = ascii
COLLATE = ascii_general_ci;
