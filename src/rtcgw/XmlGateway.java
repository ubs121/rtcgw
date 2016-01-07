package rtcgw;

import java.net.*;
import java.io.*;
import java.util.*;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Uuganbayar.S
 */

public class XmlGateway {
    public int id;
    public URL url;
    public int Err;
    public int Status;
    
    public XmlGateway()  {
        this(-1, null,0);
    }
    
    public XmlGateway(int id, URL url,int status)  {
        this.id = id;
        this.url = url;
        this.Err=0;
        this.Status=status;
    }
    protected static boolean check(Element element, User u) {
        String type=element.getName();
        List children = element.getContent();
        for (int i = 0; i < children.size(); i++) {
            Element child = (Element)children.get(i);
            if(u.pms.contains((type+child.getName()).toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    public Document Send(String uid, String pwd, String guest_host, Document xmlReq) throws IOException, JDOMException {
        SAXBuilder builder = new SAXBuilder();
        Document xmlRes=builder.build(new StringReader("<System_Response><Error>Access denied: User=" +
                uid+" Host="+guest_host+"</Error></System_Response>"));
        if(Rtcgw.DBConfig) {
            if (User.users.containsKey(uid)) {
                User u = User.users.get(uid);
                pwd = Digester.getSHA(pwd);
                
                if (u.pwd.equals(pwd) &&
                        (u.host.indexOf(guest_host) >= 0 || u.host.equals("*"))) {
                    if(check(xmlReq.getRootElement(),u)) {
                        
                        XMLOutputter fmt = new XMLOutputter();
                        HttpURLConnection con = (HttpURLConnection)this.url.openConnection();
                        con.setRequestMethod("POST");
                        con.setRequestProperty("Content-Length", String.valueOf(xmlReq.getContentSize()));
                        con.setRequestProperty("Content-Type", "text/xml");
                        con.setUseCaches(false);
                        con.setDoInput(true);
                        con.setDoOutput(true);
                        
                        fmt.output(xmlReq, con.getOutputStream());
                        con.connect();
                        
                        xmlRes = builder.build(con.getInputStream());
                        con.disconnect();
                        
                    }
                }
            }
        } else {
            
            xmlRes=builder.build(new StringReader("<System_Response><Error>Loading configuration... please wait a moment and try again</Error></System_Response>"));
        }
        return xmlRes;
    }
    
    // static members
    private static Hashtable<Integer, XmlGateway> gws = new Hashtable<Integer, XmlGateway>(5);
    private static int currentGw = 0;
    private static int LogType=0;
    
    public static void Clear() {
        currentGw = -1;
        gws.clear();
    }
    public static int ViewLogType(){
        return LogType;
    }
    public static void Add(XmlGateway gw) {
        currentGw++;
        gws.put(currentGw, gw);
    }
    
    public static int Count() {
        return gws.size();
    }
    
    public static XmlGateway Select() {        
        XmlGateway gw;
        int index=currentGw;    
        currentGw++;
        if (currentGw >= gws.size())
            currentGw = 0;
        gw=gws.get(currentGw);
        while(gw.Status==0 ) {
            if(currentGw!=index)
            {
            currentGw++;
            if (currentGw >= gws.size())
                currentGw = 0;
             gw=gws.get(currentGw);
          }
            else
                break;
        }      
        return gw;
    }
}
