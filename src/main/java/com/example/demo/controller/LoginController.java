package com.example.demo.controller;

import org.springframework.util.DigestUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;

import com.example.demo.common.JwtUtils;
import com.example.demo.common.Result;
import com.example.demo.dto.LoginRequest;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.ResetPasswordRequest;

@Tag(name = "身份认证接口")
@RestController
@CrossOrigin
@RequestMapping("/api")
public class LoginController {

    // 1. @Autowired: 自动注入
    // 意思是：Spring Boot，请你帮我把刚才定义的 UserMapper 实例化，并塞到这里来。
    // 这样我们就可以直接用 userMapper 操作数据库了。
    @Autowired
    private UserMapper userMapper;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        // (Result<User> 这里改成 User，因为我们要返回用户详情给前端)

        // 2. 构建查询条件
        // QueryWrapper 是 MyBatis-Plus 提供的"条件构造器"
        // 翻译成 SQL 就是：SELECT * FROM users WHERE username = '?'
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", loginRequest.getUsername());

        // 3. 执行查询
        // selectOne 意思是：根据条件查一条数据。
        // 如果查到了，返回 User 对象；如果没查到，返回 null。
        User user = userMapper.selectOne(queryWrapper);

        // 4. 判断逻辑
        if (user == null) {
            return Result.error("账号不存在");
        }

        // 用户输入的密码是 123456 (明文)
        // 数据库里的密码是 e10adc3949ba59abbe56e057f20f883e (密文)
        // 我们把输入的 123456 也加密成密文，再比较是否相等
        String inputPasswordEncrypted = DigestUtils.md5DigestAsHex(loginRequest.getPassword().getBytes());
        if (!user.getPassword().equals(inputPasswordEncrypted)) {
            return Result.error("密码错误");
        }

        // 生成 Token
        // 我们把用户的 id 和 username 藏在 Token 里，以后看到 Token 就知道是谁
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());

        String token = JwtUtils.generateToken(claims);

        // 打包返回数据
        user.setPassword(null); // 依然要把密码擦除

        Map<String, Object> data = new HashMap<>();
        data.put("token", token); // 放通行证
        data.put("user", user);   // 放用户信息

        return Result.success(data, "登录成功");
    }


    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterRequest registerRequest) {
        //检查两次密码是否一致
        if(!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())){
            return Result.error("两次密码不一致");
        }

        //检查用户名是否被注册
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",registerRequest.getUsername());
        if(userMapper.selectCount(queryWrapper) > 0){
            return Result.error("用户名已被注册");
        }

        //检查邮箱是否被占用
        QueryWrapper<User> emailWrapper = new QueryWrapper<>();
        emailWrapper.eq("email",registerRequest.getEmail());
        if(userMapper.selectCount(emailWrapper) > 0){
            return Result.error("邮箱已被占用");
        }

        //创建新用户并保存
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        String encryptedPassword = DigestUtils.md5DigestAsHex(registerRequest.getPassword().getBytes());
        user.setPassword(encryptedPassword);
        user.setEmail(registerRequest.getEmail());
        user.setNickname("新用户"+System.currentTimeMillis());//默认给个昵称

        userMapper.insert(user);

        return  Result.success(null,"注册成功");
    }

    //密码找回接口
    @Operation(summary = "找回密码")
    @PostMapping("/reset-password")
    public Result<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        // 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", request.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        //判断用户书否存在
        if(user == null){
            return Result.error("用户不存在");
        }

        //对比数据库里的邮箱和用户输入的邮箱是否一致
        if(!user.getEmail().equals(request.getEmail())){
            return Result.error("验证失败,请检查邮箱是否正确");
        }

        String newPasswordEncrypted = DigestUtils.md5DigestAsHex(request.getNewPassword().getBytes());
        user.setPassword(newPasswordEncrypted);
        userMapper.updateById(user);

        return Result.success(null,"密码重置成功");
    }
}
