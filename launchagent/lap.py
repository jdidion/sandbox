#!/usr/bin/env python
# Create or update a launchctl plist that monitors one or more files or directories and
# executes a program when they change. Note that there is no way to inform the triggered
# program which path has changed, so this is really only useful for monitoring a single path,
# for programs that can check the status of all paths being monitored, or for applications in
# which it doesn't matter which file has changed.
# usage:
# lap.py -n test -c 'touch ~/Desktop/foo.txt' -w '~/Desktop/bar.txt' # create a plist
# lap.py -n test -w '~/Desktop/baz' # add a folder to watch
from argparse import ArgumentParser
import os
from plistlib import readPlist, writePlist
import subprocess

def main():
    parser = ArgumentParser()
    parser.add_argument('-n', '--name')
    parser.add_argument('-c', '--command')
    parser.add_argument('-w', '--watch_path', nargs="+")
    parser.add_argument('-o', '--overwrite', action='store_true', default=False)
    args = parser.parse_args()
    
    watch_files = []
    watch_dirs = []
    for p in args.watch_path:
        if os.path.exists(p) and os.path.isdir(p):
            watch_dirs.append(p)
        else:
            watch_files.append(p)
    
    plist = os.path.abspath(os.path.expanduser('~/Library/LaunchAgents/{0}.plist'.format(args.name)))
    
    if os.path.exists(plist):
        subprocess.call(["launchctl", "unload", plist])
    
    if os.path.exists(plist) and not args.overwrite:
        pl_keys = readPlist(plist)
        if 'QueueDirectories' in pl_keys:
            pl_keys['QueueDirectories'].extend(watch_dirs)
        else:
            pl_keys['QueueDirectories'] = watch_dirs
        if 'WatchPaths' in pl_keys:
            pl_keys['WatchPaths'].extend(watch_files)
        else:
            pl_keys['WatchPaths'] = watch_files
        
    else:
        pl_keys = dict(
            Label=args.name,
            ProgramArguments=args.command.split(' '),
            QueueDirectories=watch_dirs,
            WatchPaths=watch_files
        )
    
    writePlist(pl_keys, plist)

    subprocess.call(["launchctl", "load", plist])
    
if __name__ == "__main__":
    main()