package ty.cloud.netty.mq.server.domain;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public class SessionDataTemp {

	public ByteBuf buffer;

	public String clientUserID;
	

	public SessionDataTemp() {
		buffer = PooledByteBufAllocator.DEFAULT.buffer(0);
	}


}
