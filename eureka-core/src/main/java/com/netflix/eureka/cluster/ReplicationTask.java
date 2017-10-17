package com.netflix.eureka.cluster;

import com.netflix.discovery.shared.transport.EurekaHttpResponse;
import com.netflix.eureka.registry.PeerAwareInstanceRegistryImpl.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 同步任务
 *
 * Base class for all replication tasks.
 */
abstract class ReplicationTask {

    private static final Logger logger = LoggerFactory.getLogger(ReplicationTask.class);

    /**
     * 节点名，目前使用 {@link PeerEurekaNode#targetHost}
     */
    protected final String peerNodeName;
    /**
     * 同步操作类型
     */
    protected final Action action;

    ReplicationTask(String peerNodeName, Action action) {
        this.peerNodeName = peerNodeName;
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    /**
     * @return 任务名
     */
    public abstract String getTaskName();

    /**
     * 执行请求
     *
     * @return 响应结果
     * @throws Throwable 异常
     */
    public abstract EurekaHttpResponse<?> execute() throws Throwable;

    /**
     * 执行成功
     */
    public void handleSuccess() {
    }

    /**
     * 执行失败
     *
     * @param statusCode 错误码
     * @param responseEntity 响应实体
     * @throws Throwable 异常
     */
    public void handleFailure(int statusCode, Object responseEntity) throws Throwable {
        logger.warn("The replication of task {} failed with response code {}", getTaskName(), statusCode);
    }

}
