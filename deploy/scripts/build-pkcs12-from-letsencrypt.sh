#!/usr/bin/env bash
set -euo pipefail

DOMAIN="${1:-}"
OUTPUT_FILE="${2:-/opt/secureapp/certs/spring-api.p12}"
PASSWORD="${3:-change-this-password}"
ALIAS="${4:-spring-api}"

if [[ -z "${DOMAIN}" ]]; then
  echo "Usage: $0 <domain> [output-file] [password] [alias]" >&2
  exit 1
fi

sudo mkdir -p "$(dirname "${OUTPUT_FILE}")"

sudo openssl pkcs12 -export \
  -in "/etc/letsencrypt/live/${DOMAIN}/fullchain.pem" \
  -inkey "/etc/letsencrypt/live/${DOMAIN}/privkey.pem" \
  -out "${OUTPUT_FILE}" \
  -name "${ALIAS}" \
  -passout "pass:${PASSWORD}"

echo "PKCS12 keystore created at ${OUTPUT_FILE}"
