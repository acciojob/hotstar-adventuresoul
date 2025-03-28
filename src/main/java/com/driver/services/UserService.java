package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User newUser = userRepository.save(user);
        return newUser.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){
        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            System.out.println("User not found");
            return 0;
        }

        Subscription subscription = subscriptionRepository.getSubscriptionByUserId(user.getId());
        if (subscription == null) {
            System.out.println("Subscription not found");
            return 0;
        }

        // now we have age and subscriptionType, fetch all web series and compare and get count
        // select count(*) from web_series w where age > w.age_limit and w.subscription_type = subscriptionType;
        int userAge = user.getAge();
        SubscriptionType subscriptionType = subscription.getSubscriptionType();
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();

        long count = webSeriesList.stream()
                .filter(webSeries -> webSeries.getAgeLimit() < userAge && webSeries.getSubscriptionType() == subscriptionType)
                .count();


        return (int)count;
    }


}
