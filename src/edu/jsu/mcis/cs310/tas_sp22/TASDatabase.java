package edu.jsu.mcis.cs310.tas_sp22;

import java.sql.*;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.*;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;

public class TASDatabase {
    
    private final Connection connection;
    
    public TASDatabase(String username, String password, String address) {
        this.connection = openConnection(username, password, address);
    }
   
    public Badge getBadge(String badgeid) {
        String id = null, des = null;
     
        try {
            String query = "Select *FROM Badge WHERE id = ? ;";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, badgeid);
            
            boolean ptExe = pstmt.execute();
            
            if (ptExe) {
                ResultSet resultset = pstmt.getResultSet();
                
                while (resultset.next()){
                    id = resultset.getString(1);
                    des = resultset.getString(2);
                }
            }
        }
        catch (Exception e) { 
            e.printStackTrace();
        }
        
        Badge result = new Badge(id, des);
        
        return result;
    } 
     
    public Badge getBadge(int punchid) {
        String badgeid = null;
     
        try {
            String query = "Select *FROM Event Where id = ? ;";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, punchid);
            
            boolean ptExe = pstmt.execute();
            
            if (ptExe) {
                ResultSet resultset = pstmt.getResultSet();
                
                while (resultset.next()){
                    badgeid = resultset.getString(1);
                }
            }
        }
        
        catch (Exception e) { 
            e.printStackTrace();
        }
        
        return getBadge(badgeid);
    }
    
   
    public Employee getEmployee(int id) {       
        HashMap<String, String> params = new HashMap<>();

        try{
            String query= "SELECT * FROM employee WHERE id = ? ";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, id);

            boolean pstmtExe = pstmt.execute();

            if (pstmtExe) {
                
                ResultSet resultset = pstmt.getResultSet();

                if (resultset.next()) {
                    params.put("id", String.valueOf(id));
                    params.put("badgeid", resultset.getString("badgeid"));
                    params.put("firstname",resultset.getString("firstname"));
                    params.put("lastname",resultset.getString("lastname"));
                    params.put("middlename",resultset.getString("middlename"));
                    params.put("employeetypeid", String.valueOf(resultset.getInt("employeetypeid")));
                    params.put("departmentid", String.valueOf(resultset.getInt("departmentid")));
                    params.put("shiftid", String.valueOf(resultset.getInt("shiftid")));
                    params.put("active",resultset.getDate("active").toString());
                    params.put("inactive", resultset.getString("inactive"));
                }
            }
        }
        
        catch(Exception e) {
            e.printStackTrace(); 
        }
        
        Employee employee = new Employee(params);
        
        return employee;
    }
    
    public Employee getEmployee(Badge badge) {       
        String badgeid = badge.getId();
        int id_int = 0;

        try {
            String query = "SELECT * FROM employee WHERE badgeid = ? ";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, badgeid);
            
            boolean ptExe = pstmt.execute();
            
            if (ptExe) {
                ResultSet resultset = pstmt.executeQuery();
                
                while (resultset.next()){
                    badgeid = resultset.getString(2);
                    id_int = resultset.getInt(1);
                }
            }
        }
        
        catch (Exception e) { 
            e.printStackTrace();
        }
        
        return getEmployee(id_int);
    } 
    
   
    public Punch getPunch(int id) {
        HashMap<String, String> params = new HashMap<>();
        
        try{
            String query = "SELECT * FROM tas_sp22_v1.event WHERE id = ? ";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1,id);
         
            boolean pstmtExe = pstmt.execute();

            if (pstmtExe) {

                ResultSet resultset = pstmt.getResultSet();

                while (resultset.next()) {
                    params.put("id", String.valueOf(id));
                    params.put("terminalid", String.valueOf(resultset.getInt("terminalid")));
                    params.put("eventtypeid",String.valueOf(resultset.getInt("eventtypeid")));
                    params.put("timestamp", resultset.getTimestamp("timestamp").toLocalDateTime().toString());
                    params.put("badgeid", resultset.getString("badgeid"));
                }
            }
        }
        
        catch(Exception e) {
            e.printStackTrace(); 
        }
        String badgeid = params.get("badgeid");
        Badge badge = getBadge(badgeid);
        
        Punch Results = new Punch(params,badge);
        
        return Results;
    } 
    
    public int insertPunch(Punch p){
        int result = 0;
        int key = 0;
        ResultSet keys;
         
        
        int terminalid = p.getTerminalid();
        Badge badge = p.getBadge();
        String badgeid = badge.getId();
        int eventtypeid = p.getEventtypeid();
         
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:SS");
        
        LocalDateTime now = p.getOriginalTimestamp();
        String currenttime = now.format(dtf);
        Timestamp timestamp = Timestamp.valueOf(currenttime);
        
        Employee employee = getEmployee(badge);
         
        int departmentid = employee.getDeptid();
        int terminalid_employee = (getDepartment(departmentid)).getTerminalid();
       
        boolean test =(terminalid == terminalid_employee||terminalid == 0);
        
        
        if (test){
            
            try{
            String query = "INSERT INTO event (terminalid, badgeid, timestamp, eventtypeid) VALUES (?,?,?,?);";
           
            PreparedStatement pstmt = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
            
           
            pstmt.setInt(1,terminalid);
            pstmt.setString(2,badgeid);
            pstmt.setTimestamp(3,timestamp);
            pstmt.setInt(4,eventtypeid);
           
            result = pstmt.executeUpdate();
            
                if (result == 1) {
                    keys = pstmt.getGeneratedKeys();
                    
                    if (keys.next()) { 
                        key = keys.getInt(1); 
                    }
                }
            }  
            catch (Exception e){
                e.printStackTrace(); 
            }
        }
        
        else{
            return key;
        }
        
        System.err.println(key +" this is the key returned by test 3");
        
        return key;
    }
    
            
    public Shift getShift(int id) {       
        HashMap<String, String> params = new HashMap<>();
        try {
            
            String query= "Select * FROM shift WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, id);
           
            boolean pstmtExe = pstmt.execute();

            if (pstmtExe) {
                ResultSet resultset = pstmt.getResultSet();
                
                if (resultset.next()) {

                    params.put("description", resultset.getString("description"));
                    params.put("id", String.valueOf(resultset.getInt("id")));
                    params.put("shiftstart",resultset.getTime("shiftstart").toLocalTime().toString());
                    params.put("shiftstop",resultset.getTime("shiftstop").toLocalTime().toString());
                    params.put("roundinterval", String.valueOf(resultset.getInt("roundinterval")));
                    params.put("graceperiod", String.valueOf(resultset.getInt("graceperiod")));
                    params.put("dockpenalty", String.valueOf(resultset.getInt("dockpenalty")));
                    params.put("lunchstart",resultset.getTime("lunchstart").toLocalTime().toString());
                    params.put("lunchstop",resultset.getTime("lunchstop").toLocalTime().toString());
                    params.put("lunchthreshold", String.valueOf(resultset.getInt("lunchthreshold")));
                }
            }
        }
       
        catch(Exception e) {
            e.printStackTrace(); 
        }
        Shift Results = new Shift(params);
        
        return Results;
    }
    
    public Shift getShift(Badge badge) {       
        String badgeid = badge.getId();
        int id_int = 0;
        HashMap<String, String> params = new HashMap<>();
        try {
            String query = "SELECT * FROM employee WHERE badgeid = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, badgeid);
            
            boolean ptExe = pstmt.execute();
            
            if (ptExe) {
                ResultSet resultset = pstmt.executeQuery();
                
                while (resultset.next()){
                    params.put("shiftid", String.valueOf(resultset.getInt("shiftid")));
                }
            }
        }
        
        catch (Exception e) { 
            e.printStackTrace();
        }
        int shiftid = Integer.parseInt(params.get("shiftid"));
        
        return getShift(shiftid);
    }
    
    
    public Department getDepartment(int id) {
        HashMap<String, String> params = new HashMap<>();
        
        try{
            String query= "SELECT * FROM department WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1,id);

            boolean pstmtExe = pstmt.execute();

            if (pstmtExe) {

                ResultSet resultset = pstmt.getResultSet();

                while (resultset.next()) {
                    params.put("id", String.valueOf(id));
                    params.put("description", resultset.getString("description"));
                    params.put("terminalid", String.valueOf(resultset.getInt("terminalid")));

                }
            }
        }
        catch (Exception e) {
            e.printStackTrace(); 
        }
        
        Department Results = new Department(params);
        
        return Results;
    } 
    
    
    public  ArrayList<Punch> getDailyPunchList(Badge badge, LocalDate date) {
        ArrayList<Punch> DailyPunches = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Badge name = badge;
        String badgeid =name.getId();


        HashMap<String,Integer> params = new HashMap<>();
        Date localdate = Date.valueOf(date);


        int result = 0;

        try{
            String query= "SELECT *, DATE(`timestamp`) AS tsdate FROM tas_sp22_v1.event WHERE badgeid=? HAVING tsdate=? ORDER BY `timestamp`";
           
            PreparedStatement pstmt = connection.prepareStatement(query);
           
            pstmt.setString(1,badgeid);
            pstmt.setDate(2,localdate); 
            
              
            boolean pstmtExe = pstmt.execute();

            if (pstmtExe) {
                    
                ResultSet resultset = pstmt.getResultSet();
                
                while (resultset.next()) {
                    params.put("id",(resultset.getInt("id")));
                    int id= params.get("id");
                    DailyPunches.add(getPunch(id));
                    
                }
            } 
        }  
       
        catch (Exception e){
            e.printStackTrace(); 
        }
       
        return DailyPunches;
    }
    
    public ArrayList<Punch> getPayPeriodPunchList(Badge badge, LocalDate payperiod, Shift s){

        ArrayList<Punch> payPunches = new ArrayList<>();
        TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
       
        Date payweek = java.sql.Date.valueOf(payperiod.with(fieldUS, Calendar.SUNDAY));
        LocalDate pay_week = payweek.toLocalDate();
        
        for (int i = 0; i < 7; i++) {
            LocalDate payperiod_day = pay_week.plusDays(i);
            ArrayList<Punch> dailyPunchList =  getDailyPunchList(badge, payperiod_day);
            for (Punch punch : dailyPunchList){
                payPunches.add(punch);
            }
        }
        
        return payPunches;
    }
    

    public Absenteeism getAbsenteeism(Badge badge, LocalDate payperiod){
        double percentage = 0.00;

        try {
            String query = "SELECT * FROM absenteeism WHERE badgeid = ? AND payperiod = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, badge.getId());
            pstmt.setString(2, String.valueOf(payperiod.with(DayOfWeek.SUNDAY)));

            pstmt.execute();
            
            ResultSet resultset = pstmt.executeQuery();

            while (resultset.next()) {
                percentage = resultset.getDouble("percentage");
            }
            
        }
        
        catch (Exception e) { 
            e.printStackTrace();
        }
        
        Absenteeism result = new Absenteeism(badge, payperiod, percentage);
        
        return result;
    }   
    
    public int insertAbsenteeism(Absenteeism a){
        int result = 0;
        
        String badgeid = a.getBadge().getId();
        String payperiod = a.getPayperiod().toString();
        double percentage = a.getPercentage();
        
        
        try{
            String query = "REPLACE INTO absenteeism (badgeid, payperiod, percentage) VALUES (?, ?, ?)";
           
            PreparedStatement pstmt = connection.prepareStatement(query);
           
            pstmt.setString(1, badgeid);
            pstmt.setString(2, payperiod);
            pstmt.setDouble(3, percentage);
           
            result = pstmt.executeUpdate();
            }
        
            catch (Exception e){
                e.printStackTrace(); 
            }
        
        return result;
    }
    
    
    public boolean isConnected() {
        boolean result = false;
        
        try {
            
            if ( !(connection == null) ){
                result = !(connection.isClosed());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;   
    }
    
    private Connection openConnection(String u, String p, String a) {
        Connection c = null;
        
        if (a.equals("") || u.equals("") || p.equals("")){
            System.err.println("*** ERROR: MUST SPECIFY ADDRESS/USERNAME/PASSWORD BEFORE OPENING DATABASE CONNECTION ***");
        }
        
        else {
        
            try {
                String url = "jdbc:mysql://" + a + "/tas_sp22_v1?autoReconnect=true&useSSL=false&zeroDateTimeBehavior=EXCEPTION&serverTimezone=America/Chicago";
                // System.err.println("Connecting to " + url + " ...");

                c = DriverManager.getConnection(url, u, p);
            }
            
            catch (Exception e) {
                e.printStackTrace();
            }  
        }
        
        return c;    
    }
}
 