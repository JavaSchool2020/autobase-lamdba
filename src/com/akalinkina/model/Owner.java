package com.akalinkina.model;

import java.util.Objects;

public class Owner {
    final private String fullName;
    final private long id;

    public Owner(String fullName, long id) throws IllegalArgumentException {
        if (fullName == null ) {
            throw new IllegalArgumentException("Full name must not be empty");
        }
        this.fullName = fullName;
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Owner owner = (Owner) o;
        return id == owner.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
