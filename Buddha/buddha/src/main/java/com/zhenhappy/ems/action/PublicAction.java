package com.zhenhappy.ems.action;

import com.zhenhappy.ems.buddhatime.TExhibitorBuddhaTime;
import com.zhenhappy.ems.dto.ExhibitorBooth;
import com.zhenhappy.ems.dto.LoginRequest;
import com.zhenhappy.ems.dto.LoginResponse;
import com.zhenhappy.ems.dto.Principle;
import com.zhenhappy.ems.entity.TExhibitor;
import com.zhenhappy.ems.service.ExhibitorBuddhaTimeService;
import com.zhenhappy.ems.service.ExhibitorService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by wujianbin on 2014-03-31.
 */
@Controller
@RequestMapping(value = "/")
public class PublicAction {

    @Autowired
    private ExhibitorService exhibitorService;

    @Autowired
    private ExhibitorBuddhaTimeService exhibitorTimeService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static Logger log = Logger.getLogger(PublicAction.class);

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String loginPage(HttpServletRequest request, HttpServletResponse response) {
        if ("le".equals(request.getParameter("locale"))) {
            request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.US);
        } else {
            request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.CHINA);
        }
        return "/public/login";
    }

    /**
     * login method
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public LoginResponse login(@RequestBody LoginRequest request, HttpServletRequest httpServletRequest, Locale locale) {
        LoginResponse response = new LoginResponse();
        try {
            TExhibitor exhibitor = exhibitorService.getExhibitorByUsernamePassword(request.getUsername(), request.getPassword());
            if (exhibitor == null || exhibitor.getIsLogout().intValue() == 1) {
                response.setResultCode(1);
            } else {
                ExhibitorBooth booth = exhibitorService.loadBoothInfo(exhibitor.getEid());
                httpServletRequest.getSession().setAttribute(Principle.PRINCIPLE_SESSION_ATTRIBUTE, new Principle(exhibitor));
                httpServletRequest.getSession().setAttribute("boothInfo", booth);

                //加载前台界面相关时间对象
                TExhibitorBuddhaTime tExhibitorTime = exhibitorTimeService.loadExhibitorTime();
                httpServletRequest.getSession().setAttribute("tExhibitorTime", tExhibitorTime);

                response.setResultCode(0);
            }
            if (request.getArea() != null)
                httpServletRequest.getSession().setAttribute("zone", request.getArea());
            else
                httpServletRequest.getSession().setAttribute("zone", 1);
        } catch (Exception e) {
            log.error("login error.username:" + request.getUsername(), e);
            response.setResultCode(3);
        }
        return response;
    }

    @RequestMapping(value = "logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().removeAttribute(Principle.PRINCIPLE_SESSION_ATTRIBUTE);
        response.sendRedirect(request.getContextPath());
    }

    /**
     * findPassword method
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "findPassword")
    public String findPassword(@RequestParam("company") String company, @RequestParam("username") String username) {
        if (StringUtils.isNotEmpty(company) && StringUtils.isNotEmpty(username)) {
            String password = "";
            try {
                password = (String) jdbcTemplate.queryForObject("select password from [t_exhibitor] where company = ?", new Object[]{company}, java.lang.String.class);
            } catch (Exception e) {
                return "company";
            }
            try {
                password = (String) jdbcTemplate.queryForObject("select password from [t_exhibitor] where username = ?", new Object[]{username}, java.lang.String.class);
            } catch (Exception e) {
                return "username";
            }
            try {
                password = (String) jdbcTemplate.queryForObject("select password from [t_exhibitor] where company = ? and username = ?", new Object[]{company, username}, java.lang.String.class);
            } catch (Exception e) {
                return "all";
            }
            return password;
        } else return "";
    }

    /**
     * findPassword method
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "findPasswordEn")
    public String findPasswordEn (@RequestParam("username") String username, @RequestParam("booth") String booth){
        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(booth)) {
            Integer eid = 0;
            try {
                eid = (Integer) jdbcTemplate.queryForObject("select eid from [t_exhibitor_booth] where booth_number = ?", new Object[]{booth}, java.lang.Integer.class);
            } catch (Exception e) {
                return "booth";
            }
            String password = "";
            try {
                password = (String) jdbcTemplate.queryForObject("select password from [t_exhibitor] where username = ?", new Object[]{username}, java.lang.String.class);
            } catch (Exception e) {
                return "username";
            }
            try {
                password = (String) jdbcTemplate.queryForObject("select password from [t_exhibitor] where eid = ? and username = ?", new Object[]{eid, username}, java.lang.String.class);
            } catch (Exception e) {
                return "all";
            }
            return password;
        } else return "";
    }
}
