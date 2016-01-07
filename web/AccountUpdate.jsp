<%@ page contentType="application/xml"%>
<%@ page import="rtcgw.*"%>
<%
	int adminStatus = -1;
	int serviceClass = -1;
	int cycleStatus = -1;
	String endDate = "";
	String expireDate = "";
	
	
	String isdn = request.getParameter("msisdn");
	String user = request.getParameter("user");
	String password = request.getParameter("password");
	//String host = request.getParameter("password");
	
	try {
		adminStatus = Integer.valueOf(request.getParameter("adminStatus")).intValue();
	}
	catch (Exception ex) {
		adminStatus = -1;
	}
	
	try {
		serviceClass = Integer.valueOf(request.getParameter("serviceClass")).intValue();
	}
	catch (Exception ex) {
		serviceClass = -1;
	}
	
	try {
		cycleStatus = Integer.valueOf(request.getParameter("cycleStatus")).intValue();
	}
	catch (Exception ex) {
		cycleStatus = -1;
	}
	
	if (request.getParameter("endDate") != null)
		endDate = request.getParameter("endDate");
		
	if (request.getParameter("expireDate") != null)
		expireDate = request.getParameter("expireDate");

	XmlDoc res = Rtcgw.AccountUpdate(isdn, adminStatus, serviceClass, cycleStatus, endDate, expireDate, new User(user, password, "", 0));
	response.setContentType("application/xml");
	response.getWriter().print(res.toString());
    
%>