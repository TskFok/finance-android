#!/usr/bin/env bash

set -euo pipefail

# 切到项目根目录（脚本所在目录）
cd "$(dirname "$0")"

VARIANT="${1:-release}"  # 可选参数：release（默认）或 debug

if [[ "$VARIANT" != "release" && "$VARIANT" != "debug" ]]; then
  echo "用法: $0 [release|debug]"
  exit 1
fi

echo "===> 使用变体: $VARIANT，开始编译 APK..."

if [[ "$VARIANT" == "release" ]]; then
  ./gradlew :app:assembleRelease
  APK_PATH="app/build/outputs/apk/release/app-release.apk"
else
  ./gradlew :app:assembleDebug
  APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
fi

if [[ ! -f "$APK_PATH" ]]; then
  echo "未找到 APK 文件: $APK_PATH"
  exit 1
fi

TIMESTAMP="$(date +%Y%m%d-%H%M%S)"
APK_NAME="finance-${VARIANT}-${TIMESTAMP}.apk"
DEST_PATH="./${APK_NAME}"

cp "$APK_PATH" "$DEST_PATH"

echo "===> 编译完成，APK 已复制到项目根目录:"
echo "     $DEST_PATH"

