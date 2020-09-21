package com.janisschanbacher.springmicroservicesdemo.authService.authentication;

import com.janisschanbacher.springmicroservicesdemo.authService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    /**
     * Loads the user specified by username from the userRepository
     *
     * @param username username that identifies the user to be loaded
     * @return user specified by username
     * @throws UsernameNotFoundException if no user with he specified username exists
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<com.janisschanbacher.springmicroservicesdemo.authService.document.User> appUser = userRepository.findByUsername(username);
        if (appUser.isPresent()) {
            return new org.springframework.security.core.userdetails.User(appUser.get().getUsername(), appUser.get().getPassword(), AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_" + appUser.get().getRole()));
        }

        throw new UsernameNotFoundException("Username: " + username + " not found");
    }
}
