package com.jusuck.nbsSecurity.entity.authGroup;

import com.jusuck.nbsSecurity.entity.menu.Menu;
import com.jusuck.nbsSecurity.entity.staff.Staff;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authgroup")
public class AuthGroup {

	@Id
	private String authgroupId;
	private String authgroupName;

	@ManyToMany(mappedBy = "authGroups")
	private List<Staff> staffs;

	@ManyToMany
	@JoinTable(
			name = "authgroup_menu_association",
			joinColumns = @JoinColumn(name = "authgroup_id"),
			inverseJoinColumns = @JoinColumn(name = "menu_id")
	)
	private List<Menu> menus;
}