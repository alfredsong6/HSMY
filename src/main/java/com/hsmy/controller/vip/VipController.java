package com.hsmy.controller.vip;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.dto.PurchaseVipRequest;
import com.hsmy.entity.VipPackage;
import com.hsmy.service.VipPackageService;
import com.hsmy.service.VipPurchaseService;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.VipMyInfoVO;
import com.hsmy.vo.VipPurchaseResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * VIP controller.
 */
@RestController
@RequestMapping("/vip")
@ApiVersion(ApiVersionConstant.V1_0)
@RequiredArgsConstructor
@Validated
public class VipController {

    private final VipPackageService vipPackageService;
    private final VipPurchaseService vipPurchaseService;

    /**
     * List vip packages.
     */
    @GetMapping("/list")
    public Result<List<VipPackage>> listVipPackages() {
        try {
            return Result.success(vipPackageService.listActivePackages());
        } catch (Exception e) {
            return Result.error("Failed to fetch vip packages: " + e.getMessage());
        }
    }

    /**
     * List vip packages excluding reached purchase limit.
     */
    @GetMapping("/list/available")
    public Result<List<VipPackage>> listAvailableVipPackages() {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            return Result.success(vipPackageService.listAvailablePackages(userId));
        } catch (Exception e) {
            return Result.error("Failed to fetch available vip packages: " + e.getMessage());
        }
    }

    /**
     * Get vip package detail by id.
     */
    @GetMapping("/{packageId}")
    public Result<VipPackage> getVipPackageDetail(@PathVariable Long packageId) {
        try {
            VipPackage vipPackage = vipPackageService.getActivePackageById(packageId);
            if (vipPackage == null) {
                return Result.error("VIP package not found");
            }
            return Result.success(vipPackage);
        } catch (Exception e) {
            return Result.error("Failed to fetch vip package: " + e.getMessage());
        }
    }

    /**
     * Get current user's vip info.
     */
    @GetMapping("/my")
    public Result<VipMyInfoVO> getMyVipInfo() {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            return Result.success(vipPurchaseService.getMyVipInfo(userId));
        } catch (Exception e) {
            return Result.error("Failed to fetch vip info: " + e.getMessage());
        }
    }

    /**
     * Purchase vip package.
     */
    @PostMapping("/purchase")
    public Result<VipPurchaseResultVO> purchaseVip(@RequestBody @Valid PurchaseVipRequest request) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            VipPurchaseResultVO result = vipPurchaseService.purchaseVip(userId, request.getPackageId(), request.getPaymentMethod());
            return Result.success("Purchase success", result);
        } catch (Exception e) {
            return Result.error("Purchase failed: " + e.getMessage());
        }
    }
}
