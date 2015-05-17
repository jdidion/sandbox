import re
import urllib2

def urldecode(url):
    ud = re.compile('%([0-9a-hA-H]{2})', re.MULTILINE)
    return ud.sub(lambda m: chr(int(m.group(1), 16)), url)

url = 'http://www.cad-comic.com/cad/20110225'
#url = 'http://google.com'
headers = { 'User-Agent': 'Mozilla/5.0' }
req = urllib2.Request(url, headers=headers)

u = urllib2.urlopen(req)
print u.read()