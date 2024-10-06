CREATE TABLE IF NOT EXISTS `Team`
(
    `id`   int AUTO_INCREMENT,
    `name` varchar(50) NOT NULL,
    `status` ENUM ('none','prepublish','publish') NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `User`
(
    `id`         int AUTO_INCREMENT,
    `first_name` varchar(50)                                     NOT NULL,
    `last_name`  varchar(50)                                     NOT NULL,
    `role`       ENUM ('student', 'teacher', 'admin','referent') NOT NULL,
    `email`      varchar(50)                                     NOT NULL,
    `password`   varchar(50)                                     NOT NULL,
    `gender`     ENUM ('M','F','other')                  NOT NULL,
    `option`     varchar(100),
    `team_id`    int,
    `past_grade` int DEFAULT 0,
    `bachelor`   boolean                                         NOT NULL DEFAULT false,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`team_id`) REFERENCES `Team` (`id`)
);

CREATE TABLE IF NOT EXISTS `Sprint`
(
    `id`         int AUTO_INCREMENT,
    `start_date` date         NOT NULL,
    `end_date`   date         NOT NULL,
    `end_type`   varchar(100) NOT NULL,
    `team_id`    int          NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`team_id`) REFERENCES `Team` (`id`)
);

CREATE TABLE IF NOT EXISTS `Notification`
(
    `id`          int AUTO_INCREMENT,
    `type`        varchar(50) NOT NULL,
    `status`      varchar(50) NOT NULL,
    `description` varchar(255),
    `team_id`     int         NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`team_id`) REFERENCES `Team` (`id`)
);

CREATE TABLE IF NOT EXISTS `GradeScale`
(
    `id`          int AUTO_INCREMENT,
    `name`        varchar(50) NOT NULL,
    `description` varchar(255),
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `Category`
(
    `id`             int AUTO_INCREMENT,
    `name`           varchar(255) NOT NULL,
    `grade_scale_id` int          NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`grade_scale_id`) REFERENCES `GradeScale` (`id`)
);

CREATE TABLE IF NOT EXISTS `Detail`
(
    `id`          int AUTO_INCREMENT,
    `name`        varchar(50) NOT NULL,
    `description` varchar(255),
    `mark`        int         NOT NULL,
    `category_id` int         NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`category_id`) REFERENCES `Category` (`id`)
);

CREATE TABLE IF NOT EXISTS `ProjectGrade`
(
    `id`             int AUTO_INCREMENT,
    `grade_init`     int                              NOT NULL,
    `subject`        varchar(200)                     NOT NULL,
    `grade_update`   int                              NOT NULL,
    `status`         ENUM ('published','unpublished') NOT NULL,
    `type`           varchar(50)                      NOT NULL,
    `team_id`        int,
    `user_id`        int,
    `grade_scale_id` int                              NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`team_id`) REFERENCES `Team` (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `User` (`id`),
    FOREIGN KEY (`grade_scale_id`) REFERENCES `GradeScale` (`id`)
);

