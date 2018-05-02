package com.configcommon;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson.JSONObject;
/**
 * XMLConfigHolder是一个XML配置的容器，无法解决在重复父标签下还有重复子标签的问题
 * @author kong.haishuo
 *
 */
public class XMLConfigHolderDumplicated extends XMLConfigHolderAttr implements XMLConfHolder {
	private final static String IS_LEAF = "IS_LEAF";
	private final static String BLOCK = "##";
	private TrieTree tree = new TrieTree();
	private Element root = null;
	public List<String> getTest(){
		return tree.searchLeafNodeToString(1);
	}
	@Override
	public void parse(String path) {
		File file = new File(path);
		parse(file);
	}
	@Override
	public void parse(File file) {
		root = parseXML(file);
		super.parse(file);
		afterParse();
		tree = null;
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
		String value = holder.get(key);
        if (value == null){
            JSONObject joTemp = new JSONObject(true);
            for (String keys : holder.keySet())
                if (keys.startsWith(key)) {
                    String[] currentKeys = keys.split("\\.");
                    joTemp.put(currentKeys[currentKeys.length - 1], holder.get(keys));
                }
            jos.add(joTemp);
            return jos;
        }
        String[] jo = value.split(BLOCK);
		if (jo.length > 1)
		for (int i = 0; i < jo.length; i++)
			try {
				jos.add(JSONObject.parseObject(jo[i]));
			} catch (Exception e) {
				continue;
			}
        else if (jo.length  == 1){
            try{
                jos.add(JSONObject.parseObject(jo[0]));
            }catch(Exception e){
                JSONObject joTemp = new JSONObject(true);
                String[] keys = key.split("\\.");
                joTemp.put(keys[keys.length - 1], jo[0]);
                jos.add(joTemp);
            }
        }
		return jos;
	}

    /**
     * 递归解析类型为{@link Node}.ELEMENT_NODE的{@linkplain Node},即{@link Element}.其逻辑如下:
     * <br>
     * 1) 如果此节点为叶子节点，那么按照<key.NodeName, 文本内容>的格式放入到map.
     * <br>
     * 2) 如果不是叶子节点，那么递归调用此方法.
     * <br>
     * 3) 在这里在doResolve方法中添加了对TrieTree的操作
     */
    @Override
    protected void doResolve(Element element, String key) {
        super.doResolve(element, key);
        //如果非叶子节点没有重复，那么每个节点只会加入字典树一次
         tree.addInTree(key);
    }
    
    /**
     * afterParse方法用来分析TrieTree得出重复节点并将重复节点进行压缩
     */
    private void afterParse(){
    	//获取出现重复的节点中层级最高的节点列表
    	List<TrieNode> duplicated = tree.searchNode(1);
    	//针对每一个节点进行重新读取
    	for (TrieNode tn : duplicated)
    		reRead(tree.nodeToString(tn));
    	
    }
    /**
     * 对node进行重读取
     * @param nodePath
     */
    private void reRead(String nodePath){
    	StringBuilder resultJSON = new StringBuilder();
    	List<Node> nodeList = findNode(nodePath);
    	//转换为JO并合并为一个整体的字符串
    	for (Node node : nodeList){
    		JSONObject jo = nodeToJSONObject(node);
    		//移除为了标记子节点消除不必要内部文档的标记IS_LEAF
    		jo.remove(IS_LEAF);
    		resultJSON.append(jo.toJSONString()).append(BLOCK);
    	}
    	holder.put(nodePath, resultJSON.toString());
    }
    /**
     * 查找重复的节点
     * @param nodePath
     * @return
     */
    private List<Node> findNode(String nodePath){
    	int count = tree.searchForNode(nodePath).getPrefixes();
    	Node node = root;
    	String[] path = nodePath.split("\\.");
    	for (int i = 0; i < path.length - 1; i++){
    		node = findChildNode(node, path[i], 1).get(0);
    	}
    	return findChildNode(node, path[path.length - 1], count);
    }
    
    /**
     * 查询node下的count个名为name的子节点
     * @param parent
     * @param name
     * @param count
     * @return
     */
    private List<Node> findChildNode(Node parent, String name, int count){
    	NodeList list = parent.getChildNodes();
    	List<Node> resultList = new ArrayList<>();
    	int cnt = 0;
    	for (int i = 0; i < list.getLength(); i++){
    		String thisName = list.item(i).getNodeName();
    		if (thisName.equals(name)){
    			resultList.add(list.item(i));
    			cnt += 1;
    			if (cnt == count) break;
    		}
    	}
    	return resultList;
    }
    /**
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
	public List<String> getListSingle(String key) {
		List<JSONObject> jos = getJSONList(key);
		List<String> resultList = new ArrayList<>();
		String[] keys = key.split("\\.");
		for (JSONObject jo : jos){
            String s = (String) jo.get(keys[keys.length - 1]);
			resultList.add(s);
		}
		return resultList;
	}

    public static void main(String[] args){
    	XMLConfHolder holder = new XMLConfigHolderDumplicated();
		try {
			holder.parse("etc/es.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<JSONObject> singleList = holder.getJSONList("store_list");
		for (JSONObject jo : singleList)
            System.out.println(jo.toString());

	}
}

