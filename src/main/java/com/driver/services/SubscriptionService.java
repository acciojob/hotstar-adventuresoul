package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    private int calculateAmount(SubscriptionType subscriptionType, int numberOfScreens) {
        if (subscriptionType == SubscriptionType.BASIC) {
            return 500 + (200 * numberOfScreens);
        }
        else if (subscriptionType == SubscriptionType.ELITE) {
            return 800 + (250 * numberOfScreens);
        }
        else {
            return 1000 + (350 * numberOfScreens);
        }
    }

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto) {
        //Save The subscription Object into the Db and return the total Amount that user has to pay
        int amount = calculateAmount(subscriptionEntryDto.getSubscriptionType(),
                                        subscriptionEntryDto.getNoOfScreensRequired());

        Subscription newSubscription = new Subscription(subscriptionEntryDto.getSubscriptionType(),
                                                        subscriptionEntryDto.getNoOfScreensRequired(),
                                                        new Date(), amount);

        User user = userRepository.findById(subscriptionEntryDto.getUserId())
                .orElse(null);;
        if (user == null) {
            System.out.println("User not found");
            return -1;
        }

        newSubscription.setUser(user);
        subscriptionRepository.save(newSubscription);

        return amount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        int dueAmount = 0;
        int newAmount = 0;
        Subscription presentSubscription = subscriptionRepository.getSubscriptionByUserId(userId);

        if (presentSubscription == null) {
            throw new Exception("subscription not found");
        }
        SubscriptionType subscriptionType = presentSubscription.getSubscriptionType();

        if (subscriptionType.toString().equals("ELITE")) {
            throw new Exception("Already the best Subscription");
        }

        if (subscriptionType.toString().equals("BASIC")) {
            newAmount = 800 + (250 * presentSubscription.getNoOfScreensSubscribed());
            dueAmount = newAmount - presentSubscription.getTotalAmountPaid();

            presentSubscription.setSubscriptionType(SubscriptionType.PRO);
            presentSubscription.setTotalAmountPaid(newAmount);
        }
        else if (subscriptionType.toString().equals("PRO")) {
            newAmount = 1000 + (350 * presentSubscription.getNoOfScreensSubscribed());
            dueAmount = newAmount - presentSubscription.getTotalAmountPaid();

            presentSubscription.setSubscriptionType(SubscriptionType.ELITE);
            presentSubscription.setTotalAmountPaid(newAmount);
        }

        subscriptionRepository.save(presentSubscription);
        return dueAmount;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        int totalAmount = 0;
        List<Subscription> allSubscriptions = subscriptionRepository.findAll();


        for (Subscription s: allSubscriptions) {
            totalAmount += s.getTotalAmountPaid();
        }

        return totalAmount;
    }

}
