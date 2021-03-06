package com.palm.mail.mail.auth;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
/**
 * 
 * @author weixiang.qin
 *
 */
public class MailAuthenticator extends Authenticator {
	private String userName;
	private String password;

	public MailAuthenticator() {
	}

	public MailAuthenticator(String username, String password) {
		this.userName = username;
		this.password = password;
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(userName, password);
	}
}
