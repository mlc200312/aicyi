package io.github.aicyi.commons.core.id;

/**
 * @author Mr.Min
 * @description WorkerId 分配器接口
 * @date 2026/5/21
 **/
public interface WorkerIdAllocator {

    /**
     * 申请 workerId
     */
    WorkerIdLease allocate();

    /**
     * 续约
     */
    boolean renew(WorkerIdLease lease);

    /**
     * 主动释放
     */
    boolean release(WorkerIdLease lease);
}