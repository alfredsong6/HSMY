package com.hsmy.service.impl;

import com.hsmy.entity.VipPackage;
import com.hsmy.mapper.VipPackageMapper;
import com.hsmy.mapper.VipPurchaseMapper;
import com.hsmy.service.VipPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VIP package service implementation.
 */
@Service
@RequiredArgsConstructor
public class VipPackageServiceImpl implements VipPackageService {

    private final VipPackageMapper vipPackageMapper;
    private final VipPurchaseMapper vipPurchaseMapper;

    @Override
    public List<VipPackage> listActivePackages() {
        return vipPackageMapper.selectAllActive();
    }

    @Override
    public List<VipPackage> listAvailablePackages(Long userId) {
        List<VipPackage> packages = vipPackageMapper.selectAllActive();
        if (userId == null || packages == null || packages.isEmpty()) {
            return packages;
        }

        List<Map<String, Object>> counts = vipPurchaseMapper.selectSuccessCountGroupByPackage(userId);
        Map<Long, Integer> countMap = new HashMap<>();
        if (counts != null) {
            for (Map<String, Object> row : counts) {
                Object packageIdValue = row.get("packageId");
                Object countValue = row.get("purchaseCount");
                if (!(packageIdValue instanceof Number)) {
                    continue;
                }
                Long packageId = ((Number) packageIdValue).longValue();
                int count = 0;
                if (countValue instanceof Number) {
                    count = ((Number) countValue).intValue();
                }
                countMap.put(packageId, count);
            }
        }

        List<VipPackage> available = new ArrayList<>();
        for (VipPackage vipPackage : packages) {
            if (vipPackage == null) {
                continue;
            }
            Integer limitTimes = vipPackage.getLimitTimes();
            if (limitTimes == null || limitTimes <= 0) {
                available.add(vipPackage);
                continue;
            }
            int purchased = countMap.getOrDefault(vipPackage.getId(), 0);
            if (purchased < limitTimes) {
                available.add(vipPackage);
            }
        }
        return available;
    }

    @Override
    public VipPackage getActivePackageById(Long id) {
        if (id == null) {
            return null;
        }
        return vipPackageMapper.selectActiveById(id);
    }
}
