package com.spotify_final_project.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Setter
@Getter
public class CustomAuthentication implements Authentication {
    private final String role;
    private final String email;
    private final String username;
    private boolean authenticated;

    public CustomAuthentication(String role, String email, String username) {
        this.role = role;
        this.email = email;
        this.username = username;
        this.authenticated = true;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<? extends GrantedAuthority> auths = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        System.out.println("CustomAuthentication.getAuthorities(): " + auths);
        return auths;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return username;
    }


}
