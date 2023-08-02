package it.babyloncloud.chat.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(nullable = false)
	private String name;
	private String surname;
	@Column(unique = true, nullable = false)
	private String email;
	@Column(nullable = false)
	private String username;
	private Integer strikes;
	private Boolean blocked;
	
	@ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
	private Set<Room> rooms;
	
	public User() {
		this.strikes = 0;
		this.setBlocked(false);
		this.rooms = new HashSet<>();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String name) {
		this.username = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getStrikes() {
		return strikes;
	}
	public void setStrikes(Integer strikes) {
		this.strikes = strikes;
	}
	public Boolean getBlocked() {
		return blocked;
	}
	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
	}

	@Override
	public int hashCode() {
		return Objects.hash(email);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(email, other.email);
	}
	
}
