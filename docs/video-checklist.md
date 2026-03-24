# Video Checklist

Use this as the structure for the final video:

1. Show the GitHub repository and the main folders:
   - `apache/site`
   - `src/main/java`
   - `deploy/`
   - `docs/`
2. Show the AWS console with the single EC2 instance.
3. Show the DuckDNS domain `arepecilab.duckdns.org`.
4. Open `https://arepecilab.duckdns.org` in the browser and show the TLS lock.
5. Register a new user.
6. Login and show the JSON token response in the UI.
7. Click `Load profile` and `Load secure status`.
8. Explain that passwords are stored as BCrypt hashes and not in plain text.
9. Show the Apache reverse proxy configuration.
10. Show the Spring `systemd` service and the PKCS12 certificate path.
11. Finish with a brief summary of how TLS protects:
    - client download from Apache
    - Apache to Spring communication inside the same EC2
    - authenticated user requests
