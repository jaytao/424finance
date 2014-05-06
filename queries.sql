#returns first and last date for a specific stock
(select * from quotes where ticker = "ACE" order by time desc limit 1) 
union 
(select * from quotes where ticker = "ACE" order by time asc limit 1) order by time asc;

#returns stock difference between 2 certain dates
(select * from quotes where ticker = "ACE" and time >= "2005-01-03" order by time asc limit 1)
union
(select * from quotes where ticker = "ACE" and time >= "2009-01-03" order by time asc limit 1);

#calculate max drop
select a.ticker, a.adjclose, a.time, b.adjclose, b.time, (a.adjclose - b.adjclose)/a.adjclose "Drop" from quotes a, quotes b
where a.ticker = b.ticker 
    and a.time < b.time 
    and a.adjclose > b.adjclose 
    and a.ticker = "ACE"
order by (a.adjclose-b.adjclose)/a.adjclose desc limit 5;

select * from quotes a
left join quotes b on
    a.ticker = b.ticker
    and a.time < b.time
    and a.adjclose > b.adjclose

#rank portfolio by total return
select a.adjclose / b.adjclose from
    (select * from fund, value where fund.name = value.fund order by time desc limit 1) a,
    (select * from fund, value where fund.name = value.fund order by time asc limit 1) b
where a. 

#annualized rate of return(not finished yet)
select sum(owns.percent * value.value), owns.ticker from 
(select owns.percent, value.value, owns.ticker from owns, value, quotes 
 where owns.fund = value.fund 
        and quotes.ticker = owns.ticker 
)
 group by owns.ticker
 
 
#Rank the portfolios by final net worth (cash, plus value of all stocks and portfolios held).

create view temp as 
(select fund,  max(time) as mt from value group by fund order by value);
select temp.fund, value.value from temp, value where temp.fund = value.fund and temp.mt = value.time   
order by value.value;




+---------+-------------+
| fund    | value       |
+---------+-------------+
| ind_2   |     6100.17 |
| ind_1   |     7470.93 |
| ind_3   |    31335.88 |
| fund_2  |  1939809.85 |
| fund_9  |  2541935.41 |
| fund_3  |  3400709.99 |
| fund_4  |  7129712.17 |
| fund_8  |  2368887.63 |
| fund_10 |  1286206.91 |
| fund_7  | 30584782.76 |
| fund_6  |  2638710.46 |
| fund_5  |  3032944.62 |
| fund_1  | 17964481.20 |
+---------+-------------+



      
      

