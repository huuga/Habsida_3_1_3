package ru.kata.spring.boot_security.demo.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserServiceImp;

import java.security.Principal;


@Controller
public class UserController {

    private final UserServiceImp userServiceImp;

    public UserController(UserServiceImp userServiceImp) {
        this.userServiceImp = userServiceImp;
    }

    @RequestMapping(value = "/start")
    public ModelAndView start() {
        User root = new User("root",
                "{noop}root",
                "root@mail.com", Role.ROLE_ADMIN, Role.ROLE_USER);
        User user = new User("user",
                "{noop}user",
                "user@mail.com", Role.ROLE_USER);
        userServiceImp.addUser(root);
        userServiceImp.addUser(user);
        return new ModelAndView("start_page");
    }

    @RequestMapping("/")
    public ModelAndView indexPage(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("authenticated", true);
            User user = (User) authentication.getPrincipal();
            model.addAttribute("username", user.getUsername());
        } else {
            model.addAttribute("authenticated", false);
        }
        return new ModelAndView("index");
    }

    @RequestMapping(value = "/admin")
    public ModelAndView usersManage(Model model) {
        model.addAttribute("usersList", userServiceImp.getUsersList());
        return new ModelAndView("admin_page");
    }

    @RequestMapping(value = "/admin/add")
    public ModelAndView addUser(Model model) {
        User user = new User();
        user.setEnabled(true);
        Role[] rolesArr = Role.values();
        model.addAttribute("roles_list", rolesArr);
        model.addAttribute("editable_user", user);
        return new ModelAndView("user_edit");
    }

    @GetMapping("/admin/edit/{id}")
    public ModelAndView editUser(Model model, @PathVariable("id") String userId) {
        try {
            User editableUser = userServiceImp.findUserById(Long.parseLong(userId));
            Role[] rolesArr = Role.values();
            model.addAttribute("roles_list", rolesArr);
            model.addAttribute("editable_user", editableUser);

            return new ModelAndView("user_edit");
        } catch (NumberFormatException nfe) {
            return new ModelAndView("404");
        }
    }

    @PostMapping("/admin/edit")
    public ModelAndView saveEditedUser(@ModelAttribute("editableUser") User editedUser, Model model) {
        try{
            userServiceImp.updateUser(editedUser);
            return new ModelAndView("redirect:/admin");
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("usersList", userServiceImp.getUsersList());
            return new ModelAndView("redirect:/user_already_exist");
        }

    }

    @RequestMapping("/admin/delete/{id}")
    public ModelAndView deleteUser(@PathVariable("id") String userId) {
        User user = userServiceImp.findUserById(Long.parseLong(userId));
        user.getAuthorities().clear();
        userServiceImp.removeUser(Long.parseLong(userId));
        return new ModelAndView("redirect:/admin");
    }

    @RequestMapping("/user")
    public ModelAndView usersPage(Principal principal, Model model) {
        User user = (User) ((Authentication) principal).getPrincipal();
        model.addAttribute("principal", user);
        return new ModelAndView("user");
    }

    @RequestMapping("/403")
    public ModelAndView accessDenied() {
        return new ModelAndView("403");
    }

    @RequestMapping("/user_already_exist")
    public ModelAndView usernameAlreadyExist(){
        return new ModelAndView("user_already_exist_page");
    }

}
