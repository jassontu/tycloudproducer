package ty.cloud.netty.mq.server.socket.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ty.cloud.netty.mq.server.domain.ChannelServerInfo;
import ty.cloud.netty.mq.server.utils.PageData;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class SendMqHandler extends ChannelHandlerAdapter {

	private Logger logger = LoggerFactory.getLogger(SendMqHandler.class);
	private PageData page = new PageData();
	private ChannelServerInfo channelServerInfo;
	
	private HttpHeaders headers;
	private HttpRequest request;
	private FullHttpResponse response;
	private FullHttpRequest fullRequest;
	private HttpPostRequestDecoder decoder;
	private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MAXSIZE);
    
    private static final String FAVICON_ICO = "/favicon.ico";
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String CONNECTION_KEEP_ALIVE = "keep-alive";
    private static final String CONNECTION_CLOSE = "close";
	//SessionData session;

	public SendMqHandler(ChannelServerInfo channelServerInfo) {
		this.channelServerInfo = channelServerInfo;
	}

	/*
	 * channelAction
	 *
	 * channel 通道 action 活跃的
	 *
	 * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
	 *
	 */
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// System.out.println(ctx.channel().localAddress().toString() + "
		// channelActive");
		// String str = "您已经开启与服务端链接" + " 您的channelId：" + ctx.channel().id() + "
		// " + new Date() + " " + ctx.channel().localAddress();
		// ctx.writeAndFlush(str);
		//session = new SessionData();
		logger.info(ctx.channel().remoteAddress()+"客户端建立新连接");
	}

	/*
	 * channelInactive
	 *
	 * channel 通道 Inactive 不活跃的
	 *
	 * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
	 *
	 */
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// 捕获约即可关闭
		logger.error(ctx.channel().remoteAddress()+"客户端连接断开");
		ctx.close();
	}

	/*
	 * channelRead
	 *
	 * channel 通道 Read 读
	 *
	 * 简而言之就是从通道中读取数据，也就是服务端接收客户端发来的数据 但是这个数据在不进行解码时它是ByteBuf类型的后面例子我们在介绍
	 *
	 */
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String result = "";
		if(msg instanceof HttpRequest){
			try{
				request = (HttpRequest) msg;
				headers = request.headers();
				
				String uri = request.getUri();
				logger.info("http uri: " + uri);
				System.out.println("http uri: " + uri);
				//去除浏览器"/favicon.ico"的干扰
				if(uri.equals(FAVICON_ICO)){
					return;
				}
				HttpMethod method = request.getMethod();
				
				if(method.equals(HttpMethod.GET)){
					QueryStringDecoder queryDecoder = new QueryStringDecoder(uri, Charsets.toCharset(CharEncoding.UTF_8));
					Map<String, List<String>> uriAttributes = queryDecoder.parameters();
					//此处仅打印请求参数（你可以根据业务需求自定义处理）
		            for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
		                for (String attrVal : attr.getValue()) {
		                    System.out.println(attr.getKey() + "=" + attrVal);
		                }
		            }
				}else if(method.equals(HttpMethod.POST)){
					//POST请求，由于你需要从消息体中获取数据，因此有必要把msg转换成FullHttpRequest
					fullRequest = (FullHttpRequest) msg;
					
					String status = dealWithContentType(uri);
					result = page.success(status,null, uri);
				}else{
					//其他类型在此不做处理，需要的话可自己扩展
				}
				
				//writeResponse(ctx.channel(), HttpResponseStatus.OK, SUCCESS, false);
				writeResponse(ctx.channel(), HttpResponseStatus.OK, result, false);
			}catch(Exception e){
				logger.info(e.getMessage());
				writeResponse(ctx.channel(), HttpResponseStatus.INTERNAL_SERVER_ERROR, ERROR, true);
				e.printStackTrace();
			}finally{
				ReferenceCountUtil.release(msg);
			}
			
		}else{
			//discard request...
			ReferenceCountUtil.release(msg);
		}
	}
	
	private String dealWithContentType(String topicName) throws Exception{
		String status = "ERROR";
		String contentType = getContentType();
		if (contentType.equals("application/json")) { // 可以使用HttpJsonDecoder
			String jsonStr = fullRequest.content().toString(Charsets.toCharset(CharEncoding.UTF_8));
			try {
				/*
				 * 将数据插入到队列
				 */
				if (topicName != null) { 
					channelServerInfo.setQueue(topicName);
				}
				status = channelServerInfo.getQueueSender().send(channelServerInfo.getQueue(), jsonStr.toString());
				
			} catch (Exception e) {
				logger.error("post json 异常："+e.getMessage());
				e.printStackTrace();
			}
		
		} else if(contentType.equals("application/x-www-form-urlencoded")){
            //方式二：使用 HttpPostRequestDecoder
			initPostRequestDecoder();
			List<InterfaceHttpData> datas = decoder.getBodyHttpDatas();
			StringBuilder sb = new StringBuilder();
			int count = 0;
            for (InterfaceHttpData data : datas) {
            	if(data.getHttpDataType() == HttpDataType.Attribute) {
            		Attribute attribute = (Attribute) data;
            		logger.info(attribute.getName() + "=" + attribute.getValue());
            		sb.append(attribute.getName() + "=" + attribute.getValue());
            		if(count==datas.size()-1)
            		{
            			
            		}
            		else
            		{
            			sb.append("&");
            		}
            	}
            	count++;
            }
            try {
            	if(topicName!=null)
            	{
            		channelServerInfo.setQueue(topicName.toString());
            	}
            	status = channelServerInfo.getQueueSender().send(channelServerInfo.getQueue(), sb.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
            
		}else if(contentType.equals("multipart/form-data")){  //用于文件上传
			//readHttpDataAllReceive();
			
		}else{
			//do nothing...
		}
		return status;
	}
	
	private void initPostRequestDecoder(){
		if (decoder != null) {  
            decoder.cleanFiles();  
            decoder = null;  
        }
		decoder = new HttpPostRequestDecoder(factory, request, Charsets.toCharset(CharEncoding.UTF_8));
	}
	
	private String getContentType(){
		String typeStr = headers.get("Content-Type").toString();
		String[] list = typeStr.split(";");
		return list[0];
	}
	/*
	 * channelReadComplete
	 *
	 * channel 通道 Read 读取 Complete 完成
	 *
	 * 在通道读取完成后会在这个方法里通知，对应可以做刷新操作 ctx.flush()
	 *
	 */
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	/*
	 * exceptionCaught
	 *
	 * exception 异常 Caught 抓住
	 *
	 * 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
	 *
	 */
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error(ctx.channel().remoteAddress()+"发生异常关闭:" + cause.toString());
		ctx.close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				logger.error(ctx.channel().remoteAddress()+"连接超时,客户端超时未发送数据,服务器主动断开连接");
				ctx.close();
			}
		}
	}

	private void release(Object msg) {
		try {
			ReferenceCountUtil.release(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeResponse(Channel channel, HttpResponseStatus status, String msg, boolean forceClose){
		ByteBuf byteBuf = Unpooled.wrappedBuffer(msg.getBytes());
		response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, byteBuf);
		boolean close = isClose();
		if(!close && !forceClose){
			response.headers().add(org.apache.http.HttpHeaders.CONTENT_LENGTH, String.valueOf(byteBuf.readableBytes()));
		}
		ChannelFuture future = channel.write(response);
		if(close || forceClose){
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	private boolean isClose(){
		if(request.headers().contains(org.apache.http.HttpHeaders.CONNECTION, CONNECTION_CLOSE, true) ||
				(request.getProtocolVersion().equals(HttpVersion.HTTP_1_0) && 
				!request.headers().contains(org.apache.http.HttpHeaders.CONNECTION, CONNECTION_KEEP_ALIVE, true)))
			return true;
		return false;
	}
	
	

}
