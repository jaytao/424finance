USE stocks;

CREATE TABLE quotes(
    ticker VARCHAR(10), 
    date DATE, 
    adjclose DEC(15,2), 
    PRIMARY KEY (ticker, date)
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
    date DATE, 
    amount INT(50), 
    
    INDEX (fund),
    INDEX (ticker, date),

    FOREIGN KEY (fund) REFERENCES fund(name), 
    FOREIGN KEY (ticker, date) REFERENCES quotes(ticker, date)
);

CREATE TABLE contains(
    parent VARCHAR(50), 
    child VARCHAR(50), 
    amount INT(50), 
    percent DEC(20,20),
    
    INDEX (parent),
    INDEX (child),

    FOREIGN KEY (parent) REFERENCES fund(name),
    FOREIGN KEY (child) REFERENCES fund(name)
);

LOAD DATA INFILE "/home/jeff/424/424finance/quotes.csv" INTO TABLE quotes COLUMNS TERMINATED BY ',' escaped by '"' lines terminated by '\n' ignore 1 lines;
