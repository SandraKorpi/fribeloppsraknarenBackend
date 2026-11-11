-- Antal studieveckor och studietakt per användare och kalenderhalvår.
CREATE TABLE IF NOT EXISTS study_period (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    year INT NOT NULL,
    type VARCHAR(255) NOT NULL,
    weeks INT NOT NULL,
    study_rate INT NOT NULL,
    fribelopp_override DOUBLE,
    PRIMARY KEY (id),
    CONSTRAINT uk_study_period_user_year_type UNIQUE (user_id, year, type),
    CONSTRAINT fk_study_period_user FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT ck_study_period_weeks CHECK (weeks BETWEEN 1 AND 26),
    CONSTRAINT ck_study_period_rate CHECK (study_rate IN (50, 75, 100))
) ENGINE=InnoDB;
