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
	
	/** Status of post. */
	public final static int STATUS_NOSTATUS = 0;
	public final static int STATUS_OPEN_UNDERREVIEW = 1;
	public final static int STATUS_OPEN_PLANNED = 2;
	public final static int STATUS_OPEN_STARTED = 3;
	public final static int STATUS_OPEN_NEEDSINFO = 4;
	public final static int STATUS_CLOSED_COMPLETED = 5;
	public final static int STATUS_CLOSED_DECLINED = 6;
	public final static int STATUS_CLOSED_DUPLICATE = 7;

	/** Type of post. */
	public final static int TYPE_NOTYPE = 0;
	public final static int TYPE_ENHANCEMENT = 1;
	public final static int TYPE_DEFECT = 2;

	/** Post id. */
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	/** Topic id or parent post id. */
	@Persistent
	private Long parentid;

	/** Post subject. */
	@Persistent
	private String subject;

	/** Post content. */
	@Persistent
	private Text text;

	/** Post author. */
	@Persistent
	private String author;

	/** Post date. */
	@Persistent
	private Date date;

	/** Posters ip address. */
	@Persistent
	private String ipaddress;

	/** ? */
	@Persistent
	private Boolean hidden;

	/** RESTful link. */
	@Persistent
	private String link;

	/** Post/Issue status. */
	@Persistent
	private Integer status;

	/** User votes. */
	@Persistent
	private Integer votes;

	/** Type of post. */
	@Persistent
	private Integer type;

	/** Admin comment to post. */
	@Persistent
	private Text note;

//	@Persistent
//	@Element(dependent = "true")
//	private List<Comment> comments;

	public Comment(long parentid, String subject, Text text,
			String author, String ipaddress, String link) {
		this.parentid = parentid;
		this.subject = subject;
		this.text = text;
		this.author = author;
		date = new Date();
		this.ipaddress = ipaddress;
		this.link = link;
		status = STATUS_NOSTATUS;
		type = TYPE_NOTYPE;
		votes = 0;
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
		sb.append("\nlink="+link);
		sb.append("\n=status"+status);
		sb.append("\n=type"+type);
		sb.append("\n=starred"+votes);
		sb.append("\n=note"+note);
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

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Integer getStatus() {
		return status != null ? status : STATUS_NOSTATUS;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getVotes() {
		return votes != null ? votes : 0;
	}

	public void setVotes(Integer votes) {
		this.votes = votes;
	}

	public Integer getType() {
		return type != null ? type : TYPE_NOTYPE;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Text getNote() {
		return note;
	}

	public void setNote(Text note) {
		this.note = note;
	}

}
