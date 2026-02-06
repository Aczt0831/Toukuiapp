package com.Toukui.service.impl;

import com.Toukui.common.JwtUtil;
import com.Toukui.mapper.UserMapper;
import com.Toukui.pojo.User;
import com.Toukui.service.UserService;
import com.alibaba.fastjson.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RestTemplate restTemplate;

    // 微信配置
    @Value("${wx.appid}")
    private String appid;

    @Value("${wx.secret}")
    private String secret;

    @Value("${wx.code2session-url}")
    private String code2SessionUrl;



    // 随机密码长度
    private static final int PASSWORD_LEN = 12;




    @Override
    public void register(User user) {
        userMapper.register(user);
    }

    @Override
    public List<String> AllAccount(String account) {
        return userMapper.AllAccount(account);
    }

    @Override
    public List<User> getUserInfo(String id) {
        return userMapper.getUserInfoByZh(id);
    }

    @Override
    public String HandlePassword(User user) {
        return userMapper.HandlePassword(user);
    }

    @Override
    public int changeStyleList(String id, String stylelist) {
        return userMapper.changeStyleList(id, stylelist);
    }

    @Override
    public int changeName(String id, String username) {
        return userMapper.changeName(id, username);
    }

    @Override
    public int changeTx(String id, byte[] tximg) {
        return userMapper.changeTx(id, tximg);
    }

    @Override

    @SuppressWarnings("unchecked")
    public Map<String, Object> wxLogin(Map<String, Object> requestMap) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 获取小程序传递的code和userInfo
            String code = (String) requestMap.get("code");
            Map<String, Object> userInfoMap = (Map<String, Object>) requestMap.get("userInfo");

            if (code == null || userInfoMap == null) {
                result.put("code", 1);
                result.put("message", "参数缺失（code或userInfo）");
                return result;
            }

            // 2. 调用微信接口换取openid
            String openid = getOpenidByCode(code);
            if (openid == null) {
                result.put("code", 1);
                result.put("message", "获取微信身份失败");
                return result;
            }

            // 3. 提取用户昵称和头像URL
            String nickName = (String) userInfoMap.get("username");  // 小程序传递的username是微信昵称
            String avatarUrl = (String) userInfoMap.get("avatarUrl");

            // 4. 查询用户是否已存在
            User existUser = userMapper.selectByOpenid(openid);
            User loginUser;

            if (existUser != null) {
                // 5. 已存在：更新昵称和头像
                existUser.setUsername(nickName);
                existUser.setUsertx(avatarUrl != null ? getAvatarBytes(avatarUrl) : null);
                userMapper.updateById(existUser);
                loginUser = existUser;
            } else {
                // 6. 新用户：创建并插入数据库
                loginUser = new User();  // 使用你的User类
                loginUser.setUsername(nickName);          // 微信昵称
                loginUser.setAccount(openid);            // openid作为唯一账号
                loginUser.setPassword(generateRandomPwd());// 随机加密密码
                loginUser.setStyleList("");              // 个性签名默认空
                loginUser.setUsertx(avatarUrl != null ? getAvatarBytes(avatarUrl) : null);
                loginUser.setDz(0);                      // 点赞量默认0
                userMapper.insert(loginUser);
            }

            // 7. 生成JWT token
            String token = jwtUtil.generateToken(loginUser.getId());

            // 8. 构造响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("userId", loginUser.getId());
            responseData.put("token", token);

            // 构造用户信息VO
            Map<String, Object> userVO = new HashMap<>();
            userVO.put("id", loginUser.getId());
            userVO.put("username", loginUser.getUsername());
            userVO.put("styleList", loginUser.getStyleList());
            userVO.put("avatarUrl", avatarUrl);
            userVO.put("dz", loginUser.getDz());
            responseData.put("userInfo", userVO);

            // 9. 成功响应
            result.put("code", 0);
            result.put("message", "登录成功");
            result.put("data", responseData);

        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", "登录失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }



    /**
     * 调用微信官方接口换取openid
     */
    private String getOpenidByCode(String code) {
        try {

            String url = UriComponentsBuilder.fromHttpUrl(code2SessionUrl)
                    .queryParam("appid", appid)
                    .queryParam("secret", secret)
                    .queryParam("js_code", code)
                    .queryParam("grant_type", "authorization_code")
                    .toUriString();

            // 修改：将返回类型从 Map 改为 String
            String wxResponse = restTemplate.getForObject(url, String.class);

            // 手动解析JSON字符串
            Map<String, Object> responseMap = JSONObject.parseObject(wxResponse, Map.class);


            if (responseMap == null) {
                return null;
            }

            Integer errCode = (Integer) responseMap.get("errcode");
            if (errCode != null && errCode != 0) {
                System.err.println("微信接口错误：" + responseMap.get("errmsg"));
                return null;
            }

            return (String) responseMap.get("openid");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成随机加密密码
     */
    private String generateRandomPwd() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < PASSWORD_LEN; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return BCrypt.hashpw(sb.toString(), BCrypt.gensalt());
    }

    /**
     * 头像URL转二进制
     */
    private byte[] getAvatarBytes(String avatarUrl) throws IOException {
        URL url = new URL(avatarUrl);
        try (InputStream is = url.openStream();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        }
    }
}
