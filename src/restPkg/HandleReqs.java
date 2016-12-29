package restPkg;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.libvirt.Connect;
import org.libvirt.LibvirtException;

import vmPkg.DomainCtrl;

@Path("/vm")
public class HandleReqs {
	
	
@GET
@Path("/create")
 public	String retStr(@QueryParam("name") String vmName,@QueryParam("instance_type") String instType ){ 
		DomainCtrl dctrl = new DomainCtrl();
		  dctrl.createDomain(vmName, Integer.parseInt(instType));
		return "Creating VM "+vmName+" of type "+instType;
	}

@GET
@Path("/query")
public String retVMDetails(@QueryParam("vmid") String vmid){
	//vmid is assumed to be uid
	DomainCtrl dctrl = new DomainCtrl();
	return dctrl.getVMInfo(vmid);
}

@GET
@Path("/destroy")
public String destroyVM(@QueryParam("vmid") String vmid){
	DomainCtrl dctrl = new DomainCtrl();
	return dctrl.destroyDomain(vmid);
}

@GET
@Path("/types")
public String vmTypes(){
	//DomainCtrl dctrl = new DomainCtrl();
	//dctrl.vmTypes();
	String vmFile="/home/pradeep/Dropbox/MTech/Monsoon2015/Cloud/MiniProject/sample.txt";
	  String line ="";
	  String vmTypes="";
	  System.out.println("Printing the available vm types info");
	try (BufferedReader br = new BufferedReader(new FileReader(vmFile))) {
		 
		   while ((line = br.readLine()) != null) {
		      // System.out.println(line);
		       	vmTypes = vmTypes + line + "\n";		   
		   }
		}catch(Exception e){
			
		}

	return vmTypes;
}
}
