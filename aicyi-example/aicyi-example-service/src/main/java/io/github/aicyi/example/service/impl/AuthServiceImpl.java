package io.github.aicyi.example.service.impl;

import io.github.aicyi.commons.core.cache.StringCacheManager;
import io.github.aicyi.commons.core.jwt.IJwtTokenManager;
import io.github.aicyi.commons.lang.exception.BusinessException;
import io.github.aicyi.commons.lang.IJWTInfo;
import io.github.aicyi.commons.lang.exception.UnauthorizedException;
import io.github.aicyi.commons.util.mapper.MapperUtils;
import io.github.aicyi.example.domain.LoginParam;
import io.github.aicyi.example.domain.LoginResult;
import io.github.aicyi.example.domain.RegisterParam;
import io.github.aicyi.example.domain.UserInfo;
import io.github.aicyi.example.domain.constants.Constants;
import io.github.aicyi.example.domain.entity.base.User;
import io.github.aicyi.example.service.AuthService;
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
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IJwtTokenManager<IJWTInfo> jwtInfoTokenManager;
    @Autowired
    private StringCacheManager stringCacheManager;
    @Autowired
    private UserService userService;

    @Override
    public void register(RegisterParam param) {
        User user = MapperUtils.INSTANCE.map(param, User.class);
        user.setPassword(passwordEncoder.encode(param.getPassword()));
        userService.save(user);
    }

    @Override
    public LoginResult login(LoginParam param) {

        // 验证验证码
        validateCaptcha(param.getUuid(), param.getVerCode());

        // 验证用户名密码
        User user = validate(param.getUsername(), param.getPassword());

        // 生成token
        if (Objects.nonNull(user)) {
            String token = jwtInfoTokenManager.createToken(UserInfo.of(user));
            LoginResult result = new LoginResult();
            result.setUserId(user.getId());
            result.setAccessToken(token);
            return result;
        }

        throw new UnauthorizedException("用户名或密码错误");
    }

    private void validateCaptcha(String uuid, String captcha) {
        String code = stringCacheManager.get(Constants.getCaptchaKey(uuid));
        if (!captcha.equalsIgnoreCase(code)) {
            throw new BusinessException("验证码错误");
        }
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
