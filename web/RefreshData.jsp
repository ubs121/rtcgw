<%@ page import="rtcgw.*"%>
<%
	String res = Rtcgw.LoadData();
	response.getWriter().print(res);
    
%>