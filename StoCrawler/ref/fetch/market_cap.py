#!/usr/bin/python
# -*- coding:UTF-8 -*-

import urllib2
from sgmllib import SGMLParser
import sys

class myparser(SGMLParser):
	def reset(self):
		SGMLParser.reset(self)
		self.sz=''
		self.ok=False

	def set_code(self, _code):
		self.code=_code

	def start_span(self, attrs):
		for k,v in attrs:
			if 'sz_'+self.code in v:
				self.ok=True

	def end_span(self):
		self.ok=False

	def handle_data(self,text):
		if self.ok==True:
			self.sz=text


def get_market_cap(code):
	if len(code.strip())==0:
		return ''

	if code[0]=='6':
		ex='.sh'
	else:
		ex='.sz'

	try:
		urllink=urllib2.urlopen(
				'http://baidu.hexun.com/stock/q.php?code='+code+ex,
				timeout=40)
		html_str=urllink.read()
		urllink.close()

		parser=myparser()
		parser.set_code(code)
		parser.feed(html_str)
	except:
		return ''

	return parser.sz
