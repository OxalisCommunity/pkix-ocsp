[![pkix-ocsp Master Build](https://github.com/OxalisCommunity/pkix-ocsp//workflows/pkix-ocsp%20Master%20Build/badge.svg?branch=master)](https://github.com/OxalisCommunity/pkix-ocsp/actions?query=workflow%3A%22pkix-ocsp%20Master%20Build%22)
[![Maven Central](https://img.shields.io/maven-central/v/network.oxalis.pkix/pkix-ocsp.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22network.oxalis.pkix%22%20AND%20a%3A%22pkix-ocsp%22)


# OCSP Client



## Getting started

Include dependency in your pom.xml:

```xml
<dependency>
    <groupId>network.oxalis.pkix</groupId>
    <artifactId>pkix-ocsp</artifactId>
    <version>2.1.0</version>
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



---
### Note
This implementation is based on Original version from klakegg / pkix-ocsp version 0.9.1 (https://github.com/klakegg/pkix-ocsp)
