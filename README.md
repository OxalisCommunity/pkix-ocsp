# OCSP Client



## Getting started

Include dependency in your pom.xml:

```xml
<dependency>
    <groupId>network.oxalis</groupId>
    <artifactId>oxalis-ocsp</artifactId>
    <version>1.0.0</version>
</dependency>
```

Create your own validator:

```java
// Create OCSP Client using builder.
OcspClient client = OcspClient.builder()
        .set(OcspClient.EXCEPTION_ON_UNKNOWN, false) // Remove to trigger exception on 'UNKNOWN'.
        .set(OcspClient.EXCEPTION_ON_REVOKED, false) // Remove to trigger exception on 'REVOKED'.
        .build();

// Verify certificate (issuer certificate required).
CertificateResult response = client.verify(certificate, issuer);

// Prints 'GOOD', 'REVOKED' or 'UNKNOWN'.
System.out.println(response.getStatus());
```

### Note
This implementation is based on Original version from klakegg / pkix-ocsp version 0.9.1 (https://github.com/klakegg/pkix-ocsp)
