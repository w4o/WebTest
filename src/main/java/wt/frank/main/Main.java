package wt.frank.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.fastjson.JSON;

import wt.frank.util.HttpHelper;

public class Main {
	
	private final static String url_txt = "url.txt";
	private final static String reqState = "result.json";
	
	private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHssmm");
	
	public static void main(String[] args) {
		
		// 读取当前执行环境的绝对路径
		String _path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		_path = _path.substring(0,_path.lastIndexOf("/"));
		_path = _path + "/";
		
		String urlTxt = _path + url_txt;
		
		System.out.println(_path);
		// 判断文件
		File urlfile = new File(urlTxt);
		if (!urlfile.exists()) {
			System.err.println("没有找到 [" + urlTxt + "]");
			System.exit(0);
		}
		
		// 定一个 URL 集合
		List < String > urlList = new ArrayList<>();
		
		// 读取文件中所有URL，并封装到 urlList 集合中
		try {
			InputStream in = new FileInputStream(urlfile);
			InputStreamReader read = new InputStreamReader(in);
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineText;
			while((lineText =  bufferedReader.readLine()) != null) {
				urlList.add(lineText);
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			System.err.println(" " + e.getMessage());
			System.exit(0);
		} catch (IOException e) {
			System.err.println(" " + e.getMessage());
			System.exit(0);
		}
		
		// 判断是否读到 URL
		if (CollectionUtils.isEmpty(urlList)) {
			System.err.println("老大，一个URL也没读着啊！你特么在测试我么？");
			System.exit(0);
		}
		
        	
    	// 创建历史文件输入文件
    	File historyFile = new File(_path + simpleDateFormat.format(new Date()));
    	try {
			historyFile.createNewFile();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(0);
		}
			
    	// 创建统计文件
    	File stateFile = new File(_path + reqState);
        if (stateFile.exists()) {
        	stateFile.delete();
        }
        try {
			stateFile.createNewFile();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(0);;
		}
    	
    	// 实例化 Http 助手
    	HttpHelper httpHelper = new HttpHelper();
    	
    	List < String[] > result = new ArrayList<>();
    	
    	// 遍历 URL 集合
    	for (String url : urlList) {
    		// 请求
    		httpHelper.getReq(url);
    		result.add(new String[]{url,httpHelper.getStatusCode() + "", httpHelper.getTimeCost()+ "", httpHelper.getRemark()});
    		// 结果
    		String res = "URL：" + url + "\t响应状态值：" + httpHelper.getStatusCode() + "\t耗时：" + httpHelper.getTimeCost()  + "毫秒";
    		// 输出控制台
    		System.out.println(res);
    	}
    	
    	Map < String , List < String[] >> _jsonMap  = new HashMap<>();
    	_jsonMap.put("aaData", result);
        String json = JSON.toJSONString(_jsonMap);
        byte[] jsonByte = json.getBytes();
        
        FileOutputStream out;
        
		try {
			//保存一个历史(一个副本)
			out = new FileOutputStream(historyFile);
			out.write(jsonByte);
			out.close();

			// 保存正式json文件
	        out = new FileOutputStream(stateFile);
	        out.write(jsonByte);
	        out.close();
	        
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(0);
		}
		
        System.out.println("统计完成。。。");
        
        if (java.awt.Desktop.isDesktopSupported()) {  
            try {  
                // 创建一个URI实例  
                java.net.URI uri = java.net.URI.create("file://" + _path + "index.html");  
                // 获取当前系统桌面扩展  
                java.awt.Desktop dp = java.awt.Desktop.getDesktop() ;   
                // 判断系统桌面是否支持要执行的功能  
                if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {  
                    // 获取系统默认浏览器打开链接   
                    dp.browse( uri ) ;  
                }  
                  
            } catch (Exception e) {   
               
            }  
        }  
        
	}

}
