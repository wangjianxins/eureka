package com.netflix.eureka.cluster;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.eureka.registry.PeerAwareInstanceRegistryImpl.Action;

/**
 * 应用实例同步任务
 *
 * Base {@link ReplicationTask} class for instance related replication requests.
 *
 * @author Tomasz Bak
 */
public abstract class InstanceReplicationTask extends ReplicationTask {

    /**
     * For cancel request there may be no InstanceInfo object available so we need to store app/id pair
     * explicitly.
     * 应用名
     */
    private final String appName;
    /**
     * 任务编号
     */
    private final String id;
    /**
     * 应用实例
     */
    private final InstanceInfo instanceInfo;
    /**
     * 应用实例的覆盖状态
     */
    private final InstanceStatus overriddenStatus;
    /**
     * 是否同步应用实例
     */
    private final boolean replicateInstanceInfo;

    protected InstanceReplicationTask(String peerNodeName, Action action, String appName, String id) {
        super(peerNodeName, action);
        this.appName = appName;
        this.id = id;
        this.instanceInfo = null;
        this.overriddenStatus = null;
        this.replicateInstanceInfo = false;
    }

    protected InstanceReplicationTask(String peerNodeName,
                                      Action action,
                                      InstanceInfo instanceInfo,
                                      InstanceStatus overriddenStatus,
                                      boolean replicateInstanceInfo) {
        super(peerNodeName, action);
        this.appName = instanceInfo.getAppName();
        this.id = instanceInfo.getId();
        this.instanceInfo = instanceInfo;
        this.overriddenStatus = overriddenStatus;
        this.replicateInstanceInfo = replicateInstanceInfo;
    }

    public String getTaskName() {
        return appName + '/' + id + ':' + action + '@' + peerNodeName;
    }

    public String getAppName() {
        return appName;
    }

    public String getId() {
        return id;
    }

    public InstanceInfo getInstanceInfo() {
        return instanceInfo;
    }

    public InstanceStatus getOverriddenStatus() {
        return overriddenStatus;
    }

    public boolean shouldReplicateInstanceInfo() {
        return replicateInstanceInfo;
    }
}
