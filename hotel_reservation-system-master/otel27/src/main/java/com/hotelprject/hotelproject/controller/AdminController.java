package com.hotelprject.hotelproject.controller;

import com.hotelprject.hotelproject.model.Customer;
import com.hotelprject.hotelproject.model.Product;
import com.hotelprject.hotelproject.model.dto.ReservationDto;
import com.hotelprject.hotelproject.service.AdminService;
import com.hotelprject.hotelproject.service.HotelUserService;
import com.hotelprject.hotelproject.service.OrderService;
import com.hotelprject.hotelproject.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final HotelUserService hotelUserService;
    private final OrderService orderService;
    private final ProductService productService;

    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "adminLogin";
    }

    @PostMapping("/admin/login")
    public String adminLogin(@RequestParam String email,
                             @RequestParam String password,
                             HttpSession session,
                             Model model) {
        Optional<Customer> adminUserOptional = hotelUserService.findAdminUserByEmailAndPassword(email, password);

        if (adminUserOptional.isPresent()) {
            session.setAttribute("loggedInAdmin", email);
            return "redirect:/admin/panel";
        } else {
            model.addAttribute("loginError", "Email veya şifre yanlış. Lütfen tekrar deneyin!");
            return "adminLogin";
        }
    }

    @GetMapping("/admin/panel")
    public String adminPanel(HttpSession session, Model model) {
        String adminEmail = (String) session.getAttribute("loggedInAdmin");
        if (adminEmail == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("loggedInAdmin", adminEmail);
        return "adminPanel";
    }

    @PostMapping("/admin/logout")
    public String adminLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    @GetMapping("/admin/manage-users")
    public String manageUsers(HttpSession session, Model model) {
        String adminEmail = (String) session.getAttribute("loggedInAdmin");
        if (adminEmail == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("users", hotelUserService.findAll());
        return "manage-users";
    }

    @GetMapping("/admin/manage-reservations")
    public String manageReservations(Model model, HttpSession session) {
        String adminEmail = (String) session.getAttribute("loggedInAdmin");
        if (adminEmail == null) {
            return "redirect:/admin/login";
        }

        try {
            List<ReservationDto> reservationDtoList = orderService.findAllReservations()
                    .stream()
                    .map(r -> ReservationDto.builder()
                            .id(r.getId())
                            .fullName(r.getUserInfo())
                            .roomNumber(r.getProduct().getId())
                            .checkInDate(r.getReservationDate())
                            .checkOutDate(r.getEndDate())
                            .totalPrice(r.getProduct().getPrice())
                            .status(r.getStatus())
                            .build())
                    .toList();

            model.addAttribute("reservations", reservationDtoList);
            return "manage-reservations";
        } catch (Exception e) {
            model.addAttribute("error", "Rezervasyonları yüklerken bir hata oluştu: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/admin/manage-rooms")
    public String manageRooms(Model model, HttpSession session) {
        String adminEmail = (String) session.getAttribute("loggedInAdmin");
        if (adminEmail == null) {
            return "redirect:/admin/login";
        }

        List<Product> products = productService.getAllProducts();
        model.addAttribute("rooms", products);
        return "manage-rooms";
    }

    @GetMapping("/admin/add-room")
    public String showAddRoomForm(HttpSession session) {
        String adminEmail = (String) session.getAttribute("loggedInAdmin");
        if (adminEmail == null) {
            return "redirect:/admin/login";
        }
        return "add-room";
    }

    @PostMapping("/admin/add-room")
    public String addRoom(@RequestParam String name,
                          @RequestParam Double price,
                          @RequestParam Integer size,
                          HttpSession session) {
        String adminEmail = (String) session.getAttribute("loggedInAdmin");
        if (adminEmail == null) {
            return "redirect:/admin/login";
        }

        try {
            Product product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setSize(size);
            product.setAvailable(true);
            productService.saveRoom(product);
            return "redirect:/admin/manage-rooms?success=true";
        } catch (Exception e) {
            return "redirect:/admin/manage-rooms?error=true";
        }
    }

    @PostMapping("/admin/delete-room/{id}")
    public String deleteRoom(@PathVariable("id") Long roomId, HttpSession session) {
        String adminEmail = (String) session.getAttribute("loggedInAdmin");
        if (adminEmail == null) {
            return "redirect:/admin/login";
        }

        try {
            productService.deleteRoom(roomId);
            return "redirect:/admin/manage-rooms?success=true";
        } catch (Exception e) {
            return "redirect:/admin/manage-rooms?error=true";
        }
    }
}