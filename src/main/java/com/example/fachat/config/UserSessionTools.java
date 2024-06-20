package com.example.fachat.config;


import com.example.fachat.common.ApplicationVariable;
import com.example.fachat.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


public class UserSessionTools {
    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    public static User getLoginUser(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        Object objSession = null;
        if (session != null && (objSession = session.getAttribute(ApplicationVariable.SESSION_KEY_USERINFO))!=null){
                return (User) objSession;
        }
        return null;
    }

    /**
     * 更新用户会话
     * @param request
     * @param userInfo
     */
    public static void updateLoginUser(HttpServletRequest request, User userInfo) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(ApplicationVariable.SESSION_KEY_USERINFO)!=null) {
            session.setAttribute(ApplicationVariable.SESSION_KEY_USERINFO, userInfo);
        }
    }
}
