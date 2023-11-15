package com.jusuck.nbsSecurity.entity.menu;

import com.jusuck.nbsSecurity.entity.authGroup.AuthGroup;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "menu")
public class Menu {

	@Id
	private String menuId;
	private String menuName;
	private Integer level;
	private Boolean isLeaf;

	@ManyToOne
	@JoinColumn(name = "parentMenuId")
	private Menu parentMenu;

	@OneToMany(mappedBy = "parentMenu")
	private List<Menu> childMenus;

	@ManyToMany(mappedBy = "menus")
	private List<AuthGroup> authGroups;
}
