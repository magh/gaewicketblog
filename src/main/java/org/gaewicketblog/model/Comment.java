package org.gaewicketblog.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Comment implements Serializable {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	@Persistent
	private Long parentid;

	@Persistent
	private String subject;

	@Persistent
	private Text text;

	@Persistent
	private String author;

	@Persistent
	private Date date;

	@Persistent
	private String ipaddress;

	@Persistent
	private Boolean hidden;

//	@Persistent
//	@Element(dependent = "true")
//	private List<Comment> comments;

	public Comment(long parentid, String subject, Text text,
			String author, String ipaddress) {
		this.parentid = parentid;
		this.subject = subject;
		this.text = text;
		this.author = author;
		date = new Date();
		this.ipaddress = ipaddress;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nid="+id);
		sb.append("\npid="+parentid);
		sb.append("\nipaddress="+ipaddress);
		sb.append("\ndate="+date);
		sb.append("\nauthor="+author);
		sb.append("\nsubject="+subject);
		sb.append("\ntext="+text);
		return sb.toString();
	}

	public Long getParentid() {
		return parentid;
	}

	public void setParentid(Long parentid) {
		this.parentid = parentid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

}
