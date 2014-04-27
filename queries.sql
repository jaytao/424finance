#returns first and last date for a specific stock
(select * from quotes where ticker = "ACE" order by time desc limit 1) 
union 
(select * from quotes where ticker = "ACE" order by time asc limit 1) order by time asc;

#returns stock difference between 2 certain dates
(select * from quotes where ticker = "ACE" and time >= "2005-01-03" order by time asc limit 1)
union
(select * from quotes where ticker = "ACE" and time >= "2009-01-03" order by time asc limit 1);

