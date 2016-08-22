from xml.dom import minidom

def xml2csv(xml_str):
	if len(xml_str)==0:
		return []

	try:
		xmldoc=minidom.parseString(xml_str)
		items=xmldoc.firstChild.getElementsByTagName('items')[0].getElementsByTagName('item')

		csv_list=[]
		for item in items:
			data=item.getElementsByTagName('date')[0].firstChild.toxml().strip()
			close=item.getElementsByTagName('close')[0].firstChild.toxml().strip()
			csv_list.append(data+','+close+'\n')

		return csv_list
	except Exception,e:
		return []
