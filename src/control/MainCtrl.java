package control;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import util.Info;

import com.google.gson.Gson;
import com.main.MainMethod;

import dao.CommDAO;

public class MainCtrl extends HttpServlet {
	
	public MainCtrl() {
		super();
	}

	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
	this.doPost(request, response);
	}
	MainMethod responses = new MainMethod();
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
		
		public void gor(String url,HttpServletRequest request, HttpServletResponse response)
		{
			try {
				response.sendRedirect(url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		HashMap admin = (HashMap)session.getAttribute("admin");
		HashMap member = (HashMap)session.getAttribute("member");
		String ac = request.getParameter("ac");
		if(ac==null)ac="";
		CommDAO dao = new CommDAO();
		String date = Info.getDateStr();
		String today = date.substring(0,10);
		String tomonth = date.substring(0,7);
		
		

		//新增新闻
		if(ac.equals("newsadd")){
			try {
				String title = "";
				String img = "";
				String note="";
			request.setCharacterEncoding("utf-8");
			RequestContext  requestContext = new ServletRequestContext(request);
			if(FileUpload.isMultipartContent(requestContext)){
			   DiskFileItemFactory factory = new DiskFileItemFactory();
			   factory.setRepository(new File(request.getRealPath("/upfile/")+"/"));
			   ServletFileUpload upload = new ServletFileUpload(factory);
			   upload.setSizeMax(100*1024*1024);
			   List items = new ArrayList();
			     items = upload.parseRequest(request);
			     
			     title = ((FileItem) items.get(0)).getString();
			     title = Info.getUTFStr(title);
			     
			     note = ((FileItem) items.get(2)).getString();
			     note = Info.getUTFStr(note);

			    FileItem fileItem = (FileItem) items.get(1);
			   if(fileItem.getName()!=null && fileItem.getSize()!=0)
			    {
			    if(fileItem.getName()!=null && fileItem.getSize()!=0){
			      File fullFile = new File(fileItem.getName());
			      img = Info.generalFileName(fullFile.getName());
			      File newFile = new File(request.getRealPath("/upfile/")+"/" + img);
			      try {
			       fileItem.write(newFile);
			      } catch (Exception e) {
			       e.printStackTrace();
			      }
			     }else{
			     }
			    }
			}

			String sql = "insert into news (title,img,note,savetime,type) " +
					"values('"+title+"','"+img+"','"+note+"','"+Info.getDateStr()+"','新闻')" ;
			dao.commOper(sql);
			
			request.setAttribute("suc", "操作成功!");
			gor("/schoolzjssys/admin/newslist.jsp", request, response);
			
			} catch (Exception e1) {
				e1.printStackTrace();
				request.setAttribute("error", "");
			     request.getRequestDispatcher("/schoolzjssys/admin/newsadd.jsp").forward(request, response);
			    }
		}
		//编辑新闻
		if(ac.equals("newsedit")){
			String id = request.getParameter("id");
			HashMap map = dao.select("select * from news where id="+id).get(0);
			try {
				String title="";
				String note="";
				String img=map.get("img").toString();
			request.setCharacterEncoding("utf-8");
			RequestContext  requestContext = new ServletRequestContext(request);
			if(FileUpload.isMultipartContent(requestContext)){

			   DiskFileItemFactory factory = new DiskFileItemFactory();
			   factory.setRepository(new File(request.getRealPath("/upfile/")+"/"));
			   ServletFileUpload upload = new ServletFileUpload(factory);
			   upload.setSizeMax(100*1024*1024);
			   List items = new ArrayList();
			     items = upload.parseRequest(request);
			     title = ((FileItem) items.get(0)).getString();
			     title = Info.getUTFStr(title);
			     
			     note = ((FileItem) items.get(2)).getString();
			     note = Info.getUTFStr(note);
			     
			    FileItem fileItem = (FileItem) items.get(1);
			   if(fileItem.getName()!=null && fileItem.getSize()!=0)
			    {
			    if(fileItem.getName()!=null && fileItem.getSize()!=0){
			      File fullFile = new File(fileItem.getName());
			      img = Info.generalFileName(fullFile.getName());
			      File newFile = new File(request.getRealPath("/upfile/")+"/" + img);
			      try {
			       fileItem.write(newFile);
			      } catch (Exception e) {
			       e.printStackTrace();
			      }
			     }else{
			     }
			    }
			}
			String sql = "update news set title='"+title+"',note='"+note+"',img='"+img+"' where id="+id ;
			dao.commOper(sql);
			request.setAttribute("suc", "操作成功!");
			gor("/schoolzjssys/admin/newslist.jsp?id="+id, request, response);
			} catch (Exception e1) {
				e1.printStackTrace();
				request.setAttribute("error", "");
			     request.getRequestDispatcher("/schoolzjssys/admin/newsedit.jsp?id="+id).forward(request, response);
			    }
	}
		//新增公告
		if(ac.equals("noticesadd")){
			String title = request.getParameter("title");
			String note = request.getParameter("note");
			String savetime = Info.getDateStr();
			String type = "公告";
			dao.commOper("insert into news (title,note,savetime,type) " +
					" values ('"+title+"','"+note+"','"+savetime+"','"+type+"')");
			request.setAttribute("suc", "");
			go("admin/noticesadd.jsp", request, response);
		}
		//编辑公告
		if(ac.equals("noticesedit")){
			String id = request.getParameter("id");
			String title = request.getParameter("title");
			String note = request.getParameter("note");
			dao.commOper("update news set title='"+title+"',note='"+note+"' where id="+id);
			request.setAttribute("suc", "");
			go("admin/noticesedit.jsp?id="+id, request, response);
		}
		//新增链接
		if(ac.equals("yqlinkadd")){
			String linkname = request.getParameter("linkname");
			String linkurl = request.getParameter("linkurl");
			dao.commOper("insert into yqlink (linkname,linkurl) " +
					" values ('"+linkname+"','"+linkurl+"')");
			request.setAttribute("suc", "操作成功!");
			gor("/schoolzjssys/admin/yqlink.jsp", request, response);
		}
		//编辑公告
		if(ac.equals("yqlinkedit")){
			String id = request.getParameter("id");
			String linkname = request.getParameter("linkname");
			String linkurl = request.getParameter("linkurl");
			dao.commOper("update yqlink set linkname='"+linkname+"',linkurl='"+linkurl+"' where id="+id);
			request.setAttribute("suc", "操作成功!");
			gor("/schoolzjssys/admin/yqlink.jsp", request, response);
		}
	//网站信息编辑
		if(ac.equals("siteinfoedit")){
			String id = request.getParameter("id");
			HashMap map = dao.select("select * from siteinfo where id="+id).get(0);
			try {
				String tel="";
				String addr="";
				String note="";
				String logoimg = map.get("logoimg").toString();
			request.setCharacterEncoding("utf-8");
			RequestContext  requestContext = new ServletRequestContext(request);
			if(FileUpload.isMultipartContent(requestContext)){

			   DiskFileItemFactory factory = new DiskFileItemFactory();
			   factory.setRepository(new File(request.getRealPath("/upfile/")+"/"));
			   ServletFileUpload upload = new ServletFileUpload(factory);
			   upload.setSizeMax(100*1024*1024);
			   List items = new ArrayList();
			     items = upload.parseRequest(request);
			     tel = ((FileItem) items.get(0)).getString();
			     tel = Info.getUTFStr(tel);
			     addr = ((FileItem) items.get(1)).getString();
			     addr = Info.getUTFStr(addr);
			     note = ((FileItem) items.get(3)).getString();
			     note = Info.getUTFStr(note);
			     
			    FileItem fileItem = (FileItem) items.get(2);
			   if(fileItem.getName()!=null && fileItem.getSize()!=0)
			    {
			    if(fileItem.getName()!=null && fileItem.getSize()!=0){
			      File fullFile = new File(fileItem.getName());
			      logoimg = Info.generalFileName(fullFile.getName());
			      File newFile = new File(request.getRealPath("/upfile/")+"/" + logoimg);
			      try {
			       fileItem.write(newFile);
			      } catch (Exception e) {
			       e.printStackTrace();
			      }
			     }else{
			     }
			    }
			}
			String sql = "update siteinfo set tel='"+tel+"',addr='"+addr+"',note='"+note+"',logoimg='"+logoimg+"' where id="+id ;
			dao.commOper(sql);
			request.setAttribute("suc", "");
			go("/admin/siteinfo.jsp?id="+id, request, response);
			} catch (Exception e1) {
				e1.printStackTrace();
				request.setAttribute("error", "");
			     request.getRequestDispatcher("/admin/siteinfo.jsp?id="+id).forward(request, response);
			    }
	}
		//检查用户名唯一性AJAX
		if(ac.equals("sysuserscheck")){
			String username = request.getParameter("username");
			ArrayList cklist = (ArrayList)dao.select("select * from sysuser where username='"+username+"' and delstatus='0' ");
			if(cklist.size()>0){
				out.write("true");  
			}else{
				out.write("false");  
			}
		}
		//新增管理员
		if(ac.equals("sysuseradd")){
			String usertype = "管理员";
			String username = request.getParameter("username");
			String userpwd = request.getParameter("userpwd");
			String realname = request.getParameter("realname");
			String sex = request.getParameter("sex");
			String idcard = request.getParameter("idcard");
			String tel = request.getParameter("tel");
			String email = request.getParameter("email");
			String addr = request.getParameter("addr");
			String delstatus = "0";
			String savetime = Info.getDateStr();
			dao.commOper("insert into sysuser (usertype,username,userpwd,realname,sex,idcard,tel,email,addr,delstatus,savetime,shstatus)" +
						" values ('"+usertype+"','"+username+"','"+userpwd+"','"+realname+"','"+sex+"','"+idcard+"','"+tel+"','"+email+"','"+addr+"','"+delstatus+"','"+savetime+"','已通过')");
			request.setAttribute("suc", "");
			go("/admin/sysuseradd.jsp", request, response);
		}
		//编辑管理员
		if(ac.equals("sysuseredit")){
			String id = request.getParameter("id");
			String userpwd = request.getParameter("userpwd");
			String realname = request.getParameter("realname");
			String sex = request.getParameter("sex");
			String idcard = request.getParameter("idcard");
			String tel = request.getParameter("tel");
			String email = request.getParameter("email");
			String addr = request.getParameter("addr");
			dao.commOper("update sysuser set userpwd='"+userpwd+"',realname='"+realname+"',sex='"+sex+"',idcard='"+idcard+"',tel='"+tel+"',email='"+email+"',addr='"+addr+"' where id="+id);
			request.setAttribute("suc", "");
			go("/admin/sysuseredit.jsp?id="+id, request, response);
		}
		
		
		
		//商家注册
		if(ac.equals("shopreg")){
			try {
				String usertype = "商家";
				String username = request.getParameter("username");
				String userpwd = request.getParameter("userpwd");
				String realname = request.getParameter("realname");
				String sex = "";
				String idcard = request.getParameter("idcard");
				String tel = request.getParameter("tel");
				String email = request.getParameter("email");
				String addr = request.getParameter("addr");
				String delstatus = "0";
				String savetime = Info.getDateStr();
				String pp = request.getParameter("pp");
				String filename = "";
			request.setCharacterEncoding("utf-8");
			RequestContext  requestContext = new ServletRequestContext(request);
			if(FileUpload.isMultipartContent(requestContext)){
			   DiskFileItemFactory factory = new DiskFileItemFactory();
			   factory.setRepository(new File(request.getRealPath("/upfile/")+"/"));
			   ServletFileUpload upload = new ServletFileUpload(factory);
			   upload.setSizeMax(100*1024*1024);
			   List items = new ArrayList();
			     items = upload.parseRequest(request);
			     
			     username = ((FileItem) items.get(0)).getString();
			     username = Info.getUTFStr(username);
			     
			     userpwd = ((FileItem) items.get(1)).getString();
			     userpwd = Info.getUTFStr(userpwd);
			     
			     realname = ((FileItem) items.get(3)).getString();
			     realname = Info.getUTFStr(realname);
			     
			     idcard = ((FileItem) items.get(4)).getString();
			     idcard = Info.getUTFStr(idcard);
			     
			     email = ((FileItem) items.get(5)).getString();
			     email = Info.getUTFStr(email);
			     
			     tel = ((FileItem) items.get(6)).getString();
			     tel = Info.getUTFStr(tel);
			     
			     pp = ((FileItem) items.get(7)).getString();
			     pp = Info.getUTFStr(pp);

			    FileItem fileItem = (FileItem) items.get(8);
			   if(fileItem.getName()!=null && fileItem.getSize()!=0)
			    {
			    if(fileItem.getName()!=null && fileItem.getSize()!=0){
			      File fullFile = new File(fileItem.getName());
			      filename = Info.generalFileName(fullFile.getName());
			      File newFile = new File(request.getRealPath("/upfile/")+"/" + filename);
			      try {
			       fileItem.write(newFile);
			      } catch (Exception e) {
			       e.printStackTrace();
			      }
			     }else{
			     }
			    }
			}

			dao.commOper("insert into sysuser (usertype,username,userpwd,realname,sex,idcard,tel,email,addr,delstatus,savetime,shstatus,userlevel,pp,filename)" +
					" values ('"+usertype+"','"+username+"','"+userpwd+"','"+realname+"','"+sex+"','"+idcard+"','"+tel+"','"+email+"','"+addr+"','"+delstatus+"','"+savetime+"','待审核','初级','"+pp+"','"+filename+"')");
			request.setAttribute("info", "注册成功,请等待管理员审核");
			go("shopreg.jsp", request, response);
			
			} catch (Exception e1) {
				e1.printStackTrace();
				request.setAttribute("error", "");
			     request.getRequestDispatcher("shopreg.jsp").forward(request, response);
			    }
		}
		
		
		//商家注册
//		if(ac.equals("shopreg")){
//			String usertype = "商家";
//			String username = request.getParameter("username");
//			String userpwd = request.getParameter("userpwd");
//			String realname = request.getParameter("realname");
//			String sex = "";
//			String idcard = request.getParameter("idcard");
//			String tel = request.getParameter("tel");
//			String email = request.getParameter("email");
//			String addr = request.getParameter("addr");
//			String delstatus = "0";
//			String savetime = Info.getDateStr();
//			String pp = request.getParameter("pp");
//			dao.commOper("insert into sysuser (usertype,username,userpwd,realname,sex,idcard,tel,email,addr,delstatus,savetime,shstatus,userlevel,pp)" +
//						" values ('"+usertype+"','"+username+"','"+userpwd+"','"+realname+"','"+sex+"','"+idcard+"','"+tel+"','"+email+"','"+addr+"','"+delstatus+"','"+savetime+"','待审核','初级','"+pp+"')");
//			request.setAttribute("info", "注册成功,请等待管理员审核");
//			go("shopreg.jsp", request, response);
//		}
		
		//类别新增
		if(ac.equals("protypeadd")){
			String typename = request.getParameter("typename");
			String fatherid = request.getParameter("fatherid");
			dao.commOper("insert into protype (typename,fatherid,delstatus) values ('"+typename+"','"+fatherid+"','0') ");
			request.setAttribute("suc", "操作成功!");
			gor("/schoolzjssys/admin/protype.jsp", request, response);
		}
		//类别编辑
		if(ac.equals("protypeedit")){
			String id = request.getParameter("id");
			String typename = request.getParameter("typename");
			dao.commOper("update protype set typename='"+typename+"' where id="+id);
			request.setAttribute("suc", "操作成功!");
			gor("/schoolzjssys/admin/protype.jsp", request, response);
		}
		//商品属性新增
		if(ac.equals("propertyadd")){
			String propertyname = request.getParameter("propertyname");
			dao.commOper("insert into property (propertyname,delstatus) values ('"+propertyname+"','0') ");
			request.setAttribute("suc", "");
			go("/admin/propertyadd.jsp", request, response);
		}
		//商品属性编辑
		if(ac.equals("propertyedit")){
			String id = request.getParameter("id");
			String propertyname = request.getParameter("propertyname");
			dao.commOper("update property set propertyname='"+propertyname+"' where id="+id);
			request.setAttribute("suc", "");
			go("/admin/propertyedit.jsp?id="+id, request, response);
		}
		//AJAX根据父类查子类
		if(ac.equals("searchsontype")){
			String xml_start = "<selects>";
	        String xml_end = "</selects>";
	        String xml = "";
	        String fprotype = request.getParameter("fprotype");
	        ArrayList<HashMap> list = (ArrayList<HashMap>)dao.select("select * from protype where fatherid='"+fprotype+"' and delstatus='0' ");
			if(list.size()>0){
		        for(HashMap map:list){
					xml += "<select><value>"+map.get("id")+"</value><text>"+map.get("typename")+"</text><value>"+map.get("id")+"</value><text>"+map.get("typename")+"</text></select>";
				}
			}
			String last_xml = xml_start + xml + xml_end;
			response.setContentType("text/xml;charset=GB2312"); 
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(last_xml);
			response.getWriter().flush();
			
		}
		//公用方法，图片上传
		if(ac.equals("uploadimg"))
		{
			try {
				String filename="";
			request.setCharacterEncoding("utf-8");
			RequestContext  requestContext = new ServletRequestContext(request);
			if(FileUpload.isMultipartContent(requestContext)){

			   DiskFileItemFactory factory = new DiskFileItemFactory();
			   factory.setRepository(new File(request.getRealPath("/upfile/")+"/"));
			   ServletFileUpload upload = new ServletFileUpload(factory);
			   upload.setSizeMax(100*1024*1024);
			   List items = new ArrayList();
			     items = upload.parseRequest(request);
			    FileItem fileItem = (FileItem) items.get(0);
			   if(fileItem.getName()!=null && fileItem.getSize()!=0)
			    {
			    if(fileItem.getName()!=null && fileItem.getSize()!=0){
			      File fullFile = new File(fileItem.getName());
			      filename = Info.generalFileName(fullFile.getName());
			      File newFile = new File(request.getRealPath("/upfile/")+"/" + filename);
			      try {
			       fileItem.write(newFile);
			      } catch (Exception e) {
			       e.printStackTrace();
			      }
			     }else{
			     }
			    }
			}
			
			go("/js/uploadimg.jsp?filename="+filename, request, response);
			} catch (Exception e1) {
				e1.printStackTrace();
			    }
		}
		
		
		//库存预警数值设置
		if(ac.equals("kcwarningset")){
			String num = request.getParameter("num");
			String id = request.getParameter("id");
			dao.commOper("update kcwarnning set num="+Integer.parseInt(num)+" where id="+id);
			request.setAttribute("suc", "");
			go("/admin/kcwarningset.jsp", request, response);
		}
		//商品入库
		if(ac.equals("kcinto")){
			String pid = request.getParameter("pid");
			String num = request.getParameter("num");
			String type = request.getParameter("type");
			String reason = request.getParameter("reason");
			String savetime = Info.getDateStr();
			dao.commOper("insert into kcrecord (pid,num,type,reason,savetime) values" +
					" ('"+pid+"','"+Integer.parseInt(num)+"','"+type+"','"+reason+"','"+savetime+"') ");
			request.setAttribute("suc", "");
			go("/admin/kcinto.jsp", request, response);
		}
		//商品出库
		if(ac.equals("kcout")){
			String pid = request.getParameter("pid");
			String num = request.getParameter("num");
			String type = request.getParameter("type");
			String reason = request.getParameter("reason");
			String savetime = Info.getDateStr();
			
			int znum = 0;
	    	int innum = 0;
	    	int outnum = 0;
	    	ArrayList<HashMap> inlist = (ArrayList<HashMap>)dao.select("select * from kcrecord where  type='in' and pid='"+pid+"' ");
	    	ArrayList<HashMap> outlist = (ArrayList<HashMap>)dao.select("select * from kcrecord where  type='out' and pid='"+pid+"' ");
	    	if(inlist.size()>0){
	    		for(HashMap inmap:inlist){
	    			innum += Integer.parseInt(inmap.get("num").toString());//总入库量
	    		}
	    	}
	    	if(outlist.size()>0){
	    		for(HashMap outmap:outlist){
	    			outnum += Integer.parseInt(outmap.get("num").toString());//总出库量
	    		}
	    	}
	    	znum = innum - outnum;//库存量
	    	if(Integer.parseInt(num)>znum){
	    		request.setAttribute("no", "");
				go("/admin/kcout.jsp", request, response);
	    	}else{
				dao.commOper("insert into kcrecord (pid,num,type,reason,savetime) values" +
						" ('"+pid+"','"+Integer.parseInt(num)+"','"+type+"','"+reason+"','"+savetime+"') ");
				request.setAttribute("suc", "");
				go("/admin/kcout.jsp", request, response);
	    	}
		}
		
		//新增图片
		if(ac.equals("imgadvaddold")){
			try {
				String img = "";
				String imgtype="";
			request.setCharacterEncoding("utf-8");
			RequestContext  requestContext = new ServletRequestContext(request);
			if(FileUpload.isMultipartContent(requestContext)){
			   DiskFileItemFactory factory = new DiskFileItemFactory();
			   factory.setRepository(new File(request.getRealPath("/upfile/")+"/"));
			   ServletFileUpload upload = new ServletFileUpload(factory);
			   upload.setSizeMax(100*1024*1024);
			   List items = new ArrayList();
			     items = upload.parseRequest(request);
			     
			     imgtype = ((FileItem) items.get(1)).getString();
			     imgtype = Info.getUTFStr(imgtype);

			    FileItem fileItem = (FileItem) items.get(0);
			   if(fileItem.getName()!=null && fileItem.getSize()!=0)
			    {
			    if(fileItem.getName()!=null && fileItem.getSize()!=0){
			      File fullFile = new File(fileItem.getName());
			      img = Info.generalFileName(fullFile.getName());
			      File newFile = new File(request.getRealPath("/upfile/")+"/" + img);
			      try {
			       fileItem.write(newFile);
			      } catch (Exception e) {
			       e.printStackTrace();
			      }
			     }else{
			     }
			    }
			}
			
			String cksql = "select * from imgadv where imgtype='banner'";
			ArrayList cklist = (ArrayList)dao.select(cksql);
			if(imgtype.equals("banner")&&cklist.size()!=0){
				request.setAttribute("no", "");
				go("/admin/imgadvadd.jsp", request, response);
			}else{
				String sql = "insert into imgadv (filename,imgtype) " +
				"values('"+img+"','"+imgtype+"')" ;
				dao.commOper(sql);
				request.setAttribute("suc", "");
				go("/admin/imgadvadd.jsp", request, response);
			}
			} catch (Exception e1) {
				e1.printStackTrace();
				request.setAttribute("no", "");
			     request.getRequestDispatcher("/admin/imgadvadd.jsp").forward(request, response);
			    }
		}
		//编辑图片
		if(ac.equals("imgadvedit")){
			String id = request.getParameter("id");
			HashMap map = dao.select("select * from imgadv where id="+id).get(0);
			try {
				String img = map.get("filename").toString();
			request.setCharacterEncoding("utf-8");
			RequestContext  requestContext = new ServletRequestContext(request);
			if(FileUpload.isMultipartContent(requestContext)){

			   DiskFileItemFactory factory = new DiskFileItemFactory();
			   factory.setRepository(new File(request.getRealPath("/upfile/")+"/"));
			   ServletFileUpload upload = new ServletFileUpload(factory);
			   upload.setSizeMax(100*1024*1024);
			   List items = new ArrayList();
			     items = upload.parseRequest(request);
			     
			    FileItem fileItem = (FileItem) items.get(0);
			   if(fileItem.getName()!=null && fileItem.getSize()!=0)
			    {
			    if(fileItem.getName()!=null && fileItem.getSize()!=0){
			      File fullFile = new File(fileItem.getName());
			      img = Info.generalFileName(fullFile.getName());
			      File newFile = new File(request.getRealPath("/upfile/")+"/" + img);
			      try {
			       fileItem.write(newFile);
			      } catch (Exception e) {
			       e.printStackTrace();
			      }
			     }else{
			     }
			    }
			}
					String sql = "update imgadv set filename='"+img+"' where id="+id ;
					dao.commOper(sql);
					request.setAttribute("suc", "");
					go("/admin/imgadvedit.jsp?id="+id, request, response);
			} catch (Exception e1) {
				e1.printStackTrace();
				request.setAttribute("error", "");
			     request.getRequestDispatcher("/admin/imgadvedit.jsp?id="+id).forward(request, response);
			    }
	}
		
		//检查用户名唯一性AJAX 会员注册
		if(ac.equals("memberunamecheck")){
			String uname = request.getParameter("username");
			ArrayList cklist = (ArrayList)dao.select("select * from member where uname='"+uname+"' and delstatus='0' ");
			if(cklist.size()>0){
				out.print("false");
				
			}else{
				out.print("true");
			}
		}
		
		//检查商品的库存
		if(ac.equals("checkgoodkc")){
			String gid = request.getParameter("gid");
			String sl = request.getParameter("sl");
			if(Integer.valueOf(sl)>Info.getkc(gid)){
				out.write("1");  
			}else{
				out.write("0");  
			}
		}
		
		
		//直接购买
		if(ac.equals("tobuy")){
			String gid = request.getParameter("gid");
			int sl = Integer.valueOf(request.getParameter("sl"));
			String ddno = Info.getAutoNo();
			String shrname = "";
			String shrtel = "";
			String shraddr = request.getParameter("addr");
			double ddprice = 0.0;
			String fhstatus = "待发货";
			String shstatus = "待收货";
			String fkstatus = "待付款";
			String wlinfo = "暂无骑手信息";
			String savetime = Info.getDateStr();
			
			if(member!=null){
				String memberid = member.get("id").toString();
				ArrayList<HashMap> addrlist = (ArrayList<HashMap>)dao.select("select * from addr where delstatus='0' and isdefault='yes' and memberid="+member.get("id"));
				if(addrlist.size()==0){
					out.print("false");
				}else{
					shraddr = addrlist.get(0).get("id").toString();
					HashMap gmap = dao.select("select * from goods where id="+gid).get(0);
					String price = gmap.get("price").toString();
					if(gmap.get("tprice")!=null&&!gmap.get("tprice").equals("")){
						price = gmap.get("tprice").toString();
					}
					ddprice = Double.valueOf(price)*sl;
					//直接生成订单
					dao.commOper("insert into ddinfo (ddno,memberid,ddprice,fhstatus,savetime,shstatus,wlinfo,fkstatus,shrname,shrtel,shraddr) values " +
							"('"+ddno+"','"+memberid+"','"+ddprice+"','"+fhstatus+"','"+savetime+"','"+shstatus+"','"+wlinfo+"','"+fkstatus+"','"+shrname+"','"+shrtel+"','"+shraddr+"')");
					dao.commOper("insert into dddetail (ddno,goodid,sl,price) values ('"+ddno+"','"+gid+"','"+sl+"','"+ddprice+"') ");
					
					out.print("true");
				}
			}else{
				out.print("false");
			}
//			request.setAttribute("suc", "");
//			go("/tocar.jsp?gid="+gid, request, response);
		}
		
		//购物车内商品数量修改
		if(ac.equals("updatecart")){
			String id = request.getParameter("carid");
			int sl = Integer.valueOf(request.getParameter("sl"));
			dao.commOper("update car set sl="+sl+" where id="+id);
			out.print("true");
		}
		
		//购物车内商品总价
		if(ac.equals("updatetprice")){
			ArrayList<HashMap> goodlist = (ArrayList<HashMap>)dao.select("select *,a.id as aid,b.id as bid from car a,goods b where a.gid=b.id and a.mid='"+member.get("id")+"' and b.delstatus='0' order by a.id desc");
			double totalprice = 0.0;
			for(HashMap carmap:goodlist){  
				if(carmap.get("tprice")!=null&&!carmap.get("tprice").equals("")){ 
					totalprice += Double.valueOf(carmap.get("tprice").toString())*Integer.valueOf(carmap.get("sl").toString());
				}else{
					totalprice += Double.valueOf(carmap.get("price").toString())*Integer.valueOf(carmap.get("sl").toString());
				}
			}
			out.print(totalprice);
		}
		/*
		//提交生成订单 填写收货信息
		if(ac.equals("submitorder")){
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
						"('"+ddno+"','"+carmap.get("gid")+"','"+carmap.get("sl")+"','"+String.valueOf(detailprice)+"','待付款','待发货','待收货','','"+savetime+"') ");
			}
			dao.commOper("insert into ddinfo (ddno,memberid,ddprice,fhstatus,savetime,shstatus,wlinfo,fkstatus,shrname,shrtel,shraddr) values " +
			"('"+ddno+"','"+memberid+"','"+ddprice+"','"+fhstatus+"','"+savetime+"','"+shstatus+"','"+wlinfo+"','"+fkstatus+"','"+shrname+"','"+shrtel+"','"+shraddr+"')");
			//删除购物车下的商品
			dao.commOper("delete from car where mid="+memberid);
			request.setAttribute("suc", "订单生成成功!");
			go("/mydd.jsp", request, response);
		}
		*/
		//
		if(ac.equals("pay")){
			String ddid = request.getParameter("ddid");
			String fkstatus = "已付款";
			String fhstatus = "待发货";
			String shstatus = "待收货";
			dao.commOper("update ddinfo set fkstatus='"+fkstatus+"',fhstatus='"+fhstatus+"',shstatus='"+shstatus+"' where id='"+ddid+"'");
			request.setAttribute("suc", "支付成功!");
			go("/mydd.jsp?ddid="+ddid, request, response);
		}
		
		if(ac.equals("pay2")){
			String ddid = request.getParameter("ddid");
			HashMap ddmap = dao.select("select * from ddinfo where id="+ddid).get(0);
			String fkstatus = "已付款";
			String fhstatus = "待发货";
			String shstatus = "待收货";
			dao.commOper("update dddetail set fkstatus='"+fkstatus+"',fhstatus='"+fhstatus+"',shstatus='"+shstatus+"' where ddno='"+ddmap.get("ddno")+"'");
			dao.commOper("update ddinfo set fkstatus='"+fkstatus+"',fhstatus='"+fhstatus+"',shstatus='"+shstatus+"' where ddno='"+ddmap.get("ddno")+"'");
			request.setAttribute("suc", "支付成功!");
			go("/mydd.jsp?ddid="+ddid, request, response);
		}
		
		//订单发货
		if(ac.equals("ddfh")){
			String ddid = request.getParameter("ddid");
			String wlcompany = request.getParameter("wlcompany");
			String wlno = request.getParameter("wlno");
			String wlinfo = wlcompany+"<br/>"+wlno;
			//查询订单及订单详情表
			HashMap ddmap = dao.select("select * from ddinfo where id="+ddid).get(0);
			ArrayList<HashMap> dddetaillist = (ArrayList<HashMap>)dao.select("select * from dddetail where ddno="+ddmap.get("ddno"));
			boolean flag = true;//用作订单商品库存校验结果
			for(HashMap dddetailmap:dddetaillist){
				//如果其中某个商品的数量大于其库存量 则置 FLASE标识
				if(Integer.valueOf(dddetailmap.get("sl").toString())>Info.getkc(dddetailmap.get("goodid").toString())){
					flag = false;
				}
			}
			if(flag){
				dao.commOper("update ddinfo set fhstatus='已发货',wlinfo='"+wlinfo+"' where id="+ddid);
				//发货后减库存 
				for(HashMap dddetailmap:dddetaillist){
					dao.commOper("insert into kcrecord (gid,happennum,type,savetime) values " +
							"('"+dddetailmap.get("goodid")+"','"+dddetailmap.get("sl")+"','out','"+Info.getDateStr()+"')");
				}
				
				request.setAttribute("info", "订单已发货!");
				//gor("/schoolzjssys/admin/ddgl.jsp", request, response);
				out.print("true");
			}else{
				request.setAttribute("info", "订单中商品库存不足，发货失败!");
				//gor("/schoolzjssys/admin/ddgl.jsp", request, response);
				out.print("false");
			}
		}
		
		
		//订单发货 商家
		if(ac.equals("ddfhforshop")){
			
			String dddetailid = request.getParameter("dddetailid");
			String wlcompany = request.getParameter("wlcompany");
			String wlno = request.getParameter("wlno");
			String wlinfo = "";
			//查询订单及订单详情表
			ArrayList<HashMap> dddetaillist = (ArrayList<HashMap>)dao.select("select * from dddetail where id="+dddetailid);
			boolean flag = true;//用作订单商品库存校验结果
			for(HashMap dddetailmap:dddetaillist){
				//如果其中某个商品的数量大于其库存量 则置 FLASE标识
				if(Integer.valueOf(dddetailmap.get("sl").toString())>Info.getkc(dddetailmap.get("goodid").toString())){
					flag = false;
				}
			}
			if(flag){
				dao.commOper("update dddetail set fhstatus='已发货',wlinfo='"+wlinfo+"' where id="+dddetailid);
				//发货后减库存 
				for(HashMap dddetailmap:dddetaillist){
					dao.commOper("insert into kcrecord (gid,happennum,type,savetime) values " +
							"('"+dddetailmap.get("goodid")+"','"+dddetailmap.get("sl")+"','out','"+Info.getDateStr()+"')");
				}
				//dao.commOper("insert qsdd (dddetailid,qsid,status) values ('"+dddetailid+"','"+wlcompany+"','待处理')");
				dao.commOper("update qsdd set qcstatus='已取餐' where dddetailid="+dddetailid);
				request.setAttribute("info", "订单已发货!");
				//gor("/schoolzjssys/admin/ddgl.jsp", request, response);
				out.print("true");
			}else{
				request.setAttribute("info", "订单中商品库存不足，发货失败!");
				//gor("/schoolzjssys/admin/ddgl.jsp", request, response);
				out.print("false");
			}
		}
		
		//会员注册
		if(ac.equals("register")){
				String uname = request.getParameter("username");
				String upass = request.getParameter("upass");
				String email = request.getParameter("email")==null?"":request.getParameter("email");
				String tname = request.getParameter("tname")==null?"":request.getParameter("tname");
				String sex = request.getParameter("sex")==null?"":request.getParameter("sex");
				String addr = request.getParameter("addr")==null?"":request.getParameter("addr");
				String ybcode = request.getParameter("ybcode")==null?"":request.getParameter("ybcode");
				String qq = request.getParameter("qq")==null?"":request.getParameter("qq");
				String tel = request.getParameter("tel")==null?"":request.getParameter("tel");
				String zy = request.getParameter("zy")==null?"":request.getParameter("zy");
				String delstatus = "0";
				String savetime = Info.getDateStr();
				dao.commOper("insert into member (uname,upass,email,tname,sex,addr,ybcode,qq,tel,delstatus,savetime,zy)" +
							" values ('"+uname+"','"+upass+"','"+email+"','"+tname+"','"+sex+"','"+addr+"','"+ybcode+"','"+qq+"','"+tel+"','"+delstatus+"','"+savetime+"','"+zy+"')");
				request.setAttribute("info", "注册成功");
				go("/reg.jsp", request, response);
		}
		
		//会员修改个人信息
		if(ac.equals("memberinfo")){
				String id = request.getParameter("id");
				String upass = request.getParameter("upass");
				String email = request.getParameter("email")==null?"":request.getParameter("email");
				String tname = request.getParameter("tname")==null?"":request.getParameter("tname");
				String sex = request.getParameter("sex")==null?"":request.getParameter("sex");
				String addr = request.getParameter("addr")==null?"":request.getParameter("addr");
				String ybcode = request.getParameter("ybcode")==null?"":request.getParameter("ybcode");
				String qq = request.getParameter("qq")==null?"":request.getParameter("qq");
				String tel = request.getParameter("tel")==null?"":request.getParameter("tel");
				String zy = request.getParameter("zy")==null?"":request.getParameter("zy");
				dao.commOper("update member set upass='"+upass+"',email='"+email+"',tname='"+tname+"',sex='"+sex+"'," +
							"addr='"+addr+"',ybcode='"+ybcode+"',qq='"+qq+"',tel='"+tel+"',zy='"+zy+"' where id="+id);
				request.setAttribute("info", "会员信息修改成功!");
				go("/grinfo.jsp", request, response);
		}
		
		
		
	
		
		//发布商品
		if(ac.equals("goodsadd")){
			try {
				String goodno = Info.getAutoNo();
				String goodname = "";
				String fid = "";
				String sid="";
				String goodpp="";
				String price="";
				String filename="";
				String remark="";
				String note="";
				String shstatus = "通过";
				String istj = "no";
				String savetime = Info.getDateStr();
				String tprice="";
			request.setCharacterEncoding("utf-8");
			RequestContext  requestContext = new ServletRequestContext(request);
			if(FileUpload.isMultipartContent(requestContext)){
			   DiskFileItemFactory factory = new DiskFileItemFactory();
			   factory.setRepository(new File(request.getRealPath("/upfile/")+"/"));
			   ServletFileUpload upload = new ServletFileUpload(factory);
			   upload.setSizeMax(100*1024*1024);
			   List items = new ArrayList();
			     items = upload.parseRequest(request);
			     goodname = ((FileItem) items.get(0)).getString();
			     goodname = Info.getUTFStr(goodname);
			     fid = ((FileItem) items.get(1)).getString();
			     fid = Info.getUTFStr(fid);
			     sid = ((FileItem) items.get(2)).getString();
			     sid = Info.getUTFStr(sid);
			     goodpp = ((FileItem) items.get(3)).getString();
			     goodpp = Info.getUTFStr(goodpp);
			     price = ((FileItem) items.get(4)).getString();
			     price = Info.getUTFStr(price);
			     remark = ((FileItem) items.get(6)).getString();
			     remark = Info.getUTFStr(remark);
			     note = ((FileItem) items.get(7)).getString();
			     note = Info.getUTFStr(note);
					FileItem fileItem = (FileItem) items.get(5);
					if (fileItem.getName() != null && fileItem.getSize() != 0) {
						if (fileItem.getName() != null
								&& fileItem.getSize() != 0) {
							File fullFile = new File(fileItem.getName());
							filename = Info.generalFileName(fullFile.getName());
							File newFile = new File(request
									.getRealPath("/upfile/")
									+ "/" + filename);
							try {
								fileItem.write(newFile);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
						}
					}
				}
			dao.commOper("insert into goods (goodno,goodname,fid,sid,price,remark,note,savetime,shstatus,filename,istj,tprice,delstatus,salestatus,goodpp) " +
					"values ('"+goodno+"','"+goodname+"','"+fid+"','"+sid+"','"+price+"','"+remark+"','"+note+"','"+savetime+"','通过','"+filename+"','"+istj+"','"+tprice+"','0','在售','"+goodpp+"') ");
			request.setAttribute("suc", "操作成功!");
			gor("/schoolzjssys/admin/goodsgl.jsp", request, response);
			
			} catch (Exception e1) {
				e1.printStackTrace();
				request.setAttribute("error", "");
			     request.getRequestDispatcher("/admin/goodsgl.jsp").forward(request, response);
			    }
			
			
		}
		//修改商品
		if(ac.equals("goodsedit")){
			String id = request.getParameter("id");
			HashMap map = dao.select("select * from goods where id="+id).get(0);
			try {
				String goodname = "";
				String fid = "";
				String sid="";
				String goodpp="";
				String price="";
				String remark="";
				String note="";
				String filename=map.get("filename").toString();
			request.setCharacterEncoding("utf-8");
			RequestContext  requestContext = new ServletRequestContext(request);
			if(FileUpload.isMultipartContent(requestContext)){

			   DiskFileItemFactory factory = new DiskFileItemFactory();
			   factory.setRepository(new File(request.getRealPath("/upfile/")+"/"));
			   ServletFileUpload upload = new ServletFileUpload(factory);
			   upload.setSizeMax(100*1024*1024);
			   List items = new ArrayList();
			     items = upload.parseRequest(request);
			     goodname = ((FileItem) items.get(0)).getString();
			     goodname = Info.getUTFStr(goodname);
			     fid = ((FileItem) items.get(1)).getString();
			     fid = Info.getUTFStr(fid);
			     sid = ((FileItem) items.get(2)).getString();
			     sid = Info.getUTFStr(sid);
			     goodpp = ((FileItem) items.get(3)).getString();
			     goodpp = Info.getUTFStr(goodpp);
			     price = ((FileItem) items.get(4)).getString();
			     price = Info.getUTFStr(price);
			     remark = ((FileItem) items.get(6)).getString();
			     remark = Info.getUTFStr(remark);
			     note = ((FileItem) items.get(7)).getString();
			     note = Info.getUTFStr(note);
			    FileItem fileItem = (FileItem) items.get(5);
			   if(fileItem.getName()!=null && fileItem.getSize()!=0)
			    {
			    if(fileItem.getName()!=null && fileItem.getSize()!=0){
			      File fullFile = new File(fileItem.getName());
			      filename = Info.generalFileName(fullFile.getName());
			      File newFile = new File(request.getRealPath("/upfile/")+"/" + filename);
			      try {
			       fileItem.write(newFile);
			      } catch (Exception e) {
			       e.printStackTrace();
			      }
			     }else{
			     }
			    }
			}
			dao.commOper("update goods set goodname='"+goodname+"',fid='"+fid+"',sid='"+sid+"',price='"+price+"',remark='"+remark+"',note='"+note+"',filename='"+filename+"',goodpp='"+goodpp+"' where id="+id);
			request.setAttribute("suc", "操作成功!");
			gor("/schoolzjssys/admin/goodsgl.jsp?id="+id, request, response);
			} catch (Exception e1) {
				e1.printStackTrace();
				request.setAttribute("error", "");
			     request.getRequestDispatcher("/admin/goodsgl.jsp").forward(request, response);
			    }

		}
		
		
		
		//发布商品 商家
		if(ac.equals("goodsaddforshop")){
			try {
				String goodno = Info.getAutoNo();
				String goodname = "";
				String fid = "";
				String sid="";
				String goodpp="";
				String price="";
				String filename="";
				String remark="";
				String note="";
				String shstatus = "通过";
				String istj = "no";
				String savetime = Info.getDateStr();
				String tprice="";
			request.setCharacterEncoding("utf-8");
			RequestContext  requestContext = new ServletRequestContext(request);
			if(FileUpload.isMultipartContent(requestContext)){
			   DiskFileItemFactory factory = new DiskFileItemFactory();
			   factory.setRepository(new File(request.getRealPath("/upfile/")+"/"));
			   ServletFileUpload upload = new ServletFileUpload(factory);
			   upload.setSizeMax(100*1024*1024);
			   List items = new ArrayList();
			     items = upload.parseRequest(request);
			     goodname = ((FileItem) items.get(0)).getString();
			     goodname = Info.getUTFStr(goodname);
			     fid = ((FileItem) items.get(1)).getString();
			     fid = Info.getUTFStr(fid);
			     sid = ((FileItem) items.get(2)).getString();
			     sid = Info.getUTFStr(sid);
			     goodpp = ((FileItem) items.get(3)).getString();
			     goodpp = Info.getUTFStr(goodpp);
			     price = ((FileItem) items.get(4)).getString();
			     price = Info.getUTFStr(price);
			     remark = ((FileItem) items.get(6)).getString();
			     remark = Info.getUTFStr(remark);
			     note = ((FileItem) items.get(7)).getString();
			     note = Info.getUTFStr(note);
					FileItem fileItem = (FileItem) items.get(5);
					if (fileItem.getName() != null && fileItem.getSize() != 0) {
						if (fileItem.getName() != null
								&& fileItem.getSize() != 0) {
							File fullFile = new File(fileItem.getName());
							filename = Info.generalFileName(fullFile.getName());
							File newFile = new File(request
									.getRealPath("/upfile/")
									+ "/" + filename);
							try {
								fileItem.write(newFile);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
						}
					}
				}
			dao.commOper("insert into goods (goodno,goodname,fid,sid,price,remark,note,savetime,shstatus,filename,istj,tprice,delstatus,salestatus,goodpp,saver) " +
					"values ('"+goodno+"','"+goodname+"','"+fid+"','"+sid+"','"+price+"','"+remark+"','"+note+"','"+savetime+"','通过','"+filename+"','"+istj+"','"+tprice+"','0','在售','"+goodpp+"','"+admin.get("id")+"') ");
			request.setAttribute("suc", "操作成功!");
			gor("/schoolzjssys/admin/goodsglforshop.jsp", request, response);
			
			} catch (Exception e1) {
				e1.printStackTrace();
				request.setAttribute("error", "");
			     request.getRequestDispatcher("/admin/goodsglforshop.jsp").forward(request, response);
			    }
			
			
		}
		//修改商品
		if(ac.equals("goodseditforshop")){
			String id = request.getParameter("id");
			HashMap map = dao.select("select * from goods where id="+id).get(0);
			try {
				String goodname = "";
				String fid = "";
				String sid="";
				String goodpp="";
				String price="";
				String remark="";
				String note="";
				String filename=map.get("filename").toString();
			request.setCharacterEncoding("utf-8");
			RequestContext  requestContext = new ServletRequestContext(request);
			if(FileUpload.isMultipartContent(requestContext)){

			   DiskFileItemFactory factory = new DiskFileItemFactory();
			   factory.setRepository(new File(request.getRealPath("/upfile/")+"/"));
			   ServletFileUpload upload = new ServletFileUpload(factory);
			   upload.setSizeMax(100*1024*1024);
			   List items = new ArrayList();
			     items = upload.parseRequest(request);
			     goodname = ((FileItem) items.get(0)).getString();
			     goodname = Info.getUTFStr(goodname);
			     fid = ((FileItem) items.get(1)).getString();
			     fid = Info.getUTFStr(fid);
			     sid = ((FileItem) items.get(2)).getString();
			     sid = Info.getUTFStr(sid);
			     goodpp = ((FileItem) items.get(3)).getString();
			     goodpp = Info.getUTFStr(goodpp);
			     price = ((FileItem) items.get(4)).getString();
			     price = Info.getUTFStr(price);
			     remark = ((FileItem) items.get(6)).getString();
			     remark = Info.getUTFStr(remark);
			     note = ((FileItem) items.get(7)).getString();
			     note = Info.getUTFStr(note);
			    FileItem fileItem = (FileItem) items.get(5);
			   if(fileItem.getName()!=null && fileItem.getSize()!=0)
			    {
			    if(fileItem.getName()!=null && fileItem.getSize()!=0){
			      File fullFile = new File(fileItem.getName());
			      filename = Info.generalFileName(fullFile.getName());
			      File newFile = new File(request.getRealPath("/upfile/")+"/" + filename);
			      try {
			       fileItem.write(newFile);
			      } catch (Exception e) {
			       e.printStackTrace();
			      }
			     }else{
			     }
			    }
			}
			dao.commOper("update goods set goodname='"+goodname+"',fid='"+fid+"',sid='"+sid+"',price='"+price+"',remark='"+remark+"',note='"+note+"',filename='"+filename+"',goodpp='"+goodpp+"' where id="+id);
			request.setAttribute("suc", "操作成功!");
			gor("/schoolzjssys/admin/goodsglforshop.jsp?id="+id, request, response);
			} catch (Exception e1) {
				e1.printStackTrace();
				request.setAttribute("error", "");
			     request.getRequestDispatcher("/admin/goodsglforshop.jsp").forward(request, response);
			    }

		}
		
		//商品入库
		if(ac.equals("goodskcadd")){
			String gid = request.getParameter("gid");
			String happennum = request.getParameter("happennum");
			String type = "in";
			String savetime = Info.getDateStr();
			dao.commOper("insert into kcrecord (gid,happennum,type,savetime) values " +
					"('"+gid+"','"+happennum+"','"+type+"','"+savetime+"') ");
			request.setAttribute("suc", "操作成功!");
			gor("/schoolzjssys/admin/goodskc.jsp", request, response);
		}
		
		//商品入库 商家
		if(ac.equals("goodskcaddforshop")){
			String gid = request.getParameter("gid");
			String happennum = request.getParameter("happennum");
			String type = "in";
			String savetime = Info.getDateStr();
			dao.commOper("insert into kcrecord (gid,happennum,type,savetime) values " +
					"('"+gid+"','"+happennum+"','"+type+"','"+savetime+"') ");
			request.setAttribute("suc", "操作成功!");
			gor("/schoolzjssys/admin/goodskcforshop.jsp", request, response);
		}
		
		//评价
		if(ac.equals("goodpj")){
			String goodid = request.getParameter("id");
			String memberid = member.get("id").toString();
			String note = request.getParameter("note");
			String savetime = Info.getDateStr();
			dao.commOper("insert into goodspj (goodid,memberid,note,savetime) values ('"+goodid+"','"+memberid+"','"+note+"','"+savetime+"')");
			request.setAttribute("suc", "");
			go("/goodsx.jsp?id="+goodid, request, response);
		}
		
		
		
		
		
		
		//商品购买成功后的评价
		if(ac.equals("pj")){
			String goodid = request.getParameter("goodid");
			String ddid = request.getParameter("ddid");
			String memberid = member.get("id").toString();
			String jb = request.getParameter("jb");
			String msg = request.getParameter("msg");
			String savetime = Info.getDateStr();
			HashMap mm = dao.select("select * from goods where id="+goodid).get(0);
			dao.commOper("insert into pj (goodid,goodsaver,memberid,jb,msg,savetime,hf,ddid) values " +
					"('"+goodid+"','"+mm.get("saver")+"','"+memberid+"','"+jb+"','"+msg+"','"+savetime+"','','"+ddid+"') ");
			request.setAttribute("suc", "评价成功!");
			go("/mydd.jsp", request, response);
		}
		//管理员回复评价
		if(ac.equals("pjhf")){
			String id = request.getParameter("id");
			String gid = request.getParameter("gid");
			String hf = request.getParameter("hf");
			dao.commOper("update pj set hf='"+hf+"' where id="+id);
			request.setAttribute("suc", "评价回复成功!");
			gor("/schoolzjssys/admin/goodpj.jsp?gid="+gid, request, response);
		}
		
		//商家回复评价
		if(ac.equals("pjhfforshop")){
			String id = request.getParameter("id");
			String gid = request.getParameter("gid");
			String hf = request.getParameter("hf");
			dao.commOper("update pj set hf='"+hf+"' where id="+id);
			request.setAttribute("suc", "评价回复成功!");
			gor("/schoolzjssys/admin/goodpjforshop.jsp?gid="+gid, request, response);
		}
		
		
		//滚动图片
		if(ac.equals("imgadvadd")){
			try {
				String filename="";
			request.setCharacterEncoding("utf-8");
			RequestContext  requestContext = new ServletRequestContext(request);
			if(FileUpload.isMultipartContent(requestContext)){
			   DiskFileItemFactory factory = new DiskFileItemFactory();
			   factory.setRepository(new File(request.getRealPath("/upfile/")+"/"));
			   ServletFileUpload upload = new ServletFileUpload(factory);
			   upload.setSizeMax(100*1024*1024);
			   List items = new ArrayList();
			     items = upload.parseRequest(request);
					FileItem fileItem = (FileItem) items.get(0);
					if (fileItem.getName() != null && fileItem.getSize() != 0) {
						if (fileItem.getName() != null
								&& fileItem.getSize() != 0) {
							File fullFile = new File(fileItem.getName());
							filename = Info.generalFileName(fullFile.getName());
							File newFile = new File(request
									.getRealPath("/upfile/")
									+ "/" + filename);
							try {
								fileItem.write(newFile);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
						}
					}
				}
			dao.commOper("insert into imgadv (filename) " +
					"values ('"+filename+"') ");
			request.setAttribute("suc", "操作成功!");
			gor("/schoolzjssys/admin/imgadv.jsp", request, response);
			
			} catch (Exception e1) {
				e1.printStackTrace();
				request.setAttribute("error", "");
			     request.getRequestDispatcher("/schoolzjssys/admin/imgadv.jsp").forward(request, response);
			    }
		}
		
		//商家分类新增
		if(ac.equals("ppinfoadd")){
			String ppname = request.getParameter("ppname");
			String delstatus = "0";
			dao.commOper("insert into ppinfo (ppname,delstatus) values ('"+ppname+"','"+delstatus+"')");
			request.setAttribute("suc", "操作成功!");
			gor("/schoolzjssys/admin/ppinfo.jsp", request, response);
		}
		//商家分类编辑
		if(ac.equals("ppinfoedit")){
			String id = request.getParameter("id");
			String ppname = request.getParameter("ppname");
			dao.commOper("update ppinfo set  ppname='"+ppname+"' where id="+id);
			request.setAttribute("suc", "操作成功!");
			gor("/schoolzjssys/admin/ppinfo.jsp", request, response);
		}
		
		//新增收货地址
		if(ac.equals("addradd")){
			String memberid = member.get("id").toString();
			String shr = request.getParameter("shr");
			String shrtel = request.getParameter("shrtel");
			String shraddr = request.getParameter("shraddr");
			String isdefault = "no";
			String delstatus = "0";
			dao.commOper("insert into addr (memberid,shr,shrtel,shraddr,delstatus,isdefault) values " +
					"('"+memberid+"','"+shr+"','"+shrtel+"','"+shraddr+"','"+delstatus+"','"+isdefault+"') ");
			request.setAttribute("suc", "操作成功!");
			go("/addr.jsp", request, response);
		}
		
		if(ac.equals("addrdel")){
			String id = request.getParameter("id");
			dao.commOper("update addr set delstatus=1 where id="+id);
			out.print("true");

		}
		//设置为默认收货地址
		if(ac.equals("setdefault")){
			String id = request.getParameter("id");
			String memberid = request.getParameter("memberid");
			dao.commOper("update addr set isdefault='no' where memberid="+memberid);
			dao.commOper("update addr set isdefault='yes' where id="+id);
			request.setAttribute("suc", "操作成功!");
			go("/addr.jsp", request, response);
			
		}
		
		//检查用户名唯一性AJAX 系统用户
		if(ac.equals("usernamecheck")){
			String username = request.getParameter("username");
			ArrayList cklist = (ArrayList)dao.select("select * from sysuser where username='"+username+"' and delstatus='0' ");
			if(cklist.size()>0){
				out.print("false");
			}else{
				out.print("true");
			}
		}
		
		
		if(ac.equals("useradd")){
			String username = request.getParameter("username");
			String userpwd = request.getParameter("userpwd");
			String email = request.getParameter("email")==null?"":request.getParameter("email");
			String realname = request.getParameter("realname")==null?"":request.getParameter("realname");
			String sex = request.getParameter("sex")==null?"":request.getParameter("sex");
			String addr = request.getParameter("addr")==null?"":request.getParameter("addr");
			String idcard = request.getParameter("idcard")==null?"":request.getParameter("idcard");
			String tel = request.getParameter("tel")==null?"":request.getParameter("tel");
			String delstatus = "0";
			String savetime = Info.getDateStr();
			dao.commOper("insert into sysuser (usertype,username,userpwd,email,realname,sex,addr,idcard,tel,delstatus,savetime,shstatus)" +
						" values ('骑手','"+username+"','"+userpwd+"','"+email+"','"+realname+"','"+sex+"','"+addr+"','"+idcard+"','"+tel+"','"+delstatus+"','"+savetime+"','已通过')");
			request.setAttribute("suc", "操作成功!");
			gor("/schoolzjssys/admin/userlist.jsp", request, response);
	}
		
		if(ac.equals("useredit")){
			String id = request.getParameter("id");
			String userpwd = request.getParameter("userpwd");
			String email = request.getParameter("email")==null?"":request.getParameter("email");
			String realname = request.getParameter("realname")==null?"":request.getParameter("realname");
			String sex = request.getParameter("sex")==null?"":request.getParameter("sex");
			String addr = request.getParameter("addr")==null?"":request.getParameter("addr");
			String idcard = request.getParameter("idcard")==null?"":request.getParameter("idcard");
			String tel = request.getParameter("tel")==null?"":request.getParameter("tel");
			String delstatus = "0";
			String savetime = Info.getDateStr();
			dao.commOper("update sysuser set userpwd='"+userpwd+"',email='"+email+"',realname='"+realname+"'," +
					"sex='"+sex+"',addr='"+addr+"',idcard='"+idcard+"',tel='"+tel+"' where id="+id);
			request.setAttribute("suc", "操作成功!");
			gor("/schoolzjssys/admin/userlist.jsp", request, response);
	}
		
		if(ac.equals("pwdedit")){
			String oldpwd = request.getParameter("oldpwd");
			String newpwd = request.getParameter("newpwd");
			HashMap oldmap = dao.select("select * from sysuser where id="+admin.get("id")).get(0);
			if(oldpwd.equals(oldmap.get("userpwd"))){
				dao.commOper("update sysuser set userpwd = '"+newpwd+"' where id="+admin.get("id"));
				out.print("true");
			}else{
				out.print("false");
			}
		}
		
		if(ac.equals("aboutedit")){
			String lxr = request.getParameter("lxr");
			String tel = request.getParameter("tel");
			String addr = request.getParameter("addr");
			String note = request.getParameter("note");
			dao.commOper("update about set lxr='"+lxr+"',tel='"+tel+"',addr='"+addr+"',note='"+note+"' where id=1");
			request.setAttribute("suc", "操作成功!");
			gor("/schoolzjssys/admin/aboutedit.jsp", request, response);
		}
		
		if(ac.equals("msgadd")){
			String msg = request.getParameter("msg");
			String hfmsg = "";
			String savetime = Info.getDateStr();
			String memberid = member.get("id").toString();
			dao.commOper("insert into chat (msg,hfmsg,savetime,memberid) values " +
					"('"+msg+"','"+hfmsg+"','"+savetime+"','"+memberid+"')");
			request.setAttribute("suc", "留言成功!");
			go("/msg.jsp", request, response);
		}
		
		if(ac.equals("msghf")){
			String id = request.getParameter("id");
			String hfmsg = request.getParameter("hfmsg");
			dao.commOper("update chat set  hfmsg='"+hfmsg+"' where id="+id);
			request.setAttribute("suc", "回复成功!");
			gor("/schoolzjssys/admin/msglist.jsp", request, response);
		}
		if(ac.equals("validatortprice")){
			String gid = request.getParameter("gid");
			String tprice = request.getParameter("tprice")==null?"":request.getParameter("tprice");
			if(!tprice.equals("")){
				HashMap map = dao.select("select * from goods where id="+gid).get(0);
				if(Double.valueOf(tprice)<Double.valueOf(map.get("price").toString())){
					out.print("true");
				}else{
					out.print("false");
				}
			}else{
				out.print("true");
			}
		}
		//设置特价
		if(ac.equals("goodstjset")){
			String id = request.getParameter("id");
			String tprice = request.getParameter("tprice")==null?"":request.getParameter("tprice");
			if(!tprice.equals("")){
				HashMap map = dao.select("select * from goods where id="+id).get(0);
				if(Double.valueOf(tprice)>=Double.valueOf(map.get("price").toString())){
					request.setAttribute("info", "特价必须低于原价!");
					go("/schoolzjssys/admin/goodstjset.jsp?id="+id, request, response);
				}else{
					dao.commOper("update goods set tprice='"+tprice+"' where id="+id);
					request.setAttribute("suc", "特价设置成功!");
					gor("/schoolzjssys/admin/goodsgl.jsp", request, response);
				}
			}else{
				dao.commOper("update goods set tprice='' where id="+id);
				request.setAttribute("info", "特价已取消!");
				gor("/schoolzjssys/admin/goodsgl.jsp", request, response);
			}
		}
		//设置特价  商家
		if(ac.equals("goodstjsetforshop")){
			String id = request.getParameter("id");
			String tprice = request.getParameter("tprice")==null?"":request.getParameter("tprice");
			if(!tprice.equals("")){
				HashMap map = dao.select("select * from goods where id="+id).get(0);
				if(Double.valueOf(tprice)>=Double.valueOf(map.get("price").toString())){
					request.setAttribute("info", "特价必须低于原价!");
					go("/schoolzjssys/admin/goodstjsetforshop.jsp?id="+id, request, response);
				}else{
					dao.commOper("update goods set tprice='"+tprice+"' where id="+id);
					request.setAttribute("suc", "特价设置成功!");
					gor("/schoolzjssys/admin/goodsglforshop.jsp", request, response);
				}
			}else{
				dao.commOper("update goods set tprice='' where id="+id);
				request.setAttribute("info", "特价已取消!");
				gor("/schoolzjssys/admin/goodsglforshop.jsp", request, response);
			}
		}
		if(ac.equals("removedd")){
			String id  = request.getParameter("id");
			HashMap mm = dao.select("select * from ddinfo where id="+id).get(0);
			//HashMap mm2 = dao.select("select * from dddetail where id="+mm.get("ddno")).get(0);
			//if(mm2.get("fhstatus")==null||"".equals(mm2.get("fhstatus"))){
	       		dao.commOper("delete from ddinfo where id="+id);
	       		dao.commOper("delete from dddetail where ddno="+mm.get("ddno"));
	       		out.print("true");
       	/*	}else
       			out.print("false");*/
		}
		/*
		if(ac.equals("confirmorder")){
			String id = request.getParameter("id");
			dao.commOper("update dddetail set shstatus='已收货' where id="+id);
			out.print("true");
		}
		*/
		if(ac.equals("fwup")){
			String id = admin.get("id").toString();
			dao.commOper("update sysuser set userlevel='高级' where id="+id);
			request.setAttribute("info", "升级成功，请全新登录!");
			gor("/schoolzjssys/admin/fwup.jsp", request, response);
		}
		
		if(ac.equals("tj1")){
			ArrayList<HashMap> list = (ArrayList<HashMap>)dao.select("SELECT a.goodname ,(select sum(sl) from dddetail b where a.id=b.goodid ) " +
					"as sl FROM goods a where delstatus=0 order by sl");
			String xdata = "[";
			String ydata = "[";
			for(int i=0;i<list.size();i++){
				//['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
				xdata += "'"+list.get(i).get("goodname")+"'";
				ydata += list.get(i).get("sl");
				if(i<list.size()-1){
					xdata+=",";
					ydata+=",";
				}
			}
			xdata += "]";
			ydata += "]";
			String rtn = xdata+"$"+ydata;
			Gson gson = new Gson();
			rtn = gson.toJson(rtn);
			//System.out.println(rtn);
			out.write(rtn);
		}
		
		if(ac.equals("tj1forshop")){
			ArrayList<HashMap> list = (ArrayList<HashMap>)dao.select("SELECT a.goodname ,(select sum(sl) from dddetail b where a.id=b.goodid ) as sl FROM goods a where saver='"+admin.get("id")+"' and delstatus=0 order by sl");
			String xdata = "[";
			String ydata = "[";
			for(int i=0;i<list.size();i++){
				//['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
				xdata += "'"+list.get(i).get("goodname")+"'";
				ydata += list.get(i).get("sl");
				if(i<list.size()-1){
					xdata+=",";
					ydata+=",";
				}
			}
			xdata += "]";
			ydata += "]";
			String rtn = xdata+"$"+ydata;
			Gson gson = new Gson();
			rtn = gson.toJson(rtn);
			out.write(rtn);
		}
		
		
		
		if(ac.equals("tj2")){
			ArrayList<HashMap> list = (ArrayList<HashMap>)dao.select("SELECT a.goodname ,(select count(*) from fav b where a.id=b.goodid ) as sl FROM goods a where delstatus=0");
			String xdata = "[";
			String ydata = "[";
			for(int i=0;i<list.size();i++){
				xdata += "'"+list.get(i).get("goodname")+"'";
				ydata += "{value:"+list.get(i).get("sl")+",name:'"+list.get(i).get("goodname")+"'}";
				if(i<list.size()-1){
					xdata+=",";
					ydata+=",";
				}
			}
			xdata += "]";
			ydata += "]";
			String rtn = xdata+"$"+ydata;
			Gson gson = new Gson();
			rtn = gson.toJson(rtn);
			out.write(rtn);
		}
		
		if(ac.equals("tj2forshop")){
			ArrayList<HashMap> list = (ArrayList<HashMap>)dao.select("SELECT a.goodname ,(select count(*) from fav b where a.id=b.goodid ) as sl FROM goods a where saver='"+admin.get("id")+"' and delstatus=0");
			String xdata = "[";
			String ydata = "[";
			for(int i=0;i<list.size();i++){
				xdata += "'"+list.get(i).get("goodname")+"'";
				ydata += "{value:"+list.get(i).get("sl")+",name:'"+list.get(i).get("goodname")+"'}";
				if(i<list.size()-1){
					xdata+=",";
					ydata+=",";
				}
			}
			xdata += "]";
			ydata += "]";
			String rtn = xdata+"$"+ydata;
			Gson gson = new Gson();
			rtn = gson.toJson(rtn);
			out.write(rtn);
		}
		
		if(ac.equals("tj3")){
			String sdate = request.getParameter("sdate");
			String edate = request.getParameter("edate");
			String sql = "select date_format(savetime,'%Y-%m-%d') as days,sum(ddprice) as ddprice from ddinfo where fkstatus='已付款' ";
			if(!sdate.equals("")){
				sql += " and savetime>='"+sdate+"' ";
			}
			if(!edate.equals("")){
				sql += " and savetime<='"+edate+"' ";
			}
			sql += " group by days";
			ArrayList<HashMap> list = (ArrayList<HashMap>)dao.select(sql);
			//['周一','周二','周三','周四','周五','周六','周日']
			//[11, 11, 15, 13, 12, 13, 10],
			String xdata = "[";
			String ydata = "[";
			for(int i=0;i<list.size();i++){
				xdata += "'"+list.get(i).get("days")+"'";
				ydata += list.get(i).get("ddprice");
				if(i<list.size()-1){
					xdata+=",";
					ydata+=",";
				}
			}
			xdata += "]";
			ydata += "]";
			String rtn = xdata+"$"+ydata;
			Gson gson = new Gson();
			rtn = gson.toJson(rtn);
			out.write(rtn);
		}
		
		if(ac.equals("tj3forshop")){
			String sdate = request.getParameter("sdate");
			String edate = request.getParameter("edate");
			String sql = "select date_format(savetime,'%Y-%m-%d') as days,sum(price) as price from dddetail where fkstatus='已付款' and goodid in (select id from goods where saver='"+admin.get("id")+"') ";
			if(!sdate.equals("")){
				sql += " and savetime>='"+sdate+"' ";
			}
			if(!edate.equals("")){
				sql += " and savetime<='"+edate+"' ";
			}
			sql += " group by days";
			ArrayList<HashMap> list = (ArrayList<HashMap>)dao.select(sql);
			//['周一','周二','周三','周四','周五','周六','周日']
			//[11, 11, 15, 13, 12, 13, 10],
			String xdata = "[";
			String ydata = "[";
			for(int i=0;i<list.size();i++){
				xdata += "'"+list.get(i).get("days")+"'";
				ydata += list.get(i).get("price");
				if(i<list.size()-1){
					xdata+=",";
					ydata+=",";
				}
			}
			xdata += "]";
			ydata += "]";
			String rtn = xdata+"$"+ydata;
			Gson gson = new Gson();
			rtn = gson.toJson(rtn);
			out.write(rtn);
		}
		
		
		if(ac.equals("tj4")){
			String sdate = request.getParameter("sdate");
			String edate = request.getParameter("edate");
			String sql = "select date_format(savetime,'%Y-%m-%d') as days ,count(*) as count from ddinfo where fkstatus='已付款' ";
			if(!sdate.equals("")){
				sql += " and savetime>='"+sdate+"' ";
			}
			if(!edate.equals("")){
				sql += " and savetime<='"+edate+"' ";
			}
			sql += " group by days";
			ArrayList<HashMap> list = (ArrayList<HashMap>)dao.select(sql);
			//['周一','周二','周三','周四','周五','周六','周日']
			//[11, 11, 15, 13, 12, 13, 10],
			String xdata = "[";
			String ydata = "[";
			for(int i=0;i<list.size();i++){
				xdata += "'"+list.get(i).get("days")+"'";
				ydata += list.get(i).get("count");
				if(i<list.size()-1){
					xdata+=",";
					ydata+=",";
				}
			}
			xdata += "]";
			ydata += "]";
			String rtn = xdata+"$"+ydata;
			Gson gson = new Gson();
			rtn = gson.toJson(rtn);
			out.write(rtn);
		}
		if(ac.equals("tj4forshop")){
			String sdate = request.getParameter("sdate");
			String edate = request.getParameter("edate");
			String sql = "select date_format(savetime,'%Y-%m-%d') as days ,count(*) as count from dddetail where fkstatus='已付款' and goodid in (select id from goods where saver='"+admin.get("id")+"') ";
			if(!sdate.equals("")){
				sql += " and savetime>='"+sdate+"' ";
			}
			if(!edate.equals("")){
				sql += " and savetime<='"+edate+"' ";
			}
			sql += " group by days";
			ArrayList<HashMap> list = (ArrayList<HashMap>)dao.select(sql);
			//['周一','周二','周三','周四','周五','周六','周日']
			//[11, 11, 15, 13, 12, 13, 10],
			String xdata = "[";
			String ydata = "[";
			for(int i=0;i<list.size();i++){
				xdata += "'"+list.get(i).get("days")+"'";
				ydata += list.get(i).get("count");
				if(i<list.size()-1){
					xdata+=",";
					ydata+=",";
				}
			}
			xdata += "]";
			ydata += "]";
			String rtn = xdata+"$"+ydata;
			Gson gson = new Gson();
			rtn = gson.toJson(rtn);
			out.write(rtn);
		}
		
		if(ac.equals("tj5forshop")){
			String sql1 = "select * from ppinfo where delstatus=0";
			ArrayList<HashMap> llll = (ArrayList<HashMap>)dao.select(sql1);
			String xdata = "[";
			String ydata = "[";
			for(int i=0;i<llll.size();i++){
				HashMap llllmap = llll.get(i);
				String sql2 = "select  b.goodname,sum(a.sl) as sl from dddetail a ,goods b where a.goodid=b.id and b.goodpp='"+llllmap.get("id")+"'  group by sl";
				ArrayList<HashMap> list2 = (ArrayList<HashMap>)dao.select(sql2);
				String aa = "0";
				if(list2.size()>0){
					aa = (String)list2.get(0).get("sl");
				}
				xdata += "'"+llllmap.get("ppname")+"'";
				ydata += aa;
				if(i<llll.size()-1){
					xdata+=",";
					ydata+=",";
				}
			}
			
			
			xdata += "]";
			ydata += "]";
			String rtn = xdata+"$"+ydata;
			Gson gson = new Gson();
			rtn = gson.toJson(rtn);
			out.write(rtn);
		}
		
		if(ac.equals("tj6forshop")){
			ArrayList<HashMap> list1 = (ArrayList<HashMap>)dao.select("SELECT * FROM member a where sex='男' and delstatus=0");
			ArrayList<HashMap> list2 = (ArrayList<HashMap>)dao.select("SELECT * FROM member a where sex='女' and delstatus=0");
			String xdata = "['男','女']";
			String ydata = "[{value:"+list1.size()+",name:'男'},{value:"+list2.size()+",name:'女'}]";
			
			String rtn = xdata+"$"+ydata;
			Gson gson = new Gson();
			rtn = gson.toJson(rtn);
			out.write(rtn);
		}
		
		
		if(ac.equals("tj8forshop")){
			ArrayList<HashMap> list = (ArrayList<HashMap>)dao.select("SELECT distinct zy,count(*) sl FROM member group by zy");
			String xdata = "['国家机关、党群组织、企业、事业单位负责人','专业技术人员','办事人员和有关人员','商业、服务业人员','农、林、牧、渔、水利业生产人员','生产、运输设备操作人员及有关人员','军人']";
			String ydata = "[";
			for(int i=0;i<list.size();i++){
				String str = "";
				if(list.get(i).get("zy").equals("1")){
					str = "国家机关、党群组织、企业、事业单位负责人";
				}
				if(list.get(i).get("zy").equals("2")){
					str = "专业技术人员";
				}
				if(list.get(i).get("zy").equals("3")){
					str = "办事人员和有关人员";
				}
				if(list.get(i).get("zy").equals("4")){
					str = "商业、服务业人员";
				}
				if(list.get(i).get("zy").equals("5")){
					str = "农、林、牧、渔、水利业生产人员";
				}
				if(list.get(i).get("zy").equals("6")){
					str = "生产、运输设备操作人员及有关人员";
				}
				if(list.get(i).get("zy").equals("7")){
					str = "军人";
				}
				ydata += "{value:"+list.get(i).get("sl")+",name:'"+str+"'}";
				if(i<list.size()-1){
					ydata+=",";
				}
			}
			ydata += "]";
			String rtn = xdata+"$"+ydata;
			Gson gson = new Gson();
			rtn = gson.toJson(rtn);
			out.write(rtn);
		}
		
		
		//来源分布图
		if(ac.equals("tj7forshop")){
			String sql = "SELECT distinct addr,count(*) sl FROM member group by addr ";
			ArrayList<HashMap> list = (ArrayList<HashMap>)dao.select(sql);
			List<List> merge = new ArrayList();
			String str = "[";
			for(int i=0;i<list.size();i++){
				HashMap map = list.get(i);
				String city = map.get("addr").toString();
				String stunum = map.get("sl").toString();
				if(i<list.size()-1){
					str += "{name: '"+city+"',value: "+stunum+" },";
				}else{
					str += "{name: '"+city+"',value: "+stunum+" }";
				}
			}
			str += "]";
//			JSONArray jsonArray = JSONArray.fromObject(str);
//			response.setHeader("Cache-Control", "no-cache");
//			response.setContentType("aplication/json;charset=UTF-8");
//			response.getWriter().print(str);
			Gson gson = new Gson();
			String flag = gson.toJson(str);
			out.write(flag);

		}
		
		
		
		
		
		
		
		//骑手接单
		/*if(ac.equals("jd")){
			
			String dddetailid = request.getParameter("dddetailid");
			dao.commOper("update dddetail set fhstatus = '已接单' where id = "+Integer.parseInt(dddetailid));
			
			String qsid = member.get("id").toString();
			
			dao.commOper("insert qsdd (dddetailid,qsid,status,qcstatus) values ('"+dddetailid+"','"+qsid+"','已处理','已取餐')");
			out.print("true");
		}
		*/
	dao.close();
	out.flush();
	out.close();
}

	private static Properties config = null;
	 static {
		 try {
	  config = new Properties(); 
	  // InputStream in = config.getClass().getResourceAsStream("dbconnection.properties");
     InputStream in =  CommDAO.class.getClassLoader().getResourceAsStream("dbconnection.properties");
	   config.load(in);
	   in.close();
	  } catch (Exception e) {
	  e.printStackTrace();
	  }
	 }
	public void init()throws ServletException{
		// Put your code here
//		try {
//			responses.getClassLoader((String)config.get("pid"));
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SocketException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
