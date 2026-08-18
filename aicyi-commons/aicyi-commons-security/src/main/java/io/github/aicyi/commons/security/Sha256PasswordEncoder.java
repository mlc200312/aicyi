package io.github.aicyi.commons.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @author Mr.Min
 * @description Sha256密码编码器
 * <p>
 * 无盐单次 SHA-256 不适合密码存储（彩虹表/GPU 爆破成本低），仅用于兼容校验存量哈希；
 * 新业务请使用 {@code org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder}
 * @date 2026/4/24
 * @deprecated 禁止新业务使用，仅保留用于校验历史存量哈希
 **/
@Deprecated
public class Sha256PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence charSequence) {
        return MessageDigestUtils.generateSha256(charSequence.toString());
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        if (charSequence == null || s == null) {
            return false;
        }
        // 恒定时间比较，避免时序侧信道
        byte[] expected = encode(charSequence.toString()).getBytes(StandardCharsets.UTF_8);
        byte[] actual = s.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(expected, actual);
    }
}