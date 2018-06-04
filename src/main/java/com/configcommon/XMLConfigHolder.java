package com.configcommon;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * 最简单的解析方式，用于同一级别标签下不存在任何标签重复的XML
 * @author kong.haishuo
 *
 */
public class XMLConfigHolder extends XMLConfigBase implements XMLConfHolder {
	
	@Override
	public void parse(String path) {
		File f = new File(path);
        parse(f);
	}
	
	@Override
	public void parse(File file) {
		Element root = parseXML(file);
		resolve(root);
	}

	/**
     * 解析根节点.
     *
     * @param root {@link Element}
     */
    protected void resolve(Element root) {
        NodeList list = root.getChildNodes();
        for (int i = 0, l = list.getLength(); i < l; i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                doResolve((Element) node, newKey(node, ""));
            }
        }
     }

    /**
     * 递归解析类型为{@link Node}.ELEMENT_NODE的{@linkplain Node},即{@link Element}.其逻辑如下:
     * <br>
     * 1) 如果此节点为叶子节点，那么按照<key.NodeName, 文本内容>的格式放入到map.
     * <br>
     * 2) 如果不是叶子节点，那么递归调用此方法.
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
        }
    }

	
	
    public static void main(String[] args) throws Exception {
    	XMLConfHolder holder = new XMLConfigHolderDumplicated();
        holder.parse("etc/es.xml");
        for (String s : holder.getListSingle("store_list.tablename"))
            System.out.println(s);
    }


    @Override
    public List<String> getList(String key) {
        List<String> resultList = new ArrayList<>();
        resultList.add(getString(key));
        return resultList;
    }

    @Override
    public List<JSONObject> getJSONList(String key) {
        return new ArrayList<>();
    }

    @Override
    public List<String> getListSingle(String key) {

        return new ArrayList<>();
    }
    
	@Override
	public int getIntorDefault(String key, int defaultValue) {
		if (!holder.containsKey(key)) return defaultValue;
		try {
			return Integer.parseInt(holder.get(key));
		}catch(Exception e) {
			return defaultValue;
		}
	}
	@Override
	public boolean getBooleanorDefault(String key, boolean defaultValue) {
		if (!holder.containsKey(key)) return defaultValue;
		try {
			return Boolean.parseBoolean(holder.get(key));
		}catch(Exception e) {
			return defaultValue;
		}
	}
	@Override
	public String getStringorDefault(String key, String defaultValue) {
		if (!holder.containsKey(key)) return defaultValue;
		return holder.get(key);
	}
	@Override
	public long getLongorDefault(String key, long defaultValue) {
		if (!holder.containsKey(key)) return defaultValue;
		try {
			return Long.parseLong(holder.get(key));
		}catch(Exception e) {
			return defaultValue;
		}
	}
	@Override
	public double getDoubleorDefault(String key, double defaultValue) {
		if (!holder.containsKey(key)) return defaultValue;
		try {
			return Double.parseDouble(holder.get(key));
		}catch(Exception e) {
			return defaultValue;
		}
	}

	@Override
	public void paser(InputStream is) throws Exception {
		Element root = parseXML(is);
		resolve(root);
	}

}
