<%@ page contentType="application/xml"%>
<%@ page import="rtcgw.*"%>
<%
	String isdn = request.getParameter("msisdn");
	String user = request.getParameter("user");
	String password = request.getParameter("password");
	//String host = request.getParameter("password");

	XmlDoc res = Rtcgw.AccountInquiry(isdn, new User(user, password, "", 0));
	response.setContentType("application/xml");
	response.getWriter().print(res.toString());
    
%>