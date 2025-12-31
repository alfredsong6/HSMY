package com.hsmy.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RankingVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String userId;
    
    private String rankType;
    
    private Long meritValue;
    
    private Integer rankingPosition;
    
    private Date snapshotDate;
    
    private String period;
    
    private RankingUserVO user;
}
