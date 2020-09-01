package com.book.web;

import com.book.domain.Book;
import com.book.domain.ReaderCard;
import com.book.service.BookService;
import com.book.service.LendService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LendController {

    private static final Logger logger = LogManager.getLogger(LendController.class);

    private LendService lendService;

    @Autowired
    public void setLendService(LendService lendService) {
        this.lendService = lendService;
    }

    private BookService bookService;

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @RequestMapping("/lendbook.html")
    public ModelAndView bookLend(HttpServletRequest request) {
        long bookId = Integer.parseInt(request.getParameter("bookId"));
        logger.info("Lend book[" + bookId + "]");
        Book book = bookService.getBook(bookId);
        ModelAndView modelAndView = new ModelAndView("admin_book_lend");
        modelAndView.addObject("book", book);
        return modelAndView;
    }

    @RequestMapping("/lendbookdo.html")
    public String bookLendDo(HttpServletRequest request, RedirectAttributes redirectAttributes, int readerId) {
        long bookId = Integer.parseInt(request.getParameter("id"));
        boolean lendsucc = lendService.bookLend(bookId, readerId);
        if (lendsucc) {
            logger.info("Lend book[" + bookId + "] successfully");
            redirectAttributes.addFlashAttribute("succ", "图书借阅成功！");
        } else {
            logger.error("Lend book[" + bookId + "] failed");
            redirectAttributes.addFlashAttribute("error", "图书借阅失败！");
        }
        return "redirect:/allbooks.html";
    }

    @RequestMapping("/returnbook.html")
    public String bookReturn(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        long bookId = Integer.parseInt(request.getParameter("bookId"));
        boolean retSucc = lendService.bookReturn(bookId);
        if (retSucc) {
            logger.info("Return book[" + bookId + "] successfully");
            redirectAttributes.addFlashAttribute("succ", "图书归还成功！");
        } else {
            logger.error("Return book[" + bookId + "] failed");
            redirectAttributes.addFlashAttribute("error", "图书归还失败！");
        }
        return "redirect:/allbooks.html";
    }

    @RequestMapping("/lendlist.html")
    public ModelAndView lendList() {
        logger.info("Admin get lend list");
        ModelAndView modelAndView = new ModelAndView("admin_lend_list");
        modelAndView.addObject("list", lendService.lendList());
        return modelAndView;
    }

    @RequestMapping("/mylend.html")
    public ModelAndView myLend(HttpServletRequest request) {
        ReaderCard readerCard = (ReaderCard) request.getSession().getAttribute("readercard");
        logger.info("Reader["+ readerCard.getReaderId() + "] get lend list");
        ModelAndView modelAndView = new ModelAndView("reader_lend_list");
        modelAndView.addObject("list", lendService.myLendList(readerCard.getReaderId()));
        return modelAndView;
    }
}
