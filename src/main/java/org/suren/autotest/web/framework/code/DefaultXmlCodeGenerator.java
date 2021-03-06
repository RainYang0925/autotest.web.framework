/**
 * http://surenpi.com
 */
package org.suren.autotest.web.framework.code;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;
import org.dom4j.xpath.DefaultXPath;
import org.jaxen.SimpleNamespaceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 根据xml来生成Java源码
 * @author suren
 * @date 2016年12月3日 下午8:43:19
 */
public class DefaultXmlCodeGenerator implements Generator
{
	private static final Logger logger = LoggerFactory.getLogger(DefaultXmlCodeGenerator.class);
	
	private Map<String, Map<String, String>> pageMap = new HashMap<String, Map<String, String>>();
	private Map<String, String> pageCommentMap = new HashMap<String, String>();
	
	private Map<String, String> fieldTypeMap = new HashMap<String, String>();
	
	private ClassLoader classLoader;
	private String outputDir;
	
	/**
	 * 默认构造函数
	 */
	public DefaultXmlCodeGenerator()
	{
		fieldTypeMap.put("button", "Button");
		fieldTypeMap.put("input", "Text");
		fieldTypeMap.put("select", "Selector");
		fieldTypeMap.put("file_upload", "FileUpload");
		fieldTypeMap.put("check_box_group", "CheckBoxGroup");
	}

	@Override
	public void generate(String srcCoding, String outputDir)
	{
		this.outputDir = outputDir;
		
		classLoader = this.getClass().getClassLoader();
		try(InputStream inputStream = classLoader.getResourceAsStream(srcCoding))
		{
			Document document = new SAXReader().read(inputStream);
			
			read(document);
		}
		catch (DocumentException | IOException e)
		{
			e.printStackTrace();
		}
		
		Iterator<String> pageIt = pageMap.keySet().iterator();
		while(pageIt.hasNext())
		{
			String pageCls = pageIt.next();
			Map<String, String> fieldMap = pageMap.get(pageCls);
			
			AutoPage autoPage = new AutoPage();
			int index = pageCls.lastIndexOf(".");
			if(index > 0)
			{
				autoPage.setPackageName(pageCls.substring(0, index));
			}
			autoPage.setName(pageCls.substring(index + 1));
			autoPage.setComment("");
			if(pageCommentMap.containsKey(pageCls))
			{
				autoPage.setComment(pageCommentMap.get(pageCls));
			}
			
			List<AutoField> fields = new ArrayList<AutoField>();
			Iterator<String> fieldIt = fieldMap.keySet().iterator();
			while(fieldIt.hasNext())
			{
				String field = fieldIt.next();
				String fieldType = fieldMap.get(field);
				
				AutoField autoField = new AutoField();
				autoField.setName(field);
				autoField.setType(fieldTypeMap.get(fieldType));
				
				String methodField = field.substring(0, 1).toUpperCase() + field.substring(1);
						
				autoField.setGetterMethod("get" + methodField);
				autoField.setSetterMethod("set" + methodField);
				
				fields.add(autoField);
			}
			
			autoPage.setFields(fields);
			
			create(autoPage);
		}
		
		done();
	}
	
	/**
	 * 代码生成完毕
	 */
	protected void done()
	{
	}
	
	/**
	 * 根据Page对象来创建Java源文件
	 * @param autoPage
	 */
	protected void create(AutoPage autoPage)
	{
		try
		{
			TemplateLoader templateLoader = new ClassTemplateLoader(this.getClass(), "/template");
			
			Configuration configuration = new Configuration();
//			configuration.setDirectoryForTemplateLoading(new File("d:/ftp"));
			configuration.setTemplateLoader(templateLoader);
			configuration.setObjectWrapper(new DefaultObjectWrapper()); 
			configuration.setDefaultEncoding("UTF-8");
			
			Map<String, AutoPage> paramMap = new HashMap<String, AutoPage>();
			paramMap.put("page", autoPage);
			Template template = configuration.getTemplate("page.ftl");
			
			StringBuffer pathBuf = new StringBuffer(outputDir);
			pathBuf.append("/");
			String packageName = autoPage.getPackageName();
			String[] packageArray = packageName.split("\\.");
			if(packageArray != null && packageArray.length > 0)
			{
				for(String pkName : packageArray)
				{
					if(!pkName.equals(""))
					{
						pathBuf.append(pkName).append("/");
					}
				}
			}
			
			String parentDir = pathBuf.toString();
			new File(parentDir).mkdirs();
			
			Writer writer = new OutputStreamWriter(
					new FileOutputStream(parentDir + autoPage.getName() + ".java"),"UTF-8"); 
		    template.process(paramMap, writer);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TemplateException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param document
	 */
	private void read(Document doc)
	{
		SimpleNamespaceContext simpleNamespaceContext = new SimpleNamespaceContext();
		simpleNamespaceContext.addNamespace("ns", "http://surenpi.com");
		
		DefaultXPath xpath = new DefaultXPath("/ns:autotest/ns:pages");
		xpath.setNamespaceContext(simpleNamespaceContext);
		Element pagesEle = (Element) xpath.selectSingleNode(doc);
		String pagePackage = pagesEle.attributeValue("pagePackage", "");
		if(StringUtils.isNotBlank(pagePackage))
		{
			pagePackage = (pagePackage.trim() + ".");
		}

		// pages parse progress
		xpath = new DefaultXPath("/ns:autotest/ns:pages/ns:page");
		xpath.setNamespaceContext(simpleNamespaceContext);
		@SuppressWarnings("unchecked")
		List<Element> pageNodes = xpath.selectNodes(doc);
		if (pageNodes != null)
		{
			for (Element ele : pageNodes)
			{
				String pageClsStr = ele.attributeValue("class");
				if (pageClsStr == null)
				{
					logger.warn("can not found class attribute.");
					continue;
				}

				pageClsStr = (pagePackage + pageClsStr);
				Object commentObj = ele.getData();
				if(commentObj != null)
				{
					pageCommentMap.put(pageClsStr, commentObj.toString().trim());
				}

				try
				{
					Map<String, String> fieldMap = new HashMap<String, String>();
					pageMap.put(pageClsStr, fieldMap);
					parse(pageClsStr, ele, fieldMap);
				}
				catch (Exception e)
				{
					logger.error("page element parse error.", e);
				}
			}
		}
	}

	/**
	 * 解析页面Page对象
	 * 
	 * @param pageClsStr
	 * @param dataSrcClsStr
	 * @param ele
	 * @param fieldMap 
	 */
	private void parse(final String pageClsStr, Element ele, final Map<String, String> fieldMap) throws Exception
	{
		ele.accept(new VisitorSupport()
		{

			@Override
			public void visit(Element node)
			{
				if (!"field".equals(node.getName()))
				{
					return;
				}

				String fieldName = node.attributeValue("name");
				String type = node.attributeValue("type");
				if (fieldName == null || "".equals(fieldName)
						|| type == null || "".equals(type))
				{
					return;
				}
				
				fieldMap.put(fieldName, type);
			}
		});
	}

	/**
	 * @return 解析配置文件所使用的类加载器
	 */
	public ClassLoader getClassLoader()
	{
		return classLoader;
	}

}
