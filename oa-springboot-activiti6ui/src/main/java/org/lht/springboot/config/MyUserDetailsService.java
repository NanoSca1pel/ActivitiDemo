package org.lht.springboot.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @Description: TODO
 * @author: lhtao
 * @date: 2024年01月04日 9:59
 */
//activiti7.1.0.M6版本使用，7.1.0.M4及以下不需要
@Component
public class MyUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }
}
