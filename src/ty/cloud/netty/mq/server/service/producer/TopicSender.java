package ty.cloud.netty.mq.server.service.producer;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

/**
 * Created by fuzhengwei on 2016/6/9.
 */
@Service("topicSender")
public class TopicSender {

    @Autowired
    @Qualifier("jmsTopicTemplate")
    private JmsTemplate jmsTemplate;

    /**
     * 发送一条消息到指定的队列（目标）
     *
     * @param topicName 队列名称
     * @param message   消息内容
     */
    public void send(String topicName, final String message) {
        jmsTemplate.send(topicName, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);
            }
        });
        
    }
    
    /**
     * 发送一条消息到指定的队列（目标）
     *
     * @param topicName 队列名称
     * @param message   消息内容
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