corp_codes.csv:所有公司的代码和简称，从上交所、深交所网站获得。获取日期：2010－03－19.
data/market_cap.csv:能查到的公司的代码和市值（亿元为单位）
data/no_market_cap_code:没有查到市值的公司的代码
data/csv/empty_csv:没有查到历史价格的公司，基本上是S*ST
data/csv/full_csv:查到历史价格的公司，文件名是"公司代码.csv"，有1747家。

==========================
程序运行说明：
只需要运行
$./fetch_data.py
$./fetch_market_cap.py
其它的文件都是模块
由于网络问题，抓取网页和XML文件时可能会失败。但程序内建自动重新抓取机制，降低了这种可能性。出现错误后，你可以根据屏幕输出查看错误原因，或者是用脚本处理输出结果获得原因。

fetch_data.py：
获得所有公司的历史价格数据。
数据来源：yahoo.compass.cn/stock/xml
它会在本目录的上一级目录建立allcsv目录写入数据，并从上一级目录获得公司代码数据。
这些都可以在程序中配置。
用到了多线程，线程数可以配置

fetch_market_cap.py:
获得所有公司的市值数据。
数据来源：baidu.hexun.com/stock
它同样会在上一级目录写入数据文件market_cap.csv,no_market_cap_code，并从上一级目录获得公司代码数据。
同样可以在程序中配置。
