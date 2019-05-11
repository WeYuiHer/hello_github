package control;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.CommDAO;

public class LogInBack extends HttpServlet {

	
	public LogInBack() {
		super();
	}

	
	public void gor(String url,HttpServletRequest request, HttpServletResponse response)
	{
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String ac = request.getParameter("ac");
		CommDAO dao = new CommDAO();
		HttpSession session = request.getSession();
		//登录
				if(ac.equals("login"))
				{
					    String username = request.getParameter("username");
					    String userpwd = request.getParameter("userpwd");
					    	String sql = "select * from sysuser where username='"+username+"' and userpwd='"+userpwd+"' and shstatus='已通过' and delstatus=0 ";
					    	List<HashMap> list = dao.select(sql);
					    	if(list.size()==1)
					    	{
					    	session.setAttribute("admin", list.get(0));
					    	if(list.get(0).get("usertype").equals("管理员")){
					    		gor("/schoolzjssys/admin/newslist.jsp", request, response);
					    	}else if(list.get(0).get("usertype").equals("骑手")){
					    		gor("/schoolzjssys/admin/myrw.jsp", request, response);
					    	}else{
					    		gor("/schoolzjssys/admin/goodsglforshop.jsp", request, response);
					    	}
					    	}else{
					    		request.setAttribute("error", "");
						    	gor("/schoolzjssys/admin/login.jsp", request, response);
					    	}
				}
				//后台退出
				if(ac.equals("backexit")){
					session.removeAttribute("admin");
					gor("/schoolzjssys/admin/login.jsp", request, response);
				}
	
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
		
	}

}
