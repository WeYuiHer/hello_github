package control;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.CommDAO;

public class LogIn extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public LogIn() {
		super();
	}

	public void go(String url,HttpServletRequest request, HttpServletResponse response)
	{
	try {
		request.getRequestDispatcher(url).forward(request, response);
	} catch (ServletException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			String ac = request.getParameter("ac");
			HttpSession session = request.getSession();
			CommDAO dao = new CommDAO();
		//会员登录
				if(ac.equals("frontlogin")){
					String uname = request.getParameter("uname");
					String upass = request.getParameter("upass");
					ArrayList cklist = (ArrayList)dao.select("select * from member where uname='"+uname+"' and upass='"+upass+"' and delstatus='0'");
					if(cklist.size()>0){
						session.setAttribute("member", cklist.get(0));
						go("/index.jsp", request, response);
					}else{
						request.setAttribute("info", "用户名或密码错误!");
						go("/login.jsp", request, response);
					}
					
				}
				
				//前台退出
				if(ac.equals("frontexit")){
					Cookie cookie = new Cookie("key", null);
					cookie.setMaxAge(0);
					session.removeAttribute("member");
					go("/index.jsp", request, response);
				}
				
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
