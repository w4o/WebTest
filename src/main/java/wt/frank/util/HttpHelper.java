package wt.frank.util;

import java.io.IOException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class HttpHelper {
	
	private final int HTTP_TIMEOUT = 5000;
	
	private final int REQ_TIMEOUT = 5000;
	
	private HttpClient httpClient;
	
	private int statusCode;
	
	private long timeCost;
	
	private String remark;
	
	public int getStatusCode (){
		return this.statusCode;
	}
	
	public long getTimeCost () {
		return this.timeCost;
	}
	
	public String getRemark () {
		return this.remark;
	}
	
	public HttpHelper() {
		// 生成 HttpClinet 对象并设置参数
		httpClient = new HttpClient();
		// 设置 Http 连接超时 5s
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(HTTP_TIMEOUT);
	}
	
	private void req(HttpMethodBase method) {
		
		// 设置 get 请求超时 5s
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, REQ_TIMEOUT);
        // 设置请求重试处理
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		// 设置 cookie 的 policy
		method.getParams().setParameter(HttpMethodParams.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        
        long beginTime = System.currentTimeMillis();
        //执行请求
        int statusCode = 0;
		try {
			statusCode = httpClient.executeMethod(method);
		} catch (HttpException e) {
			remark = e.getMessage();
		} catch (IOException e) {
			remark = e.getMessage();
		}
        long endTime = System.currentTimeMillis();
        long timeCost = endTime - beginTime;
        
        this.statusCode = statusCode;
        this.timeCost = timeCost;
        
        method.releaseConnection();
	}
	
	/**
	 * get请求
	 * @param url
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public void getReq(String url) {
		// 生成 GetMethod 对象并设置参数
		GetMethod getMethod = new GetMethod(url);
		req(getMethod);
	}
	
	/**
	 * post 请求
	 * @param url
	 * @return
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public void postReq(String url) throws HttpException, IOException {
		// 生成 PostMethod 对象并设置参数
		PostMethod postMethod = new PostMethod(url);
		req(postMethod);
	}

}
