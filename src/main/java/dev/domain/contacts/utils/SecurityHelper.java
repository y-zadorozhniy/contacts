package dev.domain.contacts.utils;

import lombok.experimental.UtilityClass;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import dev.domain.contacts.domain.User;
import dev.domain.contacts.security.CustomUserPrincipal;

import java.security.Principal;

@UtilityClass
public class SecurityHelper {

    public User parseUserFromPrincipal(Principal principal) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) principal;
        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) authentication.getPrincipal();
        return customUserPrincipal.getUser();
    }
}
