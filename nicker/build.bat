set x=%cd%
md build\chrome
cd chrome\nicker
7z a -tzip "%x%.jar" * -r -mx=0
move "%x%.jar" ..\..\build\chrome
cd ..\..
md build\defaults
xcopy /S defaults build\defaults
copy chrome.manifest build
copy install.rdf build
copy license.txt build
cd build
7z a -tzip "%x%.xpi" * -r -mx=9
move "%x%.xpi" ..\
cd ..
rd build /s/q
