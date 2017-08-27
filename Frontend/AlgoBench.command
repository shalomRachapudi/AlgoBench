cd `dirname $0`
java -jar frontEnd/AlgoBench_GUI.jar . backEnd/Algobench_Backend
osascript -e 'tell application "Terminal" to quit' &
exit
