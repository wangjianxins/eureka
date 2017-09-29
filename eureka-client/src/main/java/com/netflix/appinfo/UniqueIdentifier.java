package com.netflix.appinfo;

import com.netflix.yunai.Done;

/**
 * Generally indicates the unique identifier of a {@link com.netflix.appinfo.DataCenterInfo}, if applicable.
 *
 * 数据中心信息的唯一标识符接口
 * TODO 芋艿：在考虑下注释
 *
 * @author rthomas@atlassian.com
 */
public interface UniqueIdentifier {

    /**
     * @return 标识符编号
     */
    @Done(progress = 100)
    String getId();
}
