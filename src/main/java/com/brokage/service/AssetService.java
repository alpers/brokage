package com.brokage.service;

import com.brokage.entity.Asset;
import com.brokage.exception.AssetNotFoundException;
import com.brokage.exception.InsufficientBalanceException;
import com.brokage.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private static final String defaultAsset = "TRY";

    public void depositMoney(Long customerId, Double amount) {
        Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, defaultAsset)
                .orElse(buildAsset(customerId, defaultAsset));
        asset.setSize(asset.getSize() + amount);
        asset.setUsableSize(asset.getUsableSize() + amount);

        assetRepository.save(asset);
    }

    public void withdrawMoney(Long customerId, Double amount) throws Exception {
        Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, defaultAsset)
                .orElseThrow(() -> new AssetNotFoundException(customerId, defaultAsset));
        if (asset.getUsableSize() < amount) {
            throw new InsufficientBalanceException(defaultAsset);
        }

        asset.setUsableSize(asset.getUsableSize() - amount);
        assetRepository.save(asset);
    }

    public Optional<Asset> findByCustomerIdAndAssetName(Long customerId, String assetName) {
        return assetRepository.findByCustomerIdAndAssetName(customerId, assetName);
    }

    public Asset saveAsset(Asset asset) {
        return assetRepository.save(asset);
    }

    public List<Asset> listAssets(Long customerId) {
        return assetRepository.findByCustomerId(customerId);
    }

    public String getDefaultAsset() {
        return defaultAsset;
    }

    public Asset buildAsset(Long customerId, String assetName) {
        return Asset.builder()
                .customerId(customerId)
                .assetName(assetName)
                .usableSize(0d)
                .size(0d)
                .build();
    }
}
