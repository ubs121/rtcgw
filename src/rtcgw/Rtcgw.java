package rtcgw;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class Rtcgw extends HttpServlet {
    public static boolean DBConfig=true;
    static {
        DB.LoadConfig();
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uid=request.getHeader("user");
        
        Document xmlReq = null;
        Document xmlRes = null;
        BufferedReader br = new BufferedReader(request.getReader());
        String line = null;
        while ((line = br.readLine()) != null)
            System.out.println(line);
        
        XmlGateway gw = XmlGateway.Select();
        //SAXBuilder builder1 = new SAXBuilder();
        //try
        //{
        //xmlReq = builder1.build(request.getReader());
        //DB.AddLog(uid, xmlReq, 99);
        //}
        //catch(Exception ex)
        //{
        //    System.out.println(ex.toString());
        //}
        if (gw != null) {
            try {
                SAXBuilder builder = new SAXBuilder();
                xmlReq = builder.build(request.getReader());
                
                Logger logReq = new Logger(uid, xmlReq, gw.id);
                logReq.start();
                xmlRes = gw.Send(uid,request.getHeader("password"),request.getRemoteAddr(),xmlReq);
                
                Logger logRes = new Logger(uid, xmlRes, gw.id);
                logRes.start();
                
                XMLOutputter fmt = new XMLOutputter();
                
                fmt.output(xmlRes, response.getOutputStream());
                
            } catch (Exception ex) {
                response.setContentType("text/xml");
                response.getWriter().print("<System_Response><Error>" + ex.toString()
                        + "</Error><System_Response>");
                gw.Err++;
                if (gw.Err>3)
                    gw.Status=0;
            }
        } else {
            response.setContentType("text/xml");
            response.getWriter().print("<System_Response><Error>There is no more running gateways</Error><System_Response>");
        }
    }
    
    public static void main(String[] args) {
        
        XmlGateway gw = XmlGateway.Select();
        System.out.println(gw.url.toString());
        System.out.println(gw.Status);
        gw = XmlGateway.Select();
        System.out.println(gw.url.toString());
        System.out.println(gw.Status);
        DB.WriteTrace(10,"asdf");
        //System.out.println(Digester.getSHA("test"));
        
/*
         //System.out.println("sss");
        try {
            SAXBuilder builder = new SAXBuilder();
            Document   xmlRes=builder.build(new StringReader("<System_Response><aaa></aaa><Error>Loading configuration... please wait a moment and try again</Error></System_Response>"));
            // System.out.println(xmlRes.getRootElement().getName());
            XMLOutputter fmt = new XMLOutputter();
            // System.out.println(fmt.outputString(xmlRes));
 
            Element root=xmlRes.getRootElement();
       //     modifyElement(root,"aaa1");
        } catch(Exception ex) {
            System.out.println("aaaaaaaaa");
            System.out.println(ex.toString());
        }
        Hashtable sss=new Hashtable();
        sss.put("sss", "1");
        sss.put("sss", "2");
        sss.put("sss", "3");
        sss.put("sss", "45");
        String[] t=(String[])sss.get("sss");
        System.out.println(t.length);
 
                //DB.LoadConfig();
 
       /* System.out.println("User(s): " + User.Count());
        Set<String> sss=new HashSet<String>();
        sss.add("a");
        sss.add("b");
        sss.add("c");
        Iterator<String> it=sss.iterator();
        while(it.hasNext())
        {
            System.out.println(it.toString());
            it.next();
        }
 */
    }
}
