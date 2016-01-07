package rtcgw;

import java.util.*;

public class User {
    public String uid;
    public int role;
    public String pwd;
    public String host;
    public Set<String> pms;
    
    
    public User(String uid) {
        this(uid, 0, "", "");
    }
    
    public User(String uid, int role, String pwd, String host) {        
        this.uid = uid;
        this.pwd = pwd;
        this.host = host;
        this.role = role;
        pms = new HashSet<String>();
    }
    
    public boolean CheckPermission(String pm) {
        return pms.contains(pm);
    }
    
    public void AddPermission(String pm) {
        pms.add(pm);
    }
    
    // static members
    public static Hashtable<String, User> users = new Hashtable<String, User>(5);
    
    public static void Clear() {
        users.clear();
    }
    
    public static void Add(User u) {
        users.put(u.uid, u);
    }
    
    public static boolean Login(String guest_id, String guest_pwd, String guest_host) {
        boolean logged = false;
        
        if (users.containsKey(guest_id)) {
            User u = users.get(guest_id);
            guest_pwd = Digester.getSHA(guest_pwd);
            
            if (u.pwd.equals(guest_pwd) && 
                    (u.host.indexOf(guest_host) >= 0 || u.host.equals("*")))
                logged = true;
        }
        
        return logged;
    }
    
    public static int Count() {
        return users.size();
    }
}
