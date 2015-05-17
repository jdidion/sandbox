#!/usr/bin/env python

import os
import sys

sys.path.append("%s/src/lib/python" % os.environ['LAB_HOME'])
import ex

if __name__ == '__main__': sys.exit(script(sys.argv[1:]))

infile = ex.options.InfilePattern(default_file=os.path.join('%(strain)s', 'chr%(chr)s.csv'))

test_sql = '''
SELECT id, name 
  FROM foo_%(chr) 
  WHERE strain = %(strain)
  INTO OUTFILE "%(infile)" FIELDS TERMINATED BY ","
'''

script = Script(
    ex.components.sql.SQL(test_sql, options=infile),
    desc="Test script",
    options=[
        ex.options.bio.Chromosomes(), 
        ex.options.bio.Strains()
    ]
)