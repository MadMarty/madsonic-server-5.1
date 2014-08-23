@echo off

:: ###################################################################################
:: # Shell script for starting Madsonic.  See http://madsonic.org.
:: ###################################################################################

:settings
 setlocal ENABLEEXTENSIONS ENABLEDELAYEDEXPANSION

 REM JAVA JRE/JDK path
 set JRE_HOME=
 set JDK_HOME=

 REM  The directory where Madsonic will create files. Make sure it is writable.
 set MADSONIC_HOME=c:\madsonic

 REM  The host name or IP address on which to bind Madsonic. Only relevant if you have
 REM  multiple network interfaces and want to make Madsonic available on only one of them.
 REM  The default value 0.0.0.0 will bind Madsonic to all available network interfaces.
 set MADSONIC_HOST=0.0.0.0

 REM  The port on which Madsonic will listen for incoming HTTP traffic.
 set MADSONIC_PORT=4040

 REM  The port on which Madsonic will listen for incoming HTTPS traffic (0 to disable).
 set MADSONIC_HTTPS_PORT=0

 REM  The context path (i.e., the last part of the Madsonic URL).  Typically "/" or "/madsonic".
 set MADSONIC_CONTEXT_PATH=/

 REM  The directory for music
 set MADSONIC_DEFAULT_MUSIC_FOLDER=c:\Media\Artists

 REM  The directory for upload
 set MADSONIC_DEFAULT_UPLOAD_FOLDER=c:\Media\Incoming

 REM  The directory for Podcast
 set MADSONIC_DEFAULT_PODCAST_FOLDER=c:\Media\Podcast

 REM  The directory for Playlist-Import
 set MADSONIC_DEFAULT_PLAYLIST_IMPORT_FOLDER=c:\Media\Playlists\Import

 REM  The directory for Playlist-Export
 set MADSONIC_DEFAULT_PLAYLIST_EXPORT_FOLDER=c:\Media\Playlists\Export

 REM  The directory for Playlist-Backup
 set MADSONIC_DEFAULT_PLAYLIST_BACKUP_FOLDER=c:\Media\Playlists\Backup

 REM  The memory initial size (Init Java heap size) in megabytes.
 set INIT_MEMORY=192

 REM  The memory limit (max Java heap size) in megabytes.
 set MAX_MEMORY=384


:getjrelocation
 rem Resolve location of Java runtime environment
 set KeyName=HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\Java Runtime Environment
 set Cmd=reg query "%KeyName%" /s
 for /f "tokens=2*" %%i in ('%Cmd% ^| find "JavaHome"') do set JRE_HOME=%%j

:getjdklocation
 rem Resolve location of Java JDK environment
 set KeyName=HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\Java Development Kit
 set Cmd=reg query "%KeyName%" /s
 for /f "tokens=2*" %%i in ('%Cmd% ^| find "JavaHome"') do set JDK_HOME=%%j

:setenv
 rem Check
 if "%JRE_HOME%%JDK_HOME%"=="" goto errornojava
 
 if not "%JRE_HOME%"=="" set JAVA_HOME=%JRE_HOME%
 if not "%JDK_HOME%"=="" set JAVA_HOME=%JDK_HOME%
 
 if not "%JAVA_HOME%"=="" (
		"%JAVA_HOME%\bin\java.exe" -Xms%INIT_MEMORY%m -Xmx%MAX_MEMORY%m -Dmadsonic.home=%MADSONIC_HOME% -Dmadsonic.host=%MADSONIC_HOST% -Dmadsonic.port=%MADSONIC_PORT% -Dmadsonic.httpsPort=%MADSONIC_HTTPS_PORT% -Dmadsonic.contextPath=%MADSONIC_CONTEXT_PATH%  -Dmadsonic.defaultMusicFolder=%MADSONIC_DEFAULT_MUSIC_FOLDER% -Dmadsonic.defaultUploadFolder=%MADSONIC_DEFAULT_UPLOAD_FOLDER% -Dmadsonic.defaultPodcastFolder=%MADSONIC_DEFAULT_PODCAST_FOLDER% -Dmadsonic.defaultPlaylistImportFolder=%MADSONIC_DEFAULT_PLAYLIST_IMPORT_FOLDER% -Dmadsonic.defaultPlaylistExportFolder=%MADSONIC_DEFAULT_PLAYLIST_EXPORT_FOLDER% -Dmadsonic.defaultPlaylistBackupFolder=%MADSONIC_DEFAULT_PLAYLIST_BACKUP_FOLDER% -jar madsonic-booter.jar
 )
 goto end

:errornojava
 echo Failed to locate any installed java environments event tried locating a JDK, please install a Java Runtime Evnironment or JDK
 goto end
 
:end
 endlocal