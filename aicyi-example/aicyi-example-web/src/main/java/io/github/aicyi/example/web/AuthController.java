package io.github.aicyi.example.web;

import io.github.aicyi.commons.lang.IResponse;
import io.github.aicyi.commons.util.mapper.MapperUtils;
import io.github.aicyi.example.domain.LoginParam;
import io.github.aicyi.example.domain.LoginResult;
import io.github.aicyi.example.domain.RegisterParam;
import io.github.aicyi.example.service.AuthService;
import io.github.aicyi.example.web.vo.LoginReq;
import io.github.aicyi.example.web.vo.LoginResp;
import io.github.aicyi.example.web.vo.RegisterReq;
import io.github.aicyi.midware.web.IgnoreAuth;
import io.github.aicyi.midware.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
    private AuthService authService;

    @ApiOperation(value = "注册", notes = "注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public IResponse<Void> register(@RequestBody RegisterReq req) {
        RegisterParam param = MapperUtils.INSTANCE.map(req, RegisterParam.class);
        authService.register(param);
        return Response.success();
    }

    @ApiOperation(value = "登录", notes = "登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public IResponse<LoginResp> login(@RequestBody LoginReq req) {
        LoginParam param = MapperUtils.INSTANCE.map(req, LoginParam.class);
        LoginResult result = authService.login(param);
        LoginResp resp = MapperUtils.INSTANCE.map(result, LoginResp.class);
        return Response.success(resp);
    }
}
