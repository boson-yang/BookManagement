package com.book.web;

import com.book.domain.ReaderCard;
import com.book.domain.ReaderInfo;
import com.book.service.LoginService;
import com.book.service.ReaderCardService;
import com.book.service.ReaderInfoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Controller
public class ReaderController {

    private static final Logger logger = LogManager.getLogger(ReaderController.class);

    private ReaderInfoService readerInfoService;

    @Autowired
    public void setReaderInfoService(ReaderInfoService readerInfoService) {
        this.readerInfoService = readerInfoService;
    }

    private LoginService loginService;


    @Autowired
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    private ReaderCardService readerCardService;

    @Autowired
    public void setReaderCardService(ReaderCardService readerCardService) {
        this.readerCardService = readerCardService;
    }

    @RequestMapping("allreaders.html")
    public ModelAndView allReaders() {
        ArrayList<ReaderInfo> readers = readerInfoService.readerInfos();
        logger.info("Get all readers nums:" + readers.size());
        ModelAndView modelAndView = new ModelAndView("admin_readers");
        modelAndView.addObject("readers", readers);
        return modelAndView;
    }

    @RequestMapping("reader_delete.html")
    public String readerDelete(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        int readerId = Integer.parseInt(request.getParameter("readerId"));
        boolean success = readerInfoService.deleteReaderInfo(readerId);
        if (success) {
            logger.info("Delete reader[" + readerId + "] successfully");
            redirectAttributes.addFlashAttribute("succ", "读者删除成功！");
        } else {
            logger.error("Delete reader[" + readerId + "] failed");
            redirectAttributes.addFlashAttribute("error", "读者删除失败！");
        }
        return "redirect:/allreaders.html";

    }

    @RequestMapping("/reader_info.html")
    public ModelAndView toReaderInfo(HttpServletRequest request) {
        ReaderCard readerCard = (ReaderCard) request.getSession().getAttribute("readercard");
        logger.info("Get reader[" + readerCard.getReaderId() + "] info");
        ReaderInfo readerInfo = readerInfoService.getReaderInfo(readerCard.getReaderId());
        ModelAndView modelAndView = new ModelAndView("reader_info");
        modelAndView.addObject("readerinfo", readerInfo);
        return modelAndView;
    }

    @RequestMapping("reader_edit.html")
    public ModelAndView readerInfoEdit(HttpServletRequest request) {
        int readerId = Integer.parseInt(request.getParameter("readerId"));
        logger.info("Edit reader[" + readerId + "] info");
        ReaderInfo readerInfo = readerInfoService.getReaderInfo(readerId);
        ModelAndView modelAndView = new ModelAndView("admin_reader_edit");
        modelAndView.addObject("readerInfo", readerInfo);
        return modelAndView;
    }

