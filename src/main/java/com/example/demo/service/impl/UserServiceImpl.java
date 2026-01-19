package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.example.demo.common.JwtUtils;
import com.example.demo.common.Result;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.ResetPasswordRequest;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import com.example.demo.dto.UpdateUserRequest;
import com.example.demo.service.EmailService;

@Service
public class UserServiceImpl implements UserService {

    // @Autowired: 自动注入
    // 意思是：Spring Boot，请你帮我把刚才定义的 UserMapper 实例化，并塞到这里来。
    // 这样我们就可以直接用 userMapper 操作数据库了。
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private EmailService emailService;

    @Override
    public Result<Map<String, Object>> login(LoginRequest request) {

        // user，因为我们要返回用户详情给前端
        // 构建查询条件
        // QueryWrapper 是 MyBatis-Plus 提供的"条件构造器"
        // 翻译成 SQL 就是：SELECT * FROM users WHERE username = '?'
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", request.getUsername());
        // 执行查询
        // selectOne 意思是：根据条件查一条数据。
        // 如果查到了，返回 User 对象；如果没查到，返回 null。
        User user = userMapper.selectOne(queryWrapper);

        //判断有没有这个账号
        if (user == null) {
            return Result.error("账号不存在");
        }

        // 用户输入的密码是 123456 (明文)
        // 数据库里的密码是 e10adc3949ba59abbe56e057f20f883e (密文)
        // 我们把输入的 123456 也加密成密文，再比较是否相等
        String inputPwd = DigestUtils.md5DigestAsHex(request.getPassword().getBytes());
        if (!user.getPassword().equals(inputPwd)) {
            return Result.error("密码错误");
        }

        // 生成 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        String token = JwtUtils.generateToken(claims);

        // 返回结果
        user.setPassword(null); // 擦除密码
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);

        return Result.success(data, "登录成功");
    }

    @Override
    public Result<String> register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return Result.error("两次密码不一致");
        }

        //校验验证码
        //从 Redis 提取验证码
        String redisCode = redisTemplate.opsForValue().get("code:" + request.getEmail());
        if(redisCode == null) {
            return Result.error("验证码已失效，请重新发送");
        }
        if(!redisCode.equals(request.getCode())){
            return Result.error("验证码错误");
        }
        redisTemplate.delete("code:" + request.getEmail());

        // 查重：用户名
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", request.getUsername());
        if (userMapper.selectCount(queryWrapper) > 0) {
            return Result.error("用户名已被注册");
        }

        // 查重：邮箱
        QueryWrapper<User> emailWrapper = new QueryWrapper<>();
        emailWrapper.eq("email", request.getEmail());
        if (userMapper.selectCount(emailWrapper) > 0) {
            return Result.error("邮箱已被占用");
        }

        // 创建新用户并保存
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(DigestUtils.md5DigestAsHex(request.getPassword().getBytes())); // 加密
        user.setEmail(request.getEmail());
        user.setNickname("新用户" + System.currentTimeMillis());

        userMapper.insert(user);

        return Result.success(null, "注册成功");
    }

    @Override
    public Result<String> resetPassword(ResetPasswordRequest request) {
        String redisKey = "code:" + request.getEmail();
        String redisCode = redisTemplate.opsForValue().get(redisKey);

        if(redisCode == null) {
            return Result.error("验证码已失效，请重新发送");
        }
        if (!redisCode.equals(request.getCode())) {
            return Result.error("验证码错误");
        }
        redisTemplate.delete(redisKey);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", request.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) return Result.error("用户不存在");
        if (!user.getEmail().equals(request.getEmail())) return Result.error("邮箱验证失败");

        // 更新密码
        user.setPassword(DigestUtils.md5DigestAsHex(request.getNewPassword().getBytes()));
        userMapper.updateById(user);

        return Result.success(null, "密码重置成功");
    }

    @Override
    public Result<User> updateUserInfo(String username, UpdateUserRequest request) {
        // 查人
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            return Result.error("用户不存在");
        }

        // 更新字段 (只更新不为空的)
        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getAge() != null) user.setAge(request.getAge());
        if (request.getBirthday() != null) user.setBirthday(request.getBirthday());
        if (request.getQq() != null) user.setQq(request.getQq());
        if (request.getWechat() != null) user.setWechat(request.getWechat());
        if (request.getBio() != null) user.setBio(request.getBio());
        // 头像处理（如果是 Base64 可能会很长，存进去没问题）
        if (request.getAvatar() != null) user.setAvatar(request.getAvatar());

        // 保存到数据库
        userMapper.updateById(user);

        // 返回最新信息 (隐去密码，防止泄露)
        user.setPassword(null);
        return Result.success(user, "保存成功");
    }

    @Override
    public Result<String> sendCode(String email) {
        //简单校验
        if(email == null || email.isEmpty()){
            return Result.error("邮箱不能为空");
        }

        //生成六位数验证码
        // 生成 0~899999 之间的数，然后加 100000，确保肯定是6位数
        String code = String.valueOf(new Random().nextInt(900000)+100000);

        //存入 Redis
        //key:"code:用户邮箱"
        //Value:验证码
        //Time:五分钟过期
        redisTemplate.opsForValue().set("code:" + email,code,5,TimeUnit.MINUTES);

        //发送邮件
        emailService.sendSimpleMail(email,"注册验证码","【个人数字空间】您的验证码是：" + code + "，有效期5分钟，请勿泄露给他人。");

        return Result.success(null,"验证码已发送，请查收");
    }
}
