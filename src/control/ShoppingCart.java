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

public class ShoppingCart extends HttpServlet {


	public ShoppingCart() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HashMap member = (HashMap)request.getSession().getAttribute("member");
		PrintWriter out = response.getWriter();
		String ac = request.getParameter("ac");
		CommDAO dao = new CommDAO();
		//商品加入购物车
				if(ac.equals("tocar")){
					String gid = request.getParameter("gid");
					int sl = Integer.valueOf(request.getParameter("sl"));
					if(member!=null){
						String mid = member.get("id").toString();
						//检查该人的购物车是否有该物品
						ArrayList<HashMap>  cklist = (ArrayList<HashMap>)dao.select("select * from car where mid='"+mid+"' and gid='"+gid+"'");
						if(cklist.size()>0){
							dao.commOper("update car set sl=sl+"+sl+" where mid='"+mid+"' and gid='"+gid+"' ");
						}else{
							dao.commOper("insert into car (gid,sl,mid) values ('"+gid+"','"+sl+"','"+mid+"')");
						}
						out.print("true");
					}else{
						out.print("false");
					}
//					request.setAttribute("suc", "");
//					go("/tocar.jsp?gid="+gid, request, response);
				}
				//从购物车移出商品
				if(ac.equals("removecart")){
					String cartid = request.getParameter("cartid");
					dao.commOper("delete from car where id="+cartid);
					out.print("true");
				}
		
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}
