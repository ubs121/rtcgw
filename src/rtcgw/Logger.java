package rtcgw;

import org.jdom.*;
import java.util.*;
/**
 *
 * @author Uuganbayar.S
 */
public class Logger extends Thread {
    private String uid;
    private Document xml;
    private int gw;
    
    public Logger(String uid, Document xml, int gw) {
        this.uid = uid;
        this.xml = xml;
        this.gw = gw;
    }
    
    public void run() {
        try
        {
        if(XmlGateway.ViewLogType()==0)
        DB.AddLog(this.uid, this.xml, this.gw);
        else
        DB.AddLogToFile(this.uid, this.xml, this.gw);            
        }
        catch(Exception ex)
        {
            DB.AddLogToFile(uid, xml, gw);
        }
    }
}
