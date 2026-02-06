package com.Toukui.mapper;

import com.Toukui.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {


    @Insert("insert into userinfo (username, account, password, styleList, usertx, dz) values (#{username}, #{account}, #{password}, #{styleList}, #{usertx}, #{dz})")
    int register(User user);

    @Select("select account from userinfo where account = #{account}")
    List<String> AllAccount(String account);

    @Select("select * from userinfo where id = #{id}")
    List<User> getUserInfoByZh(String id);

    @Select("select id from userinfo where account = #{account} && password = #{password}")
    String HandlePassword(User user);

    @Update("update userinfo set styleList = #{styleList} where id = #{id}")
    int changeStyleList(@Param("id") String id, @Param("stylelist") String stylelist);

    @Update("update userinfo set username = #{username} where id = #{id}")
    int changeName(@Param("id") String id, @Param("username") String username);

    @Update("update userinfo set usertx = #{tximg} where id = #{id}")
    int changeTx(@Param("id") String id, @Param("tximg") byte[] tximg);

    /**
     * 用注解写SQL，无需XML文件
     */
    @Select("SELECT id, username, account, password, styleList, usertx, dz FROM userinfo WHERE account = #{openid}")
    User selectByOpenid(@Param("openid") String openid);

   
}
