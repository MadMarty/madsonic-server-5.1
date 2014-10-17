# madsonic.nsi

!include "WordFunc.nsh"
!include "MUI.nsh"

!insertmacro VersionCompare

# The name of the installer
Name "Madsonic"

# The icon of the installer
Icon "madsonic.ico"



# The default installation directory
InstallDir C:\madsonic

# Registry key to check for directory (so if you install again, it will
# overwrite the old one automatically)
InstallDirRegKey HKLM "Software\Madsonic" "Install_Dir"

#--------------------------------
#Interface Configuration

!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_BITMAP "${NSISDIR}\Contrib\Graphics\Header\orange.bmp"
!define MUI_FINISHPAGE_SHOWREADME "$INSTDIR\Getting Started.html"
!define MUI_FINISHPAGE_SHOWREADME_TEXT "View Getting Started document"
!define MUI_WELCOMEFINISHPAGE
!define MUI_WELCOMEFINISHPAGE_BITMAP "${NSISDIR}\Contrib\Graphics\Wizard\arrow.bmp"
!define MUI_UNWELCOMEFINISHPAGE_BITMAP "${NSISDIR}\Contrib\Graphics\Wizard\orange-uninstall.bmp"

!define INSTALLSIZE 80000

#--------------------------------
# Pages

# This page checks for JRE
Page custom CheckInstalledJRE

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

# Languages
!insertmacro MUI_LANGUAGE "English"

Section "Madsonic"

  SectionIn RO
  
  # Install for all users
  SetShellVarContext "all"

  # Take backup of existing madsonic-service.exe.vmoptions
  CopyFiles /SILENT $INSTDIR\madsonic-service.exe.vmoptions $TEMP\madsonic-service.exe.vmoptions

  # Silently uninstall existing version.
  ExecWait '"$INSTDIR\uninstall.exe" /S _?=$INSTDIR'

  # Remove previous Jetty temp directory.
  RMDir /r "c:\madsonic\jetty"

  # Backup database.
  RMDir /r "c:\madsonic\db.backup"
  CreateDirectory "c:\madsonic\db.backup"
  CopyFiles /SILENT "c:\madsonic\db\*" "c:\madsonic\db.backup"

  # Set output path to the installation directory.
  SetOutPath $INSTDIR

  # Write files.
  File ..\..\..\target\madsonic-agent.exe
  File ..\..\..\target\madsonic-agent.exe.vmoptions
  File ..\..\..\target\madsonic-agent-elevated.exe
  File ..\..\..\target\madsonic-agent-elevated.exe.vmoptions
  File ..\..\..\target\madsonic-service.exe
  File ..\..\..\target\madsonic-service.exe.vmoptions
  File /oname=madsonic-booter.jar ..\..\..\..\madsonic-booter\target\madsonic-booter-jar-with-dependencies.jar 
  File ..\..\..\..\madsonic-main\README.TXT
  File ..\..\..\..\madsonic-main\LICENSE.TXT
  File "..\..\..\..\madsonic-main\Getting Started.html"
  File ..\..\..\..\madsonic-main\target\madsonic.war
  File ..\..\..\..\madsonic-main\target\classes\version.txt
  File ..\..\..\..\madsonic-main\target\classes\build_number.txt

  # Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\Madsonic "Install_Dir" "$INSTDIR"

  # Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Madsonic" "DisplayName" "Madsonic"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Madsonic" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Madsonic" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Madsonic" "NoRepair" 1
  WriteUninstaller "uninstall.exe"

  # Restore madsonic-service.exe.vmoptions
  CopyFiles /SILENT  $TEMP\madsonic-service.exe.vmoptions $INSTDIR\madsonic-service.exe.vmoptions
  Delete $TEMP\madsonic-service.exe.vmoptions

  # Write transcoding pack files.
  SetOutPath "c:\madsonic\transcode"
  File ..\..\..\..\madsonic-transcode-x86\windows\*.*

  # Add Windows Firewall exception.
  # (Requires NSIS plugin found on http://nsis.sourceforge.net/NSIS_Simple_Firewall_Plugin to be installed
  # as NSIS_HOME/Plugins/SimpleFC.dll)
  SimpleFC::AddApplication "Madsonic Service" "$INSTDIR\madsonic-service.exe" 0 2 "" 1
  SimpleFC::AddApplication "Madsonic Agent" "$INSTDIR\madsonic-agent.exe" 0 2 "" 1
  SimpleFC::AddApplication "Madsonic Agent (Elevated)" "$INSTDIR\madsonic-agent-elevated.exe" 0 2 "" 1

  # Install and start service.
  ExecWait '"$INSTDIR\madsonic-service.exe" -install'
  ExecWait '"$INSTDIR\madsonic-service.exe" -start'

  # Start agent.
  Exec '"$INSTDIR\madsonic-agent-elevated.exe" -balloon'

SectionEnd


Section "GenreMap Template"
  SetOutPath "c:\madsonic\config"
 File ..\..\..\..\madsonic-genre\genremap.cfg
 
SectionEnd

