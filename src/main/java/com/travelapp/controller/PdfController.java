package com.travelapp.controller;

import com.travelapp.service.BilletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PdfController {

    private final BilletService billetService;

    @GetMapping("/reservations/billet/{id}")
    public ResponseEntity<byte[]> genererBillet(
            @PathVariable Long id) {
        try {
            byte[] pdf = billetService
                .genererBillet(id);

            String nomFichier = String.format(
                "billet-TRV-%d-%04d.pdf",
                java.time.LocalDate.now().getYear(),
                id);

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\""
                    + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

        } catch (Exception e) {
            return ResponseEntity
                .internalServerError()
                .build();
        }
    }

    @GetMapping("/reservations/billet/download/{id}")
    public ResponseEntity<byte[]> telechargerBillet(
            @PathVariable Long id) {
        try {
            byte[] pdf = billetService
                .genererBillet(id);

            String nomFichier = String.format(
                "billet-TRV-%d-%04d.pdf",
                java.time.LocalDate.now().getYear(),
                id);

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\""
                    + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

        } catch (Exception e) {
            return ResponseEntity
                .internalServerError()
                .build();
        }
    }
}