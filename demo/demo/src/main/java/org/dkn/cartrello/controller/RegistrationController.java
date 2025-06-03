package org.dkn.cartrello.controller;

import org.dkn.cartrello.Models.User;
import org.dkn.cartrello.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    // Показва формата за регистрация - достъпна без да си влязъл в системата
    @GetMapping("/register")
    public String showRegistrationForm(User user) {
        return "register";
    }

    // Показва формата за вход (логин) - достъпна без да си влязъл в системата
    @GetMapping("/login")
    public String showLoginForm(User user) {
        return "login";
    }

    // Обработва изпращането на формата за регистрация
    @PostMapping("/register")
    public String registerUser(@Valid User user, Model model) {
        userService.registerUser(user); // Регистрира потребителя чрез UserService
        model.addAttribute("message", "User successfully registered"); // Добавя съобщение за успех
        return "redirect:/login?registered"; // Пренасочва към страницата за вход с параметър за успешно регистриран
    }
}
