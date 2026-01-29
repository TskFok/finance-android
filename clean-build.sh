#!/usr/bin/env bash

set -euo pipefail

# 切到项目根目录
cd "$(dirname "$0")"

echo "===> 清理构建缓存..."

# 清理 Gradle 缓存
./gradlew clean

# 删除构建目录
rm -rf app/build
rm -rf build
rm -rf .gradle

# 删除 Android Studio 缓存
rm -rf .idea/caches
rm -rf .idea/modules.xml
rm -rf .idea/workspace.xml

echo "===> 清理完成！"
echo "    现在可以重新构建项目："
echo "    ./gradlew :app:assembleDebug"