    @RequestMapping("reader_edit_do.html")
    public String readerInfoEditDo(HttpServletRequest request, String name, String sex, String birth, String address, String telcode, RedirectAttributes redirectAttributes) {
        int readerId = Integer.parseInt(request.getParameter("id"));
        logger.info("Edit reader[" + readerId + "] info");
        ReaderCard readerCard = loginService.findReaderCardByUserId(readerId);
        String oldName = readerCard.getName();
        if (!oldName.equals(name)) {
            boolean succo = readerCardService.updateName(readerId, name);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date nbirth = new Date();
            try {
                java.util.Date date = sdf.parse(birth);
                nbirth = date;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ReaderInfo readerInfo = new ReaderInfo();
            readerInfo.setAddress(address);
            readerInfo.setBirth(nbirth);
            readerInfo.setName(name);
            readerInfo.setReaderId(readerId);
            readerInfo.setTelcode(telcode);
            readerInfo.setSex(sex);
            boolean succ = readerInfoService.editReaderInfo(readerInfo);
            if (succo && succ) {
                logger.info("Edit reader[" + readerId + "] info successfully");
                redirectAttributes.addFlashAttribute("succ", "读者信息修改成功！");
            } else {
                logger.error("Edit reader[" + readerId + "] info failed");
                redirectAttributes.addFlashAttribute("error", "读者信息修改失败！");
            }
        } else {
            // 部分修改读者信息
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date nbirth = new Date();
            try {
                java.util.Date date = sdf.parse(birth);
                nbirth = date;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ReaderInfo readerInfo = new ReaderInfo();
            readerInfo.setAddress(address);
            readerInfo.setBirth(nbirth);
            readerInfo.setName(name);
            readerInfo.setReaderId(readerId);
            readerInfo.setTelcode(telcode);
            readerInfo.setSex(sex);

            boolean succ = readerInfoService.editReaderInfo(readerInfo);
            if (succ) {
                logger.info("Edit reader[" + readerId + "] info successfully");
                redirectAttributes.addFlashAttribute("succ", "读者信息修改成功！");
            } else {
                logger.error("Edit reader[" + readerId + "] info failed");
                redirectAttributes.addFlashAttribute("error", "读者信息修改失败！");
            }
        }
        return "redirect:/allreaders.html";
    }

    @RequestMapping("reader_add.html")
    public ModelAndView readerInfoAdd() {
        ModelAndView modelAndView = new ModelAndView("admin_reader_add");
        return modelAndView;

    }

    //用户功能--进入修改密码页面
    @RequestMapping("reader_repasswd.html")
    public ModelAndView readerRePasswd() {
        ModelAndView modelAndView = new ModelAndView("reader_repasswd");
        return modelAndView;
    }

    //用户功能--修改密码执行
    @RequestMapping("reader_repasswd_do.html")
    public String readerRePasswdDo(HttpServletRequest request, String oldPasswd, String newPasswd, String reNewPasswd, RedirectAttributes redirectAttributes) {
        ReaderCard readerCard = (ReaderCard) request.getSession().getAttribute("readercard");
        int readerId = readerCard.getReaderId();
        String passwd = readerCard.getPasswd();

        if (newPasswd.equals(reNewPasswd)) {
            if (passwd.equals(oldPasswd)) {
                boolean succ = readerCardService.updatePasswd(readerId, newPasswd);
                if (succ) {
                    ReaderCard readerCardNew = loginService.findReaderCardByUserId(readerId);
                    request.getSession().setAttribute("readercard", readerCardNew);
                    logger.info("Reader reset password successfully");
                    redirectAttributes.addFlashAttribute("succ", "密码修改成功！");
                } else {
                    logger.error("Reader reset password failed");
                    redirectAttributes.addFlashAttribute("succ", "密码修改失败！");
                }
            } else {
                logger.error("Reader old password error");
                redirectAttributes.addFlashAttribute("error", "修改失败,原密码错误");
            }
        } else {
            logger.error("ReNewPasswd is not match with newPasswd");
            redirectAttributes.addFlashAttribute("error", "修改失败,两次输入的新密码不相同");
        }
        return "redirect:/reader_repasswd.html";


    }

    //管理员功能--读者信息添加
    @RequestMapping("reader_add_do.html")
    public String readerInfoAddDo(String name, String sex, String birth, String address, String telcode, int readerId, RedirectAttributes redirectAttributes) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date nbirth = new Date();
        try {
            java.util.Date date = sdf.parse(birth);
            nbirth = date;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ReaderInfo readerInfo = new ReaderInfo();
        readerInfo.setAddress(address);
        readerInfo.setBirth(nbirth);
        readerInfo.setName(name);
        readerInfo.setReaderId(readerId);
        readerInfo.setTelcode(telcode);
        readerInfo.setSex(sex);
        boolean succ = readerInfoService.addReaderInfo(readerInfo);
        boolean succc = readerCardService.addReaderCard(readerInfo);
        ArrayList<ReaderInfo> readers = readerInfoService.readerInfos();
        if (succ && succc) {
            logger.info("Add reader[" + readerId + "] info successfully");
            redirectAttributes.addFlashAttribute("succ", "添加读者信息成功！");
        } else {
            logger.error("Add reader[" + readerId + "] info failed");
            redirectAttributes.addFlashAttribute("succ", "添加读者信息失败！");
        }
        return "redirect:/allreaders.html";
    }

    //读者功能--读者信息修改
    @RequestMapping("reader_info_edit.html")
    public ModelAndView readerInfoEditReader(HttpServletRequest request) {
        ReaderCard readerCard = (ReaderCard) request.getSession().getAttribute("readercard");
        ReaderInfo readerInfo = readerInfoService.getReaderInfo(readerCard.getReaderId());
        ModelAndView modelAndView = new ModelAndView("reader_info_edit");
        modelAndView.addObject("readerinfo", readerInfo);
        return modelAndView;

    }

    @RequestMapping("reader_edit_do_r.html")
    public String readerInfoEditDoReader(HttpServletRequest request, String name, String sex, String birth, String address, String telcode, RedirectAttributes redirectAttributes) {
        ReaderCard readerCard = (ReaderCard) request.getSession().getAttribute("readercard");
        if (!readerCard.getName().equals(name)) {
            boolean succo = readerCardService.updateName(readerCard.getReaderId(), name);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date nbirth = new Date();
            try {
                java.util.Date date = sdf.parse(birth);
                nbirth = date;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ReaderInfo readerInfo = new ReaderInfo();
            readerInfo.setAddress(address);
            readerInfo.setBirth(nbirth);
            readerInfo.setName(name);
            readerInfo.setReaderId(readerCard.getReaderId());
            readerInfo.setTelcode(telcode);
            readerInfo.setSex(sex);

            boolean succ = readerInfoService.editReaderInfo(readerInfo);
            if (succ && succo) {
                ReaderCard readerCardNew = loginService.findReaderCardByUserId(readerCard.getReaderId());
                logger.info("Edit reader[" + readerCard.getReaderId() + "] info successfully");
                request.getSession().setAttribute("readercard", readerCardNew);
                redirectAttributes.addFlashAttribute("succ", "信息修改成功！");
            } else {
                logger.error("Edit reader[" + readerCard.getReaderId() + "] info failed");
                redirectAttributes.addFlashAttribute("error", "信息修改失败！");
            }

        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date nbirth = new Date();
            try {
                java.util.Date date = sdf.parse(birth);
                nbirth = date;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ReaderInfo readerInfo = new ReaderInfo();
            readerInfo.setAddress(address);
            readerInfo.setBirth(nbirth);
            readerInfo.setName(name);
            readerInfo.setReaderId(readerCard.getReaderId());
            readerInfo.setTelcode(telcode);
            readerInfo.setSex(sex);

            boolean succ = readerInfoService.editReaderInfo(readerInfo);
            if (succ) {
                ReaderCard readerCardNew = loginService.findReaderCardByUserId(readerCard.getReaderId());
                logger.info("Edit reader[" + readerCard.getReaderId() + "] info successfully");
                request.getSession().setAttribute("readercard", readerCardNew);
                redirectAttributes.addFlashAttribute("succ", "信息修改成功！");
            } else {
                logger.error("Edit reader[" + readerCard.getReaderId() + "] info failed");
                redirectAttributes.addFlashAttribute("error", "信息修改失败！");
            }
        }
        return "redirect:/reader_info.html";
    }
}
