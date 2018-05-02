package com.configcommon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
/**
 * 实现公共的获取方法以及容器的基本数据结构
 * @author kong.haishuo
 *
 */
public abstract class ConfigBase  implements ConfHolder {
	protected Map<String,String> holder = new HashMap<>();
	
	@Override
	public int getInt(String key) {
		return Integer.parseInt(holder.get(key));
	}

	@Override
	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(holder.get(key));
	}

	@Override
	public String getString(String key) {
		return holder.get(key);
	}

	@Override
	public long getLong(String key) {
		return Long.parseLong(holder.get(key));
	}

	@Override
	public double getDouble(String key) {
		return Double.parseDouble(holder.get(key));
	}

	@Override
	public String[] getStringArray(String key, String separator) {
		return holder.get(key).split(separator);
	}

	@Override
	public List<String> getStringList(String key, String separator) {
		String[] arrays = getStringArray(key, separator);
		List<String> stringList = new ArrayList<String>();
		for (int i = 0; i < arrays.length; i++){
			stringList.add(arrays[i]);
		}
		return stringList;
	}
	public Map<String, String> operateHolder(){
		return holder;
	}

    @Override
    public String toString() {
        JSONObject jo = new JSONObject(true);
        for(String key :holder.keySet())
            jo.put(key, holder.get(key));
        return jo.toString();
    }
}
