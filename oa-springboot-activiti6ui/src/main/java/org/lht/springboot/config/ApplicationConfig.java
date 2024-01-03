// package org.lht.springboot.config;
//
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//
// import java.util.Arrays;
// import java.util.List;
// import java.util.stream.Collectors;
//
// /**
//  * @Description: TODO
//  * @author: lhtao
//  * @date: 2022年10月14日 17:01
//  */
// @Configuration
// public class ApplicationConfig {
//     private Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
//     @Bean
//     public UserDetailsService myUserDetailsService() {
//         InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
//         //这里添加用户，后面处理流程时用到的任务负责人，需要添加在这里
//         String[][] usersGroupsAndRoles = {
//                 {"钱晓琦", "password", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"},
//                 {"孙宁", "password", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"},
//                 {"陆洪涛", "password", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"},
//                 {"朱小婷", "password", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"},
//                 {"朱江", "password", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"},
//                 {"jack", "password", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"},
//                 {"rose", "password", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"},
//                 {"tom", "password", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"},
//                 {"other", "password", "ROLE_ACTIVITI_USER", "GROUP_otherTeam"},
//                 {"system", "password", "ROLE_ACTIVITI_USER"},
//                 {"admin", "password", "ROLE_ACTIVITI_ADMIN"},
//         };
//
//         for (String[] user : usersGroupsAndRoles) {
//             List<String> authoritiesStrings = Arrays.asList(Arrays.copyOfRange(user, 2, user.length));
//             logger.info("> Registering new user: " + user[0] + " with the following Authorities[" + authoritiesStrings + "]");
//             inMemoryUserDetailsManager.createUser(new User(user[0], passwordEncoder().encode(user[1]),
//                     authoritiesStrings.stream().map(s -> new SimpleGrantedAuthority(s)).collect(Collectors.toList())));
//         }
//
//         return inMemoryUserDetailsManager;
//     }
//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }
// }
