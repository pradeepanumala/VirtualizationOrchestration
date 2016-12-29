package vmPkg;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import org.libvirt.*;

import dbPkg.Database;
import jsonPkg.JsonCtrl;

public class DomainCtrl {
	public void createDomain(String vmName,int instType){
		JsonCtrl jctrl = new JsonCtrl();
		HashMap<String, String> hm = jctrl.getVmHm(vmName,instType);
		//System.out.println("Returned from the xml creation code");
		Connect conn=null;
		
		int cpu = Integer.parseInt(hm.get("cpu"));
		int ram = Integer.parseInt(hm.get("ram"));
		int disk = Integer.parseInt(hm.get("disk"));
		
		PM pm = new PM();		
		conn=pm.getPmConn(cpu,ram,disk);
		
		try {
			System.out.println("creating domain...");
			conn.domainCreateLinux( hm.get("xml"), 0);
			System.out.println("domain created..");
			//domain created.. now add domain details to the database
			Database db = new Database();
			db.insertVmInfo(vmName, conn, pm.getCurrentPmId(),instType,pm.getPmName());
		
		} catch (LibvirtException e) {
			// TODO Auto-generated catch block
			System.out.println("An error occured while creating domain");
			e.printStackTrace();
		}
	}
	
	public String getVMInfo(String uid){
		Database db= new Database();
		return db.getVmInfo(uid);
	}
	
	public String getPMInfo(String pmid){
		   Connect conn=null;
		   String pmUserName=getPMName(pmid);
		   int cpusAvailable=-1;
		   int ramAvailable=-1;
		   String memFile="/proc/meminfo";
		try{ System.out.println("making ssh connection to "+pmUserName);
		conn = new Connect("qemu+ssh://"+pmUserName+"/system");
	} catch (LibvirtException e){
		e.printStackTrace();
	}
		
		 try (BufferedReader mfbr = new BufferedReader(new FileReader(memFile))){
    		 String memLine;
    		 cpusAvailable=conn.getMaxVcpus(null);
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
		return "";
	}
	
	public String getPMName(String pmid){
		String pmFile="/home/pradeep/Dropbox/MTech/Monsoon2015/Cloud/MiniProject/pm_file";
		String pmUserName="";
		 try (BufferedReader pmbr = new BufferedReader(new FileReader(pmFile))){
			    String line,pm_id;
			    while ((line = pmbr.readLine()) != null) {
			    	
			    	String[] pminfo = line.split("\\s+");
			    	 pm_id= pminfo[0];
			    	 pmUserName= pminfo[1];
			    	if(pm_id.equalsIgnoreCase(pmid)) break;
			    }
		 }catch(Exception e){
			 
		 }
		return pmUserName;
	}
	public String getVMsOfPM(String pmid){
		Database db= new Database();
		return db.getVMsOfPM(pmid);		
	}
	
	public String destroyDomain(String vmid){
		String pmName=null;
		Connect conn=null;
		//int successFlag=-1;
		Database db = new Database();
		 pmName=db.getPMName(vmid);
		try {  
			conn = new Connect("qemu+ssh://"+pmName+"/system");
			Domain dom=conn.domainLookupByID(Integer.parseInt(vmid));
			//successFlag=
				dom.destroy();
				conn.close();
				db.removeVMInfo(vmid);
		} catch (LibvirtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "1";
	}
	
	public void vmTypes(){
		String vmFile="/home/pradeep/Dropbox/MTech/Monsoon2015/Cloud/MiniProject/sample.txt";
		try (BufferedReader br = new BufferedReader(new FileReader(vmFile))) {
			   String line = null;
			   while ((line = br.readLine()) != null) {
			       System.out.println(line);
			   }
			}catch(Exception e){
				
			}
	}
    public static void main(String[] args) {
        Connect conn=null;
      
        try{
        	conn= new Connect("qemu+ssh://"+"pradeep@10.2.56.244/system");
           // conn = new Connect("qemu:///system", false);
            System.out.println(" hname "+conn.getHostName());
        } catch (LibvirtException e) {
            System.out.println("exception caught:"+e);
            System.out.println(e.getError());
        }
        
      try{ 
        
   	 
        System.out.println("no of domains "+conn.numOfDomains());
        System.out.println( conn.getHypervisorVersion(null));
        String dm2=" <domain type='qemu' id='3'> <name>'jafarkhan'</name> "
        		+ "<uuid> 4dea22b31d52d8f32516782e98ab3fa9</uuid> "
        		+ "<memory unit='KiB'>512</memory> "
        		+ "<os><type arch='x86_64' machine='pc-0.11'>hvm</type> </os>"
        		//+ "<cpu><topology sockets='1' cores='1' threads='1'/></cpu>+
        		+"</domain>";
        String dm3=" <domain type='kvm' id='3'> <name>'fv0'</name> <uuid> 4dea22b31d52d8f32516782e98ab3fa9</uuid> <memory unit='KiB'>512</memory> <os><type arch='x86_64' machine='pc-i440fx-trusty'>hvm</type> </os></domain>";
        String dm="<domain type='qemu' id='3'>   <name>fv0</name>"
        		+ "  <uuid>4dea22b31d52d8f32516782e98ab3fa0</uuid>"  
        		+ "  <memory unit='KiB'>524288</memory> "
        		//+ " <currentMemory unit='KiB'>524288</currentMemory> "
        		+ " <os> <type>hvm</type></os> "
       +" <cpu>  <topology sockets='1' cores='2' threads='1'/></cpu>"        	
+"</domain>";
        int retval=0;
        conn.domainCreateLinux(dm3, retval);
            Domain testDomain=conn.domainLookupByName("fv0");
            
            System.out.println("Domain:" + testDomain.getName() + " id " +
                               testDomain.getID() + " running " +
                               testDomain.getOSType());
            //testDomain.destroy();
       } catch (LibvirtException e) {
            System.out.println("exception caught:"+e);
            System.out.println(e.getError());
       }
    }
}