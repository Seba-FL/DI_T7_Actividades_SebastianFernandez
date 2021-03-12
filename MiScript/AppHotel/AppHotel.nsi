;NSIS Modern User Interface
;Header Bitmap Example Script
;Written by Joost Verburg

;--------------------------------
;Include Modern UI

  !include "MUI2.nsh"

;--------------------------------

;Variables

  Var AppHotelJar

;--------------------------------
;General

  ;Name and file
  Name "AppHotel"
  OutFile "InstallAppHotel.exe"
  Unicode True

  ;Default installation folder
  InstallDir "$PROGRAMFILES\AppHotel"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\AppHotel" ""
 

  ;Request application privileges for Windows Vista
  RequestExecutionLevel admin

;--------------------------------
;Interface Configuration

  !define MUI_HEADERIMAGE 
  !define MUI_HEADERIMAGE_BITMAP "log.bmp" ; optional
  !define MUI_ABORTWARNING

;--------------------------------
;Pages
  !insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_LICENSE "License.txt"
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES

;Start Menu Folder Page Configuration
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\AppHotel" 
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "App Hotel"

  !insertmacro MUI_PAGE_STARTMENU Application $AppHotelJar
  !insertmacro MUI_PAGE_FINISH
  
  !insertmacro MUI_UNPAGE_WELCOME
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_LICENSE "License.txt"
  !insertmacro MUI_UNPAGE_COMPONENTS
  !insertmacro MUI_UNPAGE_DIRECTORY
  !insertmacro MUI_UNPAGE_INSTFILES
  !insertmacro MUI_UNPAGE_FINISH

    
  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "Spanish"
;--------------------------------
;Installer Sections

Section "App Hotel" SecDummy

  SetOutPath "$INSTDIR"
  
  ;ADD YOUR OWN FILES HERE...
  File /r "AppHotelFirmado.7z"
	Nsis7z::Extract "AppHotelFirmado.7z"
  Delete "$INSTDIR\AppHotelFirmado.7z"

  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
    ;Create shortcuts
    CreateDirectory "$SMPROGRAMS\$AppHotelJar"
    CreateShortcut "$SMPROGRAMS\$AppHotelJar\AppHotel.lnk" "$INSTDIR\sAppHotel.jar"
    CreateShortcut "$SMPROGRAMS\$AppHotelJar\Uninstall.lnk" "$INSTDIR\UninstallAppHotel.exe"
  
  !insertmacro MUI_STARTMENU_WRITE_END
	
  ;Store installation folder
  WriteRegStr HKCU "Software\AppHotel" "" $INSTDIR
  
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\UninstallAppHotel.exe"
  CreateShortCut "$DESKTOP\AppHotel.lnk" "$INSTDIR\sAppHotel.jar"

SectionEnd
Section /o "Incluir Icono de Escritorio"
  ; Creates Desktop Shortcut
  CreateShortCut "$DESKTOP\AppHotel.lnk" "$INSTDIR\sAppHotel.jar"
SectionEnd



;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_SecDummy ${LANG_SPANISH} "Paquete de ayuda"

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecDummy} $(DESC_SecDummy)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END
 
;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;ADD YOUR OWN FILES HERE...
  Delete "$INSTDIR\AppHotelFirmado.7z"

  ;Delete "$INSTDIR\lib\javahelp-2.0.05.jar"
  RMDir /r "$INSTDIR\lib"
  Delete "$INSTDIR\sAppHotel.jar"
   Delete "$INSTDIR\AppHotel.jnlp"
  Delete "$INSTDIR\README.TXT"
   Delete "$INSTDIR\AppHotel.html"
  Delete "$INSTDIR\UninstallAppHotel.exe"
  

  RMDir "$INSTDIR"

!insertmacro MUI_STARTMENU_GETFOLDER Application $AppHotelJar

  Delete "$SMPROGRAMS\$AppHotelJar\AppHotel.lnk"
  Delete "$SMPROGRAMS\$AppHotelJar\Uninstall.lnk"
  RMDir "$SMPROGRAMS\$AppHotelJar"

  Delete "$DESKTOP\AppHotel.lnk"

  DeleteRegKey /ifempty HKCU "Software\AppHotel"

SectionEnd