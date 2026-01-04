package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;

// @Mapper: 这是一个非常关键的注解！
// 它告诉 Spring Boot："启动时请把这个接口扫描进去，并自动生成一个实现类代理。"
// 没有它，代码运行时会报错 "找不到 Bean"。
@Mapper
public interface UserMapper extends BaseMapper<User>{
    // 2. extends BaseMapper<User>:
    // 这就是 MyBatis-Plus 的神技。
    // 只要继承了这个接口，你的 UserMapper 瞬间就拥有了 insert, delete, update, selectById, selectList 等几十个方法。
    // 这里我们可以一行代码都不写！
}
