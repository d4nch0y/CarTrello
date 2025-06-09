package org.dkn.cartrello.controller;

import org.dkn.cartrello.repository.CarRepository;
import org.dkn.cartrello.repository.ForumPostRepository;
import org.dkn.cartrello.model.Car;
import org.dkn.cartrello.model.ForumPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Controller // Указва, че това е Spring MVC контролер – управлява HTTP заявки и връща изгледи
public class CarController {

    @Autowired
    private CarRepository carRepository; // Инжектира CarRepository за достъп до базата с коли

    @Autowired
    private ForumPostRepository forumRepository; // Инжектира ForumPostRepository за достъп до форум постове, свързани с кола

    @GetMapping("/") // Обработва заявка към кореновия адрес
    public String redirectToLogin() {
        return "redirect:/login"; // Пренасочва към login страницата
    }

    @GetMapping("/home") // Обработва заявка към началната страница
    public String showHomePage(@RequestParam(required = false) String brand,
                               @RequestParam(required = false) String model,
                               @RequestParam(required = false) Integer year,
                               @RequestParam(required = false) String owner,
                               Model modelAttr) {

        // Търси коли по подадените критерии. Ако даден параметър е празен – го игнорира
        List<Car> cars = carRepository.searchCars(
                (brand != null && !brand.isBlank()) ? brand : null,
                (model != null && !model.isBlank()) ? model : null,
                year,
                (owner != null && !owner.isBlank()) ? owner : null
        );

        modelAttr.addAttribute("cars", cars); // Добавя резултатите към модела
        return "car_home"; // Връща изгледа с име car_home
    }

    @GetMapping("/car/add") // Показва формата за добавяне на нова кола
    public String showAddCarForm(Model model) {
        model.addAttribute("car", new Car()); // Добавя празен Car обект към модела
        return "car_form"; // Връща car_form HTML шаблон
    }

    @PostMapping("/car/add") // Обработва изпратена форма за нова кола
    public String addCar(@ModelAttribute Car car,
                         @RequestParam("image") MultipartFile image) throws IOException {

        // Взема текущо логнатия потребител
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        car.setOwnerUsername(currentUsername); // Записва потребителя като собственик на колата

        if (!image.isEmpty()) {
            // Генерира уникално име за снимката и я записва в папка uploads
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get("upload");
            Files.copy(image.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            car.setPhotoFilename(fileName); // Записва името на снимката в обекта
        }

        carRepository.save(car); // Запазва колата в базата
        return "redirect:/car/" + car.getId(); // Пренасочва към детайлите на новата кола
    }

    @GetMapping("/car/{id}") // Показва подробности за дадена кола
    public String viewCarDetails(@PathVariable Long id, Model model, HttpServletRequest request) {
        Car car = carRepository.findById(id).orElseThrow(); // Взема колата по ID
        List<ForumPost> forums = forumRepository.findByCar(car); // Взема постове, свързани с колата
        model.addAttribute("car", car);
        model.addAttribute("forums", forums);

        // Добавя CSRF токен за формите в Thymeleaf
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("_csrf", csrfToken);

        return "car_details"; // Връща изглед с детайли за колата
    }

    @GetMapping("/car/{id}/edit") // Показва формата за редактиране на кола
    public String showEditCarForm(@PathVariable Long id, Model model) {
        Car car = carRepository.findById(id).orElseThrow();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        // Проверява дали текущият потребител е собственик или администратор
        boolean isOwner = currentUsername.equals(car.getOwnerUsername());
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            return "redirect:/car/" + id; // Ако няма права, връща към детайлите
        }

        model.addAttribute("car", car); // Добавя колата към модела
        return "car_form"; // Използва същата форма като при добавяне
    }

    @PostMapping("/car/{id}/edit") // Обработва редакцията на колата
    public String editCar(@PathVariable Long id, @ModelAttribute Car updatedCar,
                          @RequestParam("image") MultipartFile image) throws IOException {
        Car car = carRepository.findById(id).orElseThrow();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        // Проверка за собственик или админ
        boolean isOwner = currentUsername.equals(car.getOwnerUsername());
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            return "redirect:/car/" + id;
        }

        // Обновява полетата на колата (без снимката)
        car.setBrand(updatedCar.getBrand());
        car.setModel(updatedCar.getModel());
        car.setYear(updatedCar.getYear());
        car.setEngineType(updatedCar.getEngineType());
        car.setHorsepower(updatedCar.getHorsepower());
        car.setEngineDisplacement(updatedCar.getEngineDisplacement());
        car.setEngineCode(updatedCar.getEngineCode());

        // Ако е подадена нова снимка, заменя старата
        if (!image.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get("upload");

            Files.copy(image.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            car.setPhotoFilename(fileName);
        }

        carRepository.save(car); // Запазва обновената кола
        return "redirect:/car/" + id; // Връща се към детайлите
    }

    @PostMapping("/car/{id}/delete") // Изтрива колата
    public String deleteCar(@PathVariable Long id) {
        Car car = carRepository.findById(id).orElseThrow();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        // Проверява дали текущият потребител има право да изтрие колата
        boolean isOwner = currentUsername.equals(car.getOwnerUsername());
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            return "redirect:/car/" + id; // Ако няма права – отказ
        }

        carRepository.delete(car); // Изтрива колата от базата
        return "redirect:/home"; // Пренасочва към началната страница
    }
}
