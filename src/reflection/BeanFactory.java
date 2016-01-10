package reflection;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import util.UserDirTest;
import Model.Aop;
import MyAnnotation.Column;
import MyAnnotation.Controller;

public class BeanFactory {
	//所有的注解类都会放进这个Map里面
	public static Map<String, Object> beanMap = new HashMap<String, Object>();
  
	public static Map<String, Object> beanMap2 = new HashMap<String, Object>();
	
	public static Map<String, Aop> aopMap = new HashMap<String,Aop>();
	  
	public static  void init(String xml) {
		setBeanFactory(xml);
		setBeanFactory();
		setAopFactory();
	}
	
	//这个方法先把所有Controller类装进M
	public static void getPropertyMap(List<Class<?>> list){
		try{
		  for(int i=0;i<list.size();i++)
			beanMap.put(list.get(i).getAnnotation(Controller.class).id(),list.get(i).newInstance());
		}catch(Exception e){e.printStackTrace();}
	}
	
	//这个方法先把xml里所有bean类装进M
	public static void getPropertyMap2(Element ele){
		try{
			//获取它的子节点bean
			Iterator i = ele.elementIterator("bean");
			
			Element foo;
			// 遍历bean
			while(i.hasNext()){
				foo = (Element) i.next();
				// 获取bean的属性id和class
				Attribute id = foo.attribute("id");
				Attribute cls = foo.attribute("class");
				beanMap2.put(id.getText(),Class.forName(cls.getText()).newInstance());
			}
		}catch(Exception e){e.printStackTrace();}
	}
	
	//从xml将bean装入BeanFactory
	public static void setBeanFactory(String xml){
	 try{
		/**读取xml文件装入Factory Start**/
		// 读取指定的配置文件
		SAXReader reader = new SAXReader();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		// 从class目录下获取指定的xml文件
		InputStream ins = classLoader.getResourceAsStream(xml);
		Document doc = reader.read(ins);
		Element root = doc.getRootElement();
		getPropertyMap2(root);
		Element foo;
		//获取它的子节点bean
		Iterator i = root.elementIterator("bean");
		// 遍历bean
		while(i.hasNext()){
			foo = (Element) i.next();
			// 获取bean的属性id和class
			Attribute id = foo.attribute("id");
			Attribute cls = foo.attribute("class");
			// 利用Java反射机制，通过class的名称获取Class对象
			Class bean = Class.forName(cls.getText());
			
			// 获取对应class的信息
			java.beans.BeanInfo info = java.beans.Introspector.getBeanInfo(bean);
			// 获取其属性描述
			java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();
			// 设置值的方法
			Method mSet = null;
			// 创建一个对象
			Object obj = bean.newInstance();
			// 遍历该bean的property属性
			for (Iterator ite = foo.elementIterator("property"); ite.hasNext();) {
				Element foo2 = (Element) ite.next();
				// 获取该property的name属性和value属性
				Attribute name = foo2.attribute("name");
				Attribute ref = foo2.attribute("ref");
			 if(ref==null){
				String value = foo2.attribute("value").getText();
				for (int k = 0; k < pd.length; k++) {
					if (pd[k].getName().equalsIgnoreCase(name.getText())) {
						Class class1 = pd[k].getPropertyType();
						mSet = pd[k].getWriteMethod();
						Object val = null;
						if (class1.getName().indexOf("int") != -1) {
							val = Integer.parseInt(value);
						} else if (class1.getName().indexOf("String") != -1) {
							val = value;
						} else if (class1.getName().indexOf("long") != -1) {
							val = Long.parseLong(value);
						} else if (class1.getName().indexOf("short") != -1) {
							val = Short.parseShort(value);
						}if (class1.getName().indexOf("float") != -1) {
							val = Float.parseFloat(value);
						} else if (class1.getName().indexOf("double") != -1) {
							val = Double.parseDouble(value);
						}
						
						// 利用Java的反射调用对象的某个set方法，并将值设置进去 
						mSet.invoke(obj,val);
					}
				}
			 }else{
				 String refValue = ref.getText();
				 Object o = beanMap2.get(refValue);
				 for (int k = 0; k < pd.length; k++) {
						if (pd[k].getName().equalsIgnoreCase(name.getText())) {
							mSet = pd[k].getWriteMethod();
							mSet.invoke(obj, o);
						}
				}
			 }
			}

			// 将对象放入beanMap中，其中key为id值，value为对象
			beanMap.put(id.getText(), obj);
		}
		/**读取xml文件装入Factory End**/
	 }catch(Exception e){e.printStackTrace();}
	}
	//从注解里将bean装入beanFactory
    public static void setBeanFactory(){
      try {
			/**扫描注解文件装入Factory Start**/
			List<Class<?>> list = UserDirTest.getList();
			if (list.size() > 0) {
			  getPropertyMap(list);
				for (int j = 0; j < list.size(); j++) {
					Field[] fs = list.get(j).getDeclaredFields();
					String id = list.get(j).getAnnotation(Controller.class).id();
					
					Map<String,Column> map = new HashMap<String,Column>();
					for(int b=0;b<fs.length;b++){
					  map.put(fs[b].getName(),fs[b].getAnnotation(Column.class));
					}
					BeanInfo info = Introspector.getBeanInfo(list.get(j));
					
					PropertyDescriptor[] pros = info.getPropertyDescriptors();
					
					for(int a = 0 ;a<pros.length;a++){
						if(map.get(pros[a].getName())!=null){
						  Column col = map.get(pros[a].getName());
						  if(col!=null){
							//这个是要给属性注入的类
							Object o = beanMap.get(col.id());
							//这个是属性所在类，也就是说属性是属于这个类的,即Controller类
							Object o2 = beanMap.get(id); 
						    Method method = pros[a].getWriteMethod();
						    method.invoke(o2,o);
						    beanMap.put(id, o2);
						 }
					    }
					}
				}
			}
			/**扫描注解文件装入Factory End**/
		} catch (Exception e) {
			System.out.println(e.toString());
		}	
	}

