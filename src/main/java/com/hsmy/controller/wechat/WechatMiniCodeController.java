package com.hsmy.controller.wechat;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.dto.WechatMiniCodeRequest;
import com.hsmy.service.wechat.WechatMiniCodeService;
import com.hsmy.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信小程序码相关接口
 */
@RestController
@RequestMapping("/wechat/mini")
@ApiVersion(ApiVersionConstant.V1_0)
@RequiredArgsConstructor
public class WechatMiniCodeController {

    private final WechatMiniCodeService wechatMiniCodeService;

    /**
     * 获取小程序码（无限制）
     */
    @PostMapping(value = "/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getMiniCode(@Validated @RequestBody WechatMiniCodeRequest request) {
        Long currentUserId = UserContextUtil.getCurrentUserId();
        byte[] data = wechatMiniCodeService.getUnlimitedCode(request.getPage(), currentUserId.toString(), request.getWidth());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(data.length);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
