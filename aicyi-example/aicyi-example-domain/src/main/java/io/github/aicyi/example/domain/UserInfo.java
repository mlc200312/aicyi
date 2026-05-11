package io.github.aicyi.example.domain;

import io.github.aicyi.commons.codec.jwt.JWTInfo;
import io.github.aicyi.commons.core.IJWTInfo;
import io.github.aicyi.commons.util.MapperUtils;
import io.github.aicyi.commons.util.id.IdUtils;
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
        IJWTInfo jwtInfo = new JWTInfo(String.valueOf(user.getId()), user.getUsername(), IdUtils.generateV7Id());
        UserInfo userInfo = MapperUtils.getInstance().map(jwtInfo, UserInfo.class);
        userInfo.setNickname(user.getNickname());
        userInfo.setMobile(user.getMobile());
        return userInfo;
    }
}
