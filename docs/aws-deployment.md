# AWS Deployment Guide

## Topology

- `EC2`: Apache + static site + Spring Boot + Let's Encrypt certificate for `arepecilab.duckdns.org`

## 1. Prepare the current EC2

1. Confirm the DuckDNS record for `arepecilab.duckdns.org` points to the Apache EC2 public IP.
2. Install the Apache modules required for TLS and proxying:

```bash
sudo dnf install -y mod_ssl
sudo mkdir -p /var/www/secureapp
```

3. Copy the static client:

```bash
sudo cp -r apache/site/* /var/www/secureapp/
```

4. Enable the Apache site using `deploy/apache/secureapp.conf`.
5. Make sure the security group allows inbound `80` and `443`.

## 2. Install Java 17 on the same EC2

```bash
sudo dnf install -y java-17-amazon-corretto-headless
```

## 3. Build and copy the Spring jar

From your development machine:

```bash
mvn clean package
scp target/secureapp-0.0.1-SNAPSHOT.jar ec2-user@<YOUR_EC2_PUBLIC_IP>:/tmp/
```

On the EC2:

```bash
sudo mkdir -p /opt/secureapp/data /opt/secureapp/certs
sudo mv /tmp/secureapp-0.0.1-SNAPSHOT.jar /opt/secureapp/
```

## 4. Reuse the existing Let's Encrypt certificate

If Apache already has the certificate for `arepecilab.duckdns.org`, convert it to PKCS12 for Spring:

```bash
sudo bash deploy/scripts/build-pkcs12-from-letsencrypt.sh \
  arepecilab.duckdns.org \
  /opt/secureapp/certs/secureapp-local.p12 \
  "<CHANGE_ME>" \
  secureapp-local
```

## 5. Install the Spring service

1. Copy `deploy/spring/secureapp.service` to `/etc/systemd/system/secureapp.service`.
2. Replace the placeholder keystore password.
3. Keep `SERVER_ADDRESS=127.0.0.1` so the backend is not public.
4. Start the service:

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now secureapp
sudo systemctl status secureapp
```

## 6. Configure Apache reverse proxy

Important directives:

- `ProxyPass /api https://127.0.0.1:5000/api`
- `ProxyPassReverse /api https://127.0.0.1:5000/api`
- `SSLProxyEngine on`
- `SSLProxyCheckPeerName off`

After copying the config:

```bash
sudo apachectl configtest
sudo systemctl restart httpd
```

## 7. Validation Checklist

Run these checks and capture screenshots:

```bash
curl -I https://arepecilab.duckdns.org
curl https://arepecilab.duckdns.org/api/public/info
```

Then from the browser:

1. Open `https://arepecilab.duckdns.org`.
2. Register a user.
3. Login.
4. Load the protected profile.
5. Show the browser lock icon and certificate details.