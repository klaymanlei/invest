import urllib

def get_url(url,try_num=1):
	if len(url)==0:
		return ''

	for i in range(try_num):
		try:
			urllink=urllib.urlopen(url)
			urldoc=urllink.read()
			urllink.close()
			if urldoc[-7:]=='hqdata>':
				return urldoc
			else:
				continue
		except:
			return ''

	return ''
