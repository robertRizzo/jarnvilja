package com.jarnvilja.service;

import com.jarnvilja.exception.DemoRestrictionException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class DemoGuard {

    public void checkNotDemo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null && auth.getName().startsWith("demo")) {
            throw new DemoRestrictionException(
                    "Denna åtgärd är inaktiverad i demo-läge. Testa att boka ett pass istället!");
        }
    }

    public boolean isDemoUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getName() != null && auth.getName().startsWith("demo");
    }
}
