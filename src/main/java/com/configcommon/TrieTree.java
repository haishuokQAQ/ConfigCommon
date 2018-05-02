package com.configcommon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * TrieTree是一种字典树，适用于使用固定的分隔符分隔的字符串，用来统计某个路径出现的次数
 * @author kong.haishuo
 *
 */
public class TrieTree {
	private TrieNode root = new TrieNode("");
	private String ss = "\\.";
	/**
	 * 查找字典树中是否存在这个字串
	 * @param value
	 * @return
	 */
	public boolean hasString(String value){
		if (searchForNode(value) != null) return true;
		return false;
	}
	/**
	 * 将新的字符串加入字典树,在终点位置统计数量加一。
	 * @param value
	 */
	public void addInTree(String value){
		String[] args = value.split(ss);
		TrieNode tn = root;
		for (int i = 0; i < args.length; i++){
			//如果存在这个节点那么定位到最后的节点
			if (tn.getChild().get(args[i]) != null) {
				tn = tn.getChild().get(args[i]);
			}else{
				TrieNode t = new TrieNode(args[i]);
				tn.getChild().put(args[i], t);
				t.setParent(tn);
				tn = t;
			}
		}
		tn.setPrefixes(tn.getPrefixes() + 1);
	}
	/**
	 * 查找相应的字符串，如果不存在那么返回null
	 * @param arg
	 * @return
	 */
	public TrieNode searchForNode(String arg){
		String[] args = arg.split(ss);
		TrieNode tn = root;
		for (int i = 0; i < args.length; i++){
			if (tn.getChild().get(args[i]) == null) return null;
			tn = tn.getChild().get(args[i]);
		}
		return tn;
	}
	/**
	 * 查找超过某个出现次数的最高节点并复原
	 * @param profix
	 * @return
	 */
	public List<String> searchLeafNodeToString(int profix){
		List<TrieNode> resultList = searchNode(profix);
		List<String> stringList = new ArrayList<>();
		for (TrieNode tn : resultList)
			stringList.add(nodeToString(tn));
		return stringList;
	}
	/**
	 * 遍历查找指定出现次数的最高节点
	 * @param profix
	 * @return
	 */
	public List<TrieNode> searchNode(int profix){
		List<TrieNode> resultList = new ArrayList<>();
		TrieNode tn = root;
		searchNodeUponStandard(tn, profix,resultList);
		return resultList;
	}
	
	/**
	 * 实际进行遍历查找的方法
	 * @param tn
	 * @param profix
	 * @param resultList
	 */
	private void searchNodeUponStandard(TrieNode tn, int profix,List<TrieNode> resultList){
		//如果为子节点则检查出现次数
		if (tn.getChild().isEmpty())
			if (tn.getPrefixes() > profix) resultList.add(tn);
		//如果不为子节点
		if (!tn.getChild().isEmpty()){
			//当非子节点出现次数尚未超标时，可以检查一下子节点
			if (tn.getPrefixes() <= profix)
			for (Entry<String, TrieNode> entry : tn.getChild().entrySet()){
				searchNodeUponStandard(entry.getValue(), profix, resultList);
			}
			//当非子节点的出现次数超标后，再检查子节点已经毫无意义，剪掉这一枝。
			else{
				resultList.add(tn);
		}
		}
	}
	/**
	 * 还原名称
	 * @param node
	 * @return
	 */
	protected String nodeToString(TrieNode node){
		TrieNode tn = node;
		StringBuilder sb = new StringBuilder();
		while (tn != root){
			if (tn == root) break;
			sb.insert(0, tn.getNodeName());
			//TODO 需要解决转义问题
			sb.insert(0, ".");
			tn = tn.getParnet();
		}
		sb.delete(0, 1);
		return sb.toString();
	}
}
