package com.hotelprject.hotelproject.controller;

import com.hotelprject.hotelproject.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final OrderService orderService;

    @PostMapping("/reserve/process-payment")
    public String processPayment(
            @RequestParam("cardHolderName") String cardHolderName,
            @RequestParam("cardNumber") String cardNumber,
            @RequestParam("expiryDate") String expiryDate,
            @RequestParam("cvv") String cvv,
            HttpSession session,
            Model model
    ) {
        try {
            String username = (String) session.getAttribute("loggedInUser");
            if (username == null) {
                return "redirect:/login";
            }


            // Session'dan rezervasyon bilgilerini temizle
            session.removeAttribute("pendingReservation");

            // Success sayfası için bilgileri ekle
            model.addAttribute("successMessage", "Ödemeniz başarıyla tamamlandı!");

            return "payment-success";
        } catch (Exception e) {
            model.addAttribute("error", "Ödeme işlemi sırasında bir hata oluştu: " + e.getMessage());
            return "payment";
        }
    }
}