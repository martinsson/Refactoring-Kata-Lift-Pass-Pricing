DROP database IF EXISTS `test`;
CREATE database `test`;
USE `test`;
CREATE TABLE IF NOT EXISTS test.liftpass (
    pass_id INT AUTO_INCREMENT,
    type VARCHAR(255) NOT NULL,
    cost INT NOT NULL,
    PRIMARY KEY (pass_id),
    UNIQUE KEY (type)
);
CREATE TABLE IF NOT EXISTS test.holidays (
    holiday DATE NOT NULL,
    description VARCHAR(255) NOT NULL,
    PRIMARY KEY (holiday)
);
INSERT INTO test.holidays VALUES ('2019-02-18', 'winter');
INSERT INTO test.holidays VALUES ('2019-02-25', 'winter');
INSERT INTO test.holidays VALUES ('2019-03-04', 'winter');
