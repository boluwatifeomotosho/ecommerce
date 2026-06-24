package com.justjava.ecommerce.mobile;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MobileAuthController {

    static final String MOBILE_SESSION_KEY = "mobile";

    /** Public entry point for the Cordova WebView.
     *  Marks the session as mobile so the login success handler can redirect
     *  to /mobile instead of the desktop dashboard. */
    @GetMapping("/mobile/login")
    public String mobileLogin(HttpServletRequest request) {
        request.getSession(true).setAttribute(MOBILE_SESSION_KEY, true);
        return "redirect:/oauth2/authorization/keycloak";
    }
}
