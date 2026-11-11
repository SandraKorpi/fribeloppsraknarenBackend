-- Grundschema som motsvarar tabellerna som Hibernate skapade tidigare.
-- På en befintlig databas hoppas den här över (baseline-version=1), på en ny databas körs den.

CREATE TABLE semester (
    id BIGINT NOT NULL AUTO_INCREMENT,
    income_limit DOUBLE NOT NULL,
    year INT NOT NULL,
    type VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    semester_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_user_user_name UNIQUE (user_name),
    CONSTRAINT uk_user_email UNIQUE (email),
    CONSTRAINT fk_user_semester FOREIGN KEY (semester_id) REFERENCES semester (id)
) ENGINE=InnoDB;

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(255),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES user (id)
) ENGINE=InnoDB;

CREATE TABLE worked_hours (
    id BIGINT NOT NULL AUTO_INCREMENT,
    hours DOUBLE NOT NULL,
    month INT NOT NULL,
    date INT NOT NULL,
    year INT NOT NULL,
    hourly_rate DOUBLE NOT NULL,
    vacation_pay DOUBLE NOT NULL,
    user_id BIGINT,
    semester_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_worked_hours_user FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT fk_worked_hours_semester FOREIGN KEY (semester_id) REFERENCES semester (id)
) ENGINE=InnoDB;
