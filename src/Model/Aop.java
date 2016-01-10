package Model;

import java.util.HashMap;
import java.util.Map;

public class Aop {
	private String beanName;
	// 数据类型是type&方法名的格式 如 after&getName 意思是在目标类执行后才执行切面类的getName方法
	private String typeAndMethodName;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getTypeAndMethodName() {
		return typeAndMethodName;
	}

	public void setTypeAndMethodName(String typeAndMethodName) {
		this.typeAndMethodName = typeAndMethodName;
	}

	/*
	 * private String aopId; private String aopRef;
	 * //key存放pointcut元素的id,value存放expression private Map<String, Object>
	 * pointcut = new HashMap<String,Object>();
	 * //key存放pointcut-ref值,value存放type!method的值 private Map<String, Object>
	 * method = new HashMap<String,Object>();
	 * 
	 * public String getAopId() { return aopId; }
	 * 
	 * public void setAopId(String aopId) { this.aopId = aopId; }
	 * 
	 * public String getAopRef() { return aopRef; }
	 * 
	 * public void setAopRef(String aopRef) { this.aopRef = aopRef; }
	 * 
	 * public Map<String, Object> getPointcut() { return pointcut; }
	 * 
	 * public void setPointcut(Map<String, Object> pointcut) { this.pointcut =
	 * pointcut; }
	 * 
	 * public Map<String, Object> getMethod() { return method; }
	 * 
	 * public void setMethod(Map<String, Object> method) { this.method = method;
	 * }
	 */

}
