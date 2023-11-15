package com.jusuck.nbsSecurity;

import com.jusuck.nbsSecurity.entity.authGroup.AuthGroup;
import com.jusuck.nbsSecurity.entity.authGroup.AuthGroupRepository;
import com.jusuck.nbsSecurity.entity.menu.Menu;
import com.jusuck.nbsSecurity.entity.menu.MenuRepository;
import com.jusuck.nbsSecurity.entity.staff.Staff;
import com.jusuck.nbsSecurity.entity.staff.StaffRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@SpringBootApplication
public class NbsSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(NbsSecurityApplication.class, args);
	}

	// 임시데이터 생성

	@Component
	@AllArgsConstructor
	public static class DataLoader implements CommandLineRunner {

		private final StaffRepository staffRepository;
		private final AuthGroupRepository authGroupRepository;
		private final MenuRepository menuRepository;

		@Override
		public void run(String... args) throws Exception {
			// Staff 객체 생성
			Staff staff1 = Staff.builder()
					.userId("user1")
					.password("password1")
					.userName("User One")
					.build();

			Staff staff2 = Staff.builder()
					.userId("user2")
					.password("password2")
					.userName("User Two")
					.build();

			Staff staff3 = Staff.builder()
					.userId("user3")
					.password("password3")
					.userName("User Three")
					.build();

			// AuthGroup 객체 생성
			AuthGroup authGroup1 = AuthGroup.builder()
					.authgroupId("group1")
					.authgroupName("Group One")
					.build();

			AuthGroup authGroup2 = AuthGroup.builder()
					.authgroupId("group2")
					.authgroupName("Group Two")
					.build();

			AuthGroup authGroup3 = AuthGroup.builder()
					.authgroupId("group3")
					.authgroupName("Group Three")
					.build();

			// Menu 객체 생성
			Menu menu1 = Menu.builder()
					.menuId("menu1")
					.menuName("Menu One")
					.level(1)
					.isLeaf(true)
					.build();

			Menu menu2 = Menu.builder()
					.menuId("menu2")
					.menuName("Menu Two")
					.level(1)
					.isLeaf(true)
					.build();

			Menu menu3 = Menu.builder()
					.menuId("menu3")
					.menuName("Menu Three")
					.level(1)
					.isLeaf(true)
					.build();

			// 데이터베이스에 저장
			staffRepository.saveAll(Arrays.asList(staff1, staff2, staff3));
			authGroupRepository.saveAll(Arrays.asList(authGroup1, authGroup2, authGroup3));
			menuRepository.saveAll(Arrays.asList(menu1, menu2, menu3));

			// 연관 관계 설정 및 저장
			// 예시: staff1은 authGroup1과 연결되고, authGroup1은 menu1과 연결됨
			staff1.setAuthGroups(Arrays.asList(authGroup1));
			staff2.setAuthGroups(Arrays.asList(authGroup2));
			staff3.setAuthGroups(Arrays.asList(authGroup3));

			authGroup1.setMenus(Arrays.asList(menu1));
			authGroup2.setMenus(Arrays.asList(menu2));
			authGroup3.setMenus(Arrays.asList(menu3));

			// 업데이트된 객체 저장
			staffRepository.saveAll(Arrays.asList(staff1, staff2, staff3));
			authGroupRepository.saveAll(Arrays.asList(authGroup1, authGroup2, authGroup3));
		}
	}



}
