package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo
        // Ensure series is unique
        WebSeries temp = webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());
        if (temp != null) {
            throw new Exception("Series is already present");
        }

        // Create and save web series
        WebSeries webSeries = new WebSeries(webSeriesEntryDto.getSeriesName(), webSeriesEntryDto.getAgeLimit(),
                webSeriesEntryDto.getRating(), webSeriesEntryDto.getSubscriptionType());

        ProductionHouse prodHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId())
                .orElseThrow(() -> new Exception("ProductionHouse not found"));

        webSeries.setProductionHouse(prodHouse);
        webSeriesRepository.save(webSeries);

        // Update production house ratings
        Double avgRating = webSeriesRepository.findAverageRatingByProductionHouseId(prodHouse.getId());
        prodHouse.setRatings(avgRating);
        productionHouseRepository.save(prodHouse);

        return webSeries.getId();
    }

}
