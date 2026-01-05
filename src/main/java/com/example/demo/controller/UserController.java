package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.common.Result;
import com.example.demo.dto.UpdateUserRequest;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserMapper userMapper;

    // 更新个人信息接口
    @PostMapping("/update")
    public Result<User> update(@RequestParam String username, @RequestBody UpdateUserRequest request) {
        // 1. 查人
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) return Result.error("用户不存在");

        // 2. 更新字段 (只更新不为空的)
        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getAge() != null) user.setAge(request.getAge());
        if (request.getBirthday() != null) user.setBirthday(request.getBirthday());
        if (request.getQq() != null) user.setQq(request.getQq());
        if (request.getWechat() != null) user.setWechat(request.getWechat());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getAvatar() != null) user.setAvatar(request.getAvatar());

        // 3. 保存到数据库
        userMapper.updateById(user);

        // 4. 返回最新信息 (隐去密码)
        user.setPassword(null);
        return Result.success(user, "保存成功");
    }
}
