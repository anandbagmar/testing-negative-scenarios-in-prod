#!/usr/bin/env bash
set -euo pipefail

# Enable debug by running: DEBUG=1 ./run-specmatic-studio.sh
DEBUG="${DEBUG:-0}"

log()  { echo "ℹ️  $*"; }
ok()   { echo "✅ $*"; }
warn() { echo "⚠️  $*"; }
err()  { echo "❌ $*" >&2; }

if [[ "$DEBUG" == "1" ]]; then
  set -x
  log "Debug mode ON"
fi

# Run from project root (script location)
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TEMP_DIR="$PROJECT_DIR/temp"
WORK_DIR="$PROJECT_DIR/src/test/resources/specmatic"

log "PROJECT_DIR=$PROJECT_DIR"
log "TEMP_DIR=$TEMP_DIR"
log "WORK_DIR=$WORK_DIR"

if [[ ! -d "$TEMP_DIR" ]]; then
  err "temp directory not found: $TEMP_DIR"
  exit 1
fi

if [[ ! -d "$WORK_DIR" ]]; then
  err "work directory not found: $WORK_DIR"
  exit 1
fi

LATEST_JAR="$(ls -1 "$TEMP_DIR"/specmatic-studio-*.jar 2>/dev/null | sort -V | tail -n 1 || true)"
if [[ -z "${LATEST_JAR:-}" ]]; then
  err "No specmatic-studio jar found in: $TEMP_DIR"
  log "Contents of temp:"
  ls -la "$TEMP_DIR" || true
  exit 1
fi

JAR_NAME="$(basename "$LATEST_JAR")"
ok "Using jar: $JAR_NAME"
ok "Running from: $WORK_DIR"

cd "$WORK_DIR"
log "PWD=$(pwd)"

# Correct relative path: from src/test/resources/specmatic -> project root temp is ../../../../temp
REL_JAR_PATH="../../../../temp/$JAR_NAME"
ABS_JAR_PATH="$PROJECT_DIR/temp/$JAR_NAME"

log "REL_JAR_PATH=$REL_JAR_PATH"
log "ABS_JAR_PATH=$ABS_JAR_PATH"

# Validate jar exists (both relative and absolute perspectives)
if [[ ! -f "$REL_JAR_PATH" ]]; then
  warn "Jar not found via relative path: $REL_JAR_PATH"
fi

if [[ ! -f "$ABS_JAR_PATH" ]]; then
  err "Jar not found via absolute path: $ABS_JAR_PATH"
  log "Contents of temp:"
  ls -la "$TEMP_DIR" || true
  exit 1
fi

log "Jar size (bytes): $(wc -c < "$ABS_JAR_PATH" | tr -d ' ')"
log "Java version:"
java -version || true

ok "Starting Specmatic Studio..."
# Prefer relative path (matches your intention). If that fails, fall back to absolute.
if [[ -f "$REL_JAR_PATH" ]]; then
  java -jar "$REL_JAR_PATH" --specs-dir=proxy_recordings_sample proxy
else
  warn "Falling back to absolute jar path..."
  java -jar "$ABS_JAR_PATH" --specs-dir=proxy_recordings_sample proxy
fi
