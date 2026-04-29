package io.github.aicyi.example.web;

import io.github.aicyi.commons.lang.IResponse;
import io.github.aicyi.commons.lang.SmartMapper;
import io.github.aicyi.example.domain.LoginParam;
import io.github.aicyi.example.domain.LoginResult;
import io.github.aicyi.example.domain.RegisterParam;
import io.github.aicyi.example.domain.UpdatePasswordParam;
import io.github.aicyi.example.service.AuthService;
import io.github.aicyi.example.web.vo.LoginReq;
import io.github.aicyi.example.web.vo.LoginResp;
import io.github.aicyi.example.web.vo.RegisterReq;
import io.github.aicyi.example.web.vo.UpdatePasswordReq;
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
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SmartMapper smartMapper;
    @Autowired
    private AuthService authService;

    @ApiOperation(value = "注册", notes = "注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public IResponse<Void> register(@Validated @RequestBody RegisterReq req) {
        RegisterParam param = smartMapper.map(req, RegisterParam.class);
        authService.register(param);
        return Response.success();
    }

    @ApiOperation(value = "登录", notes = "登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public IResponse<LoginResp> login(@Validated @RequestBody LoginReq req) {
        LoginParam param = smartMapper.map(req, LoginParam.class);
        LoginResult result = authService.login(param);
        LoginResp resp = smartMapper.map(result, LoginResp.class);
        return Response.success(resp);
    }

    @ApiOperation(value = "更新密码", notes = "更新密码")
    @RequestMapping(value = "/update-password", method = RequestMethod.POST)
    public IResponse<Void> updatePassword(@Validated @RequestBody UpdatePasswordReq req) {
        UpdatePasswordParam param = smartMapper.map(req, UpdatePasswordParam.class);
        authService.updatePassword(param);
        return Response.success();
    }
}
