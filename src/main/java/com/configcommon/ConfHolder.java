package com.configcommon;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 配置容器接口，定义了配置容器的通用的操作
 * @author kong.haishuo
 *
 */
public interface ConfHolder {
	/**
	 * 根据传入的路径对配置文件进行解析
	 * @param path
	 */
	public void parse(String path) throws Exception;
	/**
	 * parse方法将会对传入的配置文件进行解析
	 * @param file 传入的配置文件
	 */
	public void parse(File file) throws Exception;
	/**
	 * 获取int类型的配置数据
	 * @param key 需要获取的配置名称
	 * @return 转换为int的对应的配置值
	 */
	public int getInt(String key);
	
	public int getIntorDefault(String key, int defaultValue);
	
	/**
	 * 获取boolean类型的配置数据
	 * @param key 需要获取的配置名称
	 * @return 转换为boolean的对应的配置值
	 */
	public boolean getBoolean(String key);
	
	public boolean getBooleanorDefault(String key, boolean defaultValue);
	/**
	 * 获取String类型的配置数据
	 * @param key 需要获取的配置名称
	 * @return 对应的配置值
	 */
	public String getString(String key);
	
	public String getStringorDefault(String key, String defaultValue);
	/**
	 * 获取long类型的配置数据
	 * @param key 需要获取的配置名称
	 * @return 转换为long的对应的配置值
	 */
	public long getLong(String key);
	
	public long getLongorDefault(String key, long defaultValue);
	/**
	 * 获取double类型的配置数据
	 * @param key 需要获取的配置名称
	 * @return 转换为double的对应配置值
	 */
	public double getDouble(String key);
	
	public double getDoubleorDefault(String key, double defaultValue);
	/**
	 * 配置文件中可能存在\<text\>a,b,c,d\</text\>这类形式，getStringArray方法
	 * 可以将这种形式的配置切成字符串数组并返回
	 * 注意：由于JAVA原生切割功能只会去掉尾部的空值，请关注空值问题
	 * @param key 需要获取的配置名称
	 * @param separator 名称分隔符
	 * @return 将配置值使用分隔符分割后的字符串数组
	 */
	public String[] getStringArray(String key, String separator);
	/**
	 * 配置文件中可能存在\<text\>a,b,c,d\</text\>这类形式，getStringArray方法
	 * 可以将这种形式的配置切分后转换为字符串{@link List}
	 * 注意：由于JAVA原生切割功能只会去掉尾部的空值，请关注空值问题
	 * @param key 需要获取的配置名称
	 * @param separator 名称分隔符
	 * @return 将配置值使用分隔符分割后的字符串数组
	 */
	public List<String> getStringList(String key, String separator);

	/**
	 * 提供了操作内部数据的方法，可以通过这个方法操作内部的数据，可以用于后期的配置修改
	 * @return
     */
	public Map<String, String> operateHolder();
}
