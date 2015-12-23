package com.bounty.utils;

import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.bounty.utils.taskagent.TaskPool;

public class CredentialManager {

	private AWSCredentials credentials = null;
	private static CredentialManager credentialMngr = null;
	
	public static CredentialManager getInstance() {
		if (credentialMngr == null)
			credentialMngr = new CredentialManager();
		return credentialMngr;
	}
	
	public AWSCredentials getCredential() {
		return credentials;
	};
	
	private CredentialManager() {
		try {
			this.credentials = new PropertiesCredentials(TaskPool.class.getResourceAsStream("/AwsCredentials.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
