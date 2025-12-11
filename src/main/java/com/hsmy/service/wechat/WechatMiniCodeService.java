package com.hsmy.service.wechat;

/**
 * 微信小程序码服务
 */
public interface WechatMiniCodeService {

    /**
     * 获取无限制小程序码图片（返回 PNG 二进制）
     *
     * @param page  小程序页面路径，如 pages/index/index
     * @param scene 自定义场景参数，形如 uid=123&from=immersive
     * @param width 二维码宽度(px)，可空
     * @return 图片字节数组
     */
    byte[] getUnlimitedCode(String page, String scene, Integer width);
}
