package org.dkn.cartrello.controller;

import org.dkn.cartrello.model.ForumPost;
import org.dkn.cartrello.model.Car;
import org.dkn.cartrello.model.Reply;
import org.dkn.cartrello.repository.CarRepository;
import org.dkn.cartrello.repository.ForumPostRepository;
import org.dkn.cartrello.repository.ReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ForumController {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ForumPostRepository forumRepository;

    @Autowired
    private ReplyRepository replyRepository;

    private void checkPermission(Authentication auth, String author) {
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Not authenticated");
        }
        String currentUser = auth.getName();
        if (!isAdmin(auth) && !currentUser.equals(author)) {
            throw new AccessDeniedException("Not authorized");
        }
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"));
    }


    @GetMapping("/car/{carId}/forums")
    public String showForumsForCar(@PathVariable Long carId, Model model) {
        Car car = carRepository.findById(carId).orElseThrow();
        List<ForumPost> forums = forumRepository.findByCar(car);
        model.addAttribute("car", car);
        model.addAttribute("forums", forums);
        return "car_forums";
    }

    @PostMapping("/car/{id}/forum/add")
    public String addForumPost(@PathVariable Long id,
                               @RequestParam String title,
                               @RequestParam String content,
                               Authentication authentication) {

        Car car = carRepository.findById(id).orElseThrow();

        ForumPost post = new ForumPost();
        post.setTitle(title);
        post.setContent(content);
        post.setCar(car);
        post.setAuthor(authentication.getName()); // Just set the username string

        forumRepository.save(post);
        return "redirect:/car/" + id;
    }

    @PostMapping("/car/{carId}/forum/{forumId}/reply")
    public String addReply(@PathVariable Long carId,
                           @PathVariable Long forumId,
                           @RequestParam String replyContent) {
        ForumPost forumPost = forumRepository.findById(forumId).orElseThrow();
        Reply reply = new Reply();

        reply.setContent(replyContent);
        reply.setForumPost(forumPost);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


            reply.setAuthor(authentication.getName());


        replyRepository.save(reply);


        // Redirect to the GET handler that loads the car forums page
        return "redirect:/car/" + carId;
    }

    // --- Edit forum post ---
    @GetMapping("/car/{carId}/forum/{forumId}/edit")
    public String showEditForumPostForm(@PathVariable Long carId,
                                        @PathVariable Long forumId,
                                        Model model) {
        ForumPost forum = forumRepository.findById(forumId).orElseThrow();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkPermission(auth, forum.getAuthor());

        model.addAttribute("forum", forum);
        model.addAttribute("carId", carId);
        return "forum_edit_form";
    }


    @PostMapping("/car/{carId}/forum/{forumId}/edit")
    public String updateForumPost(@PathVariable Long carId,
                                  @PathVariable Long forumId,
                                  @RequestParam String title,
                                  @RequestParam String content) {
        ForumPost forum = forumRepository.findById(forumId).orElseThrow();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkPermission(auth, forum.getAuthor());

        forum.setTitle(title);
        forum.setContent(content);
        forumRepository.save(forum);

        return "redirect:/car/" + carId;
    }
    // --- Delete forum post ---
    @PostMapping("/forum/{forumId}/delete")
    public String deleteForumPost(@PathVariable Long forumId) {
        ForumPost forum = forumRepository.findById(forumId).orElseThrow();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkPermission(auth, forum.getAuthor());

        Long carId = forum.getCar().getId();
        replyRepository.deleteAll(forum.getReplies());
        forumRepository.delete(forum);

        return "redirect:/car/" + carId;
    }

    // --- Edit reply ---
    @GetMapping("/reply/{replyId}/edit")
    public String showEditReplyForm(@PathVariable Long replyId, Model model) {
        Reply reply = replyRepository.findById(replyId).orElseThrow();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkPermission(auth, reply.getAuthor());

        model.addAttribute("reply", reply);
        return "edit_reply";
    }

    @PostMapping("/reply/{replyId}/edit")
    public String updateReply(@PathVariable Long replyId, @RequestParam String content) {
        Reply reply = replyRepository.findById(replyId).orElseThrow();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkPermission(auth, reply.getAuthor());

        reply.setContent(content);
        replyRepository.save(reply);

        return "redirect:/car/" + reply.getForumPost().getCar().getId();
    }

    @PostMapping("/reply/{replyId}/delete")
    public String deleteReply(@PathVariable Long replyId) {
        Reply reply = replyRepository.findById(replyId).orElseThrow();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkPermission(auth, reply.getAuthor());

        Long carId = reply.getForumPost().getCar().getId();
        replyRepository.delete(reply);
        return "redirect:/car/" + carId;
    }

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "ForumController is alive";
    }
}