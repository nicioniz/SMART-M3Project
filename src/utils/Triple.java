package utils;

import java.util.Vector;

public class Triple {

	public static final String LITERAL = "literal";
	public static final String URI = "uri";
	
	private String subject;
	private String predicate;
	private String object;
	private String subjectType;
	private String objectType;
	public Triple() {
		this("","","","","");
	}
	public Triple(String subject, String predicate, String object, String subjectType, String objectType) {
		super();
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
		this.subjectType = subjectType;
		this.objectType = objectType;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getPredicate() {
		return predicate;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public String getSubjectType() {
		return subjectType;
	}
	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public Vector<String> getAsVector(){
		Vector<String> res = new Vector<>();
		res.add(getSubject());
		res.add(getPredicate());
		res.add(getObject());
		res.add(getSubjectType());
		res.add(getObjectType());
		return res;
	}

}
