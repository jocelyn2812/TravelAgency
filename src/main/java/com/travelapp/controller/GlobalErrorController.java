package com.travelapp.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error
    .ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation
    .RequestMapping;

@Controller
public class GlobalErrorController
        implements ErrorController {

    @RequestMapping("/error")
    public String handleError(
            HttpServletRequest request,
            Model model) {

        Object status = request.getAttribute(
            RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(
            RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(
            RequestDispatcher.ERROR_EXCEPTION);
        Object exceptionType = request.getAttribute(
            RequestDispatcher.ERROR_EXCEPTION_TYPE);
        Object uri = request.getAttribute(
            RequestDispatcher.ERROR_REQUEST_URI);

        model.addAttribute("status", status);
        model.addAttribute("message", message);
        model.addAttribute("uri", uri);

        System.out.println(
            "========== ERREUR DETAIL ==========");
        System.out.println("URI: " + uri);
        System.out.println("Status: " + status);
        System.out.println("Message: " + message);
        System.out.println("Exception type: "
            + exceptionType);

        if (exception != null) {
            System.out.println(
                "Exception: " + exception);
            ((Throwable) exception)
                .printStackTrace();
            model.addAttribute("exception",
                exception.toString());
        }
        System.out.println(
            "====================================");

        return "error";
    }
}