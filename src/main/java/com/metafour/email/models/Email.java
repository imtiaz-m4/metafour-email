package com.metafour.email.models;

import java.util.List;

import com.metafour.util.StringsM4;

public class Email {

	private String from;
	private String replyTo;
	private String to;
	private String cc;
	private String bcc;
	private String subject;
	private String details;

	private List<String> files;

	public Email() {
		super();
	}
	
	public Email(String to, String subject, String details) {
		super();
		this.to = to;
		this.subject = subject;
		this.details = details;
	}
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		if (StringsM4.isNotBlank(to) && to.contains(";")){
			to = to.replaceAll(";", ",");
		}
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}

	public String getCc() {
		if (StringsM4.isNotBlank(cc) && cc.contains(";")){
			cc = cc.replaceAll(";", ",");
		}
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		if (StringsM4.isNotBlank(bcc) && bcc.contains(";")){
			bcc = bcc.replaceAll(";", ",");
		}
		return bcc;
	}
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return details;
	}

	public void setBody(String body) {
		this.details = body;
	}

	public String getReplyTo() {
		if (StringsM4.isNotBlank(replyTo) && replyTo.contains(";")){
			replyTo = replyTo.replaceAll(";", ",");
		}
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}
	
	@Override
	public String toString() {
		return "Email [from=" + from + ", replyTo=" + replyTo + ", to=" + to + ", cc=" + cc + ", bcc=" + bcc
				+ ", subject=" + subject + ", details=" + details + ", attachedFiles=" + files + "]";
	}


}
