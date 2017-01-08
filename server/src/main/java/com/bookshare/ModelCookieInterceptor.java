package com.bookshare;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.bookshare.domain.Session;

public class ModelCookieInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView modelAndView)
            throws Exception {
        if (modelAndView != null) {
            Session s = (Session) modelAndView.getModel().get("session");
            Cookie c = new Cookie("session", s.getSessionID());
            res.addCookie(c);
        }
    }

}
