import os.path
import urllib2
def app(environ, start_response):
    cache_dir = '/tmp'
    gchart_url = 'http://chart.apis.google.com/chart?chst=d_map_spin&chld=%.1f|0|%s|%i|b|%s'

    fname = environ['PATH_INFO'].split('/').pop()
    local = os.path.join(cache_dir, fname)

    content = None
    if os.path.exists(local):
        with open(local, 'r') as f: content = f.read()
    else:
        (txt, col, icon_size, font_size) = os.path.splitext(fname)[0].split('_')
        url = gchart_url % (int(icon_size) / 10.0, col, int(font_size), txt)
        response = urllib2.urlopen(url)
        length = int(response.info().getheader('Content-Length'))
        content = response.read(length)
        with open(local, 'w') as f: f.write(content)
    
    start_response("200 OK", [
        ("Content-Type", "image/png"),
        ("Content-Length", str(len(content)))])
    
    return iter([content])