    //从xml将aop类装进beanFactory
	public static void setAopFactory() {
		try {
			SAXReader reader = new SAXReader();
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			// 从class目录下获取指定的xml文件
			InputStream ins = classLoader.getResourceAsStream("config.xml");
			Document doc = reader.read(ins);
			Element root = doc.getRootElement();
			Iterator i = root.elementIterator("aop");
			Element foo = null;
			Aop aop = null;
			while(i.hasNext()){
				String key = null;
				foo = (Element) i.next();
				//初始化Aop类
				aop = new Aop();
				
				Attribute ref = foo.attribute("ref");
				
				aop.setBeanName(ref.getText());
				
				Iterator a = foo.elementIterator("pointcut");
				
                Iterator b = foo.elementIterator("method");
				
				Iterator c = foo.elementIterator("before");
				
				
				while(a.hasNext()){
					  foo = (Element) a.next();
					  Attribute id1 = foo.attribute("id");
					  Attribute expression = foo.attribute("expression");
					  key = expression.getText();
			   }
				
				while (b.hasNext()) {
					foo = (Element) b.next();
					Attribute pointcutRef = foo.attribute("pointcut-ref");
					Attribute method = foo.attribute("method");
					Attribute type = foo.attribute("type");
                    aop.setTypeAndMethodName(type.getText()+"&"+method.getText());
				}
				
				// 获取bean的属性id和class
				/*Attribute id = foo.attribute("id");
				  Attribute ref = foo.attribute("ref");
				
				aop.setAopId(id.getText());
				aop.setAopRef(ref.getText());
				
				Iterator a = foo.elementIterator("pointcut");
	
				Iterator b = foo.elementIterator("after");
				
				Iterator c = foo.elementIterator("before");
               
				while(a.hasNext()){
				  foo = (Element) a.next();
				  Attribute id1 = foo.attribute("id");
				  Attribute expression = foo.attribute("expression");
				  aop.getPointcut().put(id1.getText(),expression.getText());
				}
				
				while (b.hasNext()) {
					foo = (Element) b.next();
					Attribute pointcutRef = foo.attribute("pointcut-ref");
					Attribute method = foo.attribute("method");
					aop.getMethod().put(pointcutRef.getText(),"after!"+method.getText());

				}
				
				while (c.hasNext()) {
					foo = (Element) c.next();
					Attribute pointcutRef = foo.attribute("pointcut-ref");
					Attribute method = foo.attribute("method");
					aop.getMethod().put(pointcutRef.getText(),"before!"+method.getText());

				}*/
				// 将对象放入beanMap中，其中key为id值，value为对象
				aopMap.put(key, aop);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
	public Object getBean(String beanName) {
		Object obj = beanMap.get(beanName);
		return obj;
	}
}
