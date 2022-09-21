## kauth

Open-source authentication system made in [Kotlin](https://github.com/JetBrains/kotlin)

#### Run

`java -jar kauth.jar Parameters...`

Parameters:

- `string` host (default: `"127.0.0.1"`)
- `integer` port (default: `8080`)
- `string` jwt-audience (default: `"http://${host}:${port}/api"`)
- `string` jwt-issuer (default: `"http://${host}:${port}/api"`)
- `string` jwt-secret (default: `"jwt-secret"`)
- `string` jwt-realm (default: `"Access to 'kauth'"`)

#### TODO

- [ ] **Webhooks**
- [ ] **Files**

#### Links

- [kauth-dashboard](https://github.com/ya-ilya/kauth-dashboard)