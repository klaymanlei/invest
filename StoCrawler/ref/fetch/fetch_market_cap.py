#!/usr/bin/python

from market_cap import get_market_cap

#the file contains the stock codes.
codes_file=r'../corp_codes.csv'
err_file=r'../no_market_cap_code'
result_file=r'../market_cap.csv'

f=file(codes_file,'r')
lines=f.readlines()
f.close()
codes=[] #the stock codes
for line in lines:
	codes.append(line[0:6])

market_cap_list=[] #the market value of shares

try_num=3 #retry times
n=0
for code in codes:

	#if get an empty value, retry
	for i in range(try_num):
		mc=get_market_cap(code.strip())
		if len(mc.strip())!=0:
			break

	if len(mc.strip())!=0:
		market_cap_list.append(code+','+mc+'\n')
		print code,mc,n
		n+=1
	else:
		print 'Error:',code
		#record the error stock code
		err=file(err_file,'a')
		err.write(code+'\n')
		err.close()

try:
	f=file(result_file,'w')
	f.writelines(market_cap_list)
	f.close()
except Exception,e:
	print e
