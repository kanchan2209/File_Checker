package osproject1;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class fsck1 {
  private static final Integer Integer = null;
public static File folder = new File("C:/Users/kanch/workspace/osproject1/FS");
  static String temp = "";
  static long latestTime = System.currentTimeMillis()/1000;
  //Path of the folder where all files are saved
  static String folderpath= "C:/Users/kanch/workspace/osproject1/FS/";
  
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    System.out.println("Reading files under the folder "+ folder.getAbsolutePath());
    deviceIdCheck();
    timeCheck(folder);
    freeBlockListCheck(folder);
    noFilesInFreeBlockListCheck(folder);
    indirectCheck(folder);
    sizeAndindirectCheck(folder);
    blockNumberCheck(folder);
  }
//Checking if device Id is correct
  public static void deviceIdCheck(){
	  System.out.println("Question 1");
	  JSONParser parser= new JSONParser();
	  Object obj="";
		try {
			obj = parser.parse(new FileReader(folderpath+"fusedata.0"));
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jsonObject=(JSONObject) obj;
		Long deviceid=(Long) jsonObject.get("devId");
		Long freeEnd=(Long) jsonObject.get("freeEnd");
		Long freeStart=(Long) jsonObject.get("freeStart");
		if(deviceid==20)
		{
			jsonObject.put("devId",20);
			System.out.println("Device Id is Correct and is equal to " + deviceid);
			try {
//Correcting the device Id
				FileWriter file = new FileWriter(folderpath+"fusedata.0");
				file.write(obj.toString());
				file.flush();
				file.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {

			System.out.println("Device Id is Incorrect");
		}
		}
 //Checking Time(creation time, ctime,atime,mtime are in past
public static void timeCheck(final File folder) {
	 System.out.println("Question 2");
	JSONParser parser= new JSONParser();
	Object fileTimeCheck="" ;
    for (final File fileEntry : folder.listFiles()) {
      if (fileEntry.isDirectory()) {
        // System.out.println("Reading files under the folder "+folder.getAbsolutePath());
    	  timeCheck(fileEntry);
        
      } else {
        if (fileEntry.isFile()) {
          temp = fileEntry.getName();
          Object filepathObj= fileEntry.getAbsoluteFile();
          String filepath=filepathObj.toString();
          try {
        	 
        	fileTimeCheck = parser.parse(new FileReader(filepath));
        	String data=fileTimeCheck.toString();
        	
        	if (data.contains("{"))
        	{
        		JSONObject jsonObject1=(JSONObject) fileTimeCheck;
              	System.out.println(filepath);
          		System.out.println(fileTimeCheck);
          		Long creationTime1=(Long) jsonObject1.get("creationTime");
          		Long aTime1=(Long) jsonObject1.get("atime");
          		Long cTime1=(Long) jsonObject1.get("ctime");
          		Long mTime1=(Long) jsonObject1.get("mtime");
        		
        		if (creationTime1!=null)
        		{
        			if(latestTime < creationTime1)
        			{
        				System.out.println("Time is in future");
        			}
        			else
        			{
        				System.out.println("creationTime is: " + creationTime1);
        			}
        		}
        		if (aTime1!=null)
        		{
        			if(latestTime < aTime1)
        			{
        				System.out.println("Time is in future");
        				
        			}
        			else
        			{
        				System.out.println("aTime is: " + aTime1);
        			}
        		}
        		if (cTime1!=null)
        		{
        			if(latestTime < cTime1)
        			{
        				System.out.println("Time is in future");
        			}
        			else
        			{
        				System.out.println("cTime is: " + cTime1);
        			}
        		}
        		if (mTime1!=null)
        		{
        			if(latestTime < mTime1)
        			{
        				System.out.println("Time is in future");
        			}
        			else
        			{
        				System.out.println("mTime is: " + mTime1);
        			}
        		}
          	}
        	
} catch (Exception e) {
  			
          
        }
      }
    }
  }
 }
//Checking free Block List is accurate
public static void freeBlockListCheck(final File folder){
	 System.out.println("Question 3a");
		JSONParser parser= new JSONParser();
		Object fileFreeBlockCheck;
		Object freeEndAndStartValue="";
		Long l=(long) 31;
		
		try{
		freeEndAndStartValue= parser.parse(new FileReader((folderpath+"fusedata.0")));
			JSONObject jsonObject=(JSONObject) freeEndAndStartValue;
			Long freeEnd=(Long) jsonObject.get("freeEnd");
			Long freeStart=(Long) jsonObject.get("freeStart");
			
			for (Long i=freeStart;i <=freeEnd; i++) {
				
	  			fileFreeBlockCheck = parser.parse(new FileReader(folderpath+"fusedata."+new Long(i)));
	  			JSONArray jsonArray = (JSONArray) fileFreeBlockCheck;
	  
	  			for (int k=0;k <jsonArray.size();)
	  			{	  				
	  				
	  				Object m= ( jsonArray).get(k);
	  				//Removing values > 10000
	  				if(((Long) m).longValue()> 10000)
	  				{
	  					System.out.println("Removing the value as its greater than maximum block size allowed, value removed is" +jsonArray.remove(k));
	  					jsonArray.remove(k);
	  				}
	  				//checking for missing values
	  				while(((Long) m).longValue()!=(l.longValue())&&(l <(i*400))&&((Long) l).longValue()<((Long) m).longValue()){
	  					Long missingFileNumber= i;
	  					System.out.println("missing l value = " +l+" in file fusedata." + i);
	  					//Adding missing values in required file
	  				    jsonArray.add(l);
	  					FileWriter file1 = new FileWriter((folderpath+"fusedata." +new Long(i)).toString());
	  					file1.write(jsonArray.toString());
	  					
	  					file1.flush();
	  					file1.close();
	  					l++;
	  					
	  				}

	  					l++;
	  					k++;
	  				
	  				
	  			}
	  			
			}
		
			
		}
		
		catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
// Checking Accuracy of Free Block List
public static void noFilesInFreeBlockListCheck(final File folder){
	 System.out.println("Question 3b");
	for (final File fileEntry : folder.listFiles()) {
        if (fileEntry.isDirectory()) {
        	noFilesInFreeBlockListCheck(fileEntry);
        } else {
        	String filenames=fileEntry.getName();
            //System.out.println(fileEntry.getName());
            String extensionRemoved = (filenames.split("\\.")[1]);
            Integer num = Integer.parseInt(extensionRemoved);
            if (num.intValue()>30)
            {
            System.out.println("File System is Incorrect");
            }
        }
    }
	System.out.println("File System is CORRECT and there are no files/directories stored on items listed in the free blocks");
   }
//Checking if Indirect is 1 data in block is pointed by location pointer in the array
public static void indirectCheck(final File folder){
	 System.out.println("Question 4");
	JSONParser parser= new JSONParser();
	Object fileindirectCheck="" ;
	Object fileWithArrayCheck="";
    for (final File fileEntry : folder.listFiles()) {
      if (fileEntry.isDirectory()) {
        // System.out.println("Reading files under the folder "+folder.getAbsolutePath());
    	  indirectCheck(fileEntry);
        
      } else {
        if (fileEntry.isFile()) {
          temp = fileEntry.getName();
          Object filepathObj= fileEntry.getAbsoluteFile();
          String filepath=filepathObj.toString();
          Integer blockSize= ( Integer)4096;
          
        	 
        	try {
				fileindirectCheck = parser.parse(new FileReader(filepath));
				String data1=fileindirectCheck.toString();
	        	if (data1.contains("indirect"))
	        	{
	        		JSONObject jsonObject2=(JSONObject) fileindirectCheck;
	        		Long indirectValue=(Long) jsonObject2.get("indirect");
	        		Long locationPoniter=(Long) jsonObject2.get("location");
	              	System.out.println("IndirectValue is in " +filepath + " "
	              			+ " with value "+ indirectValue + " and Location Poniter as " + locationPoniter + "." );
	              	String data2=fileWithArrayCheck.toString();
	              	//Validating the data in the block pointed by location pointer is an array 
	              	if(!(data2.contains("{") ))
	              	{
	              		System.out.println("The data in the block pointed by location pointer " + locationPoniter + " IS AN ARRAY." );
	              	}
	              	else 
	              	{
	              		System.out.println("The data in the block pointed by location pointer " + locationPoniter + "IS NOT AN ARRAY" );
	              	}
	              	//Validating 3 possibilities
	              	// Indirect=0, 0 <File Size < Block Size (4096)

	              	}
	        	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} 
           }
          }
         }
        }
//Checking Three Possibilities on Size, indirect and Length of Location Array values
public static void sizeAndindirectCheck(final File folder){
	 System.out.println("Question 5");
	JSONParser parser= new JSONParser();
	Object fileindirectCheck="" ;
	Object fileWithArrayCheck="";
    for (final File fileEntry : folder.listFiles()) {
      if (fileEntry.isDirectory()) {
        // System.out.println("Reading files under the folder "+folder.getAbsolutePath());
    	  indirectCheck(fileEntry);
        
      } else {
        if (fileEntry.isFile()) {
          temp = fileEntry.getName();
          Object filepathObj= fileEntry.getAbsoluteFile();
          String filepath=filepathObj.toString();
          Integer blockSize= ( Integer)4096;
          
        	 
        	try {
				fileindirectCheck = parser.parse(new FileReader(filepath));
				String data1=fileindirectCheck.toString();
	        	if (data1.contains("indirect"))
	        	{
	        		JSONObject jsonObject2=(JSONObject) fileindirectCheck;
	        		Long indirectValue=(Long) jsonObject2.get("indirect");
	        		Long locationPoniter=(Long) jsonObject2.get("location");
	        		Long size=(Long) jsonObject2.get("location");
	        		fileWithArrayCheck = parser.parse(new FileReader(folderpath+"fusedata."+new Long(locationPoniter)));
		  			JSONArray jsonArray = (JSONArray) fileWithArrayCheck;
		  			int ArraySize =jsonArray.size();
		  			// Block Size Multiplied with Length of Array
		  			Long bsMulArrLen= (long) (blockSize * ArraySize);
		  			Long bsMulArrLen1= (long) (blockSize * (ArraySize-1));
	              	 File file = new File(folderpath+"fusedata." + (jsonArray).get(0).toString()  );
	                 System.out.println("File Length " + file.length());
	              	String data2=fileWithArrayCheck.toString();
	              	//Validating the data in the block pointed by location pointer is an array 
	              	if(!(data2.contains("{") ))
	              	{
	              	//Validating 3 possibilities
	              	// Indirect=0, 0 <File Size < Block Size (4096) and Value of Size= File Size
	              	if ((indirectValue==0)&&(file.length()< blockSize.intValue()&&(size>file.length()&&(size>0))))
	              	{
	              		System.out.println("Indirect Value Is Correct");
	              		
	              	}
	               // Indirect!=0, File Size < Block Size * length of location array
	              	if ((indirectValue!=0)&&(size < blockSize)&& (ArraySize==1)&& (size < bsMulArrLen))
	              	{
	              		System.out.println("INDIRECT VALUE is INCORRECT  therefore correcting the value ...");
	              		jsonObject2.put("indirect",0);
	    				FileWriter file1 = new FileWriter(filepath);
	    				file1.write(fileindirectCheck.toString());
	    				file1.flush();
	    				file1.close();
	              	}
	              	if ((indirectValue!=0)&&(size < blockSize)&& (ArraySize>1)&& (size > bsMulArrLen1))
	              	{
	              		System.out.println("INDIRECT VALUE is CORRECT ");

	              	}
	              }
	        	}
	        	
			} catch (Exception e) {

			} 
           }
          }
         }
        }
public static void blockNumberCheck(final File folder)
 	{
	 System.out.println("Question 6");

	JSONParser parser= new JSONParser();
	Object fileblockNumberCheck="" ;
	Object rootFile="";
	
    for (final File fileEntry : folder.listFiles()) {
      if (fileEntry.isDirectory()) {
        // System.out.println("Reading files under the folder "+folder.getAbsolutePath());
    	  blockNumberCheck(fileEntry);
        
      } else {
        if (fileEntry.isFile()) {
          
          Object filepathObj= fileEntry.getAbsoluteFile();
          String filepath=filepathObj.toString();

          
        	 
        	try {
        		fileblockNumberCheck = parser.parse(new FileReader(filepath));
				String data1=fileblockNumberCheck.toString();
				JSONObject y=(JSONObject)fileblockNumberCheck ;
				rootFile = parser.parse(new FileReader(folderpath+"fusedata.0"));
				JSONObject rootObject=(JSONObject)rootFile ;
				Long rootId=(Long)rootObject.get("root");
				//JSONArray listOfStates=(JSONArray)fileblockNumberCheck ;
				if (data1.contains("filename_to_inode_dict"))
	        	  {
	        		temp = fileEntry.getName();
	        		System.out.println(temp);
	        		 String extensionRemoved = (temp.split("\\.")[1]);
	                 Long num = Long.parseLong(extensionRemoved);
	        		JSONArray listOfStates =(JSONArray)y.get("filename_to_inode_dict");
	        		
	        		Iterator<?> iterator = listOfStates.iterator();
	        		while (iterator.hasNext()){
	        			JSONObject innerobj=(JSONObject) iterator.next();
	        			String type=(String) innerobj.get("name");
	        			
	        			if (num.equals(rootId))
	        			{
	        				if (type.equals("."))
	        				{
	        					Long currentDirectory1=(Long) innerobj.get("location");        					
	        					if (currentDirectory1==26)
	        					{
	        						System.out.println(" As this is a root CurrentDirectory is " + currentDirectory1 + " is correct");
	        					}
	        					else 
	        					{
	        						System.out.println(" As this is a root CurrentDirectory is " + currentDirectory1 + " is Incorrect");
	        					}
	        				}
	        				if (type.startsWith(".."))
	        				{
	        					Long parentDirectory1=(Long) innerobj.get("location");
	        					if (parentDirectory1==rootId)
	        					{
	        
	        						System.out.println(" As this is a root parentDirectory is " + parentDirectory1 + " is correct");
	        						innerobj.put("location",26);
	        						FileWriter file = new FileWriter(folderpath+"fusedata.26");
	        						file.write(y.toString());
	        						file.flush();
	        						file.close();
	        					}
	        					else 
	        					{
	        						System.out.println(" As this is a root parentDirectory is " + parentDirectory1 + " is Incorrect");
	        						innerobj.put("location",rootId);
	        						FileWriter file = new FileWriter(folderpath+"fusedata." +new Long(num));
	        						file.write(y.toString());
	        						file.flush();
	        						file.close();
	        					}
	        				}
	        			}
	        				
	        				
	        			if (!(num.equals(rootId)))
		        			{
		        				if (type.equals("."))
		        				{
		        					Long currentDirectory2=(Long) innerobj.get("location");        					
		        					if (currentDirectory2==num)
		        					{
		        						System.out.println(" As this is a root CurrentDirectory is " + currentDirectory2 + " is correct");
		        					}
		        					else 
		        					{
		        						System.out.println(" As this is a root CurrentDirectory is " + currentDirectory2 + " is Incorrect");
		        						innerobj.put("location",num);
		        						FileWriter file = new FileWriter(folderpath+"fusedata."+new Long(num));
		        						file.write(y.toString());
		        						file.flush();
		        						file.close();
		        					}
		        				}
		        				if (type.startsWith(".."))
		        				{
		        					Long parentDirectory2=(Long) innerobj.get("location");
		        					if (parentDirectory2==rootId)
		        					{
		        						System.out.println(" As this is a root parentDirectory is " + parentDirectory2 + " is correct");
		        					}
		        					else 
		        					{
		        						System.out.println(" As this is a root parentDirectory is " + parentDirectory2 + " is Incorrect");
		        						innerobj.put("location",26);
		        						FileWriter file = new FileWriter(folderpath+"fusedata.26");
		        						file.write(y.toString());
		        						file.flush();
		        						file.close();
		        					}
		        				}

	        			
	        			
		        			}
	        			}
	        		}
	        		

	        		
	        	
	        	  }
        	   
        	catch (Exception e) {

        	}
           }
          }
         }
        }
}


	  			
	  			
		
		
		
		
		
		
		
		  		
	
	    
       
      
