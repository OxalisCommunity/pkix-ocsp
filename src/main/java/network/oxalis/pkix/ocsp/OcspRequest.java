package network.oxalis.pkix.ocsp;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.CertID;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author erlend
 */
class OcspRequest {

    private final List<Extension> extensions = new ArrayList<>();

    private final List<BigInteger> certificates = new ArrayList<>();

    private ASN1OctetString issuerNameHash;

    private ASN1OctetString issuerKeyHash;

    private AlgorithmIdentifier algorithmIdentifier;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();


    public void setIssuer(CertificateIssuer certificateIssuer) {
        this.issuerNameHash = new DEROctetString(certificateIssuer.getIssuerNameHash());
        this.issuerKeyHash = new DEROctetString(certificateIssuer.getIssuerKeyHash());
        this.algorithmIdentifier =
                new AlgorithmIdentifier(new ASN1ObjectIdentifier(certificateIssuer.getAlgorithmIdentifier()));
    }

    public void addNonce() {
        byte[] nonce = new byte[16];
        SECURE_RANDOM.nextBytes(nonce);
        addExtension(new Extension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false, new DEROctetString(nonce)));
    }

    public void addExtension(Extension extension) {
        this.extensions.add(extension);
    }

    public void addCertificates(BigInteger... serialNumbers) {
        Collections.addAll(this.certificates, serialNumbers);
    }

    protected byte[] generateRequest() throws OcspException {
        try {
            // Initiate OCSP Request Builder.
            OCSPReqBuilder reqBuilder = new OCSPReqBuilder();


            // Create request for each certificate.
            for (BigInteger certificate : certificates) {
                reqBuilder.addRequest(new CertificateID((new CertID(
                        algorithmIdentifier,
                        issuerNameHash,
                        issuerKeyHash,
                        new ASN1Integer(certificate)))));
            }

            if (!extensions.isEmpty())
                reqBuilder.setRequestExtensions(new Extensions(extensions.toArray(new Extension[0])));

            // Build OCSP Request.
            return reqBuilder.build().getEncoded();
        } catch (OCSPException | IOException e) {
            throw new OcspException("Exception while creating OCSP request: '%s'", e, e.getMessage());
        }
    }
}
