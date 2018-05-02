package com.configcommon;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
	private String nodeName;
	private Map<String,TrieNode> child = new HashMap<>();
	private TrieNode parent;
	private int prefixes = 0;
	
	public TrieNode(String nodeName){
		this.nodeName = nodeName;
	}
	
	public Map<String,TrieNode> getChild(){
		return child;
	}
	
	public String getNodeName(){
		return nodeName;
	}
	
	public void setParent(TrieNode parent){
		this.parent = parent;
	}
	
	public TrieNode getParnet(){
		return parent;
	}

	public int getPrefixes() {
		return prefixes;
	}

	public void setPrefixes(int prefixes) {
		this.prefixes = prefixes;
	}
	
	
}
