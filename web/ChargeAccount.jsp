<%@ page contentType="application/xml"%>
<%@ page import="rtcgw.*"%>
<%
	String isdn = request.getParameter("msisdn");
	String user = request.getParameter("user");
	String origin = request.getParameter("origin");
	String dest = request.getParameter("dest");
	int volume = 0;
	String password = request.getParameter("password");
	//String host = request.getParameter("password");

	try {
		volume = Integer.valueOf(request.getParameter("volume")).intValue();
	}
	catch (Exception ex) {}
	
	XmlDoc res = Rtcgw.ChargeAccount(isdn, origin, dest, volume, new User(user, password, "", 0));
	response.setContentType("application/xml");
	response.getWriter().print(res.toString());
    
%>