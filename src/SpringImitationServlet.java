import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Model.Aop;
import MyAnnotation.RequestMapper;
import reflection.BeanFactory;
import util.IsRunUtil;

public class SpringImitationServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String url = request.getRequestURI(); // 工程名

		String[] strs = url.split("/");

		String controllerName = strs[2];
		String methodName = strs[3].substring(0, strs[3].length()-3);

		Map<String, Object> beanFactory = (Map<String, Object>) getServletContext() .getAttribute("beanFactory");
		// 拿到请求的Controller
		Object o = beanFactory.get(controllerName);
		if (o != null) {
			StringBuilder sb = new StringBuilder();
			Class c = o.getClass();
			sb.append(c.getName());
			// 现在拿请求的那个方法
			Method[] ms = c.getMethods();
			for (int i = 0; i < ms.length; i++) {
				Method m = ms[i];
				 
				Annotation[] as = m.getDeclaredAnnotations();
				for (int j = 0; j < as.length; j++) {
					if (as[j].toString().indexOf("RequestMapper") != -1) {
						sb.append("."+m.getName()+"(");
						RequestMapper r = (RequestMapper) as[j];		
						String val = r.id();
						if(val.equals(methodName)){
						  try{	
							Class[] ts = m.getParameterTypes();
							if(ts.length==0){
								sb.append(")");
							}else
							for(int a=0;a<ts.length;a++){
								if(a==ts.length-1){
								  sb.append(ts[a].getSimpleName()+")");
								}else{
								  sb.append(ts[a].getSimpleName()+",");
								}
							}
							sb.insert(0, m.getReturnType().getSimpleName()+"#");
							boolean b = IsRunUtil.isRun(sb.toString(),o,m.getName());
							//如果这里返回true，则说明没有为当前这个请求配置Aop
							if(!b){
						      m.invoke(o,null);
							}
						  }catch(Exception e){e.printStackTrace();}
						}
					}
				}

			}
		}

		String param = request.getQueryString(); // 参数

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	@Override
	public final void init() throws ServletException {
		BeanFactory.init("config.xml");
		Map<String, Object> beanFactory = BeanFactory.beanMap;
		Map<String, Aop> aopMap = BeanFactory.aopMap;
		ServletContext context = getServletContext();
		context.setAttribute("beanFactory", beanFactory);
		context.setAttribute("aopFactory",aopMap);
	}
}
