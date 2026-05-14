package io.github.aicyi.example.web;

import io.github.aicyi.commons.core.IJWTInfo;
import io.github.aicyi.commons.core.IResponse;
import io.github.aicyi.commons.core.BeanMapper;
import io.github.aicyi.commons.core.token.AuthenticationTokenService;
import io.github.aicyi.commons.core.token.TokenPair;
import io.github.aicyi.example.domain.LoginParam;
import io.github.aicyi.example.domain.LoginResult;
import io.github.aicyi.example.domain.RegisterParam;
import io.github.aicyi.example.domain.UpdatePasswordParam;
import io.github.aicyi.example.service.AuthService;
import io.github.aicyi.example.web.vo.*;
import io.github.aicyi.midware.web.IgnoreAuth;
import io.github.aicyi.midware.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Mr.Min
 * @description 授权控制器
 * @date 15:45
 **/
@IgnoreAuth
@Api(value = "授权控制器", tags = {"授权控制器"})
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private BeanMapper beanMapper;
    @Autowired
    private AuthService authService;
    @Autowired
    private AuthenticationTokenService<IJWTInfo> tokenService;

    @ApiOperation(value = "注册", notes = "注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public IResponse<Void> register(@Validated @RequestBody RegisterReq req) {
        RegisterParam param = beanMapper.map(req, RegisterParam.class);
        authService.register(param);
        return Response.success();
    }

    @ApiOperation(value = "登录", notes = "登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public IResponse<LoginResp> login(@Validated @RequestBody LoginReq req) {
        LoginParam param = beanMapper.map(req, LoginParam.class);
        LoginResult result = authService.login(param);
        LoginResp resp = beanMapper.map(result, LoginResp.class);
        return Response.success(resp);
    }

    @ApiOperation(value = "刷新Token接口", notes = "刷新Token接口")
    @RequestMapping(value = "/refresh-token", method = RequestMethod.POST)
    public IResponse<TokenPair> refreshToken(@Validated @RequestBody RefreshTokenReq req) {
        TokenPair tokenPair = tokenService.refreshToken(req.getRefreshToken());
        return Response.success(tokenPair);
    }

    @ApiOperation(value = "更新密码", notes = "更新密码")
    @RequestMapping(value = "/update-password", method = RequestMethod.POST)
    public IResponse<Void> updatePassword(@Validated @RequestBody UpdatePasswordReq req) {
        UpdatePasswordParam param = beanMapper.map(req, UpdatePasswordParam.class);
        authService.updatePassword(param);
        return Response.success();
    }
}
