@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"
where java >nul 2>nul
if errorlevel 1 (
  echo Chua tim thay Java. Hay cai JDK 8 tro len va kiem tra JAVA trong PATH.
  pause
  exit /b 1
)
if not exist "target\NNland.war" (
  where mvn >nul 2>nul
  if errorlevel 1 (
    echo Chua co ban WAR va chua tim thay Maven. Hay mo du an trong NetBeans va chon Build.
    pause
    exit /b 1
  )
  call mvn package
  if errorlevel 1 (
    echo Build chua thanh cong. Hay xem loi phia tren.
    pause
    exit /b 1
  )
)
echo NNland se chay tai http://localhost:8081
echo Can bat MySQL va dien application-local.properties truoc.
echo Dung Ctrl+C de dung ung dung.
java -jar "target\NNland.war"
pause
