# OCPP Tesing #4

```shell
# Install websocat
curl -L https://github.com/vi/websocat/releases/download/v1.11.0/websocat.x86_64-unknown-linux-musl -o websocat
chmod +x websocat

# Connect to the central system
./websocat ws://localhost:8887
```

- Send Boot notification:

```javascript
[2,"1234",  "BootNotification",{"chargePointVendor": "Test","chargePointModel": "Test"}]
```