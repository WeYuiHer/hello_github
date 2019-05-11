package control;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.CommDAO;

import util.Info;

public class SubmitOrder extends HttpServlet {

	
	public SubmitOrder() {
		super();
	}
	
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		HashMap member = (HashMap)session.getAttribute("member");
		CommDAO dao = new CommDAO();
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		String memberid= member.get("id").toString();
		//生成订单号
		String ddno = Info.getAutoNo();
		String shrname = "";
		String shrtel = "";
		String shraddr = request.getParameter("addr");
		double ddprice = 0.0;
		String fhstatus = "待接单";
		String shstatus = "待收货";
		String fkstatus = "待付款";
		String wlinfo = "暂无送货人信息";
		String savetime = Info.getDateStr();
		//查询该会员的购物车所有商品
		ArrayList<HashMap> carlist = (ArrayList<HashMap>)dao.select("select * from car where mid="+memberid);
		for(HashMap carmap:carlist){
			HashMap goodmap = dao.select("select * from goods where id="+carmap.get("gid")).get(0);
			if(goodmap.get("tprice")!=null&&!goodmap.get("tprice").equals("")){ 
				ddprice += Double.valueOf(goodmap.get("tprice").toString())*Integer.valueOf(carmap.get("sl").toString());
			}else{
				ddprice += Double.valueOf(goodmap.get("price").toString())*Integer.valueOf(carmap.get("sl").toString());
			}
			double detailprice = 0.0;
			if(goodmap.get("tprice")!=null&&!goodmap.get("tprice").equals("")){ 
				detailprice = Double.valueOf(goodmap.get("tprice").toString())*Integer.valueOf(carmap.get("sl").toString());
			}else{
				detailprice = Double.valueOf(goodmap.get("price").toString())*Integer.valueOf(carmap.get("sl").toString());
			}
			dao.commOper("insert into dddetail (ddno,goodid,sl,price,fkstatus,fhstatus,shstatus,wlinfo,savetime) values " +
					"('"+ddno+"','"+carmap.get("gid")+"','"+carmap.get("sl")+"','"+String.valueOf(detailprice)+"','待付款','待接单','待收货','','"+savetime+"') ");
		}
		dao.commOper("insert into ddinfo (ddno,memberid,ddprice,fhstatus,savetime,shstatus,wlinfo,fkstatus,shrname,shrtel,shraddr) values " +
		"('"+ddno+"','"+memberid+"','"+ddprice+"','"+fhstatus+"','"+savetime+"','"+shstatus+"','"+wlinfo+"','"+fkstatus+"','"+shrname+"','"+shrtel+"','"+shraddr+"')");
		//删除购物车下的商品
		dao.commOper("delete from car where mid="+memberid);
		request.setAttribute("suc", "订单生成成功!");
		go("/mydd.jsp", request, response);
		
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
		
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
}
