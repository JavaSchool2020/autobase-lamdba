package com.akalinkina.model;

import java.util.Objects;

public class Car {
    private final String vin;
    private Owner owner;

    public Car(String vin) {
        this.vin = vin;
    }

    public String getVin() {
        return vin;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return vin.equals(car.vin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin);
    }

    @Override
    public String toString() {
        return "Car{" +
                "vin='" + vin + '\'' +
                ", owner=" + owner +
                '}';
    }
}
