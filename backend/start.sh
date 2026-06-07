#!/bin/bash
echo "========================================"
echo "  云悟英语 - 后端服务启动"
echo "========================================"
cd "$(dirname "$0")"

echo "[1/2] Building..."
mvn package -DskipTests -q
if [ $? -ne 0 ]; then
    echo "[FAIL] Build failed!"
    exit 1
fi

echo "[2/2] Starting on port 2667..."
echo
java -jar yunwu-bootstrap/target/yunwu-bootstrap-1.0.0-SNAPSHOT.jar
