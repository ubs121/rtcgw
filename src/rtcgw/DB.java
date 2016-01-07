package rtcgw;

import java.sql.*;
import java.util.*;
import org.jdom.*;
import org.jdom.output.*;
import java.text.*;
import java.io.*;

import oracle.jdbc.pool.*;
import oracle.jdbc.oci.*;
/**
 *
 * @author Uuganbayar.S
 */

public class DB {
    //private static OracleOCIConnectionPool cpool = null;
    private static OracleDataSource cpool = null;
    
    static {
        try {
            cpool = new OracleDataSource();
            cpool.setURL("jdbc_url");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return cpool.getConnection();
    }
    
    private static Object m_oTraceLock = new Object();
    private static FileWriter m_oTraceFile = null;
    private static SimpleDateFormat m_oFormat1 = new SimpleDateFormat(
            "_yyyyMMdd_HHmmssSSS");
    private static SimpleDateFormat m_oFormat3 = new SimpleDateFormat("E");
    private static int m_nFileSize = 0;
    private static java.util.Date m_oOpenDate = null;
    private static File m_oTraceFolder = new File(".");
    private static int MaxFileSize=10;
    
    public static String Usercnt() {
        Connection con=null;
        String str="";
        try {
            con = getConnection();
            Statement sql = con.createStatement();
            ResultSet rs = sql.executeQuery("select count(*) as cnt from t_user where c_status>0");
            rs.next();
            int id = rs.getInt("cnt");
            str="sss"+id;
            rs.close();
            con.close();
        } catch(Exception ex){
            str=ex.toString();
        } finally {
            try {
                if (con != null) con.close();
            } catch(Exception ex) {}
        }
        
        return str;
    }
    public static void LoadConfig() {
        Connection con = null;
        // lock gws, users
        try {
            con = getConnection();
            Statement sql = con.createStatement();
            // load gateways
            System.out.println("-----Gateway(s)-----");
            XmlGateway.Clear();
            ResultSet rs = sql.executeQuery("select * from t_xmlgateway ");
            while (rs.next()) {
                try {
                    int id = rs.getInt("c_id");
                    java.net.URL url = new java.net.URL(rs.getString("c_url"));
                    int status = rs.getInt("c_status");
                    
                    XmlGateway.Add(new XmlGateway(id, url,status));
                    
                    System.out.println(id + ", " + url.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            rs.close();
            
            // load users
            System.out.println("----User(s)-----");
            User.Clear();
            PreparedStatement sqlPms = con.prepareStatement("select  p.c_desc from " +
                    "t_rolepermission rp, t_user u, t_permission p " +
                    "where u.c_uid=? and u.c_role = rp.c_role " +
                    "and rp.c_permission = p.c_id");
            
            rs = sql.executeQuery("select * from t_user where c_status>0");
            
            while (rs.next()) {
                try {
                    User u = new User(
                            rs.getString("c_uid"),
                            rs.getInt("c_role"),
                            rs.getString("c_pwd"),
                            rs.getString("c_host"));
                    
                    sqlPms.setString(1, u.uid);
                    ResultSet rsPms = sqlPms.executeQuery();
                    while (rsPms.next()) {
                        u.AddPermission(rsPms.getString(1));
                    }
                    rsPms.close();
                    
                    User.Add(u);
                    
                    System.out.println(u.uid + ", " + u.host);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            rs.close();
            con.close();
            
            System.out.println("Toxirgoog amjilttai achaallaa:");
            System.out.println("Gateway(s): " + XmlGateway.Count());
            System.out.println("User(s): " + User.Count());
        } catch (Exception ex) {
            System.out.println("Toxirgoog achaalaxad aldaa garlaa");
            System.out.println(ex.getMessage());
        } finally {
            try {
                if (con != null) con.close();
            } catch(Exception ex) {}
        }
    }
    final public static void WriteTrace(int nTraceLevel, String sMessage) {
        
        synchronized (m_oTraceLock) {
            try {
                java.util.Date oDate = new java.util.Date();
                if (m_oTraceFile == null ||
                        m_nFileSize >= MaxFileSize * 1024 * 1024) {
                    OpenTraceFile();
                } else if (m_oFormat3.format(oDate).equals(m_oFormat3.format(
                        m_oOpenDate)) == false) {
                    OpenTraceFile();
                }
                m_oTraceFile.write(sMessage);
                m_oTraceFile.flush();
                // update current file size
                m_nFileSize += sMessage.length();
            }
            // in case of error, close the current log file
            catch (Exception oBug) {
                CloseTraceFile();
            }
        }
    }
    public static void PrintEx(String sss) {
        WriteTrace(10,sss);
    }
    public static void AddLogToFile(String uid, Document xml, int gw) {
        XMLOutputter fmt = new XMLOutputter();
        StringBuilder sb=new StringBuilder();
        String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        java.text.SimpleDateFormat sdf =new java.text.SimpleDateFormat(DATE_FORMAT);
        sb.append("<c_ognoo>");
        sb.append(sdf.format(new java.util.Date()));
        sb.append("</c_ognoo>");
        sb.append("<c_uid>");
        sb.append(uid);
        sb.append("</c_uid>");
        sb.append("<c_phone>");
        sb.append(xml.getRootElement().getChildText("MSISDN"));
        sb.append("</c_phone>");
        sb.append("<c_gw>");
        sb.append(gw);
        sb.append("</c_gw>");
        sb.append("<c_type>");
        sb.append(xml.getRootElement().getName());
        sb.append("</c_type>");
        sb.append("<c_xml>");
        sb.append(fmt.outputString(xml).replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", ""));
        sb.append("<c_xml>");
        WriteTrace(10,sb.toString());
    }
    public static void AddLog(String uid, Document xml, int gw) {
        Connection con = null;
        try {
            con = cpool.getConnection();
            
            CallableStatement sql = con.prepareCall("{call sp_AddLog(?,?,?,?,?)}");
            sql.setString(1, uid);
            sql.setString(2, xml.getRootElement().getName());
            sql.setString(3, xml.getRootElement().getChildText("MSISDN"));
            sql.setInt(4, gw);
            XMLOutputter fmt = new XMLOutputter();
            sql.setString(5,fmt.outputString(xml));
            sql.execute();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {}
            }
        }
        
    }
    final private static void OpenTraceFile() {
        CloseTraceFile();
        try {
            m_oOpenDate = new java.util.Date();
            m_oTraceFile = new FileWriter(m_oTraceFolder.getPath() + "/RTCGW2" +
                    m_oFormat1.format(m_oOpenDate) +
                    ".xml");
        } catch (Exception oBug) {}
    }
    final private static void CloseTraceFile() {
        if (m_oTraceFile != null) {
            try {
                m_oTraceFile.close();
            } catch (Exception oBug) {}
        }
        m_oTraceFile = null;
        m_nFileSize = 0;
        m_oOpenDate = null;
    }
    
}
