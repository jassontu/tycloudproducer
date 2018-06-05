package ty.cloud.netty.mq.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import ty.cloud.netty.mq.server.domain.ChannelServerInfo;
import ty.cloud.netty.mq.server.service.producer.QueueSender;
import ty.cloud.netty.mq.server.socket.netty.SendMqHandler;
import ty.cloud.netty.mq.server.socket.netty.ServerSocket;


public class Start {

	private static Logger logger = LoggerFactory.getLogger(SendMqHandler.class);
    private static QueueSender queueSender;
    private static String queue = "cloud";//默认为cloud队列名称
    private static int port = 10061;
    //protected static ApplicationContext context = null;

    
    public static void main(String[] args)  throws Exception{
        //logger.info("启动Netty服务开始");
    	ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"spring-config.xml" });
    	if(args!=null&&args.length>0){
    		port = Integer.parseInt(args[0]);
    	}
        ChannelServerInfo channelServerInfo = new ChannelServerInfo();
        channelServerInfo.setQueueSender((QueueSender)context.getBean("queueSender"));
        channelServerInfo.setQueue(queue);
        channelServerInfo.setPort(port);

        //实例化socket服务
        ServerSocket serverSocket = new ServerSocket(channelServerInfo);
        // 开启线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        //启动
        executorService.execute(serverSocket);
        logger.info("启动Netty服务完成v0.6,port="+port);
        //定
        System.in.read();
    }

	

}
