## Applitools Visual AI Setup

To run tests with Applitools Visual AI:

1. Sign up for a <a href="https://auth.applitools.com/users/general-register?app=eyes" target="_blank" rel="noopener noreferrer">
   Applitools trial account
   </a>

2. Obtain your **APPLITOOLS_API_KEY**

3. Configure the API key using one of the following options:

### Option A: Environment Variable (Recommended)
```bash

export APPLITOOLS_API_KEY=<your_api_key>
```

### Option B: Hardcode in Test (Not recommended for CI)
Update the following line in  
`src/test/java/com/eot/e2e/negative/JioRecharge_UFG_Test.java`

```java
eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
```

Replace with:
```java
eyes.setApiKey("<replace_me>");
```

---

## Enable/Disable Applitools validation (DISABLE_EYES)

By default, Applitools validations are **skipped**.

- Default: `DISABLE_EYES=true` (Eyes validations will be skipped)
- To enable Applitools validations: set `DISABLE_EYES=false`

### Disable Eyes (default)
```bash

export DISABLE_EYES=true
```

### Enable Eyes validations
```bash

export DISABLE_EYES=false
```