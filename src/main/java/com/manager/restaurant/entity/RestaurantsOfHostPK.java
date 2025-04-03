package com.manager.restaurant.entity;

import java.io.Serializable;
import java.util.Objects;

public class RestaurantsOfHostPK implements Serializable {

    private String idRestaurant;
    private String idAccount;

    public RestaurantsOfHostPK() {}

    public RestaurantsOfHostPK(String idRestaurant, String idAccount) {
        this.idRestaurant = idRestaurant;
        this.idAccount = idAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantsOfHostPK that = (RestaurantsOfHostPK) o;
        return Objects.equals(idRestaurant, that.idRestaurant) &&
                Objects.equals(idAccount, that.idAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRestaurant, idAccount);
    }
}
