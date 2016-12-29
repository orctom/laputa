## Laputa
A Mirocservice Framework

### quick start
 1. You need add `SpringFramework` to the classpath
 1. Create a class with the `main` method, similar to `SpringBoot`
 1. TODO...

### application.conf
The configuration file is format of [HOCON](https://github.com/typesafehub/config#using-hocon-the-json-superset)

### SSL
To enable ssl, you just need to provide the path of certificate and private key in `application.conf`.
Sample:
```
server {
  ...
  https {
    port = 7443
    privateKey = "ssl/private.key"
    certificate = "ssl/certificate.crt"
  }
  ...
}
```

#### Key
The expected format of private key is PKCS#8, if you got a traditional format of PKCS#1, you need convert it.
```
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in pkcs1.key -out pkcs8.key
```

If your private key starts with `BEGIN RSA PRIVATE KEY`, which means your private key is format of PKCS#1;
While PKCS#8 starts with `BEGIN PRIVATE KEY`
