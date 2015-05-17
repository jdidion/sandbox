#!/usr/bin/env python
# encoding: utf-8
"""Loads a module that defines an ex.Script and executes the script. 

The behavior in this script can be replicated within a script module by making it executable 
and adding the following line:

if __name__ == '__main__': sys.exit(script(sys.argv[1:]))
"""
import imp
import sys

def main(argv=sys.argv[1:]):
    assert len(argv) > 0
    
    # Script file must be the first argument. Import and look for the 'script' variable.
    try:
        script_module = imp.load_source('script', argv[0])
    except:
        logging.critical("Could not load script module %s", script_file)
        return 1
    
    if 'script' not in dir(script_module):
        logging.critical("Script must be assigned to the 'script' variable in %s", script_file)
        return 1
    
    # Execute the script and return an exit status
    return script_module.script(argv[1:])

if __name__ == '__main__':
    sys.exit(main())
