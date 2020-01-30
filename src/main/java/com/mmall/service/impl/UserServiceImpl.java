package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import static java.util.Objects.isNull;

/**
 * create by Stefan on 2020-01-29
 */
@Service("userService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByError("用户名不存在");
        }
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (isNull(user)) {
            return ServerResponse.createByError("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse<String> usernameResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!usernameResponse.isSuccess()) {
            return usernameResponse;
        }
        ServerResponse<String> emailResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!emailResponse.isSuccess()) {
            return emailResponse;
        }
        user.setRole(Const.Role.CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByError("注册失败");
        }
        return ServerResponse.createBySuccess("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String val, String type) {
        if (StringUtils.isNotBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(val);
                if (resultCount > 0) {
                    return ServerResponse.createByError("用户名已存在");
                }
            } else if (Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(val);
                if (resultCount > 0) {
                    return ServerResponse.createByError("Email已存在");
                }
            }
        } else {
            return ServerResponse.createByError("参数错误");
        }
        return ServerResponse.createBySuccess("校验成功");
    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse<String> response = this.checkValid(username, Const.USERNAME);
        if (response.isSuccess()) {
            return ServerResponse.createByError("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isBlank(question)) {
            return ServerResponse.createByError("找回密码的问题是空的");
        }
        return ServerResponse.createBySuccess(question);
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByError("问题的答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByError("参数错误,token需要传递");
        }
        ServerResponse<String> response = this.checkValid(username, Const.USERNAME);
        if (response.isSuccess()) {
            return ServerResponse.createByError("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByError("Token无效或者过期");
        }
        if (StringUtils.equals(token, forgetToken)) {
            String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int resultCount = userMapper.updatePasswordByUsername(username, md5Password);
            if (resultCount > 0) {
                return ServerResponse.createBySuccess("修改密码成功");
            }
        } else {
            return ServerResponse.createByError("Token错误,请重新获取重置密码的Token");
        }
        return ServerResponse.createByError("修改密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(User user, String oldPassword, String newPassword) {
        int resultCount = userMapper.checkPassword(user.getId(), MD5Util.MD5EncodeUtf8(oldPassword));
        if (resultCount == 0) {
            return ServerResponse.createByError("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("密码更新成功");
        }
        return ServerResponse.createByError("密码更新失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        int resultCount = userMapper.checkEmailByUserId(user.getId(), user.getEmail());
        if (resultCount > 0) {
            return ServerResponse.createByError("Email已存在,请更换email再尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setUpdateTime(new Date());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
        }
        return ServerResponse.createByError("更新个人信息失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (isNull(user)) {
            return ServerResponse.createByError("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }
}
