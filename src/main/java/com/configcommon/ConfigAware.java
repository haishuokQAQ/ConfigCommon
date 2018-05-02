package com.configcommon;
/**
 * 配置文件类ConfHolder注入的标志
 * @author kong.haishuo
 *
 */
public interface ConfigAware {
	public void setConf(ConfHolder configHolder);
}
