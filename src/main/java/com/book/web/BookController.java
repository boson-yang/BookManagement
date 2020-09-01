package com.book.web;

import com.book.domain.Book;
import com.book.service.BookService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@Controller
public class BookController {

    private static final Logger logger = LogManager.getLogger(BookController.class);

    private BookService bookService;

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @RequestMapping("/querybook.html")
    public ModelAndView queryBookDo(HttpServletRequest request, String searchWord) {
        logger.info("Admin query book for searchWord:" + searchWord);
        boolean exist = bookService.matchBook(searchWord);
        if (exist) {
            ArrayList<Book> books = bookService.queryBook(searchWord);
            logger.info("Admin query book num:" + books.size());
            ModelAndView modelAndView = new ModelAndView("admin_books");
            modelAndView.addObject("books", books);
            return modelAndView;
        } else {
            logger.info("No book match for searchWord:" + searchWord);
            return new ModelAndView("admin_books", "error", "没有匹配的图书");
        }
    }

    @RequestMapping("/reader_querybook.html")
    public ModelAndView readerQueryBook() {
        return new ModelAndView("reader_book_query");
    }

    @RequestMapping("/reader_querybook_do.html")
    public String readerQueryBookDo(HttpServletRequest request, String searchWord, RedirectAttributes redirectAttributes) {
        logger.info("Reader query book for searchWord:" + searchWord);
        boolean exist = bookService.matchBook(searchWord);
        if (exist) {
            ArrayList<Book> books = bookService.queryBook(searchWord);
            logger.info("Reader query book num:" + books.size());
            redirectAttributes.addFlashAttribute("books", books);
        } else {
            logger.info("No book match for searchWord:" + searchWord);
            redirectAttributes.addFlashAttribute("error", "没有匹配的图书！");
        }
        return "redirect:/reader_querybook.html";

    }

    @RequestMapping("/allbooks.html")
    public ModelAndView allBook() {
        ArrayList<Book> books = bookService.getAllBooks();
        logger.info("Get all books num:" + books.size());
        ModelAndView modelAndView = new ModelAndView("admin_books");
        modelAndView.addObject("books", books);
        return modelAndView;
    }

    @RequestMapping("/deletebook.html")
    public String deleteBook(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        long bookId = Integer.parseInt(request.getParameter("bookId"));
        int res = bookService.deleteBook(bookId);
        if (res == 1) {
            logger.info("Delete book[" + bookId + "] successfully");
            redirectAttributes.addFlashAttribute("succ", "图书删除成功！");
        } else {
            logger.error("Delete book[" + bookId + "] failed");
            redirectAttributes.addFlashAttribute("error", "图书删除失败！");
        }
        return "redirect:/allbooks.html";
    }

    @RequestMapping("/book_add.html")
    public ModelAndView addBook(HttpServletRequest request) {
        return new ModelAndView("admin_book_add");
    }

    @RequestMapping("/book_add_do.html")
    public String addBookDo(BookAddCommand bookAddCommand, RedirectAttributes redirectAttributes) {
        Book book = new Book();
        book.setBookId(0);
        book.setPrice(bookAddCommand.getPrice());
        book.setState(bookAddCommand.getState());
        book.setPublish(bookAddCommand.getPublish());
        book.setPubdate(bookAddCommand.getPubdate());
        book.setName(bookAddCommand.getName());
        book.setIsbn(bookAddCommand.getIsbn());
        book.setClassId(bookAddCommand.getClassId());
        book.setAuthor(bookAddCommand.getAuthor());
        book.setIntroduction(bookAddCommand.getIntroduction());
        book.setPressmark(bookAddCommand.getPressmark());
        book.setLanguage(bookAddCommand.getLanguage());

        boolean succ = bookService.addBook(book);
        if (succ) {
            logger.info("Add book[" + book.getName() + "] successfully");
            redirectAttributes.addFlashAttribute("succ", "图书添加成功！");
        } else {
            logger.error("Add book[" + book.getName() + "] failed");
            redirectAttributes.addFlashAttribute("succ", "图书添加失败！");
        }
        return "redirect:/allbooks.html";
    }

    @RequestMapping("/updatebook.html")
    public ModelAndView bookEdit(HttpServletRequest request) {
        long bookId = Integer.parseInt(request.getParameter("bookId"));
        logger.info("Admin get book[" + bookId + "] detail");
        Book book = bookService.getBook(bookId);
        ModelAndView modelAndView = new ModelAndView("admin_book_edit");
        modelAndView.addObject("detail", book);
        return modelAndView;
    }

    @RequestMapping("/book_edit_do.html")
    public String bookEditDo(HttpServletRequest request, BookAddCommand bookAddCommand, RedirectAttributes redirectAttributes) {
        long bookId = Integer.parseInt(request.getParameter("id"));
        Book book = new Book();
        book.setBookId(bookId);
        book.setPrice(bookAddCommand.getPrice());
        book.setState(bookAddCommand.getState());
        book.setPublish(bookAddCommand.getPublish());
        book.setPubdate(bookAddCommand.getPubdate());
        book.setName(bookAddCommand.getName());
        book.setIsbn(bookAddCommand.getIsbn());
        book.setClassId(bookAddCommand.getClassId());
        book.setAuthor(bookAddCommand.getAuthor());
        book.setIntroduction(bookAddCommand.getIntroduction());
        book.setPressmark(bookAddCommand.getPressmark());
        book.setLanguage(bookAddCommand.getLanguage());

        boolean succ = bookService.editBook(book);
        if (succ) {
            logger.info("Edit book[" + book.getBookId() + "] successfully");
            redirectAttributes.addFlashAttribute("succ", "图书修改成功！");
        } else {
            logger.error("Edit book[" + book.getBookId() + "] failed");
            redirectAttributes.addFlashAttribute("error", "图书修改失败！");
        }
        return "redirect:/allbooks.html";
    }

    @RequestMapping("/bookdetail.html")
    public ModelAndView bookDetail(HttpServletRequest request) {
        long bookId = Integer.parseInt(request.getParameter("bookId"));
        logger.info("Admin get book[" + bookId + "] detail");
        Book book = bookService.getBook(bookId);
        ModelAndView modelAndView = new ModelAndView("admin_book_detail");
        modelAndView.addObject("detail", book);
        return modelAndView;
    }

    @RequestMapping("/readerbookdetail.html")
    public ModelAndView readerBookDetail(HttpServletRequest request) {
        long bookId = Integer.parseInt(request.getParameter("bookId"));
        logger.info("Reader get book[" + bookId + "] detail");
        Book book = bookService.getBook(bookId);
        ModelAndView modelAndView = new ModelAndView("reader_book_detail");
        modelAndView.addObject("detail", book);
        return modelAndView;
    }
}
