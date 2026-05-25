package io.github.aicyi.example.service.util;

import io.github.aicyi.commons.core.cache.Cache;
import io.github.aicyi.commons.util.CurrentContextHolder;
import io.github.aicyi.example.domain.UserInfo;
import io.github.aicyi.example.domain.entity.base.User;
import io.github.aicyi.example.service.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 16:29
 **/
@Component
public class UserSessionUtils implements InitializingBean {

    private static UserSessionUtils INSTANCE;

    @Autowired(required = false)
    private Cache<String, UserInfo> userInfoRedisCache;
    @Autowired(required = false)
    private UserService userService;


    public static UserInfo getUserInfo() {

        String userId = CurrentContextHolder.getUserId();

        UserInfo userInfo = INSTANCE.userInfoRedisCache.get(userId, key -> {

            User user = INSTANCE.userService.getById(Long.valueOf(userId));

            return UserInfo.of(user, null);
        });

        return userInfo;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        INSTANCE = this;
        INSTANCE.userInfoRedisCache = userInfoRedisCache;
        INSTANCE.userService = userService;
    }
}
