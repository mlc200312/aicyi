package io.github.aicyi.example.web;

import io.github.aicyi.commons.core.IResponse;
import io.github.aicyi.commons.core.BeanMapper;
import io.github.aicyi.commons.util.CurrentContextHolder;
import io.github.aicyi.example.domain.entity.base.User;
import io.github.aicyi.example.service.UserService;
import io.github.aicyi.example.web.vo.UserInfoResp;
import io.github.aicyi.midware.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Mr.Min
 * @description 用户控制器
 * @date 15:45
 **/
@Api(value = "用户控制器", tags = {"用户控制器"})
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BeanMapper beanMapper;
    @Autowired
    private UserService userService;

    @ApiOperation(value = "查询用户信息", notes = "查询用户信息")
    @ApiImplicitParam(
            name = "Authorization",
            value = "令牌",
            required = true,
            paramType = "header",
            dataTypeClass = String.class
    )
    @RequestMapping(value = "/get-user-info", method = RequestMethod.GET)
    public IResponse<UserInfoResp> getUserInfo() {
        String userId = CurrentContextHolder.getUserId();
        User user = userService.getById(Long.valueOf(userId));
        UserInfoResp resp = beanMapper.map(user, UserInfoResp.class);
        return Response.success(resp);
    }
}
