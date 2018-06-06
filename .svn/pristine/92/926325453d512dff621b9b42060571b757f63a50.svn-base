package com.toptime.cmssync.entity;

import com.toptime.cmssync.util.CommonUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 多线程同步稿件任务队列
 */
public class TaskQueueManager {

    public static Logger logger = Logger.getLogger(TaskQueueManager.class);

    /**
     * 任务队列
     */
    private List<String> task_queue = Collections.synchronizedList(new ArrayList(10));

    /**
     * 获取任务队列
     */
    public synchronized List<String> getTaskQueue() {
        return task_queue;
    }

    /**
     * 获取任务队列大小
     */
    public synchronized int getTaskQueueSize() {
        return task_queue.size();
    }

    /**
     * 将任务从队列中移除
     */
    public synchronized boolean removeFromTaskQueue(String url) {
        return task_queue.remove(url);
    }

    /**
     * 向队列中插入任务
     */
    public synchronized boolean addToTaskQueue(String url) {
        return task_queue.add(url);
    }

    /**
     * 判断任务是否在队列中
     */
    public synchronized boolean isInTaskQueue(String url) {
        boolean status = false;
        if (!CommonUtil.isEmpty(url) && task_queue != null && task_queue.size() > 0) {
            try {
                for (String s : task_queue) {
                    if (s.trim().equals(url.trim())) {
                        status = true;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return status;
    }

}
