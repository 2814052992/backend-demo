package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data// 自动生成 Getter/Setter
// @TableName: 告诉 MyBatis-Plus，这个类对应数据库里的 "users" 表
// 如果不加这个，它默认会去找叫 "user" 的表，那就报错了
@TableName("users")
public class User {

    //  @TableId: 告诉它，这个字段是主键(Primary Key)
    // type = IdType.AUTO 表示数据库里设置了 id 自增，Java 这边不要瞎操心生成 id，听数据库的
    @TableId(type = IdType.AUTO)
    private Integer id;

    //  下面的属性名必须和数据库表的列名(username, password...)一模一样
    private String username;
    private String password;
    private String email;
    private String nickname;
}