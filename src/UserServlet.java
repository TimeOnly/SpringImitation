import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("User Get方法启动");
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	@Override
	public final void init() throws ServletException {
		System.out.println("User Start");
		ServletContext context = getServletContext();
		Map<String, Object> o = (Map) context.getAttribute("beanFactory");

		Iterator iter = o.entrySet().iterator();

		while (iter.hasNext()) {

			Map.Entry entry = (Map.Entry) iter.next();

			Object key = entry.getKey();

			Object val = entry.getValue();

			System.out.println("key是:" + key + ",val是:" + val);

		}
	}

}
