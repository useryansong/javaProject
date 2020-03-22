package com.xchinfo.erp.bpm.helper;

/**
 * @author roman.li
 * @date 2019/3/20
 * @update
 */
public final class ProcessConstant {

    // 流程实例状态
    public static final int PROCESS_INSTANCE_STATUS_NEW = 0;
    public static final int PROCESS_INSTANCE_STATUS_DOING = 1;
    public static final int PROCESS_INSTANCE_STATUS_TERMINAL = 2;
    public static final int PROCESS_INSTANCE_STATUS_FINISHED = 3;

    // 节点类型
    public static final int NODE_TYPE_START = 1;/** 开始节点 */
    public static final int NODE_TYPE_END = 2;/** 结束节点 */
    public static final int NODE_TYPE_TASK = 3;/** 任务节点 */
    public static final int NODE_TYPE_ROUTING = 4;/** 路由节点 */
    public static final int NODE_TYPE_SUBPROCESS = 5;/** 子流程 */

    // 工作项类型
    public static final int WORK_ITEM_TYPE_NORMAL = 1;/** 普通任务 */
    public static final int WORK_ITEM_TYPE_ASSIST = 2;/** 协办任务 */

    // 参与类型
    public static final int WORK_ITEM_PERFORM_TYPE_NORMAL = 1;/** 普通任务 */
    public static final int WORK_ITEM_PERFORM_TYPE_ASSIST = 2;/** 会签任务 */

    // 参与者选择类型
    public static final int ACTOR_TYPE_CREATOR = 1;/** 流程启动着 */
    public static final int ACTOR_TYPE_GROUP = 2;/** 按组选择 */
    public static final int ACTOR_TYPE_ROLE = 3;/** 按角色选择 */
    public static final int ACTOR_TYPE_USER = 4;/** 按人选择 */

    // 工作项状态
    public static final int WORK_ITEM_STATUS_NEW = 0;/** 新疆 */
    public static final int WORK_ITEM_STATUS_DOING = 1;/** 执行中 */
    public static final int WORK_ITEM_STATUS_TERMINAL = 2;/** 已终止 */
    public static final int WORK_ITEM_STATUS_FINISHED = 3;/** 已完成 */
    public static final int WORK_ITEM_STATUS_ROLLBACK = 4;/** 已回退 */
}
