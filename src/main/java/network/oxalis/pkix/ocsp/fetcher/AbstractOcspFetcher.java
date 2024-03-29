package network.oxalis.pkix.ocsp.fetcher;

import network.oxalis.pkix.ocsp.api.OcspFetcher;
import network.oxalis.pkix.ocsp.builder.Properties;
import network.oxalis.pkix.ocsp.builder.Property;

/**
 * @author erlend
 */
abstract class AbstractOcspFetcher implements OcspFetcher {

    public static final Property<Integer> TIMEOUT_CONNECT = Property.create(15000);

    public static final Property<Integer> TIMEOUT_READ = Property.create(15000);

    protected final Properties properties;

    protected AbstractOcspFetcher(Properties properties) {
        this.properties = properties;
    }
}
