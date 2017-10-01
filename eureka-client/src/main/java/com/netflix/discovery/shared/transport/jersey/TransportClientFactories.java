package com.netflix.discovery.shared.transport.jersey;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.shared.transport.TransportClientFactory;

import java.util.Collection;

/**
 * 生成 Jersey 客户端工厂的工厂接口
 *
 * @param <F> 过滤器泛型
 */
public interface TransportClientFactories<F> {
    
    @Deprecated
    TransportClientFactory newTransportClientFactory(final Collection<F> additionalFilters,
                                                     final EurekaJerseyClient providedJerseyClient);

    TransportClientFactory newTransportClientFactory(final EurekaClientConfig clientConfig,
                                                     final Collection<F> additionalFilters,
                                                     final InstanceInfo myInstanceInfo);
}