;NSIS Modern User Interface
;Header Bitmap Example Script
;Written by Joost Verburg

;--------------------------------
;Include Modern UI

  !include "MUI2.nsh"

;--------------------------------

;Variables

  Var LanzaAyudaJar

;--------------------------------
;General

  ;Name and file
  Name "LanzaAyuda"
  OutFile "InstallAyuda.exe"
  Unicode True

  ;Default installation folder
  InstallDir "$PROGRAMFILES\LanzaAyuda"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\LanzaAyuda" ""
 

  ;Request application privileges for Windows Vista
  RequestExecutionLevel admin

;--------------------------------
;Interface Configuration

  !define MUI_HEADERIMAGE 
  !define MUI_HEADERIMAGE_BITMAP "logo.bmp" ; optional
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
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\LanzaAyuda" 
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Lanza Ayuda"

  !insertmacro MUI_PAGE_STARTMENU Application $LanzaAyudaJar
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
 
  !insertmacro MUI_LANGUAGE "English"
;--------------------------------
;Installer Sections

Section "Dummy Section" SecDummy

  SetOutPath "$INSTDIR"
  
  ;ADD YOUR OWN FILES HERE...
  File /r "LanzaAyuda.7z"
	Nsis7z::Extract "LanzaAyuda.7z"
  Delete "$INSTDIR\LanzaAyuda.7z"

  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
    ;Create shortcuts
    CreateDirectory "$SMPROGRAMS\$LanzaAyudaJar"
    CreateShortcut "$SMPROGRAMS\$LanzaAyudaJar\LanzaAyuda.lnk" "$INSTDIR\LanzaAyuda.jar"
    CreateShortcut "$SMPROGRAMS\$LanzaAyudaJar\Uninstall.lnk" "$INSTDIR\UninstallLanzaAyuda.exe"
  
  !insertmacro MUI_STARTMENU_WRITE_END
	
  ;Store installation folder
  WriteRegStr HKCU "Software\LanzaAyuda" "" $INSTDIR
  
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\UninstallLanzaAyuda.exe"
  CreateShortCut "$DESKTOP\LanzaAyuda.lnk" "$INSTDIR\LanzaAyuda.jar"

SectionEnd
Section /o "Incluir Icono de Escritorio"
  ; Creates Desktop Shortcut
  CreateShortCut "$DESKTOP\LanzaAyuda.lnk" "$INSTDIR\LanzaAyuda.jar"
SectionEnd



;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_SecDummy ${LANG_ENGLISH} "Paquete de ayuda"

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecDummy} $(DESC_SecDummy)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END
 
;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;ADD YOUR OWN FILES HERE...
  Delete "$INSTDIR\LanzaAyuda.7z"

  Delete "$INSTDIR\lib\javahelp-2.0.05.jar"
  RMDir "$INSTDIR\lib"
  Delete "$INSTDIR\LanzaAyuda.jar"
  Delete "$INSTDIR\README.TXT"
  Delete "$INSTDIR\UninstallLanzaAyuda.exe"
  

  RMDir "$INSTDIR"

!insertmacro MUI_STARTMENU_GETFOLDER Application $LanzaAyudaJar

  Delete "$SMPROGRAMS\$LanzaAyudaJar\LanzaAyuda.lnk"
  Delete "$SMPROGRAMS\$LanzaAyudaJar\Uninstall.lnk"
  RMDir "$SMPROGRAMS\$LanzaAyudaJar"

  Delete "$DESKTOP\LanzaAyuda.lnk"

  DeleteRegKey /ifempty HKCU "Software\LanzaAyuda"

SectionEnd