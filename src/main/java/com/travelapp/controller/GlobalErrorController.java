package com.travelapp.controller;

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
            "javax.servlet.error.status_code");
        Object message = request.getAttribute(
            "javax.servlet.error.message");
        Object exception = request.getAttribute(
            "javax.servlet.error.exception");

        model.addAttribute("status", status);
        model.addAttribute("message", message);

        if (exception != null) {
            model.addAttribute("exception",
                exception.toString());
        }

        System.out.println(
            "❌ ERREUR : status=" + status
            + " message=" + message
            + " exception=" + exception);

        return "error";
    }
}