package com.hsmy.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 每日愿望记录VO
 *
 * @author HSMY
 * @date 2025/11/17
 */
@Data
public class DailyWishRecordVO {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 愿望内容
     */
    @NotBlank(message = "愿望内容不能为空")
    private String wishContent;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String userName;

    /**
     * 生辰
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime birthTime;

    /**
     * 愿望创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime wishTime;
}