Section "Start Menu Shortcuts"

  CreateDirectory "$SMPROGRAMS\Madsonic"
  CreateShortCut "$SMPROGRAMS\Madsonic\Open Madsonic.lnk"          "$INSTDIR\madsonic.url"         ""         "$INSTDIR\madsonic-agent.exe"  0
  CreateShortCut "$SMPROGRAMS\Madsonic\Madsonic Tray Icon.lnk"     "$INSTDIR\madsonic-agent.exe"   "-balloon" "$INSTDIR\madsonic-agent.exe"  0
  CreateShortCut "$SMPROGRAMS\Madsonic\Start Madsonic Service.lnk" "$INSTDIR\madsonic-service.exe" "-start"   "$INSTDIR\madsonic-service.exe"  0
  CreateShortCut "$SMPROGRAMS\Madsonic\Stop Madsonic Service.lnk"  "$INSTDIR\madsonic-service.exe" "-stop"    "$INSTDIR\madsonic-service.exe"  0
  CreateShortCut "$SMPROGRAMS\Madsonic\Uninstall Madsonic.lnk"     "$INSTDIR\uninstall.exe"        ""         "$INSTDIR\uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\Madsonic\Getting Started.lnk"        "$INSTDIR\Getting Started.html" ""         "$INSTDIR\Getting Started.html" 0

  CreateShortCut "$SMSTARTUP\Madsonic.lnk"                         "$INSTDIR\madsonic-agent.exe"   ""         "$INSTDIR\madsonic-agent.exe"  0

SectionEnd

# Uninstaller

UninstallIcon "madsonic.ico"

Section "Uninstall"

  # Uninstall for all users
  SetShellVarContext "all"

  # Stop and uninstall service if present.
  ExecWait '"$INSTDIR\madsonic-service.exe" -stop'
  ExecWait '"$INSTDIR\madsonic-service.exe" -uninstall'

  # Stop agent by killing it.
  # (Requires NSIS plugin found on http://nsis.sourceforge.net/Processes_plug-in to be installed
  # as NSIS_HOME/Plugins/Processes.dll)
  Processes::KillProcess "madsonic-agent"
  Processes::KillProcess "madsonic-agent-elevated"
  Processes::KillProcess "Audioffmpeg"
  Processes::KillProcess "ffmpeg"
  Processes::KillProcess "lame"
  
  ExecWait "taskkill /f /im lame.exe"
  ExecWait "taskkill /f /im ffmpeg.exe"
  ExecWait "taskkill /f /im Audioffmpeg.exe"
  ExecWait "taskkill /f /im madsonic-agent.exe"
  ExecWait "taskkill /f /im madsonic-agent-elevated.exe"
  
  # Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Madsonic"
  DeleteRegKey HKLM SOFTWARE\Madsonic

  # Remove files.
  Delete "$SMSTARTUP\Madsonic.lnk"
  RMDir /r "$SMPROGRAMS\Madsonic"
  Delete "$INSTDIR\build_number.txt"
  Delete "$INSTDIR\elevate.exe"
  Delete "$INSTDIR\Getting Started.html"
  Delete "$INSTDIR\LICENSE.TXT"
  Delete "$INSTDIR\README.TXT"
  Delete "$INSTDIR\madsonic.url"
  Delete "$INSTDIR\madsonic.war"
  Delete "$INSTDIR\madsonic-agent.exe"
  Delete "$INSTDIR\madsonic-agent.exe.vmoptions"
  Delete "$INSTDIR\madsonic-agent-elevated.exe"
  Delete "$INSTDIR\madsonic-agent-elevated.exe.vmoptions"
  Delete "$INSTDIR\madsonic-booter.jar"
  Delete "$INSTDIR\madsonic-service.exe"
  Delete "$INSTDIR\madsonic-service.exe.vmoptions"
  Delete "$INSTDIR\uninstall.exe"
  Delete "$INSTDIR\version.txt"
  
  Delete "$INSTDIR\madsonic.ico"
  
  RMDir /r "$INSTDIR\log"
  RMDir "$INSTDIR"

  # Remove Windows Firewall exception.
  # (Requires NSIS plugin found on http://nsis.sourceforge.net/NSIS_Simple_Firewall_Plugin to be installed
  # as NSIS_HOME/Plugins/SimpleFC.dll)
  SimpleFC::RemoveApplication "$INSTDIR\elevate.exe"
  SimpleFC::RemoveApplication "$INSTDIR\madsonic-service.exe"
  SimpleFC::RemoveApplication "$INSTDIR\madsonic-agent.exe"
  SimpleFC::RemoveApplication "$INSTDIR\madsonic-agent-elevated.exe"

SectionEnd


Function CheckInstalledJRE
    # Read the value from the registry into the $0 register
	
#	!include x64.nsh
#	${If} ${RunningX64} 
#		SetRegView 64
#	    DetailPrint "Installer running on 64-bit host"
#		ReadRegStr $0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
#	${Else}   
#		SetRegView 32
#	    DetailPrint "Installer running on 32-bit host"
		ReadRegStr $0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
#	${EndIf}
	

    # Check JRE version. At least 1.7 is required.
    #   $1=0  Versions are equal
    #   $1=1  Installed version is newer
    #   $1=2  Installed version is older (or non-existent)
    ${VersionCompare} $0 "1.7" $1
    IntCmp $1 2 InstallJRE 0 0
    Return

    InstallJRE:
      # Launch Java web installer.
      MessageBox MB_OK "Java 7 was not found and will now be installed."
      File /oname=$TEMP\jre-setup.exe jre-7u71-windows-i586-iftw.exe
      ExecWait '"$TEMP\jre-setup.exe"' $0
      Delete "$TEMP\jre-setup.exe"

FunctionEnd
