package com.configcommon;

import java.io.File;

/**
 * 对JSON文件进行解析，将JSON文件中的配置归档
 * @author Administrator
 *
 */
public class JSONConfigHolder extends ConfigBase {

	@Override
	public void parse(String path) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parse(File file) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getIntorDefault(String key, int defaultValue) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getBooleanorDefault(String key, boolean defaultValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getStringorDefault(String key, String defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getLongorDefault(String key, long defaultValue) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDoubleorDefault(String key, double defaultValue) {
		// TODO Auto-generated method stub
		return 0;
	}

}
