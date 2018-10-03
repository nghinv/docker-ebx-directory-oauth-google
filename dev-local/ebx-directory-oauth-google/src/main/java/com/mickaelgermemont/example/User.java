package com.mickaelgermemont.example;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class User extends GenericJson {
	@Key
	private String kind;
	@Key
	private String gender;
	@Key
	private String id;
	@Key
	private String displayName;
	@Key
	private String url;
	@Key
	private Boolean isPlusUser;
	@Key
	private String language;
	@Key
	private Integer circleByCount;
	@Key
	private Boolean verified;
	
	public String getKind() {
		return kind;
	}
	
	public String getGender() {
		return gender;
	}
	
	public String getId() {
		return id;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getUrl() {
		return url;
	}
	
	public Boolean getIsPlusUser() {
		return isPlusUser;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public Integer getCircleByCount() {
		return circleByCount;
	}
	
	public Boolean getVerified() {
		return verified;
	}
}