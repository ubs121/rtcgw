<%@ page contentType="application/xml"%>
<%@ page import="rtcgw.*"%>
<%
	String isdn = request.getParameter("isdn");
	String birthday = request.getParameter("birthday");
	String servclas = request.getParameter("servclas");
	String firstnam = request.getParameter("firstnam");
	String mn = request.getParameter("mn");
	String password = request.getParameter("password");
	//String host = request.getParameter("password");


	XmlDoc res = Rtcgw.Register(isdn, birthday, servclas, firstnam, mn, nill);
	response.setContentType("application/xml");
	response.getWriter().print(res.toString());
    
%>