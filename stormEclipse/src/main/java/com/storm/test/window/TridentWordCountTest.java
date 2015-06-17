package com.storm.test.window;

import java.util.Arrays;
import java.util.List;

import org.apache.thrift7.TException;

import backtype.storm.generated.DRPCExecutionException;
import backtype.storm.utils.DRPCClient;

public class TridentWordCountTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TridentWordCountTest test = new TridentWordCountTest();
		DRPCClient client = new DRPCClient("192.168.13.74", 3772); 
		if(args==null || args.length==0){
			test.cal1(client);
		}else if(args[0].equalsIgnoreCase("2")){
			test.cal2(client);
		}else if(args[0].equalsIgnoreCase("1")){
			test.cal1(client);
		}
	}
	
	
	
	private void cal1(DRPCClient client){
		String params = "cat dog the man";
		
		try {
			String[] parr = params.split(" ");
			for(String word : parr){
				System.out.println(word+": " + client.execute("words", word));
			}
		} catch (TException e) {
			e.printStackTrace();
		} catch (DRPCExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public void cal2(DRPCClient client){
		List<String> urls = Arrays.asList("/index.html","/pay.html","/user.xhtml","/stat.xhtml","/shop.xhtml");
	     for(String url : urls){
	    	 try {
	    		 System.out.println("url: " + url);
				System.out.println(url+"'s  visit count: " + client.execute("urlCount", url) );
			} catch (TException e) {
				e.printStackTrace();
			} catch (DRPCExecutionException e) {
				e.printStackTrace();
			}
	     }
	}

}
