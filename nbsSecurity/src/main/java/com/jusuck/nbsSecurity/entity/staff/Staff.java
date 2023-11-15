package com.jusuck.nbsSecurity.entity.staff;

import com.jusuck.nbsSecurity.entity.authGroup.AuthGroup;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "staff")
public class Staff {

	@Id
	private String userId;
	private String password;
	private String userName;

	@ManyToMany
	@JoinTable(
			name = "staff_authgroup_association",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "authgroup_id")
	)
	private List<AuthGroup> authGroups;
}
