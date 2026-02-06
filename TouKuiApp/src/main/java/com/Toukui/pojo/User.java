package com.Toukui.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("userinfo")

public class User {
    // 关键添加：标记 id 为自增主键
    @TableId(type = IdType.AUTO)
    private String id;
    private String username;
    private String account;
    private String password;
    @TableField("styleList")
    private String styleList;//个性签名
    private byte[] usertx;
    private Integer dz;
    
    // getter and setter methods
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getAccount() {
        return account;
    }
    
    public void setAccount(String account) {
        this.account = account;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getStyleList() {
        return styleList;
    }
    
    public void setStyleList(String styleList) {
        this.styleList = styleList;
    }
    
    public byte[] getUsertx() {
        return usertx;
    }
    
    public void setUsertx(byte[] usertx) {
        this.usertx = usertx;
    }
    
    public Integer getDz() {
        return dz;
    }
    
    public void setDz(Integer dz) {
        this.dz = dz;
    }


}
