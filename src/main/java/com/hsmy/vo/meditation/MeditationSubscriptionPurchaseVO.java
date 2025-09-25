package com.hsmy.vo.meditation;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MeditationSubscriptionPurchaseVO {
    @NotBlank(message = "订阅类型不能为空")
    private String planType; // DAY/WEEK/MONTH
}
