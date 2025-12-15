package com.hsmy.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Merit level progress response for a user.
 */
@Data
public class MeritLevelProgressVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private Long totalMerit;
    private Integer currentLevel;
    private Long remainingMeritToNextLevel;
    private MeritLevelStatusVO currentLevelDetail;
    private List<MeritLevelStatusVO> levels;
}
