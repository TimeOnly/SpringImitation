package util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.Aop.AopTest;
import com.controller.UserController;

import reflection.BeanFactory;
import reflection.BeanProxy;
import Model.Aop;
import MyAnnotation.Column;
import MyAnnotation.RequestMapper;

public class IsRunUtil {
	// str是当前请求的那个Controller的路径,o就是那个Controller,methodName是请求的那个方法的名字
	public static boolean isRun(String str, Object o, String methodName) throws Exception {
		boolean b = false;
		Map<String, Aop> aopMap = BeanFactory.aopMap;

		Iterator iter = aopMap.entrySet().iterator();
		Map<String, Object> beanMap = BeanFactory.beanMap;
		// 拿到所有公开属性
		Field[] fs = o.getClass().getDeclaredFields();
		Map<String, Column> map = new HashMap<String, Column>();
		for (int c = 0; c < fs.length; c++) {
			map.put(fs[c].getName(), fs[c].getAnnotation(Column.class));
		}
		BeanInfo info = Introspector.getBeanInfo(o.getClass());
		PropertyDescriptor[] pros = info.getPropertyDescriptors();

		while (iter.hasNext()) {

			Map.Entry entry = (Map.Entry) iter.next();

			String key = (String) entry.getKey();

			Aop val = (Aop) entry.getValue();

			key = key.replace(" ", "#");

			b = judge(key, str);

			if (b) {
				    Method m = o.getClass().getDeclaredMethod(methodName, null);
					BeanProxy beanProxy = new BeanProxy();
					// 生成的代理类
					o = beanProxy.getInstance(o,BeanFactory.beanMap.get(val.getBeanName()),val.getTypeAndMethodName());

					// 重新为这个代理类注入属性值。
					for (int a = 0; a < pros.length; a++) {
						if (map.get(pros[a].getName()) != null) {
							Column col = map.get(pros[a].getName());
							if (col != null) {
								// 这个是要给属性注入的类
								Object o1 = beanMap.get(col.id());
								Method method = pros[a].getWriteMethod();
								method.invoke(o, o1);
							}
						}
					}
					m.invoke(o, null);
				break;
			}
		}
		return b;
	}

	// 判断是否需要aop,true为需要。
	private static boolean judge(String aop, String url) {
		boolean b = true;

		String aopStr1 = aop.split("#")[0];
		String aopStr2 = aop.split("#")[1];

		String urlStr1 = url.split("#")[0];
		String urlStr2 = url.split("#")[1];

		if (!"*".equals(aopStr1) && !aopStr1.equals(urlStr1)) {
			return false;
		}

		if (aopStr2.split(".").length == urlStr2.split(".").length) {
			String[] str1 = aopStr2.split(".");
			String[] str2 = urlStr2.split(".");
			for (int i = 0; i < str1.length; i++) {
				if (str1[i].indexOf("(") == -1) {
					if (!"*".equals(str1[i]) && !str1[i].equals(str2[i])) {
						b = false;
					}
				} else {
					String aopkh = str1[i].substring(str1[i].indexOf("(") + 1,str1[i].lastIndexOf(")"));
					String urlkh = str2[i].substring(str2[i].indexOf("(") + 1,str2[i].lastIndexOf(")"));
					String aopMName = str1[i].substring(0, str1[i].indexOf("("));
					String urlMName = str2[i].substring(0, str2[i].indexOf("("));
					if (("..".equals(aopkh) || aopkh.equals(urlkh))&& aopMName.equals(urlMName)) {
						b = true;
					} else {
						b = false;
					}
				}
			}
		} else {
			return false;
		}

		return b;
	}

}
