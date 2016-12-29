package jsonPkg;

import java.io.FileReader;
import java.util.*;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import dbPkg.Database;
 
public class JsonCtrl {
	
	public HashMap<String,String> getVmHm(String vmName,int instType){
		String cpu=null,ram = null,disk=null;
		  String xml = " ";
	        JSONParser parser = new JSONParser();
	        try {	 

	            Object obj = parser.parse(new FileReader("/home/pradeep/Dropbox/MTech/Monsoon2015/Cloud/MiniProject/sample.txt"));	 
	            JSONObject jsonObject = (JSONObject) obj;	 
	            
	            JSONArray vmTypes = (JSONArray) jsonObject.get("types");	
	            Iterator<JSONObject> iterator = vmTypes.iterator();
	           

	            while (iterator.hasNext()) {
	            	JSONObject vm = iterator.next();
	            	if (vm.get("tid").toString().equalsIgnoreCase(""+instType)){
	                System.out.println(vm.get("ram"));
	                System.out.println(vm.get("cpu"));
	            		
	            		cpu=vm.get("cpu").toString();
	    	            ram= vm.get("ram").toString();
	    	            disk= vm.get("disk").toString();
	                	break;
	            	}
	            }
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	        }    
	       // UUID uniqueKey = UUID.randomUUID(); 
	        //get the max id from the vm table
	        Database db = new Database();
	        int uid = db.getUid();
	        uid +=1;
	        
	        	xml  += "<domain type='kvm' id='"+uid+"'> ";
	        	xml  += "<name>"+vmName+"</name> ";
	        	//xml  += "<uuid> 4dea22b31d52d8f32516782e98ab3fa9</uuid> ";
	        	xml  += "<memory unit='KiB'>"+ram+"</memory>";
	        	xml  +=  " <os><type arch='x86_64' machine='pc-i440fx-trusty'>hvm</type> </os>";
	        	xml  += "<cpu><topology sockets='1' cores='"+cpu+"' threads='1'/></cpu>";
	        	//xml  += "<disk>" +disk+"</disk>";   
	        	xml += "</domain>";
	       System.out.println(xml);
	       HashMap<String,String> hm = new HashMap<String,String>();
	       hm.put("uid", uid+"");
	
	       hm.put("ram", ram);
	       hm.put("cpu", cpu);
	       hm.put("disk",disk);
	       hm.put("xml", xml);
	       return hm;
	}
}
