## Troubleshooting

### Emulator cannot access localhost:8080
Ensure:
```bash

adb reverse tcp:8080 tcp:8080
```

### SampleApps missing
Ensure you have copied `MockedE2EDemo-debug.apk` into:
```text
sampleApps/
```
