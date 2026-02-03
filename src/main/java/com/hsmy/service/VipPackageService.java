package com.hsmy.service;

import com.hsmy.entity.VipPackage;

import java.util.List;

/**
 * VIP package service.
 */
public interface VipPackageService {

    /**
     * List all active vip packages.
     */
    List<VipPackage> listActivePackages();

    /**
     * List active vip packages available for a user (exclude reached limit).
     */
    List<VipPackage> listAvailablePackages(Long userId);

    /**
     * Get active vip package by id.
     */
    VipPackage getActivePackageById(Long id);
}
