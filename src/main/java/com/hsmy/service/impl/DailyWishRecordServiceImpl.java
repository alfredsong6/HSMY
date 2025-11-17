package com.hsmy.service.impl;

import com.hsmy.entity.DailyWishRecord;
import com.hsmy.mapper.DailyWishRecordMapper;
import com.hsmy.service.DailyWishRecordService;
import com.hsmy.utils.IdGenerator;
import com.hsmy.vo.DailyWishRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 每日愿望记录Service实现
 *
 * @author HSMY
 * @date 2025/11/17
 */
@Service
@RequiredArgsConstructor
public class DailyWishRecordServiceImpl implements DailyWishRecordService {

    private final DailyWishRecordMapper dailyWishRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createDailyWish(Long userId, DailyWishRecordVO dailyWishRecordVO) {
        DailyWishRecord record = new DailyWishRecord();
        record.setId(IdGenerator.nextId());
        record.setUserId(userId);
        record.setWishContent(dailyWishRecordVO.getWishContent());
        record.setUserName(dailyWishRecordVO.getUserName());
        record.setBirthTime(dailyWishRecordVO.getBirthTime());
        record.setWishTime(dailyWishRecordVO.getWishTime() != null ? dailyWishRecordVO.getWishTime() : LocalDateTime.now());
        return dailyWishRecordMapper.insert(record) > 0;
    }

    @Override
    public List<DailyWishRecordVO> listDailyWishes(Long userId) {
        List<DailyWishRecord> records = dailyWishRecordMapper.selectByUserId(userId);
        return convertToList(records);
    }

    @Override
    public List<DailyWishRecordVO> listTodayDailyWishes(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startTime = today.atStartOfDay();
        LocalDateTime endTime = startTime.plusDays(1);
        List<DailyWishRecord> records = dailyWishRecordMapper.selectByUserIdAndTimeRange(userId, startTime, endTime);
        return convertToList(records);
    }

    @Override
    public DailyWishRecordVO getLastDailyWish(Long userId) {
        DailyWishRecord record = dailyWishRecordMapper.selectLatestByUserId(userId);
        return record == null ? null : convertToVO(record);
    }

    private List<DailyWishRecordVO> convertToList(List<DailyWishRecord> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }
        return records.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private DailyWishRecordVO convertToVO(DailyWishRecord record) {
        if (record == null) {
            return null;
        }
        DailyWishRecordVO vo = new DailyWishRecordVO();
        vo.setId(record.getId());
        vo.setWishContent(record.getWishContent());
        vo.setUserName(record.getUserName());
        vo.setBirthTime(record.getBirthTime());
        vo.setWishTime(record.getWishTime());
        return vo;
    }
}
