package com.hotelprject.hotelproject.controller;

import com.hotelprject.hotelproject.model.Room;
import com.hotelprject.hotelproject.model.Reservation;
import com.hotelprject.hotelproject.repository.ReservationRepository;
import com.hotelprject.hotelproject.service.ReservationService;
import com.hotelprject.hotelproject.service.RoomService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final RoomService roomService;
    private final ReservationRepository reservationRepository;

    @GetMapping("/reserve")
    public String showReservationPage(@RequestParam("roomId") Long roomId, Model model) {
        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            model.addAttribute("error", "Oda bulunamadı!");
            return "error";
        }

        List<Reservation> reservations = reservationRepository.findByRoomAndStatus(room, "ONAYLANDI");
        List<Map<String, String>> bookedDates = new ArrayList<>();

        for (Reservation res : reservations) {
            Map<String, String> dateRange = new HashMap<>();
            dateRange.put("start", res.getReservationDate().toString());
            dateRange.put("end", res.getEndDate().toString());
            bookedDates.add(dateRange);
        }

        model.addAttribute("room", room);
        model.addAttribute("bookedDates", bookedDates);
        model.addAttribute("properties", room.getRoomProperties().stream()
                .map(property -> property.getProperty())
                .toList());
        return "reserve";
    }

    @PostMapping("/reserve")
    public String processReservation(
            @RequestParam("roomId") Long roomId,
            @RequestParam("orderDate") String orderDate,
            @RequestParam("quantity") int quantity,
            HttpSession session,
            Model model
    ) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username == null) {
            return "redirect:/login";
        }

        try {
            Room room = roomService.getRoomById(roomId);
            if (room == null) {
                throw new RuntimeException("Oda bulunamadı!");
            }


            double totalPrice = room.getPrice() * quantity;

            model.addAttribute("room", room);
            model.addAttribute("orderDate", orderDate);
            model.addAttribute("quantity", quantity);
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("customerName", username);

            session.setAttribute("pendingReservation", Map.of(
                    "roomId", roomId,
                    "orderDate", orderDate,
                    "quantity", quantity,
                    "totalPrice", totalPrice
            ));

            return "payment";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/my-reservations")
    public String showMyReservations(HttpSession session, Model model) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username == null) {
            return "redirect:/login";
        }

        List<Reservation> reservations = reservationRepository.findAll().stream()
                .filter(r -> r.getUserInfo().equals(username))
                .toList();

        model.addAttribute("reservations", reservations);
        return "my-reservations";
    }

    // ID ile iptal sayfasını göster
    @GetMapping("/cancel-reservation")
    public String showCancelPage() {
        return "cancel-reservation";
    }

    // Form ile oda ID'si girerek iptal
    @PostMapping("/cancel-reservation")
    public String cancelReservationWithRoomId(@RequestParam("reservationId") Long roomId,
                                              HttpSession session,
                                              Model model) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username == null) {
            return "redirect:/login";
        }

        try {
            // Kullanıcının bu odadaki aktif rezervasyonunu bul
            List<Reservation> userReservations = reservationRepository.findAll().stream()
                    .filter(r -> r.getUserInfo().equals(username) &&
                            r.getRoom().getId().equals(roomId) &&
                            r.getStatus().equals("ONAYLANDI"))
                    .toList();

            if (userReservations.isEmpty()) {
                throw new RuntimeException("Bu oda numarasına ait aktif rezervasyonunuz bulunmamaktadır!");
            }

            // İlk bulunan rezervasyonu iptal et
            Reservation reservation = userReservations.get(0);
            reservationService.cancelReservation(reservation.getId(), username);
            model.addAttribute("message", "Rezervasyon başarıyla iptal edildi.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "cancel-reservation";
    }

    // Direkt link ile iptal
    @GetMapping("/cancel-reservation/{id}")
    public String cancelReservation(@PathVariable Long id,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username == null) {
            return "redirect:/login";
        }

        try {
            reservationService.cancelReservation(id, username);
            redirectAttributes.addFlashAttribute("message", "Rezervasyon başarıyla iptal edildi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/my-reservations";
    }
}