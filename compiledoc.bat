@ECHO ON

SET GAME_CLASS_PATH=%CD%\..\bomberman\src\com\orbischallenge\bombman\api\game\
SET GAME_CLASSES_TO_COMPILE=%GAME_CLASS_PATH%MapItems.java %GAME_CLASS_PATH%PlayerAction.java %GAME_CLASS_PATH%PowerUps.java
SET CLIENT_CLASS_PATH=%CD%\bombmanplayer\
SET CLIENT_CLASSES=%CLIENT_CLASS_PATH%Move*.java %CLIENT_CLASS_PATH%Bomb*.java %CLIENT_CLASS_PATH%Player.java

SET DEST="..\..\docs\javadoc"
javadoc %GAME_CLASSES_TO_COMPILE% %CLIENT_CLASSES% -d %DEST%