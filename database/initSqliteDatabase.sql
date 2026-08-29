CREATE TABLE IF NOT EXISTS base_price (
    pass_id INT AUTO_INCREMENT,
    type VARCHAR(255) NOT NULL,
    cost INT NOT NULL,
    PRIMARY KEY (pass_id),
    UNIQUE (type)
);
INSERT OR REPLACE INTO base_price (type, cost) VALUES ('1jour', 35);
INSERT OR REPLACE INTO base_price (type, cost) VALUES ('night', 19);

CREATE TABLE IF NOT EXISTS holidays (
    holiday DATE NOT NULL,
    description VARCHAR(255) NOT NULL,
    PRIMARY KEY (holiday)
);
INSERT OR REPLACE INTO holidays (holiday, description) VALUES ('2019-02-18', 'winter');
INSERT OR REPLACE INTO holidays (holiday, description) VALUES ('2019-02-25', 'winter');
INSERT OR REPLACE INTO holidays (holiday, description) VALUES ('2019-03-04', 'winter');
