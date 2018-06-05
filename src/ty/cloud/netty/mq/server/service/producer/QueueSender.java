package ty.cloud.netty.mq.server.service.producer;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

/**
 * Created by fuzhengwei on 2016/6/9.
 */
@Service("queueSender")
public class QueueSender {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	@Qualifier("jmsQueueTemplate")
	private JmsTemplate jmsTemplate;

	/**
	 * 发送一条消息到指定的队列（目标）
	 * 
	 * @param topicName
	 *            队列名称
	 * @param message
	 *            消息内容
	 */
	public String send(String topicName, final String message) {

		try {
			jmsTemplate.send(topicName, new MessageCreator() {
				@Override
				public Message createMessage(Session session)
						throws JMSException {
					return session.createTextMessage(message);
				}
			});
			return "SUCCESS";
		} catch (JmsException e) {
			logger.error("队列插入异常：" + e.getMessage());
			e.printStackTrace();
		}

		return "ERROR";
	}

	/**
	 * 发送一条消息到指定的队列（目标）
	 * 
	 * @param topicName
	 *            队列名称
	 * @param message
	 *            消息内容
	 */
	public void sendBytesMessage(String topicName, final byte[] message) {
		jmsTemplate.send(topicName, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {

				BytesMessage content = session.createBytesMessage();
				content.writeBytes(message);
				return content;
			}
		});
	}

}