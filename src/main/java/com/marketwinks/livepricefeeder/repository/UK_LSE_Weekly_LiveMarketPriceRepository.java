package com.marketwinks.livepricefeeder.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.marketwinks.livepricefeeder.model.uk_lse_weekly_livemarketprice;

@Repository
public interface UK_LSE_Weekly_LiveMarketPriceRepository extends MongoRepository<uk_lse_weekly_livemarketprice, String> {
}
