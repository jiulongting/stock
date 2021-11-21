列表
http://quote.eastmoney.com/stocklist.html

参数 fs 说明
- m:1 t:2 上证A股主板
- m:0 t:6,m:0 t:13,m:0 t:80 深圳A股 主板 中小板 创业板
- s: m:0 t:81 s:2048 北证A股
- m:1 t:23 科创板
- m:0 t:7,m:1 t:3 B股
- b:MK0021,b:MK0022,b:MK0023,b:MK0024 ETF

例如
http://20.push2.eastmoney.com/api/qt/clist/get?pn=1&pz=1&np=1&fid=f3&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f22,f11,f62,f128,f136,f115,f152&fs=m:0+t:6,m:0+t:13,m:0+t:81+s:2048,m:1+t:2,m:1+t:23,b:MK0021,b:MK0022,b:MK0023,b:MK0024

返回说明
f2: 收盘
f3: 跌幅
f4: 涨跌
f8: 换手
f9: 市盈率
f10: 量比
f12: 代码
f13: 交易所 0 深交所或北交所, 1 上交所
f14: 名称
f20: 总市值
f21: 流通市值

其他的返回有需要可以获取

当前数据
http://hq.sinajs.cn/list=sz300542,sz300059

历史行情
https://www.aigaogao.com/tools/history.html
