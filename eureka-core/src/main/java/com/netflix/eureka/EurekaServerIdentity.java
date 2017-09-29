package com.netflix.eureka;

import com.netflix.appinfo.AbstractEurekaIdentity;
import com.netflix.yunai.Done;

/**
 * This class holds metadata information related to eureka server auth with peer eureka servers
 *
 * Eureka 服务端标识符
 */
public class EurekaServerIdentity extends AbstractEurekaIdentity {

    public static final String DEFAULT_SERVER_NAME = "DefaultServer";

    private final String serverVersion = "1.0";
    private final String id;

    public EurekaServerIdentity(String id) {
        this.id = id;
    }

    @Override
    @Done(progress = 100)
    public String getName() {
        return DEFAULT_SERVER_NAME;
    }

    @Override
    @Done(progress = 100)
    public String getVersion() {
        return serverVersion;
    }

    @Override
    @Done(progress = 100)
    public String getId() {
        return id;
    }
}
