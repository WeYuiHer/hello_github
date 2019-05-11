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

public class AcceptOrder extends HttpServlet {

	
	public AcceptOrder() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		
		HttpSession session = request.getSession();
		HashMap member = (HashMap)session.getAttribute("member");
		PrintWriter out = response.getWriter();
		CommDAO dao = new CommDAO();
		String dddetailid = request.getParameter("dddetailid");
		dao.commOper("update dddetail set fhstatus = '已接单' where id = "+Integer.parseInt(dddetailid));
		String qsid = member.get("id").toString();
		HashMap qsMap = dao.select("select * from member where id = "+Integer.parseInt(qsid)).get(0);
		dao.commOper("update ddinfo set shrtel = '"+qsMap.get("tel")+"' ,shrname = '"
		+qsMap.get("uname")+"' where ddno = (select ddno from dddetail where id="+Integer.parseInt(dddetailid)+")");
		
		
		dao.commOper("insert qsdd (dddetailid,qsid,status,qcstatus) values ('"+dddetailid+"','"+qsid+"','已处理','已取餐')");
		dao.close();
		out.print("true");
		
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
