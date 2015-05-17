import re
import urllib2
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

def urldecode(url):
    ud = re.compile('%([0-9a-hA-H]{2})', re.MULTILINE)
    return ud.sub(lambda m: chr(int(m.group(1), 16)), url)

class MainPage(webapp.RequestHandler):
    def get(self):
        url = urldecode(self.request.get('q'))
        headers = { 'User-Agent': 'Mozilla/5.0' }
        req = urllib2.Request(url, headers=headers)
        u = urllib2.urlopen(req)
        self.response.headers['Content-Type'] = 'text/html'
        self.response.out.write(u.read())

application = webapp.WSGIApplication([('/', MainPage)], debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()