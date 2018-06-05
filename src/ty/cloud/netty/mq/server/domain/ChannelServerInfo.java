package ty.cloud.netty.mq.server.domain;

import ty.cloud.netty.mq.server.service.producer.QueueSender;

/**
 */
public class ChannelServerInfo {

    private int port;

    private String queue;
    private QueueSender queueSender;
	//private SessionQueueStack sessionQueueStack; 		// 队列
    
//    public SessionQueueStack getSessionQueueStack() {
//		return sessionQueueStack;
//	}
//
//	public void setSessionQueueStack(SessionQueueStack sessionQueueStack) {
//		this.sessionQueueStack = sessionQueueStack;
//	}

	public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public QueueSender getQueueSender() {
		return queueSender;
	}

	public void setQueueSender(QueueSender queueSender) {
		this.queueSender = queueSender;
	}

    
}
