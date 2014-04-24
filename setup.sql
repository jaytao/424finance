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
    cash INT(50) NOT NULL, 
    isIndividual BOOL NOT NULL, 

    PRIMARY KEY (name)
);

CREATE TABLE activity(
    action VARCHAR(10) NOT NULL, 
    col_1 VARCHAR(50), 
    col_2 VARCHAR(50), 
    col_3 VARCHAR(50), 
    col_4 VARCHAR(50)
); 

CREATE TABLE owns(
    fund VARCHAR(50), 
    ticker VARCHAR(10), 
    time Date, 
    amount INT(50), 
    date_order Date, 
    date_execute Date, 
 
    INDEX (fund),
    INDEX (ticker, time),

    FOREIGN KEY (fund) REFERENCES fund(name), 
    FOREIGN KEY (ticker, time) REFERENCES quotes(ticker, time)       
);

CREATE TABLE contains(
    individual VARCHAR(50), 
    portofolio VARCHAR(50), 
    amount INT(50), 
    percent DEC(20,20),
    date_order Date, 
    date_execute Date,
 
    INDEX (individual),
    INDEX (portofolio),

    FOREIGN KEY (individual) REFERENCES fund(name),
    FOREIGN KEY (portofolio) REFERENCES fund(name)
);

LOAD DATA INFILE "/home/jeff/424/424finance/quotes.csv" INTO TABLE quotes COLUMNS TERMINATED BY ',' escaped by '"' lines terminated by '\n' ignore 1 lines;
