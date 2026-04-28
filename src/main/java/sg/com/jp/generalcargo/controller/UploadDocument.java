package sg.com.jp.generalcargo.controller;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.Constants;

@Component
public class UploadDocument {

	private static final Log log = LogFactory.getLog(UploadDocument.class);
	
	private static String edi;
	private static String ediDownload;
	
	@Value("${MiscApp.file.upload.path}")
    public void setEdi(String Edi) {
		edi = Edi;
    }
	
	@Value("${MiscApp.file.download.path}")
    public void setEdiDownload(String EdiDownload) {
		ediDownload = EdiDownload;
    }
	
	
	 //read file content
		public static byte[] getFileContent(String fileName) throws IOException {
		    log.info("fileName in  getFileContent " + CommonUtility.deNull(fileName));
		    byte[] fileContent = null;
		    InputStream inputStream = null;
		    DataInputStream dis = null;
		    try {
		    	inputStream = new FileInputStream(new File(fileName));
				dis = new DataInputStream(inputStream);
				fileContent = new byte[dis.available ()]; 
				dis.readFully (fileContent); 
				log.info("myFile size... " + fileContent.length);
		    } catch (IOException be) {
		        log.info("IOException getFileContent: " + be.getMessage());
		    } finally {
		    	if(inputStream != null) {
		    		inputStream.close(); 
		    	}
		    	
		    	if(dis != null) {
		    		dis.close();
		    	}

		    }
		    
		    return fileContent;
		}


		public static void writeToFile(byte[] fileContent, String toFileName, String subDir) {
		    log.info("Starting writeToFile ...");
		    log.info("toFileName ..." + CommonUtility.deNull(toFileName) +" fileContent:"+fileContent +" subDir:"+ CommonUtility.deNull(subDir));
		    try {
		        String path = getOutputFileDir(subDir, "upload");
		        log.info("path ..." + path);
		        if (createPath(path)) {    
		            File file = new File(path, toFileName);
		    		FileOutputStream fos = new FileOutputStream(file);
		    		fos.write (fileContent, 0, fileContent.length); 
		    		
		    		PrintStream out = new PrintStream(fos, true);			
		    		out.write(fileContent, 0, fileContent.length);
		    		
		    		out.close();
		    		log.info("After wrote file....");
		        }
		    } catch (Exception exp) {
		        log.info("Unexpected Exception writeToFile");
		    }
		}    

		private static boolean createPath(String path) {
			log.info("START: createPath "+" path:"+ CommonUtility.deNull(path) );
		    File fp=null;
		    try {
		        fp = new File (path);
		        if ( !fp.exists() )
		            fp.mkdir();
		        return true;
		    } catch (Exception exp) {
				log.info("Exception: createPath " , exp);
		    } finally {
		        fp=null;
		    }
		    return false;
		}

		public static String getOutputFileDir(String subDir, String type) {
			log.info("START: getOutputFileDir "+" subDir:"+ CommonUtility.deNull(subDir) +" type:"+ CommonUtility.deNull(type));
			String dirPath = "";
		 if(type.equalsIgnoreCase("upload")) {
			 dirPath = edi;
		 } else {
			 dirPath = ediDownload;
		 }
		    String dir = null;
		    if("CONTRACT".equals(subDir))
		        dir =Constants.MiscAppUpload_ContractDir;
		    else if("MACHINE".equals(subDir))
		        dir = Constants.MiscAppUpload_MacDir;
		    
		    log.info("END: *** getOutputFileDir Result *****" +" dirPath:" + CommonUtility.deNull(dirPath) 
		    +" dir:"+ CommonUtility.deNull(dir));

		    return dirPath + dir + "/";
		    //return edi + dir + "\\";
		}
		
		public static void deleteFile(String fileName, String subDir) {
			log.info("START: deleteFile "+" subDir:"+ CommonUtility.deNull(subDir) +" fileName:"+ CommonUtility.deNull(fileName));
		    log.info("fileName in deleteFile ---> " + CommonUtility.deNull(fileName));

		    // A File object to represent the filename
		    File f = new File(fileName);
		
		    // Make sure the file or directory exists and isn't write protected
		    if (!f.exists())
		    throw new IllegalArgumentException(
		        "Delete: no such file or directory: " + fileName);
		
		    if (!f.canWrite())
		    	throw new IllegalArgumentException("Delete: write protected: "
		            + fileName);
		
		    // If it is a directory, make sure it is empty
			if (f.isDirectory()) {
			    String[] files = f.list();
			    if (files.length > 0)
			      throw new IllegalArgumentException(
			          "Delete: directory not empty: " + fileName);
			}
		
			// Attempt to delete it
		    boolean success = f.delete();
		
		    if (!success)
		        throw new IllegalArgumentException("Delete: deletion failed");
		}
		
		/*public static byte[] getContent(HttpServletRequest req){
		    log.info("getContent ---> ");
			byte[] fileContent = null;
			try{
				FileUpload fup=new FileUpload();
				boolean isMultipart = FileUpload.isMultipartContent(req);
				log.info("isMultipart ---> " + isMultipart);
				
				// Create a new file upload handler
				DiskFileUpload upload = new DiskFileUpload();

				// Parse the request
				List items = upload.parseRequest(req);
				log.info("items ---> " + items);
				Iterator iter = items.iterator();
				while (iter.hasNext()) {
					FileItem item = (FileItem) iter.next();
					if (item.isFormField()) {
						log.info("its a field");
					} else {
						log.info("its a file");
						log.info(item.getName());
						fileContent = item.get();
					}
				}
			}catch(Exception e){
			    log.info(e);
			}
			return fileContent;
		}*/
		
	    public static void deleteFile(String uploadPath) {
	       log.info("Delete File : " + CommonUtility.deNull(uploadPath));
	        File temp=null;
	        try {
	            temp=new File(FilenameUtils.normalize(uploadPath));
	            if (temp.exists() && temp.isFile()) {
	                temp.delete();
	               log.info("File delete ");
	            }
	        } catch (Exception exp) {
	            log.info("Error while deleting file");
	        }
	    }	
}
