package com.driver.repository;

import com.driver.model.WebSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WebSeriesRepository extends JpaRepository<WebSeries,Integer> {
    WebSeries findBySeriesName(String seriesName);

    @Query(value = "SELECT AVG(w.rating) FROM WebSeries w WHERE w.productionHouse.id =:prodId")
    Double findAverageRatingByProductionHouseId(Integer prodId);
}
