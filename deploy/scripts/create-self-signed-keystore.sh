#!/usr/bin/env bash
set -euo pipefail

OUTPUT_FILE="${1:-src/main/resources/ecikeystore.p12}"
PASSWORD="${2:-123456.}"
ALIAS="${3:-ecikeypair}"
CN="${4:-localhost}"

keytool -genkeypair \
  -alias "${ALIAS}" \
  -keyalg RSA \
  -keysize 2048 \
  -storetype PKCS12 \
  -keystore "${OUTPUT_FILE}" \
  -validity 3650 \
  -storepass "${PASSWORD}" \
  -keypass "${PASSWORD}" \
  -dname "CN=${CN}, OU=AREP, O=Escuela Colombiana de Ingenieria, L=Bogota, S=Cundinamarca, C=CO"
