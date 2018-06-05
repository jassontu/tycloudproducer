package ty.cloud.netty.mq.server.domain;



import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SessionQueueStack {

	private BlockingQueue<SessionDataTemp> sessionQueue = null;
	
	public SessionQueueStack(){
		//实例化队列
		sessionQueue = new LinkedBlockingQueue<SessionDataTemp>(100);
	}
	
	/**
	 * 添加数据到队列
	 * @param dataBean
	 * @return
	 */
	public boolean doOfferData(SessionDataTemp dataBean){
		
		try {
			
			/*offer(anObject):表示如果可能的话,将anObject加到BlockingQueue里,即如果BlockingQueue可以容纳,
		　　　　则返回true,否则返回false.（本方法不阻塞当前执行方法的线程）
		　　offer(E o, long timeout, TimeUnit unit),可以设定等待的时间，如果在指定的时间内，还不能往队列中
		　　　　加入BlockingQueue，则返回失败。*/
			
			return sessionQueue.offer(dataBean, 2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 弹出队列数据
	 * @return
	 */
	public SessionDataTemp doPollData(){
		
		try {
			return sessionQueue.poll(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获得队列数据个数
	 * @return
	 */
	public int doGetQueueCount(){
		return sessionQueue.size();
	}
	
}
