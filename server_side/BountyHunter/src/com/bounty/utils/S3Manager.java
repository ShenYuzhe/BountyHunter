package com.bounty.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

public class S3Manager {
	public static S3Manager s3Manager = null;
	private AmazonS3Client s3client;
	
	private String bucketName = "bountyhunter2015";
	private String defaultPath = "default.jpeg";
	//private String keyName        = "aaa";
	//private String uploadFileName = "/Users/trunksexy/cat.jpg";
	
	private S3Manager() {
		 s3client = new AmazonS3Client(CredentialManager.getInstance().getCredential());
	}
	
	public static S3Manager getInstance() {
		if (s3Manager == null)
			s3Manager = new S3Manager();
		return s3Manager;
	}
	
	public String uploadFile(String filePath, InputStream ifstream, String contentType) {
		String url = null;
		try {
            System.out.println("Uploading a new object to S3 from a file\n");
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType(contentType);
            PutObjectRequest p = new PutObjectRequest(bucketName, filePath, ifstream, meta);
            PutObjectResult result = s3client.putObject(p.withCannedAcl(CannedAccessControlList.PublicRead));

         } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
            		"means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
            		"means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
		return url;
	}
	
	public String getUserFileUrl(String filePath) {
		return s3client.getResourceUrl(bucketName, filePath);
	}
	
	
	public String getDefaultUrl() {
		return s3client.getResourceUrl(bucketName, defaultPath);
	}
	
	public static void main(String[] args) throws IOException {
		
        
    }
}
