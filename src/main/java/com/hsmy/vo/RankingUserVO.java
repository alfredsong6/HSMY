package com.hsmy.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RankingUserVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    
    private String nickname;
    
    private String avatarUrl;
    
    private boolean avatarBase64Encoded;
    
    private String avatarBase64Content;
}
