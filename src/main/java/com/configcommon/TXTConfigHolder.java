package com.configcommon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Scanner;

/** 一个通用的txt阅读工具，支持#开头的注释和空行的过滤，按行读入配置数据并且可以根据给定的分割符将每行分割成String数组
 * Created by kong.haishuo on 2017/11/15.
 */
public class TXTConfigHolder {
    private List<String> lineHolder = new ArrayList<>();

    public  TXTConfigHolder(){

    }

    public TXTConfigHolder parse(String fileName) throws IOException{
        File file = new File(fileName);
        return parse(file);
    }

	public TXTConfigHolder parse(File file) throws IOException{
        Scanner sc = new Scanner(file);
        while(sc.hasNextLine()){
            String next = sc.nextLine();
            if (next.trim().equals("")) continue;
            if (next.charAt(0) == '#') continue;
            lineHolder.add(next);
        }
        sc.close();
        return this;
    }

    public List<String> getAll(){
        return  lineHolder;
    }

    public List<String[]> getSplit(String regex){
        List<String[]> result = new ArrayList<>();
        for (String line : lineHolder){
            String[] after = line.split(regex);
            result.add(after);
        }
        return result;
    }
    public static void main(String[] args) {
        TXTConfigHolder tch = new TXTConfigHolder();
        try {
            List<String> resultAll = tch.parse("etc/test.txt").getAll();
            List<String[]> resultSpliut = tch.getSplit(":");
            System.out.println(resultAll.size());
            System.out.println(resultSpliut.size());
            for (String s : resultAll)
                System.out.println(s);
            for (String[] s1 : resultSpliut){
                for(String s2 :s1)
                    System.out.print(s2 + " ");
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
