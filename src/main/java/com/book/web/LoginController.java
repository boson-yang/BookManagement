package com.book.web;

import com.book.domain.Admin;
import com.book.domain.ReaderCard;
import com.book.service.LoginService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

//标注为一个Spring mvc的Controller
@Controller
public class LoginController {

    private static final Logger logger = LogManager.getLogger(LoginController.class);

    private LoginService loginService;

    @Autowired
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    //负责处理login.html请求
    @RequestMapping(value = {"/", "/login.html"})
    public String toLogin(HttpServletRequest request) {
        request.getSession().invalidate();
        return "index";
    }

    @RequestMapping("/logout.html")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/login.html";
    }

    //负责处理loginCheck.html请求
    //请求参数会根据参数名称默认契约自动绑定到相应方法的入参中
    @RequestMapping(value = "/api/loginCheck", method = RequestMethod.POST)
    public @ResponseBody
    Object loginCheck(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        String passwd = request.getParameter("passwd");
        logger.info("Login check for ID:" + id);
        boolean isReader = loginService.hasMatchReader(id, passwd);
        boolean isAdmin = loginService.hasMatchAdmin(id, passwd);
        HashMap<String, String> res = new HashMap<String, String>();

        if (isAdmin == false && isReader == false) {
            res.put("stateCode", "0");
            res.put("msg", "账号或密码错误！");
            logger.error("ID:" + id + " login failed.");
        } else if (isAdmin) {
            Admin admin = new Admin();
            admin.setAdminId(id);
            admin.setPassword(passwd);
            request.getSession().setAttribute("admin", admin);
            res.put("stateCode", "1");
            res.put("msg", "管理员登陆成功！");
            logger.info("Admin:" + id + " login successfully.");
        } else {
            ReaderCard readerCard = loginService.findReaderCardByUserId(id);
            request.getSession().setAttribute("readercard", readerCard);
            res.put("stateCode", "2");
            res.put("msg", "读者登陆成功！");
            logger.info("Reader:" + id + " login successfully.");
        }
        return res;
    }

    @RequestMapping("/admin_main.html")
    public ModelAndView toAdminMain(HttpServletResponse response) {
        return new ModelAndView("admin_main");
    }

    @RequestMapping("/reader_main.html")
    public ModelAndView toReaderMain(HttpServletResponse response) {
        return new ModelAndView("reader_main");
    }


    @RequestMapping("/admin_repasswd.html")
    public ModelAndView reAdminPasswd() {
        return new ModelAndView("admin_repasswd");
    }

    @RequestMapping("/admin_repasswd_do")
    public String reAdminPasswdDo(HttpServletRequest request, String oldPasswd, String newPasswd, String reNewPasswd, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) request.getSession().getAttribute("admin");
        int id = admin.getAdminId();
        String passwd = loginService.getAdminPasswd(id);

        if (!newPasswd.equals(reNewPasswd)) {
            logger.error("ReNewPasswd is not match with newPasswd");
            redirectAttributes.addFlashAttribute("error", "新密码二次确认不一致！");
            return "redirect:/admin_repasswd.html";
        }

        if (passwd.equals(oldPasswd)) {
            boolean succ = loginService.adminRePasswd(id, newPasswd);
            if (succ) {
                logger.info("Admin reset password successfully");
                redirectAttributes.addFlashAttribute("succ", "密码修改成功！");
            } else {
                logger.error("Admin reset password failed");
                redirectAttributes.addFlashAttribute("error", "密码修改失败！");
            }
        } else {
            logger.error("Admin old password error");
            redirectAttributes.addFlashAttribute("error", "旧密码错误！");
        }
        return "redirect:/admin_repasswd.html";
    }

    //配置404页面
    @RequestMapping("*")
    public String notFind() {
        return "404";
    }
}