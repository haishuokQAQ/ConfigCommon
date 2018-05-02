package com.configcommon;

import java.io.File;

/**
 * 静态工厂类，提供不同的静态工厂入口
 * Created by kong.haishuo on 2017/12/6.
 */
public class XMLConfHolders {
    public static XMLConfHolder xmlHolder(File f) throws Exception {
        XMLConfHolder holder = new XMLConfigHolder();
        holder.parse(f);
        return holder;
    }

    public static XMLConfHolder xmlHolderAttr(File f) throws Exception {
        XMLConfHolder holder = new XMLConfigHolderAttr();
        holder.parse(f);
        return holder;
    }

    public static XMLConfHolder xmlHolderDumplicated(File f) throws Exception {
        XMLConfHolder holder = new XMLConfigHolderDumplicated();
        holder.parse(f);
        return holder;
    }

    public static XMLConfHolder xmlHolderDumplicated(String path) throws Exception {
        XMLConfHolder holder = new XMLConfigHolderDumplicated();
        holder.parse(new File(path));
        return holder;
    }

    public static XMLConfHolder xmlHolderAttr(String path) throws Exception {
        XMLConfHolder holder = new XMLConfigHolderAttr();
        holder.parse(new File(path));
        return holder;
    }

    public static XMLConfHolder xmlHolder(String path) throws Exception {
        XMLConfHolder holder = new XMLConfigHolder();
        holder.parse(new File(path));
        return holder;
    }
}
