package edu.jsu.mcis.cs310.tas_sp22;
import java.sql.*;
import java.sql.Connection;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TASDatabase {
    
    private final Connection connection;
    
    public TASDatabase(String username, String password, String address) {
        
        this.connection = openConnection(username, password, address);
        
        
    }
    String results =null;
    public String getEmployee(int id) {
        
        
        ArrayList<String> keys = new ArrayList<String>();
        HashMap <String, String> empl =new HashMap <>() ;
        try{
         String query= "SELECT * FROM tas_sp22_v1.employee WHERE id =?;";
         PreparedStatement pstmt = connection.prepareStatement(query);
         pstmt.setInt(1,id);
         boolean pstmtExe = pstmt.execute();
         
         if(pstmtExe){
            ResultSet resultset = pstmt.getResultSet(); 
              
            while(resultset.next()){
                ResultSetMetaData metadata = resultset.getMetaData();
                
                int columnCount = metadata.getColumnCount();
            
                    for (int i = 1; i <= columnCount; ++i) {

                    keys.add(metadata.getColumnLabel(i));
                   
                    }
                     for (int i = 1; i <= columnCount; ++i) {
                  
                    Object value = resultset.getObject(i);
                    empl.put(keys.get(i - 1), String.valueOf(value));
                    }  
                }
            }
        }
        catch(Exception e) {
         e.printStackTrace(); 
        }
        Employee hmap = new Employee(empl);
        return empl.toString();
    } 
    public String getShift(int id) {
       
        ArrayList<String> keys = new ArrayList<String>();
        HashMap <String, String> shif =new HashMap <>() ;
        try{
         String query= "Select *FROM shift WHERE id = ?;";
         PreparedStatement pstmt = connection.prepareStatement(query);
         pstmt.setInt(1,id);
        boolean pstmtExe = pstmt.execute();
        
         if(pstmtExe){
            ResultSet resultset = pstmt.getResultSet(); 
              
            while(resultset.next()){
                ResultSetMetaData metadata = resultset.getMetaData();
                
                int columnCount = metadata.getColumnCount();
            
                    for (int i = 1; i <= columnCount; ++i) {

                    keys.add(metadata.getColumnLabel(i));
                   
                    }
                     for (int i = 1; i <= columnCount; ++i) {
                  
                    Object value = resultset.getObject(i);
                    shif.put(keys.get(i - 1), String.valueOf(value));
                    }  
                }
            }
        }
        catch(Exception e) {
         e.printStackTrace(); 
        }
        Shift hmap = new Shift(shif);
        return shif.toString();
        }
    
    public boolean isConnected() {

        boolean result = false;
        
        try {
            
            if ( !(connection == null) )
                
                result = !(connection.isClosed());
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return result;
        
    }
    private Connection openConnection(String u, String p, String a) {
        
        Connection c = null;
        
        if (a.equals("") || u.equals("") || p.equals(""))
            
            System.err.println("*** ERROR: MUST SPECIFY ADDRESS/USERNAME/PASSWORD BEFORE OPENING DATABASE CONNECTION ***");
        
        else {
        
            try {

                String url = "jdbc:mysql://" + a + "/tas_sp22_v1?autoReconnect=true&useSSL=false&zeroDateTimeBehavior=EXCEPTION&serverTimezone=America/Chicago";
                // System.err.println("Connecting to " + url + " ...");

                c = DriverManager.getConnection(url, u, p);

            }
            catch (Exception e) { e.printStackTrace(); }
           
        }
        
        return c;
         
    }
}
 