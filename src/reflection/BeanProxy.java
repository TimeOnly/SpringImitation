package reflection;

import java.beans.MethodDescriptor;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class BeanProxy implements MethodInterceptor {
	private Object target;
	private Object proxy2;
	private String method;

	public Object getInstance(Object target, Object proxy, String method) {
		this.target = target;
		this.proxy2 = proxy;
		this.method = method;

		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(this.target.getClass());
		// 回调方法
		enhancer.setCallback(this);
		// 创建代理对象
		return enhancer.create();
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args,MethodProxy proxy) throws Throwable {
		String[] strs = this.method.split("&");
		// 获取对应class的信息,执行Aop切面逻辑
		Method m = proxy2.getClass().getDeclaredMethod(strs[1], null);
		if ("before".equals(strs[0])) {
			// 执行目标类方法
			proxy.invokeSuper(obj, args);
			m.invoke(proxy2, null);
		} else {
			m.invoke(proxy2, null);
			// 执行目标类方法
			proxy.invokeSuper(obj, args);
		}
		return null;
	}

}
