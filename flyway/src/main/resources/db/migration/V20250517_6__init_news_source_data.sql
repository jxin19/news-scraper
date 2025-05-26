
insert into news_source (name, url, code, type, is_active)
values ('연합뉴스 - 최신', 'https://www.yna.co.kr/rss/news.xml', 'YNA', 'RSS_STATIC', true);

insert into news_source (name, url, code, type, is_active)
values ('SBS - 최신', 'https://news.sbs.co.kr/news/headlineRssFeed.do?plink=RSSREADER', 'SBS', 'RSS_STATIC', true);

insert into news_source (name, url, code, type, is_active)
values ('매일경제 - 최신', 'https://www.mk.co.kr/rss/30000001', 'MK', 'RSS_STATIC', true);

insert into news_source (name, url, code, type, is_active)
values ('구글 - 최신', 'https://news.google.com/rss/search?q=[keyword]&hl=ko&gl=KR&ceid=KR:ko', 'GOOGLE', 'RSS_KEYWORD', true);
