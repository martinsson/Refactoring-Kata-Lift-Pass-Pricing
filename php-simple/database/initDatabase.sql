DROP database IF EXISTS `lift_pass`;
CREATE database `lift_pass`;
USE `lift_pass`;

CREATE TABLE IF NOT EXISTS lift_pass.base_price (
    pass_id INT AUTO_INCREMENT,
    type VARCHAR(255) NOT NULL,
    cost INT NOT NULL,
    PRIMARY KEY (pass_id),
    UNIQUE KEY (type)
);
INSERT INTO lift_pass.base_price (type, cost) VALUES ('1jour', 35);
INSERT INTO lift_pass.base_price (type, cost) VALUES ('night', 19);
