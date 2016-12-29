package dbPkg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.libvirt.*;

import vmPkg.PM;

public class Database {
	  // JDBC driver name and database URL
	   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   static final String DB_URL = "jdbc:mysql://localhost/cloud";

	   //  Database credentials
	   static final String USER = "cloud";
	   static final String PASS = "cloud";
		  Connection con = null;
		   Statement stmt = null;
		   
  public int getUid(){
	  int maxVal=-1;

	   
	   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...");
		      con = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt = con.createStatement();
		      String sql;
		      sql = "SELECT ifnull(max(uid),0) max_val from vm_info";
		      ResultSet rs = stmt.executeQuery(sql);

		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Retrieve by column name    
		          maxVal = rs.getInt("max_val");		     
		      }
		      //STEP 6: Clean-up environment
		      rs.close();
		      stmt.close();
		      con.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(con!=null)
		            con.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	   
	  return maxVal;
  }
  
  public void insertVmInfo(String vmName,Connect conn,int pmid,int instType,String pmName){
	  Domain domain;
	try { 
		domain = conn.domainLookupByName(vmName);	
		
		int id = domain.getID();
		long memory = domain.getMaxMemory();
		int cpu=domain.getMaxVcpus();
		String os=domain.getOSType();
		String uuid=domain.getUUIDString();
      
      System.out.println("Domain:" + domain.getName() + " id " +
                         domain.getID() + " running " + domain.getOSType());    
	   
	   try{		     
		      Class.forName("com.mysql.jdbc.Driver");
		      con = DriverManager.getConnection(DB_URL,USER,PASS);
		      stmt = con.createStatement();
		      String sql;
		      sql = "insert into vm_info values ("+id+",'"+vmName+"',"+memory+","+cpu+",'"+os+"',"+pmid+",'"+uuid+"',"+instType+",'"+pmName+"')";
		      System.out.println("inserting the followin vm info into the table");
		      System.out.println(sql);
		     stmt.executeUpdate(sql);
		      stmt.close();
		      con.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(con!=null)
		            con.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		} catch (LibvirtException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	}
  

  public String getVmInfo(String uid){
	   String vmInfo="";
	   String instanceType="";
	   String name="";
	   String pmId="";
	   try{		     
		      Class.forName("com.mysql.jdbc.Driver");
		      con = DriverManager.getConnection(DB_URL,USER,PASS);
		      stmt = con.createStatement();
		      String sql;
		      sql = "select  instance_type, name, uid, pm_id from vm_info where uid ="+uid;
		      System.out.println(sql);
		      ResultSet rs = stmt.executeQuery(sql);
		      if(!rs.next()){
		    	  return "VM "+uid+ " doesn't exist. Please enter correct vm id";
		    	  
		      }else {
		        do {
		    	  instanceType=rs.getString("instance_type");
		    	  name=rs.getString("name");
		    	  pmId=rs.getString("pm_id");
		    	  
		     	}	while (rs.next()); 
		      }
		      stmt.close();
		      con.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(con!=null)
		            con.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	   vmInfo += "Instance Type : "+instanceType;
	   vmInfo += "....Pm Id : "+ pmId;
	   vmInfo += "....uid : "+ uid;
	   vmInfo += "....name : "+name;
		return vmInfo;
	}
  public String getVMsOfPM(String pmid){
	  String vmids="";
	  try{		     
	      Class.forName("com.mysql.jdbc.Driver");
	      con = DriverManager.getConnection(DB_URL,USER,PASS);
	      stmt = con.createStatement();
	      String sql;
	      sql = "select  uid from vm_info where pm_id ="+pmid;
	      System.out.println(sql);
	      ResultSet rs = stmt.executeQuery(sql);
	      
	      if(!rs.next()){
	    	  return "No VMs found on the given PM";
	      }else {
	    	  do{
	    		  vmids += rs.getString("uid")+",";
	    	  } while (rs.next());
	      }
	     
	    	  
	      
	    }catch(SQLException se){
	      //Handle errors for JDBC
	      se.printStackTrace();
	   }catch(Exception e){
	      //Handle errors for Class.forName
	      e.printStackTrace();
	   }finally{
	      //finally block used to close resources
	      try{
	         if(stmt!=null)
	            stmt.close();
	      }catch(SQLException se2){
	      }// nothing we can do
	      try{
	         if(con!=null)
	            con.close();
	      }catch(SQLException se){
	         se.printStackTrace();
	      }//end finally try
	   }//end try
	  vmids = vmids.substring(0, vmids.length()-1);
	  return "vmids:["+vmids+"]";
  }
  public void removeVMInfo(String vmid){

	   try{		     
		      Class.forName("com.mysql.jdbc.Driver");
		      con = DriverManager.getConnection(DB_URL,USER,PASS);
		      stmt = con.createStatement();
		      String sql;
		      sql = "delete from vm_info where uid="+vmid;
		      System.out.println("Removing the vm "+vmid+" from database");
		      stmt.executeUpdate(sql);
	   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(con!=null)
		            con.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }
  }
  public String getPMName(String vmid){
	  String pmName="";
	  try{		     
	      Class.forName("com.mysql.jdbc.Driver");
	      con = DriverManager.getConnection(DB_URL,USER,PASS);
	      stmt = con.createStatement();
	      String sql;
	      sql = "select pm_name from vm_info where uid ="+vmid;
	      System.out.println(sql);
	      ResultSet rs = stmt.executeQuery(sql);
	      while (rs.next()) {
	    	  pmName=rs.getString("pm_name");    	  
	      }	     
	      stmt.close();
	      con.close();
	   }catch(SQLException se){
	      //Handle errors for JDBC
	      se.printStackTrace();
	   }catch(Exception e){
	      //Handle errors for Class.forName
	      e.printStackTrace();
	   }finally{
	      //finally block used to close resources
	      try{
	         if(stmt!=null)
	            stmt.close();
	      }catch(SQLException se2){
	      }// nothing we can do
	      try{
	         if(con!=null)
	            con.close();
	      }catch(SQLException se){
	         se.printStackTrace();
	      }//end finally try
	   }//end try
	   return pmName;
  }
}
