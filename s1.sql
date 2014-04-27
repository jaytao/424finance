use stocks;

LOAD DATA INFILE "/home/xwang125/Class/cmsc424/project/424finance/script.csv" INTO TABLE activity COLUMNS TERMINATED BY ',' escaped by '"' lines terminated by '\n' ignore 1 lines;
