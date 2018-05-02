package com.configcommon;

import java.io.File;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson.JSONObject;
/**
 * 提供了Attributes解析功能的配置文件解析器
 * @author Administrator
 *
 */
public class XMLConfigHolderAttr extends XMLConfigHolder {
	@Override
	/**
	 * 在解析的基础上添加了对于Attributes的JSON拼接，如果遇到类似于"<text1 id="aaa">bbb</text1 .>"的标签，这个类会将其解析为
	 * {"text1":"bbb","attrs":{"id":"aaa"}}
	 */
	protected void doResolve(Element element, String key) {
		 NodeList list = element.getChildNodes();
	        boolean isLeaf = true;
	          for (int i = 0, l = list.getLength(); i < l; i++) {
	            Node node = list.item(i);
	            if (node.getNodeType() == Node.ELEMENT_NODE) {
	                isLeaf = false;
	                doResolve((Element) node, newKey(node, key));
	            }
	        }
	        key = key.substring(0, key.length() - 1);
	        if (isLeaf) {
	        		holder.put(key, element.getTextContent().trim());
	        }else{
	        	element.setTextContent("");
	        }
	        if (element.hasAttributes()){
        		String value = parseAttrs(element);
        		holder.put(key, value);
        	}
	}
	/**
	 * 将标签值和其中的Attributes拼接成JSON
	 * @param element
	 * @return
	 */
	protected String parseAttrs(Element element){
		JSONObject jo = new JSONObject(), inner = new JSONObject();
		jo.put(element.getNodeName(), element.getTextContent().trim());
		NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
			inner.put(attrs.item(i).getNodeName(), attrs.item(i).getTextContent().trim());
		jo.put("attrs", inner);
		return jo.toJSONString();
	}
	
	/**
	 * 返回JSONObject形式
	 * @param key
	 * @return
	 */
	public JSONObject getJSONObj(String key){
		String value = holder.get(key);
		try{
			JSONObject jo = JSONObject.parseObject(value);
			return jo;
		}catch(Exception e){
			System.out.println("JSONError when parse! Value is " + value +". Exception is "+e.getMessage());
			return null;
		}
	}
	public static void main(String[] args){
		XMLConfigHolderAttr holder = new XMLConfigHolderAttr();
		holder.parse(new File("etc/conf.xml"));
		System.out.println(holder.getJSONObj("mgsa").toString());
	}
}
