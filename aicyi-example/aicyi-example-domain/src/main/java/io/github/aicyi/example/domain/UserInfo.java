package io.github.aicyi.example.domain;

import io.github.aicyi.commons.core.jwt.JWTInfo;
import io.github.aicyi.commons.lang.IJWTInfo;
import io.github.aicyi.commons.util.id.IdGenerator;
import io.github.aicyi.commons.util.mapper.MapperUtils;
import io.github.aicyi.example.domain.entity.base.User;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Mr.Min
 * @description 用户信息
 * @date 11:15
 **/
@Getter
@Setter
public class UserInfo extends JWTInfo {
    private String nickname;
    private String mobile;

    public static UserInfo of(User user) {
        IJWTInfo jwtInfo = new JWTInfo(String.valueOf(user.getId()), user.getUsername(), IdGenerator.generateV7Id());
        UserInfo userInfo = MapperUtils.INSTANCE.map(jwtInfo, UserInfo.class);
        userInfo.setNickname(user.getNickname());
        userInfo.setMobile(user.getMobile());
        return userInfo;
    }
}
