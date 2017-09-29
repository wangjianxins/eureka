package com.netflix.appinfo;

import com.netflix.yunai.Done;

@Done
public interface RefreshableInstanceConfig {

    /**
     * resolve the default address
     *
     * @param refresh
     * @return
     */
    String resolveDefaultAddress(boolean refresh);
}
