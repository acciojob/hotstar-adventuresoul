package com.driver.repository;

import com.driver.model.Subscription;
import com.driver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubscriptionRepository extends JpaRepository<Subscription,Integer> {
    @Query(value = "SELECT s FROM Subscription s WHERE s.user.id = :userId")
    Subscription getSubscriptionByUserId(Integer userId);
}
