package control;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.CommDAO;

public class Collection extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public Collection() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			HashMap member = (HashMap)request.getSession().getAttribute("member");
			PrintWriter out = response.getWriter();
			String ac = request.getParameter("ac");
			CommDAO dao = new CommDAO();
				if(ac.equals("addfav")){
					String goodid = request.getParameter("goodid");
					if(member==null){
						out.print("unlogin");
					}else{
						ArrayList cklist = (ArrayList)dao.select("select * from fav where memberid='"+member.get("id")+"' and goodid='"+goodid+"'");
						if(cklist.size()==0){
							dao.commOper("insert into fav (goodid,memberid) values ('"+goodid+"','"+member.get("id")+"')");
							out.print("true");
						}else{
							out.print("false");
						}
					}
				}
				//取消收藏 
				if(ac.equals("delfav")){
					String favid = request.getParameter("favid");
					dao.commOper("delete from fav where id='"+favid+"'");
					out.print("true");
				}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
		
	}

	public void init() throws ServletException {
		// Put your code here
	}

}
