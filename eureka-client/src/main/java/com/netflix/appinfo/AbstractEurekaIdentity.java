package com.netflix.appinfo;

import com.netflix.yunai.Done;

import javax.annotation.Nullable;

/**
 * Eureka 对象标识符抽象类
 */
public abstract class AbstractEurekaIdentity {

    public static final String PREFIX = "DiscoveryIdentity-";

    public static final String AUTH_NAME_HEADER_KEY = PREFIX + "Name";
    public static final String AUTH_VERSION_HEADER_KEY = PREFIX + "Version";
    public static final String AUTH_ID_HEADER_KEY = PREFIX + "Id";

    /**
     * @return 对象名
     */
    @Done(progress = 100)
    public abstract String getName();

    /**
     * @return 对象版本号
     */
    @Done(progress = 100)
    public abstract String getVersion();

    /**
     * @return 对象编号
     */
    @Nullable
    @Done(progress = 100)
    public abstract String getId();
}
