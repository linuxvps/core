package org.example.amlak.impl;

// 📁 security/UserDetailsServiceImpl.java

import org.example.amlak.model.Permission;
import org.example.amlak.model.Role;
import org.example.amlak.model.User;
import org.example.amlak.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            for (Permission perm : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(perm.getName()));
            }
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), user.isEnabled(),
                true, true, true, authorities);
    }
}
