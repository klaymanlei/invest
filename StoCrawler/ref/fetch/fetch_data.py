#!/usr/bin/python


from xml2csv import xml2csv
from get_url import get_url
import os
import sys
import threading


def thread_func(urls, codes, dest, thread_name):
	if len(urls)==0 or len(codes)==0 or len(dest)==0 or len(urls)!=len(codes):
		return

	for n in range(len(codes)):
		url=urls[n]
		code=codes[n]

		xmlstr=get_url(url,5)
		csvlist=xml2csv(xmlstr)

		if xmlstr=='':
			print 'Fail getting url:',url

		if len(csvlist)==0:
			print 'Fail parsing:',url

		try:
			f=file(
					os.path.join(dest,code+'.csv'),
					'w')
			f.writelines(csvlist)
			f.close()
		except Exception,e:
			print e

		print thread_name,n


url_head=r'http://yahoo.compass.cn/stock/xml/'
url_tail_ssday=r'.ss_day.xml'
url_tail_szday=r'.sz_day.xml'
codes_file=r'../corp_codes.csv'
csv_local=r'../allcsv'
thread_num=5

codes=[]
urls=[]

try:
	f=file(codes_file,'r')
	code_name=f.readlines()
	for i in code_name:
		codes.append(i[0:6])
	f.close()
except Exception,e:
	print e
	sys.exit()


for code in codes:
	if code[0]=='6':
		urls.append(url_head+code+url_tail_ssday)
	else:
		urls.append(url_head+code+url_tail_szday)


a_thread_handle_num=len(codes)/thread_num

for n in range(thread_num):
	_start= n * a_thread_handle_num
	if n==thread_num-1:
		_end=len(codes)
	else:
		_end=_start+a_thread_handle_num

	par_urls=urls[_start:_end]
	par_codes=codes[_start:_end]
	thread_name='thread_'+str(n)

	threading.Thread(
			target=thread_func,
			args=(par_urls,par_codes,csv_local, thread_name)).start()
