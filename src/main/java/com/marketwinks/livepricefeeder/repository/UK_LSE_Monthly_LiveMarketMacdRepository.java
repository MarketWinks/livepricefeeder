package com.marketwinks.livepricefeeder.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.marketwinks.livepricefeeder.model.uk_lse_monthly_livemarketmacd;

@Repository
public interface UK_LSE_Monthly_LiveMarketMacdRepository extends MongoRepository<uk_lse_monthly_livemarketmacd, String> {
}
