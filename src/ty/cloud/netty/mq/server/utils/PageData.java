package ty.cloud.netty.mq.server.utils;

public class PageData {
	private  String code; //0 正常 1 异常
	private String status; // success /error
	private String data;	// 正常返回数据
	private String msg;     //异常文本
	private String uri; 	//客户端访问的uri
	
	public PageData(){
		this.code="1";
		this.status="ERROR";
	}
	public  String success(String status ,Object data,String uri){
		this.code = "0";
		if(data==null){
			this.data="\"null\"";
		}else{
			this.data = data.toString();
		}
		
		this.status="SUCCESS";
		this.uri = uri;
		return "{\"code\":\""+this.code+"\",\"status\":\""+this.status+"\",\"data\":"+this.data+",\"uri\":\""+this.uri+"\"}";
	}
	
	
	public String error(String msg){
		this.code = "1";
		this.status="error";
		this.msg = msg;
		return "{\"code\":\""+this.code+"\",\"msg\":\""+this.msg+"\"}";
	}
	
}
