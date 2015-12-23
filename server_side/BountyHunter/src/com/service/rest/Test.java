package com.service.rest;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
//org.apache.http.entity.mime.MultipartEntity;


public class Test {
	public static void main(String args[]) throws MalformedURLException, IOException {
		String urlToConnect = "http://localhost:8080/BountyHunter/FileUploader?userId=1&fileName=aa.jpg";
		File fileToUpload = new File("/Users/trunksexy/Pictures/cat.jpg");
		String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.

		URLConnection connection = new URL(urlToConnect).openConnection();
		connection.setDoOutput(true); // This sets request method to POST.
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		PrintWriter writer = null;
		DataOutputStream dout = null;
		try {
			dout = new DataOutputStream(connection.getOutputStream());
		    /*writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
		    writer.println("Content-Disposition: form-data; name=\"picture\"; filename=\"bla.jpg\"");
		    writer.println("Content-Type: image/jpeg");
		    //writer.println();
		    BufferedReader reader = null;*/
		    try {
		    	InputStream inputStream = new FileInputStream("/Users/trunksexy/Pictures/cat.jpg");
		    	byte[] bytes = new byte[1024];
		    	int read = 0;
		    	while ((read = inputStream.read(bytes)) != -1) {
		    		dout.write(bytes);
					//outputStream.write(bytes, 0, read);
				}
		        /*reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToUpload)));
		        for (String line; (line = reader.readLine()) != null;) {
		            writer.print(line);
		        }*/
		    } finally {
		    	
		        if (dout != null) try { dout.close(); } catch (IOException logOrIgnore) {}
		    }
		    //writer.println("--" + boundary + "--");
		} finally {
		    if (writer != null) writer.close();
		}

		// Connection is lazily executed whenever you request any status.
		int responseCode = ((HttpURLConnection) connection).getResponseCode();
		System.out.println(responseCode); // Should be 200
	}
	
	
	
	/*public static void main(String args[]) throws Exception
    {
        Test fileUpload = new Test () ;
        File file = new File("/Users/trunksexy/Pictures/cat.jpg") ;
        //Upload the file
        fileUpload.executeMultiPartRequest("http://localhost:8080/BountyHunter/profile/image-upload",
                file, file.getName(), "File Uploaded :: cat.jpg") ;
    } 
     
    public void executeMultiPartRequest(String urlString, File file, String fileName, String fileDescription) throws Exception
    {
        HttpClient client = new DefaultHttpClient() ;
        HttpPost postRequest = new HttpPost (urlString) ;
        try
        {
            //Set various attributes
            MultipartEntity multiPartEntity = new MultipartEntity () ;
            multiPartEntity.addPart("fileDescription", new StringBody(fileDescription != null ? fileDescription : "")) ;
            multiPartEntity.addPart("fileName", new StringBody(fileName != null ? fileName : file.getName())) ;
  
            FileBody fileBody = new FileBody(file, "application/octect-stream") ;
            //Prepare payload
            multiPartEntity.addPart("attachment", fileBody) ;
  
            //Set to request body
            postRequest.setEntity(multiPartEntity);
            postRequest.setHeader("Content-Type", "multipart/form-data");
             
            //Send request
            HttpResponse response = client.execute(postRequest) ;
             
            //Verify response if any
            if (response != null)
            {
                System.out.println(response.getStatusLine().getStatusCode());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace() ;
        }
    }*/
}