package ty.cloud.netty.mq.server.socket.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import ty.cloud.netty.mq.server.domain.ChannelServerInfo;

public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

    private ChannelServerInfo channelServerInfo;

    public ChildChannelHandler(ChannelServerInfo channelServerInfo){
        this.channelServerInfo = channelServerInfo;
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 在管道中添加我们自己的接收数据实现方法
		  //创建DelimiterBasedFrameDecoder对象，加入到ChannelPipeline中，它有两个参数，第一个参数表示单条消息的
		  //最大长度，当达到该长度的后仍然没有查找到分隔符，就抛出TooLongFrameException异常，防止由于异常码流缺失分隔符导致的内存溢出
		  //这是Netty解码器的可靠性保护；第二个参数就是分隔符缓冲对象
		/*ch.pipeline().addLast("codec",new HttpServerCodec());
    	ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(3*60, 3*60, 3*60));
    	ch.pipeline().addLast(new SendMqHandler(channelServerInfo));*/
    	ch.pipeline().addLast("codec",new HttpServerCodec());
    	ch.pipeline().addLast("aggregator",new HttpObjectAggregator(1024*1024));
    	ch.pipeline().addLast("handler",new SendMqHandler(channelServerInfo));

    }

}
