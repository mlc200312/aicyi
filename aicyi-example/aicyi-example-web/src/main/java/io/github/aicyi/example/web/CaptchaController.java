package io.github.aicyi.example.web;

import io.github.aicyi.commons.core.cache.StringCacheManager;
import io.github.aicyi.commons.lang.IResponse;
import io.github.aicyi.commons.util.CaptchaUtils;
import io.github.aicyi.commons.util.id.IdGenerator;
import io.github.aicyi.example.domain.constants.Constants;
import io.github.aicyi.example.web.vo.GetCaptchaResp;
import io.github.aicyi.midware.web.IgnoreAuth;
import io.github.aicyi.midware.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 验证码控制器
 * @date 15:13
 **/
@IgnoreAuth
@Api(value = "验证码控制器", tags = {"验证码控制器"})
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    private StringCacheManager stringCacheManager;

    @ApiOperation(value = "生成验证码", notes = "生成验证码")
    @RequestMapping(value = "/get-captcha", method = RequestMethod.GET)
    public IResponse<GetCaptchaResp> getCaptcha(HttpServletRequest request) {
        String uuid = IdGenerator.generateV7Id();
        String code = CaptchaUtils.randomCode();
        stringCacheManager.put(Constants.getCaptchaKey(uuid), code, 5, TimeUnit.MINUTES);
        String captcha = request.getScheme() + "://" + request.getServerName() + "/captcha/" + uuid;
        GetCaptchaResp resp = new GetCaptchaResp();
        resp.setUuid(uuid);
        resp.setCaptcha(captcha);
        return Response.success(resp);
    }

    @ApiOperation(value = "验证码", notes = "验证码")
    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    public void show(HttpServletResponse response, @PathVariable String uuid) {
        String code = stringCacheManager.get(Constants.getCaptchaKey(uuid));
        try {
            BufferedImage image = CaptchaUtils.generateImage(code);

            response.setContentType("image/jpeg");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            ImageIO.write(image, "png", response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
