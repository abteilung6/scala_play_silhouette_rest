# --- !Ups
CREATE TABLE IF NOT EXISTS `database`
(
    `id`     INT(11)     NOT NULL AUTO_INCREMENT,
    `name`   VARCHAR(45) NULL DEFAULT NULL,
    `engine` VARCHAR(45) NULL DEFAULT NULL,
    `status` VARCHAR(45) NULL DEFAULT NULL,
    `owner` VARCHAR(45) NULL DEFAULT NULL,
    PRIMARY KEY (`id`)
)
    AUTO_INCREMENT = 2
    DEFAULT CHARACTER SET = utf8

# --- !Downs