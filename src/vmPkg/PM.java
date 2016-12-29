
package vmPkg;

import java.io.BufferedReader;
import java.io.FileReader;
import org.libvirt.Connect;
import org.libvirt.LibvirtException;


public class PM {
	
	private int currentPmId;
	private String pmName;
	
	public String getPmName() {
		return pmName;
	}

	public void setPmName(String pmName) {
		this.pmName = pmName;
	}

	public void setCurrentPmId(int pmId){
		this.currentPmId=pmId;
	}
	
	public int getCurrentPmId(){
		return this.currentPmId;
	}
   public Connect  getPmConn(int cpuReqd , int ramReqd , int diskReqd){
	   Connect conn=null;
	    int ramAvailable=0;
	   	String pmFile="/home/pradeep/Dropbox/MTech/Monsoon2015/Cloud/MiniProject/pm_file";
	   try (BufferedReader pmbr = new BufferedReader(new FileReader(pmFile))){
		    String line,pmId;
		    while ((line = pmbr.readLine()) != null) {
		    	
		    	String[] pminfo = line.split("\\s+");
		    	 pmId= pminfo[0];
		    	String pmUserName= pminfo[1];
		    	try{ System.out.println("making ssh connection to "+pmUserName);
		    		conn = new Connect("qemu+ssh://"+pmUserName+"/system");
		    	} catch (LibvirtException e){
		    		e.printStackTrace();
		    	}
		    	int cpusAvailable=conn.getMaxVcpus(null);
		    	System.out.println("cpus available "+cpusAvailable);
		    	String memFile="/proc/meminfo";
		    	 try (BufferedReader mfbr = new BufferedReader(new FileReader(memFile))){
		    		 String memLine;
		    		 while ((memLine = mfbr.readLine()) != null) {
		    			 System.out.println("mem file :"+memLine);
		    			 String[] meminfosplit = memLine.split("\\s+");
		    			 if(meminfosplit[0].equalsIgnoreCase("MemFree:")){
		    				 ramAvailable = Integer.parseInt(meminfosplit[1]);
		    				 System.out.println("available ram is "+ramAvailable);
		    				 break;
		    			 }
		    		 }
		    		 
		    	 }catch(Exception e){
		    		 
		    	 }
		    	 if(ramAvailable>=ramReqd && cpusAvailable>=cpuReqd){
		    		 System.out.println("Setting pm id to "+pmId);
		    		 this.setCurrentPmId(Integer.parseInt(pmId));
		    		 this.setPmName(pmUserName);
		    		 return conn;
		    	 }
		    	 
		    }
		} catch( Exception e){
			
		}
    	  return conn;
   }
   
   public static void main (String args[]){
	   PM pm = new PM();
	   pm.getPmConn(1, 2, 3);
   }
   
}