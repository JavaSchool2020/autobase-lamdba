package com.akalinkina.storage;

import com.akalinkina.model.Car;
import com.akalinkina.model.Owner;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

public class CarStorage {
    private final NavigableSet<Car> storage = new TreeSet<>(Comparator.comparing(
            car -> ofNullable(car.getOwner()).map(Owner::getFullName).orElse("")));

    private static final CarStorage INSTANCE = new CarStorage();

    public static CarStorage getStorage() {
        return INSTANCE;
    }

    private CarStorage() {}

    private final BiPredicate<Car, String> equalsVin = (car, vin) ->
            of(car).map(Car::getVin).filter(vin::equals).isPresent();

    public Car addCar(String vin) throws IllegalArgumentException {
        return ofNullable(vin).map(Car::new).filter(storage::add)
                .orElseThrow(() -> new IllegalArgumentException("Машина c таким vin уже существует в хранилище данных"));
    }

    public boolean delete(String vin) {
        return storage.removeIf(car -> equalsVin.test(car, vin));
    }

    public Car updateOwner(String vin, Owner owner) throws IllegalArgumentException {
        return getByVin(vin).map(car -> {
            car.setOwner(owner);
            return car;
        }).orElseThrow(() -> new IllegalArgumentException("Машина c таким vin не существует в хранилище данных"));
    }

    public Set<Car> getAll() {
        return Collections.unmodifiableNavigableSet(this.storage);
    }

    public Optional<Car> getByVin(String vin) {
        return storage.stream().filter(car -> equalsVin.test(car, vin)).findFirst();
    }

    public Set<Car> getByFullName(String fullName) {
        return getCars(car -> ofNullable(car.getOwner()).map(Owner::getFullName).filter(fullName::equals).isPresent());
    }

    public Set<Car> getById(Long id) {
        return getCars(car -> ofNullable(car.getOwner()).map(Owner::getId).filter(id::equals).isPresent());
    }

    private Set<Car> getCars(Predicate<Car> filter) {
        return storage.stream().filter(filter).collect(Collectors.toSet());
    }

    public Set<Car> getById2(Long id) {
        return getCarsByOwnerParams(Owner::getId, id);
    }

    public Set<Car> getByFullName2(String fullName) {
        return getCarsByOwnerParams(Owner::getFullName, fullName);
    }

    private <T> Set<Car> getCarsByOwnerParams(Function<Owner, T> getParam, T eq) {
        return storage.stream().filter(car -> ofNullable(car.getOwner()).map(getParam).filter(eq::equals).isPresent())
                .collect(Collectors.toSet());
    }

    public Set<Owner> getOwners() {
        return storage.stream().map(Car::getOwner).collect(Collectors.toSet());
    }

    public Map<Owner, Set<String>> mapByOwner() {
        return storage.stream().filter(car -> ofNullable(car.getOwner()).isPresent())
                .collect(Collectors.groupingBy(Car::getOwner, Collectors.mapping(Car::getVin, Collectors.toSet())));
    }
}
