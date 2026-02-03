package com.hsmy.enums;

public enum PaymentStatusEnum {
    PENDING(0, "未支付"),
    SUCCESS(1, "支付成功"),
    FAILED(2, "支付失败"),
    REFUND(3, "已退款"),
    CLOSED(4, "订单已关闭");

    private final int code;
    private final String defaultDesc;

    PaymentStatusEnum(int code, String defaultDesc) {
        this.code = code;
        this.defaultDesc = defaultDesc;
    }

    public int getCode() {
        return code;
    }

    public String getDefaultDesc() {
        return defaultDesc;
    }
}
