package network.oxalis.pkix.ocsp;

import network.oxalis.pkix.ocsp.api.OcspFetcher;
import network.oxalis.pkix.ocsp.api.OcspFetcherResponse;
import network.oxalis.pkix.ocsp.builder.Properties;
import network.oxalis.pkix.ocsp.builder.Property;
import network.oxalis.pkix.ocsp.fetcher.UrlOcspFetcher;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

/**
 * This abstract class implements common features related to OCSP verification.
 *
 * @author erlend
 * @author aaron-kumar
 */
class AbstractOcspClient {

    public static final Property<Boolean> EXCEPTION_ON_NO_PATH = Property.create(false);

    public static final Property<OcspFetcher> FETCHER = Property.create(UrlOcspFetcher.builder().build());

    public static final Property<List<X509Certificate>> INTERMEDIATES =
            Property.create(Collections.<X509Certificate>emptyList());

    public static final Property<URI> OVERRIDE_URL = Property.create();

    public static final Property<Boolean> NONCE = Property.create(false);

    /**
     * Properties provided by the builder.
     */
    protected final Properties properties;

    /**
     * Constructor accepting properties provided by the builder.
     *
     * @param properties Properties provided by the builder.
     */
    protected AbstractOcspClient(Properties properties) {
        this.properties = properties;
    }

    /**
     * Method for finding issuer by provided issuers in properties given an issued certificate.
     *
     * @param certificate Issued certificate.
     * @return Issuer of the issued certificate.
     * @throws OcspException Thrown when no issuer is found.
     */
    protected X509Certificate findIntermediate(X509Certificate certificate) throws OcspException {
        for (X509Certificate issuer : properties.get(INTERMEDIATES))
            if (issuer.getSubjectX500Principal().equals(certificate.getIssuerX500Principal()))
                return issuer;

        throw new OcspException("Unable to find issuer '%s'.", certificate.getIssuerX500Principal().getName());
    }

    protected URI detectOcspUri(X509Certificate certificate) throws OcspException {
        byte[] extensionValue = certificate.getExtensionValue(Extension.authorityInfoAccess.getId());

        if (extensionValue == null) {
            OcspException.trigger(properties.get(EXCEPTION_ON_NO_PATH),
                    "Unable to detect path for OCSP (%s)", Extension.authorityInfoAccess.getId());
            return null;
        }

        try {
            AuthorityInformationAccess authInfo = AuthorityInformationAccess.getInstance(
                    JcaX509ExtensionUtils.parseExtensionValue(extensionValue));

            for (AccessDescription accessDescription : authInfo.getAccessDescriptions())
            {
                ASN1ObjectIdentifier accessMethod = accessDescription.getAccessMethod();
                GeneralName accessLocation = accessDescription.getAccessLocation();

                if (X509ObjectIdentifiers.id_ad_ocsp.equals(accessMethod))
                {
                    if (GeneralName.uniformResourceIdentifier == accessLocation.getTagNo()) {
                        ASN1IA5String uri = ASN1IA5String.getInstance(accessLocation.getName());
                        return URI.create(new String(uri.getOctets()));
                    }
                }
            }
        } catch (Exception e) {
            throw new OcspException("Exception when reading AIA: '%s'.", e, e.getMessage());
        }

        OcspException.trigger(properties.get(EXCEPTION_ON_NO_PATH),
                "Unable to detect path for OCSP in AIA (%s)", Extension.authorityInfoAccess.getId());
        return null;
    }

    protected OcspResponse fetch(OcspRequest ocspReq, URI uri) throws OcspException {
        try (OcspFetcherResponse response = properties.get(FETCHER).fetch(uri, ocspReq.generateRequest())) {
            if (response.getStatus() != 200)
                throw new OcspServerException("Received HTTP code '%s' from responder.", response.getStatus());

            if (!response.getContentType().equalsIgnoreCase("application/ocsp-response"))
                throw new OcspServerException("Response was of type '%s'.", response.getContentType());

            try (InputStream inputStream = response.getContent()) {
                return OcspResponse.parse(uri, inputStream);
            }
        } catch (IOException e) {
            throw new OcspServerException(e.getMessage(), e);
        }
    }
}
