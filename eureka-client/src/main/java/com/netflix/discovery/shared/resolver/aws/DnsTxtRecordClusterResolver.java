/*
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.discovery.shared.resolver.aws;

import com.netflix.discovery.endpoint.DnsResolver;
import com.netflix.discovery.shared.resolver.ClusterResolver;
import com.netflix.discovery.shared.resolver.ClusterResolverException;
import com.netflix.discovery.shared.resolver.ResolverUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A cluster resolver implementation that assumes that Eureka cluster configuration is provided in two level,
 * cascading DNS TXT records. The first level shall point to zone level DNS entries, while at the zone level
 * server pools shall be provided (either CNAMEs or A records).
 * If no TXT record is found at the provided address, the resolver will add 'txt.' suffix to the address, and try
 * to resolve that address.
 *
 *
 * <h3>Example</h3>
 * Lets assume we have a service with root domain myservice.net, and a deployment in AWS us-east-1 on all three zones.
 * The root discovery domain would be:<br/>
 * <table border='1'>
 *     <thead>
 *       <th>DNS record</th>
 *       <th>TXT record content</th>
 *     </thead>
 *     <tbody>
 *     <tr><td>txt.myservice.net</td>
 *      <td>
 *          txt.us-east-1a.myservice.net
 *          txt.us-east-1b.myservice.net
 *          txt.us-east-1c.myservice.net
 *      </td>
 *     </tr>
 *     <tr><td>txt.us-east-1a.myservice.net</td>
 *      <td>
 *          ec2-1-2-3-4.compute-1.amazonaws.com
 *          ec2-1-2-3-5.compute-1.amazonaws.com
 *      </td>
 *     </tr>
 *     <tr><td>txt.us-east-1b.myservice.net</td>
 *      <td>
 *          ec2-1-2-3-6.compute-1.amazonaws.com
 *          ec2-1-2-3-7.compute-1.amazonaws.com
 *      </td>
 *     </tr>
 *     <tr><td>txt.us-east-1c.myservice.net</td>
 *      <td>
 *          ec2-1-2-3-8.compute-1.amazonaws.com
 *          ec2-1-2-3-9.compute-1.amazonaws.com
 *      </td>
 *     </tr>
 *     </tbody>
 * </table>
 *
 * @author Tomasz Bak
 */
public class DnsTxtRecordClusterResolver implements ClusterResolver<AwsEndpoint> {

    private static final Logger logger = LoggerFactory.getLogger(DnsTxtRecordClusterResolver.class);

    /**
     * 地区
     */
    private final String region;
    /**
     * 集群根地址，例如 eureka.iocoder.cn
     */
    private final String rootClusterDNS;
    /**
     * 是否解析可用区( zone )
     */
    private final boolean extractZoneFromDNS;
    /**
     * 端口
     */
    private final int port;
    /**
     * 是否安全
     */
    private final boolean isSecure;
    /**
     * 相对地址
     */
    private final String relativeUri;

    /**
     * @param rootClusterDNS top level domain name, in the two level hierarchy (see {@link DnsTxtRecordClusterResolver} documentation).
     * @param extractZoneFromDNS if set to true, zone information will be extract from zone DNS name. It assumed that the zone
     *                           name is the name part immediately followint 'txt.' suffix.
     * @param port Eureka sever port number
     * @param relativeUri service relative URI that will be appended to server address
     */
    public DnsTxtRecordClusterResolver(String region, String rootClusterDNS, boolean extractZoneFromDNS, int port, boolean isSecure, String relativeUri) {
        this.region = region;
        this.rootClusterDNS = rootClusterDNS;
        this.extractZoneFromDNS = extractZoneFromDNS;
        this.port = port;
        this.isSecure = isSecure;
        this.relativeUri = relativeUri;
    }

    @Override
    public String getRegion() {
        return region;
    }

    @Override
    public List<AwsEndpoint> getClusterEndpoints() {
        List<AwsEndpoint> eurekaEndpoints = resolve(region, rootClusterDNS, extractZoneFromDNS, port, isSecure, relativeUri);
        if (logger.isDebugEnabled()) {
            logger.debug("Resolved {} to {}", rootClusterDNS, eurekaEndpoints);
        }
        return eurekaEndpoints;
    }

    private static List<AwsEndpoint> resolve(String region, String rootClusterDNS, boolean extractZone, int port, boolean isSecure, String relativeUri) {
        try {
            // 解析 第一层 DNS 记录
            Set<String> zoneDomainNames = resolve(rootClusterDNS);
            if (zoneDomainNames.isEmpty()) {
                throw new ClusterResolverException("Cannot resolve Eureka cluster addresses; there are no data in TXT record for DN " + rootClusterDNS);
            }
            // 记录 第二层 DNS 记录
            List<AwsEndpoint> endpoints = new ArrayList<>();
            for (String zoneDomain : zoneDomainNames) {
                String zone = extractZone ? ResolverUtils.extractZoneFromHostName(zoneDomain) : null; //
                Set<String> zoneAddresses = resolve(zoneDomain);
                for (String address : zoneAddresses) {
                    endpoints.add(new AwsEndpoint(address, port, isSecure, relativeUri, region, zone));
                }
            }
            return endpoints;
        } catch (NamingException e) {
            throw new ClusterResolverException("Cannot resolve Eureka cluster addresses for root: " + rootClusterDNS, e);
        }
    }

    private static Set<String> resolve(String rootClusterDNS) throws NamingException {
        Set<String> result;
        try {
            result = DnsResolver.getCNamesFromTxtRecord(rootClusterDNS);
            // TODO 芋艿：这块是bug，不需要这一段
            if (!rootClusterDNS.startsWith("txt.")) {
                result = DnsResolver.getCNamesFromTxtRecord("txt." + rootClusterDNS);
            }
        } catch (NamingException e) {
            if (!rootClusterDNS.startsWith("txt.")) {
                result = DnsResolver.getCNamesFromTxtRecord("txt." + rootClusterDNS);
            } else {
                throw e;
            }
        }
        return result;
    }
}
