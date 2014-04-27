#returns first and last date for a specific stock
(select * from quotes where ticker = "ACE" order by time desc limit 1) 
union 
(select * from quotes where ticker = "ACE" order by time asc limit 1) order by time asc;

#returns stock difference between 2 certain dates
(select * from quotes where ticker = "ACE" and time >= "2005-01-03" order by time asc limit 1)
union
(select * from quotes where ticker = "ACE" and time >= "2009-01-03" order by time asc limit 1);

#calculate max drop
select a.ticker, a.adjclose, b.adjclose, (a.adjclose - b.adjclose)/a.adjclose "Drop" from quotes a, quotes b 
where a.ticker = b.ticker 
    and a.time < b.time 
    and a.adjclose > b.adjclose 
    and a.ticker = "ACE"
order by (a.adjclose-b.adjclose)/a.adjclose desc limit 5;

#rank portfolio by total return
select a.adjclose / b.adjclose from
    (select * from fund, value where fund.name = value.fund order by time desc limit 1) a,
    (select * from fund, value where fund.name = value.fund order by time asc limit 1) b
where a. 
