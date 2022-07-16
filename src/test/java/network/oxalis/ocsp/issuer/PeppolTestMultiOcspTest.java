package network.oxalis.ocsp.issuer;

import network.oxalis.ocsp.CertificateStatus;
import network.oxalis.ocsp.OcspException;
import network.oxalis.ocsp.OcspMultiClient;
import network.oxalis.ocsp.OcspResult;
import network.oxalis.ocsp.util.CertificateHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.cert.X509Certificate;
import java.util.Collections;

/**
 * @author erlend
 */
public class PeppolTestMultiOcspTest {

    private X509Certificate subjectExpired =
            CertificateHelper.parse(getClass().getResourceAsStream("/peppol-ap-test/certificate-expired.cer"));

    private X509Certificate subjectValid =
            CertificateHelper.parse(getClass().getResourceAsStream("/peppol-ap-test/certificate-valid.cer"));

    private X509Certificate issuer =
            CertificateHelper.parse(getClass().getResourceAsStream("/peppol-ap-test/issuer.cer"));

    @Test(enabled = false)
    public void simple() throws OcspException {
        OcspMultiClient ocspMultiClient = OcspMultiClient.builder()
                .set(OcspMultiClient.INTERMEDIATES, Collections.singletonList(issuer))
                .build();

        OcspResult ocspResult = ocspMultiClient.verify(
                subjectValid, subjectExpired
        );

        Assert.assertEquals(ocspResult.get(subjectValid).getStatus(), CertificateStatus.GOOD);
        Assert.assertNull(ocspResult.get(subjectExpired)); // Multi not supported.
    }
}
