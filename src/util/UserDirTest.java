package util;
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import MyAnnotation.Controller;

public class UserDirTest {
	public static List<Class<?>> getList() throws Exception{
		List<Class<?>> list = new ArrayList<Class<?>>();	
		List<Class<?>> list2 = new ArrayList<Class<?>>();	
		Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources("");
		while(dirs.hasMoreElements()){
			 URL url = dirs.nextElement();  
			 String filePath = URLDecoder.decode(url.getFile(), "UTF-8");  
			 findAndAddClassesInPackageByFile("", filePath,true, list);			 
		}
		if (list.size() > 0) {
			for (int j = 0; j < list.size(); j++) {
				Controller c = list.get(j).getAnnotation(Controller.class);
				if (c != null) {
				  list2.add(list.get(j));
				}
			}
		}
		return list2;
	}
	
	 public static void findAndAddClassesInPackageByFile(String packageName,  
	            String packagePath, final boolean recursive, List<Class<?>> classes) {  
	        // 获取此包的目录 建立一个File  
	        File dir = new File(packagePath);  
	        // 如果不存在或者 也不是目录就直接返回  
	        if (!dir.exists() || !dir.isDirectory()) {  
	            return;  
	        }  
	        // 如果存在 就获取包下的所有文件 包括目录  
	        File[] dirfiles = dir.listFiles(new FileFilter() {  
	            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)  
	            @Override
				public boolean accept(File file) {  
	                return (recursive && file.isDirectory())||(file.getName().endsWith(".class"));  
	            }  
	        });  
	        // 循环所有文件  
	        for (File file : dirfiles) {  
	        	// 如果是目录 则继续扫描  
	            if (file.isDirectory()) {
	            	if(!"".equals(packageName)){
	            	
	                 findAndAddClassesInPackageByFile(packageName + "." + file.getName(),file.getAbsolutePath(), recursive, classes);
	            	}else
	            	findAndAddClassesInPackageByFile(file.getName(),file.getAbsolutePath(), recursive, classes);	
	            } else {  
	            	
	                // 如果是java类文件 去掉后面的.class 只留下类名  
	                String className = file.getName().substring(0,file.getName().length() - 6);  
	                try {  
	                    // 添加到集合中去  
	                	if(!"".equals(packageName)){
	                    classes.add(Class.forName(packageName + '.' + className)); 
	                	}else
	                	classes.add(Class.forName(className)); 	
	                } catch (ClassNotFoundException e) {  
	                    e.printStackTrace();  
	                }  
	            }  
	        }  
	    }  
}
