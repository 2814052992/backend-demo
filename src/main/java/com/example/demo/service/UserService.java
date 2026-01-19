package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.common.Result;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UpdateUserRequest;
import com.example.demo.dto.ResetPasswordRequest;
import java.util.Map;

// 接口：只写函数名，不写大括号里的内容
public interface UserService {

    // 登录服务
    Result<Map<String, Object>> login(LoginRequest request);

    // 注册服务
    Result<String> register(RegisterRequest request);

    // 重置密码服务
    Result<String> resetPassword(ResetPasswordRequest request);

    // 更新用户信息服务
    // 输入：用户名，更新请求包
    // 输出：更新后的 User 对象（给前端刷新缓存用）
    Result<User> updateUserInfo(String username, UpdateUserRequest request);

    //发送邮箱验证码,type 参数暂时预留，以后可以区分是"注册"还是"找回密码"
    Result<String> sendCode(String email);
}