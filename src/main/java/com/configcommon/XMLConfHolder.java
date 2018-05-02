package com.configcommon;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
/**
 * XML配置容器接口，定义了关于XML的特定操作方法
 * @author kong.haishuo
 *
 */
public interface XMLConfHolder extends ConfHolder {
	/**
	 * 针对XML，如果存在多个同名的选项，会使用JSON进行压缩，使用这个方法可以返回一个将压缩的选项还原以
	 * 后形成的List
	 * @param key 需要获取的配置名称
	 * @return 返回一个包含所有重名的选项的值的List
	 */
	public List<String> getList(String key);
	/**
	 * 针对出现多个同名的多级选项的情况，经过JSON压缩后，会将每个重合的最高级标签压缩成一个JSON对象并
	 * 合成一个字符串。使用这个方法可以将压缩后的JSONObject还原返回一个JSONObject的List
	 * @param key
	 * @return 返回一个包含所有重名最高级别节点的JSONObject的List
	 */
	public List<JSONObject> getJSONList(String key);

	/**
	 * 针对单个重复节点将JO抽为String
	 * @param key
	 * @return
     */
	public List<String> getListSingle(String key);

	public static XMLConfHolder createHolder(File f) throws Exception {
		XMLConfHolder holder = new XMLConfigHolder();
		holder.parse(f);
		return holder;
	}

	public static XMLConfHolder createHolderAttr(File f) throws Exception {
		XMLConfHolder holder = new XMLConfigHolderAttr();
		holder.parse(f);
		return holder;
	}

	public static XMLConfHolder createHolderDumplicated(File f) throws Exception {
		XMLConfHolder holder = new XMLConfigHolderDumplicated();
		holder.parse(f);
		return holder;
	}

	public static XMLConfHolder createHolderDumplicated(String path) throws Exception {
		XMLConfHolder holder = new XMLConfigHolderDumplicated();
		holder.parse(new File(path));
		return holder;
	}

	public static XMLConfHolder createHolderAttr(String path) throws Exception {
		XMLConfHolder holder = new XMLConfigHolderAttr();
		holder.parse(new File(path));
		return holder;
	}

	public static XMLConfHolder createHolder(String path) throws Exception {
		XMLConfHolder holder = new XMLConfigHolder();
		holder.parse(new File(path));
		return holder;
	}
}
