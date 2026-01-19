package com.monefy.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monefy.entity.User;
import com.monefy.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

//    @GetMapping("/{id}")
//    public Optional<User> getUserById(@PathVariable Long id) {
//        return userService.getUserById(id);
//    }

    @GetMapping("/{username}")
    public Optional<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PutMapping("/{username}")
    public User updateUser(@PathVariable String username, @RequestBody User user) {
//        return userService.saveUser(user);
    	throw new UnsupportedOperationException();
    }

    @PostMapping("/signup")
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PostMapping("/verifyEmail")
    public User verifyEmail(@RequestBody User user) {
//        return userService.saveUser(user);
    	throw new UnsupportedOperationException();
//    	return null;
    }

    @GetMapping("/login")
    public Optional<User> login() {
    	throw new UnsupportedOperationException();
    }

    @GetMapping("/logout")
    public Optional<User> logout() {
    	throw new UnsupportedOperationException();
    }

    @PutMapping("/changePassword")
    public User changePassword(@RequestBody User user) {
    	throw new UnsupportedOperationException();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}