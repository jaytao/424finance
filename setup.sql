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
    cash DEC(50,2) NOT NULL, 
    isIndividual BOOL NOT NULL, 
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
    amount DEC(50,2), 
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
    amount DEC(50,2), 
    date_order Date, 
 
    INDEX (individual),
    INDEX (portofolio),

    FOREIGN KEY (individual) REFERENCES fund(name)
);

CREATE TABLE cash(
    name VARCHAR(50),
    cash DEC(50,2),
    time DATE 
);

CREATE TABLE value(
    fund VARCHAR(50),
    value DEC(50,2),
    time DATE 
);


+---------+------------+------------+
| name    | cash       | time       |
+---------+------------+------------+
| fund_1  |  976419.00 | 2005-01-03 |
| fund_1  | 2422483.56 | 2013-02-13 |
| fund_1  |  547262.00 | 2005-01-04 |
| fund_1  | 1465830.37 | 2013-09-20 |
| fund_1  | 1465830.37 | 2013-09-20 |
| fund_1  | 1459149.00 | 2005-01-25 |
| fund_1  | 1473603.05 | 2010-07-13 |
| fund_2  |  692231.00 | 2005-01-18 |
| fund_2  | 1319767.89 | 2013-12-31 |
| fund_2  |  812587.00 | 2005-01-13 |
| fund_2  | 1319767.89 | 2013-12-31 |
| fund_3  |  287062.00 | 2005-01-21 |
| fund_3  | 2780714.04 | 2012-11-23 |
| fund_3  |  913479.00 | 2005-01-18 |
| fund_3  | 1065897.09 | 2011-04-25 |
| fund_3  | 1065897.09 | 2011-04-25 |
| fund_4  |  925673.00 | 2005-01-03 |
| fund_4  | 3241637.90 | 2013-12-31 |
| fund_4  | 3241637.90 | 2013-12-31 |
| fund_4  |  348104.00 | 2005-01-13 |
| fund_4  | 3241637.90 | 2013-12-31 |
| fund_5  |   62760.00 | 2005-01-03 |
| fund_5  | 1399589.55 | 2013-12-31 |
| fund_5  | 1399589.55 | 2013-12-31 |
| fund_5  | 1391588.00 | 2005-01-25 |
| fund_5  | 1399589.55 | 2013-12-31 |
| fund_5  | 1399589.55 | 2013-12-31 |
| fund_6  | 1359664.00 | 2005-01-27 |
| fund_6  | 1430479.49 | 2011-03-28 |
| fund_6  | 1073553.00 | 2005-01-03 |
| fund_6  | 2290934.54 | 2013-12-31 |
| fund_7  | 1306758.00 | 2005-01-06 |
| fund_7  | 1534499.54 | 2013-12-31 |
| fund_7  |  161693.00 | 2005-01-20 |
| fund_7  | 1534499.54 | 2013-12-31 |
| fund_7  | 1534499.54 | 2013-12-31 |
| fund_7  | 1300258.00 | 2005-01-27 |
| fund_8  |  245268.00 | 2005-01-21 |
| fund_8  | 2916462.38 | 2012-08-13 |
| fund_8  | 1023641.00 | 2005-01-24 |
| fund_8  | 1398074.02 | 2013-12-31 |
| fund_9  |  299415.00 | 2005-01-28 |
| fund_9  | 3183931.81 | 2012-10-01 |
| fund_9  |  983479.00 | 2005-01-11 |
| fund_9  | 1007412.93 | 2013-05-10 |
| fund_9  | 1007412.93 | 2013-05-10 |
| fund_10 | 1209444.00 | 2005-01-24 |
| fund_10 | 1309141.54 | 2012-12-26 |
| fund_10 | 1227038.00 | 2005-01-18 |
| fund_10 | 1303903.84 | 2013-12-31 |
+---------+------------+------------+


