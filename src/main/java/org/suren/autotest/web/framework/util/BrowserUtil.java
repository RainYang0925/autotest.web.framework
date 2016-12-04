/**
 * http://surenpi.com
 */
package org.suren.autotest.web.framework.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * 读取浏览器配置的工具类
 * @author suren
 * @date 2016年9月13日 下午8:16:08
 */
public class BrowserUtil
{
	private Map<String, Boolean> preBoolMap;
	private Map<String, String> preStrMap;
	private Map<String, Integer> preIntMap;
	
	/**
	 * @return 火狐浏览器的布尔值类型Map
	 */
	public Map<String, Boolean> getFirefoxPreBoolMap()
	{
		preBoolMap = new HashMap<String, Boolean>();
		Properties properties = System.getProperties();
		Iterator<Object> keyIt = properties.keySet().iterator();
		while(keyIt.hasNext())
		{
			Object key = keyIt.next();
			String keyStr= key.toString();
			
			if(keyStr.indexOf("firefox.bool.") != -1)
			{
				preBoolMap.put(keyStr.substring("firefox.bool.".length()),
						Boolean.parseBoolean(properties.getProperty(keyStr, "false")));
			}
		}
		
		return preBoolMap;
	}
	
	/**
	 * @return 火狐浏览器的字符串类型Map
	 */
	public Map<String, String> getFirefoxPreStrMap()
	{
		preStrMap = new HashMap<String, String>();
		Properties properties = System.getProperties();
		Iterator<Object> keyIt = properties.keySet().iterator();
		while(keyIt.hasNext())
		{
			Object key = keyIt.next();
			String keyStr= key.toString();
			
			if(keyStr.indexOf("firefox.string.") != -1)
			{
				preStrMap.put(keyStr.substring("firefox.string.".length()),
						properties.getProperty(keyStr));
			}
		}
		
		return preStrMap;
	}
	
	/**
	 * @return 火狐浏览器的整型类型Map
	 */
	public Map<String, Integer> getFirefoxPreIntMap()
	{
		preIntMap = new HashMap<String, Integer>();
		Properties properties = System.getProperties();
		Iterator<Object> keyIt = properties.keySet().iterator();
		while(keyIt.hasNext())
		{
			Object key = keyIt.next();
			String keyStr= key.toString();
			
			if(keyStr.indexOf("firefox.int.") != -1)
			{
				preIntMap.put(keyStr.substring("firefox.int.".length()),
						Integer.parseInt(properties.getProperty(keyStr, "0")));
			}
		}
		
		return preIntMap;
	}
}
