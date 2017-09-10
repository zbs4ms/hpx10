package com.jishi.reservation.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sloan on 2017/6/1.
 */
public class CookieUtil {

    /**
     * 设置cookie
     * @param response
     * @param name  cookie名字
     * @param value cookie值
     * @param maxAge cookie生命周期  以秒为单位
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly){
        Cookie cookie = new Cookie(name,value);
        cookie.setPath("/");
        cookie.setHttpOnly(httpOnly);
        if(maxAge>0)  cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * 根据名字获取cookie
     * @param request
     * @param name cookie名字
     * @return
     */
    public static Cookie getCookieByName(HttpServletRequest request, String name){
        Map<String,Cookie> cookieMap = ReadCookieMap(request);
        if(cookieMap.containsKey(name)){
            Cookie cookie = (Cookie)cookieMap.get(name);

            return cookie;
        }else{
            return null;
        }
    }

    /**
     * 根据名字删除cookie
     * @param request
     * @param name
     */
    public static void  deleteCookieByName(HttpServletRequest request,HttpServletResponse response,String name){
        Cookie cookies[] = request.getCookies();
        if (cookies != null)
        {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    cookie.setValue("");
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break;

                }
            }
        }
    }




    /**
     * 将cookie封装到Map里面
     * @param request
     * @return
     */
    private static Map<String,Cookie> ReadCookieMap(HttpServletRequest request){
        Map<String,Cookie> cookieMap = new HashMap<String,Cookie>();
        Cookie[] cookies = request.getCookies();
        if(null!=cookies){
            for(Cookie cookie : cookies){
                cookieMap.put(cookie.getName(), cookie);
            }
        }

        return cookieMap;
    }

}
