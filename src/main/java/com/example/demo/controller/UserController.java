package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.UpdateUserRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    // 更新个人信息接口
    @PostMapping("/update")
    public Result<User> update(@RequestParam String username, @RequestBody UpdateUserRequest request) {
        return userService.updateUserInfo(username, request);
    }
}
