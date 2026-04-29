package io.github.aicyi.example.service.impl;

import io.github.aicyi.commons.core.jwt.IJwtTokenManager;
import io.github.aicyi.commons.lang.SmartMapper;
import io.github.aicyi.commons.lang.IJWTInfo;
import io.github.aicyi.commons.lang.exception.BusinessException;
import io.github.aicyi.commons.lang.exception.UnauthorizedException;
import io.github.aicyi.example.domain.*;
import io.github.aicyi.example.domain.entity.base.User;
import io.github.aicyi.example.domain.type.CaptchaType;
import io.github.aicyi.example.domain.type.ExampleResultCode;
import io.github.aicyi.example.service.AuthService;
import io.github.aicyi.example.service.CaptchaService;
import io.github.aicyi.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Mr.Min
 * @description 权限管理业务实现类
 * @date 20:03
 **/
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SmartMapper smartMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IJwtTokenManager<IJWTInfo> jwtInfoTokenManager;
    @Autowired
    private UserService userService;
    @Autowired
    private CaptchaService captchaService;

    @Override
    public void register(RegisterParam param) {
        User user = smartMapper.map(param, User.class);
        // 密码加密
        user.setPassword(passwordEncoder.encode(param.getPassword()));
        userService.save(user);
    }

    @Override
    public LoginResult login(LoginParam param) {

        // 验证验证码
        captchaService.validateCaptcha(CaptchaType.IMAGE_CAPTCHA_TYPE, param.getUuid(), param.getVerCode());

        // 验证用户名密码
        User user = validate(param.getUsername(), param.getPassword());

        if (Objects.isNull(user)) {
            throw new UnauthorizedException("用户名或密码错误");
        }

        // 生成token
        String token = jwtInfoTokenManager.createToken(UserInfo.of(user));
        LoginResult result = new LoginResult();
        result.setUserId(user.getId());
        result.setAccessToken(token);
        return result;


    }

    @Override
    public void updatePassword(UpdatePasswordParam param) {

        // 验证验证码
        captchaService.validateCaptcha(CaptchaType.UPDATE_PASSWORD_CAPTCHA_TYPE, param.getUuid(), param.getVerCode());

        // 查询用户
        User user = userService.getByUsername(param.getUsername());

        // 判断用户是否存在
        if (Objects.isNull(user)) {
            throw new BusinessException(ExampleResultCode.OBJECT_NOT_FOUND);
        }

        // 密码加密
        user.setPassword(passwordEncoder.encode(param.getNewPassword()));

        // 更新密码
        userService.update(user);
    }

    private User validate(String username, String password) {
        User user = userService.getByUsername(username);
        if (Objects.isNull(user)) {
            return null;
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }
}
