package ty.cloud.netty.mq.server.utils;

public class T808Reply {

	/**
	 *  流水号计数器，每次下发自动增加1
	 */
	private static int serialNo = 0;
	
	public static int getSerialNo() {
		return serialNo++;
	}
}
