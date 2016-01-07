<%@ page contentType="application/xml"%>
<%@ page import="rtcgw.*"%>
<%
	String isdn = request.getParameter("msisdn");
	int amount = 0;
	int day = 0;
	String user = request.getParameter("user");
	String password = request.getParameter("password");
	//String host = request.getParameter("password");
	
	try {
		amount = Integer.valueOf(request.getParameter("amount")).intValue();
	}
	catch (Exception ex) {
		amount = 0;
	}
	
	try {
		day = Integer.valueOf(request.getParameter("day")).intValue();
	}
	catch (Exception ex) {
		day = 0;
	}

	XmlDoc res = Rtcgw.BalanceModify(isdn, amount, day, new User(user, password, "", 0));
	response.setContentType("application/xml");
	response.getWriter().print(res.toString());
    
%>