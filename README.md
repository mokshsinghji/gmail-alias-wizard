# gmail-alias-wizard

First make the application.yaml file in the `server/src/main/resources` directory.

This requires the following:
```yaml
ktor:
  application:
    modules:
      - com.moksh.ApplicationKt.module
    dependencies:
      - com.moksh.config.DatabaseConnectionKt.provideDatabaseConnection
      - com.moksh.services.AuthServiceImpl
      - com.moksh.services.GoogleServiceImpl
  deployment:
    port: 8080

database:
  url: jdbc:sqlite:./data.db # can be changed to any other database

auth:
  sessions:
    encryptKey: <CREATE_WITH_OPENSSL>
    signKey: <CREATE_WITH_OPENSSL>
  google:
    clientId: <YOUR_GOOGLE_CLIENT_ID>
    clientSecret: <YOUR_GOOGLE_CLIENT_SECRET>
    appPassword: <YOUR_GMAIL_APP_PASSWORD>
    smtpUsername: <YOUR_GMAIL_ADDRESS>
```
