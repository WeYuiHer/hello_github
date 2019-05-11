package control;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.CommDAO;

public class OrderConfirm extends HttpServlet {

	
	public OrderConfirm() {
		super();
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		CommDAO dao = new CommDAO();
		PrintWriter out = response.getWriter();
		String id = request.getParameter("id");
		
		dao.commOper("update dddetail set shstatus='已收货' where id="+id);
		dao.commOper("update ddinfo set shstatus = '已收货' where ddno = (select ddno from dddetail where id ="+id+")");
		
		out.print("true");
		
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
