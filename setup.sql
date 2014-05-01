DROP DATABASE stocks;
CREATE DATABASE stocks;
USE stocks;

CREATE TABLE quotes(
    ticker VARCHAR(10), 
    time Date, 
    adjclose DEC(15,2),  
    PRIMARY KEY (ticker, time)
);

CREATE TABLE fund(
    name VARCHAR(50), 
    isIndividual BOOL NOT NULL,

    PRIMARY KEY (name)
);

CREATE TABLE activity(
    action VARCHAR(10) NOT NULL, 
    col_1 VARCHAR(50), 
    col_2 VARCHAR(50), 
    col_3 VARCHAR(50), 
    time VARCHAR(50),
    PRIMARY KEY (action, col_1, col_2, col_3, time)
); 

CREATE TABLE owns(
    fund VARCHAR(50), 
    ticker VARCHAR(10), 
    percent DEC(15,14), 
    date_order Date, 
    date_execute Date, 
 
    INDEX (fund),
    INDEX (ticker, date_execute),

    FOREIGN KEY (fund) REFERENCES fund(name), 
    FOREIGN KEY (ticker, date_execute) REFERENCES quotes(ticker, time)       
);

CREATE TABLE contains(
    individual VARCHAR(50), 
    portofolio VARCHAR(50), 
    percent DEC(15,14),
    date_order Date, 
 
    INDEX (individual),
    INDEX (portofolio),

    FOREIGN KEY (individual) REFERENCES fund(name)
);

CREATE TABLE cash(
    name VARCHAR(50),
    percent DEC(15,14),
    time DATE 
);

CREATE TABLE value(
    fund VARCHAR(50),
    value DEC(50,2),
    time DATE 
);

LOAD DATA INFILE "/home/jeff/424/424finance/quotes.csv" INTO TABLE quotes COLUMNS TERMINATED BY ',' escaped by '"' lines terminated by '\n' ignore 1 lines;

