package com.travelapp.controller;

import com.travelapp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation
    .GetMapping;
import org.springframework.web.bind.annotation
    .PathVariable;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService
        notificationService;

    @GetMapping("/notifications/lue/{id}")
    public String marquerLue(
            @PathVariable Long id) {
        notificationService.marquerLue(id);
        return "redirect:/dashboard";
    }

    @GetMapping("/notifications/tout-lire")
    public String marquerToutesLues() {
        notificationService.marquerToutesLues();
        return "redirect:/dashboard";
    }
}