package network.oxalis.pkix.ocsp.issuer;

import network.oxalis.pkix.ocsp.CertificateStatus;
import network.oxalis.pkix.ocsp.OcspException;
import network.oxalis.pkix.ocsp.OcspMultiClient;
import network.oxalis.pkix.ocsp.OcspResult;
import network.oxalis.pkix.ocsp.util.CertificateHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.cert.X509Certificate;
import java.util.Collections;

/**
 * @author erlend
 * @author aaron-kumar
 */
public class PeppolTestMultiOcspTest {

    private X509Certificate subjectExpired =
            CertificateHelper.parse(getClass().getResourceAsStream("/peppol-ap-test/certificate-expired.cer"));

    private X509Certificate subjectValid =
            CertificateHelper.parse(getClass().getResourceAsStream("/peppol-ap-test/certificate-valid.cer"));

    private X509Certificate issuer =
            CertificateHelper.parse(getClass().getResourceAsStream("/peppol-ap-test/issuer.cer"));

    @Test
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
