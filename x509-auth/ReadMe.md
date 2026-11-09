# X509 Authentication


## Certificates

### 1. Create a server keystore (contains server private key + certificate)

```
keytool -genkeypair -alias server -keyalg RSA -keysize 3048 -validity 365 -keystore server.p12 -storetype PKCS12 -storepass changeit -dname "CN=localhost"
```

### 2. Export the server's public certificate

```
keytool -exportcert -alias server -file server.crt -keystore server.p12 -storepass changeit
```

### 3. Create a client keystore (contains client private key + certificate)

```
keytool -genkeypair -alias client -keyalg RSA -keysize 3048 -validity 365 -keystore client-a.p12 -storetype PKCS12 -storepass changeit -dname "CN=client-a"
```

### 4. Export the client's public certificate

```
keytool -exportcert -alias client -file client-a.crt -keystore client-a.p12 -storepass changeit
```

### 5. Import client's certificate into a server truststore (so the server trusts the client)

```
keytool -importcert -alias client-a -file client-a.crt -keystore server-truststore.p12 -storetype PKCS12 -storepass changeit -noprompt
```

### 6. Import server's certificate into a client truststore 

```
keytool -importcert -alias server -file server.crt -keystore client-truststore.p12 -storetype PKCS12 -storepass changeit -noprompt
```

* Server 
  * no sessions
  * server key and certificate loaded via keystore
  * client certificates are loaded via truststore

* Client
  * Server certificate is loaded via truststore (SslBundle)
  * Uses RestClient with custom configuration


There are two ways to configure custom SSL context on client side:

```java
@Bean
public RestClient restClient(RestClient.Builder restClientBuilder, RestClientSsl restClientSsl) {
    return restClientBuilder
            .apply(restClientSsl.fromBundle("client-bundle"))
            .build();
}
```

OR (specific to Apache HttpClient)

```java
@Bean
public RestClient restClient(SslBundles sslBundles) {
    SSLContext sslContext = sslBundles.getBundle("client-bundle").createSslContext();

    var clientTlsStrategy = new DefaultClientTlsStrategy(sslContext);

    var clientConnectionManager = PoolingHttpClientConnectionManagerBuilder.create()
            .setTlsSocketStrategy(clientTlsStrategy)
            .build();

    var httpClient = HttpClients.custom()
            .setConnectionManager(clientConnectionManager)
            .build();

    ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

    return RestClient.builder()
            .requestFactory(requestFactory)
            .build();
}
```