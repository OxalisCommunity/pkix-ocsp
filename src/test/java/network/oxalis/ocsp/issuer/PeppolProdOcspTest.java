package network.oxalis.ocsp.issuer;

import network.oxalis.ocsp.CertificateStatus;
import network.oxalis.ocsp.OcspClient;
import network.oxalis.ocsp.OcspException;
import network.oxalis.ocsp.util.CertificateHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.cert.X509Certificate;
import java.util.Collections;

/**
 * @author erlend
 */
public class PeppolProdOcspTest {

    private X509Certificate subjectValid01 =
            CertificateHelper.parse(getClass().getResourceAsStream("/peppol-ap-prod/certificate-valid-01.cer"));

    private X509Certificate subjectValid02 =
            CertificateHelper.parse(getClass().getResourceAsStream("/peppol-ap-prod/certificate-valid-02.cer"));

    private X509Certificate issuer =
            CertificateHelper.parse(getClass().getResourceAsStream("/peppol-ap-prod/issuer.cer"));

    @Test(enabled = false)
    public void simple() throws OcspException {
        OcspClient ocspClient = OcspClient.builder()
                .build();

        Assert.assertEquals(ocspClient.verify(subjectValid01, issuer).getStatus(), CertificateStatus.GOOD);
        Assert.assertEquals(ocspClient.verify(subjectValid02, issuer).getStatus(), CertificateStatus.GOOD);
    }

    @Test(enabled = false)
    public void providedIssuers() throws OcspException {
        OcspClient ocspClient = OcspClient.builder()
                .set(OcspClient.INTERMEDIATES, Collections.singletonList(issuer))
                .build();

        Assert.assertEquals(ocspClient.verify(subjectValid01).getStatus(), CertificateStatus.GOOD);
        Assert.assertEquals(ocspClient.verify(subjectValid02).getStatus(), CertificateStatus.GOOD);
    }
}
