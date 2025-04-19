package org.example.model;

import java.time.LocalDate;

public class CustomUser extends User {
	private String email;
	private LocalDate birthDate;
	private int provinceId;
	private String avatarFileName;

	public CustomUser(long id, String email, LocalDate birthDate, int provinceId, String avatarFileName) {
		super(id);
		this.email = email;
		this.birthDate = birthDate;
		this.provinceId = provinceId;
		this.avatarFileName = avatarFileName;
	}
	
	public CustomUser(String username, String email, String birthDateStr, int provinceId) {
		super();
		this.setUsername(username); // từ lớp cha User
		this.email = email;
		try {
			this.birthDate = LocalDate.parse(birthDateStr);
		} catch (Exception e) {
			this.birthDate = null;
		}
		this.provinceId = provinceId;
	}

	public CustomUser() {
		super();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	public String getAvatarFileName() {
		return avatarFileName;
	}

	public void setAvatarFileName(String avatarFileName) {
		this.avatarFileName = avatarFileName;
	}

}
