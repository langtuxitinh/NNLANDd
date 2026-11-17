#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
if [ ! -f target/NNland.war ]; then
  command -v mvn >/dev/null 2>&1 || { echo 'Cần Maven hoặc build dự án bằng NetBeans trước.'; exit 1; }
  mvn package
fi
exec java -jar target/NNland.war
