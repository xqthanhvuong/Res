package com.manager.restaurant.repository;

import com.manager.restaurant.entity.Restaurant;
import com.manager.restaurant.entity.RestaurantsOfHost;
import com.manager.restaurant.entity.RestaurantsOfHostPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Struct;
import java.util.List;

@Repository
public interface RestaurantsOfHostRepository  extends JpaRepository<RestaurantsOfHost, RestaurantsOfHostPK> {
    List<RestaurantsOfHost> findByIdAccount(String idAccount);
    List<RestaurantsOfHost> findAllByIdAccount(String idAccount);
}
