package restPkg;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import vmPkg.DomainCtrl;

@Path("/pm")
public class HandlePMRequests {
	
@GET
@Path("/list")
public String getPMList(){

	String pmFile="/home/pradeep/Dropbox/MTech/Monsoon2015/Cloud/MiniProject/pm_file";
	  String line ="";
	  String pms="";
	  System.out.println("Printing the available pms info");
	try (BufferedReader br = new BufferedReader(new FileReader(pmFile))) {
		 
		   while ((line = br.readLine()) != null) {
		      // System.out.println(line);
		       	pms = pms + line + "\n";		   
		   }
		}catch(Exception e){
			
		}

	return pms;
	}

@GET
@Path("/listvms")
public String retVMDetails(@QueryParam("pmid") String vmid){
	//vmid is assumed to be uid
	DomainCtrl dctrl = new DomainCtrl();
	return dctrl.getVMsOfPM(vmid);
}

@GET
@Path("/query")
@Produces({ MediaType.APPLICATION_JSON })
public String getPMInfo(@QueryParam("pmid") String pmid){
	DomainCtrl dctrl = new DomainCtrl();
	return dctrl.getPMInfo(pmid);
}


@GET
@Path("/test")
@Produces({ MediaType.APPLICATION_JSON })
public String test(){
	 JSONParser parser = new JSONParser();
	 JSONObject jsonObject=null;
     try {	 

         Object obj = parser.parse(new FileReader("/home/pradeep/Dropbox/MTech/Monsoon2015/Cloud/MiniProject/sample.txt"));	 
          jsonObject = (JSONObject) obj;	 
     }catch(Exception e){
    	 
     }
	return jsonObject.toJSONString();
}
}
