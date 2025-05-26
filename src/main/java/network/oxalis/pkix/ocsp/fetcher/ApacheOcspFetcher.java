package network.oxalis.pkix.ocsp.fetcher;

import network.oxalis.pkix.ocsp.api.OcspFetcher;
import network.oxalis.pkix.ocsp.api.OcspFetcherResponse;
import network.oxalis.pkix.ocsp.builder.Builder;
import network.oxalis.pkix.ocsp.builder.Properties;
import network.oxalis.pkix.ocsp.builder.Property;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author erlend
 */
public class ApacheOcspFetcher extends AbstractOcspFetcher {

    public static final Property<HttpClientConnectionManager> CONNECTION_MANAGER = Property.create();

    public static final Property<Boolean> CONNECTION_MANAGER_SHARED = Property.create(false);

    public static final Property<Integer> TIMEOUT_CONNECTION_MANAGER = Property.create(-1);

    /**
     * Builder to create an instance of OcspFetcher using Apache HttpClient for connectivity.
     *
     * @return Prepared fetcher.
     */
    public static Builder<OcspFetcher> builder() {
        return new Builder<>(ApacheOcspFetcher::new);
    }

    private ApacheOcspFetcher(Properties properties) {
        super(properties);
    }

    @Override
    public OcspFetcherResponse fetch(URI uri, byte[] content) throws IOException {
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeader("Content-Type", "application/ocsp-request");
        httpPost.setHeader("Accept", "application/ocsp-response");
        httpPost.setEntity(new ByteArrayEntity(content, ContentType.create("application/ocsp-request")));
        return new ApacheOcspFetcherResponse(getHttpClient().execute(httpPost));
    }

    protected CloseableHttpClient getHttpClient() {
        return HttpClients.custom()
                .setConnectionManager(properties.get(CONNECTION_MANAGER))
                .setConnectionManagerShared(properties.get(CONNECTION_MANAGER_SHARED))
                .setDefaultRequestConfig(getRequestConfig())
                .build();
    }

    protected RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(getTimeout(TIMEOUT_CONNECT))
                .setResponseTimeout(getTimeout(TIMEOUT_READ))
                .setConnectionRequestTimeout(getTimeout(TIMEOUT_CONNECTION_MANAGER))
                .build();
    }

    private Timeout getTimeout(Property<Integer> timeoutProperty) {
        int timeoutValue = properties.get(timeoutProperty);
        return timeoutValue > 0 ? Timeout.ofMilliseconds(timeoutValue) : Timeout.DISABLED;
    }

    private static class ApacheOcspFetcherResponse implements OcspFetcherResponse {
        private final CloseableHttpResponse response;

        public ApacheOcspFetcherResponse(CloseableHttpResponse response) {
            this.response = response;
        }

        @Override
        public int getStatus() {
            return response.getCode();
        }

        @Override
        public String getContentType() {
            return response.getFirstHeader("Content-Type").getValue();
        }

        @Override
        public InputStream getContent() throws IOException {
            return response.getEntity().getContent();
        }

        @Override
        public void close() throws IOException {
            response.close();
        }
    }
}
