package com.example.backend.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * Classe User
 * Bean représentant un objet User.
 * @author Louis Peculier
 *
 */
@Setter
@Getter
@Entity
@NamedQuery(name="User.findAll", query="SELECT e FROM User e")
@NamedQuery(name="User.findById", query="SELECT e FROM User e WHERE e.id = :id")
@Table(name="user")
public class User implements UserDetails {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;

	
	@Column(name = "first_name", nullable = false)
	private String firstName;

	
	@Column(name = "last_name", nullable = false)
	private String lastName;

	@JsonIgnore
	@ManyToMany(cascade = { CascadeType.PERSIST },fetch = FetchType.EAGER)
	@JoinTable(
		name = "user_role",
		joinColumns = @JoinColumn(name = "user_id"),
		inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	
	@Column(name = "gender", nullable = false)
	private String gender;

	
	@Column(name = "option", nullable = false)
	private String option;

	
	@Column(name = "email", nullable = false)
	private String email;

	
	@Column(name = "password")
	private String password;

	
	@Column(name = "bachelor", nullable = false)
	private boolean bachelor;

	
	@Column(name = "past_grade")
	private Double gradePast;

	@JsonIgnore
	@ManyToMany(cascade =  CascadeType.ALL)
	@JoinTable(
			name = "userteam",
			joinColumns = { @JoinColumn(name = "user_id") },
			inverseJoinColumns = { @JoinColumn(name = "team_id") }
	)
	private Set<Team> teams = new HashSet<>();

	public void addTeam (Team team) {
		teams.add(team);
	}

	@Override
    public String getUsername() {
        return null;
    }

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName().toString()));
		}
		return authorities;
	}

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", first_name=" + firstName + ", last_name=" + lastName + ", role=" + roles + ", gradePast=" + gradePast+ "]";
	}

	public void addRole(Role role) {
		roles.add(role);
	}

}
