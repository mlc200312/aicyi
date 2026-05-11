package io.github.aicyi.commons.codec;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Mr.Min
 * @description Sha256密码编码器
 * @date 2026/4/24
 **/
public class Sha256PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence charSequence) {
        return MessageDigestUtils.generateSha256(charSequence.toString());
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return this.encode(charSequence.toString()).equals(s);
    }
}