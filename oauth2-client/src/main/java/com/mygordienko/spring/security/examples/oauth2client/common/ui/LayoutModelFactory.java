package com.mygordienko.spring.security.examples.oauth2client.common.ui;

import java.util.Collection;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component
public class LayoutModelFactory {

    public Map<String, Object> create(OidcUser user, String activePage) {
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        String displayName = user.getPreferredUsername() != null ? user.getPreferredUsername() : user.getName();

        return Map.of(
                "username", displayName,
                "activePage", activePage,
                "hasHrRole", hasAuthority(authorities, "ROLE_HR"),
                "hasManagerRole", hasAuthority(authorities, "ROLE_MANAGER")
        );
    }

    private boolean hasAuthority(Collection<? extends GrantedAuthority> authorities, String authority) {
        return authorities.stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }
}
