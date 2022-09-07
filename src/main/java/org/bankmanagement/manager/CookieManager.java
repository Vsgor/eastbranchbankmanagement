package org.bankmanagement.manager;

import org.bankmanagement.dataobject.JwtPair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static java.util.Objects.isNull;

@Component
public class CookieManager {
    private static final String COOKIE_KEY_ACCESS = "access_cookie_key";
    private static final String COOKIE_KEY_REFRESH = "refresh_cookie_key";
    @Value("${cookie.lifetime.minutes}")
    private int cookieLifeTime;

    public void addAuthCookies(HttpServletResponse response, JwtPair jwtPair) {
        response.addCookie(generateCookie(COOKIE_KEY_ACCESS, jwtPair.getAccessToken(), cookieLifeTime));
        response.addCookie(generateCookie(COOKIE_KEY_REFRESH, jwtPair.getRefreshToken(), cookieLifeTime));
    }

    public JwtPair getAuthCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (isNull(cookies)) return null;
        String accessToken = getCookieValue(cookies, COOKIE_KEY_ACCESS);
        String refreshToken = getCookieValue(cookies, COOKIE_KEY_REFRESH);
        return new JwtPair(accessToken, refreshToken);
    }

    private String getCookieValue(Cookie[] cookies, String cookieKeyAccess) {
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieKeyAccess))
                .map(Cookie::getValue)
                .findFirst()
                .orElse("");
    }

    public void removeAuthCookies(HttpServletResponse response) {
        response.addCookie(generateCookie(COOKIE_KEY_ACCESS, null, 0));
        response.addCookie(generateCookie(COOKIE_KEY_REFRESH, null, 0));
    }

    private Cookie generateCookie(String cookieKey, String cookieValue, int cookieLifeTime) {
        Cookie cookie = new Cookie(cookieKey, cookieValue);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(cookieLifeTime);
        cookie.setSecure(true);

        return cookie;
    }
}
