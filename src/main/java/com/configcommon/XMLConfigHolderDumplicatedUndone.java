package com.configcommon;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson.JSONObject;
/**
 * 单次扫描实现的配置容器，效率较高,无法解决同一个标签下有两种重复的问题
 * @author kong.haishuo
 *
 */
@Deprecated
public class XMLConfigHolderDumplicatedUndone extends XMLConfigHolderAttr implements XMLConfHolder {
	private final static String IS_LEAF = "IS_LEAF";
	private final static String BLOCK = "##";
	@Override
	protected void resolve(Element root) {
		doResolve(root, "");
	}
	/**
	 * 单次扫描的实现，不需要额外的字典树分析。在{@link XMLConfigHolderAttr}的{@code doResolve}方法的基础上进行改良
	 * 扫描算法如下：
	 * 1.扫描扫描节点寻找子节点
	 * 2.判断节点缓存中是否存在同名子节点，如果不存在则将该节点放入缓存Map，如果存在则放入另一个缓存
	 * 3.扫描
	 * @param element
	 * @param key
	 */
	@Override
	protected void doResolve(Element element, String key) {
		NodeList list = element.getChildNodes();
		List<Node> dumplicateNodes = new ArrayList<Node>();
        boolean isLeaf = true;
        //去重
        Map<String, Node> cacheMap = new HashMap<String, Node>();//从MAP进去以后出来变成了一个空节点？？？
          for (int i = 0, l = list.getLength(); i < l; i++) {
        	  Node node = list.item(i);
        	  if (node.getNodeType() == Node.ELEMENT_NODE) {
        		  isLeaf = false;
        		  //如果已存在则将节点记录下来
        		  if (cacheMap.containsKey(node.getNodeName())){
        			  dumplicateNodes.add(node);
        		  }else{
        			  cacheMap.put(node.getNodeName(), node);
        			  //doResolve((Element) node, newKey(node, key));
        		  }
        	  }
        }
        //搞重复的
        if (dumplicateNodes.size() > 0){
        	List<Node> nodeList = new ArrayList<Node>();
        	int count = dumplicateNodes.size();
        	//对重复节点进行重读取
            for (int i = 0; i < count; i++){
            	//如果下一个元素不存在则直接从map取出Node，将当前节点和map中取出的节点放入nodeList，进行reRead
            	//如果下一个元素与当前元素不同则将Map中的Node添加到nodeList中，进行reRead，然后重建nodeList
            	//如果下一个元素存在且与当前元素相同则将当前元素加入nodeList
            	Node currentNode = dumplicateNodes.get(i);
            	nodeList.add(currentNode);
            	if (i + 1 == count || (i + 1 < count && !dumplicateNodes.get(i + 1).getNodeName().equals(currentNode.getNodeName()))){
            		nodeList.add(cacheMap.get(currentNode.getNodeName()));
            		cacheMap.remove(currentNode.getNodeName());
            		reRead(newKey(currentNode, key), nodeList);
            	}else
            	if (i + 1 < count)
            		nodeList = new ArrayList<Node>();
            }
        }
        for (Entry<String,Node> entry : cacheMap.entrySet()){
        	doResolve((Element) entry.getValue(), newKey(entry.getValue(), key));
        }
        if (key.length() > 0)
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
	protected void outNode(Node node){
		NodeList nl = node.getChildNodes();
		String name = node.getNodeName();
		System.out.println(name);
		//System.out.println(node.getParentNode().getNodeName());
		for (int i = 0; i < nl.getLength(); i ++)
			if (nl.item(i).getNodeType() == Node.ELEMENT_NODE)
				System.out.println(nl.item(i).getNodeName());
		System.out.println();
	}
	protected void outAll(){
		for (Entry<String, String> entry : holder.entrySet())
			System.out.println(entry.getKey() + " " + entry.getValue());

	}
	 /**
     * 对node进行重读取
     */
    private void reRead(String nodePath, List<Node> nodeList){
    	StringBuilder resultJSON = new StringBuilder();
    	//转换为JO并合并为一个整体的字符串
    	for (Node node : nodeList){
    		JSONObject jo = nodeToJSONObject(node);
    		//移除为了标记子节点消除不必要内部文档的标记IS_LEAF
    		jo.remove(IS_LEAF);
    		resultJSON.append(jo.toJSONString()).append(BLOCK);
    	}
    	nodePath = nodePath.substring(0, nodePath.length() - 1);
    	holder.put(nodePath, resultJSON.toString());
    } /**
     * 将node转为JSONObject
     * @param node
     * @return
     */
    private JSONObject nodeToJSONObject(Node node){
    	JSONObject jo = new JSONObject();
    	Map<String,JSONObject> jos = new HashMap<>();
    	//如果有子节点，递归转换
    	NodeList list = node.getChildNodes();
    	//判断是否有Attr，如果有转换为JSON
    	JSONObject attr = null;
    	if (node.hasAttributes()){
    		NamedNodeMap attrs = node.getAttributes();
    		attr = new JSONObject();
    		for (int i = 0; i < attrs.getLength(); i++)
    			attr.put(attrs.item(i).getNodeName(), attrs.item(i).getTextContent().trim());
    	}
    	//查找子节点,子节点的名称和相应的JSONObject存在Map中
    	//P.S. 因为使用的是原生DOM，所以list不可能是空的
    	for (int i = 0; i < list.getLength(); i++){
    		Node child = list.item(i);
    		if (child.getNodeType() == Node.ELEMENT_NODE){
    			//如果存在子节点，递归执行
    			JSONObject inner = nodeToJSONObject(child);
    			jos.put(child.getNodeName(), inner);
    		}	
    	}
    	if (jos.size() == 0) {
    		jo.put(node.getNodeName(), node.getTextContent().trim());
    		if (attr != null) jo.put("attrs", attr);
    		else jo.put(IS_LEAF, "true");
    	}
    	else {
    		//jo.put(node.getNodeName(), "");
    		if (attr != null) jo.put("attrs", attr);
    		for (Entry<String,JSONObject> entry : jos.entrySet()){
    			JSONObject j = entry.getValue();
    			if (j.getString(IS_LEAF) == "true") {
    				String va = null;
    				for (Entry<String, Object> en :j.entrySet())
    					if(en.getKey() != IS_LEAF)
    						va = (String) en.getValue();
    				jo.put(entry.getKey(), va);
    			}else
    				jo.put(entry.getKey(), entry.getValue());
    		}
    	}
    	return jo;
    }
    @Override
	public List<String> getList(String key) {
		String[] result = holder.get(key).split(BLOCK);
		List<String> resultList = new ArrayList<>();
		for (int i = 0; i < result.length; i++)
			resultList.add(result[i]);
		return resultList; 
	}

	@Override
	public List<JSONObject> getJSONList(String key) {
		List<JSONObject> jos = new ArrayList<>();
		String[] jo = holder.get(key).split(BLOCK);
		for (int i = 0; i < jo.length; i++)
			jos.add(JSONObject.parseObject(jo[i]));
		return jos;
	}

    public static void main(String[] args){
    	XMLConfigHolderDumplicatedUndone ch = new XMLConfigHolderDumplicatedUndone();
    	ch.parse(new File("etc/es.xml"));
    	List<String> result = ch.getList("store_list.tablename");
    	for (String s  : result)
    		System.out.println(s);
    	 
    }
}
