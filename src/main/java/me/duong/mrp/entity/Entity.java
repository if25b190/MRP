package me.duong.mrp.entity;

public abstract class Entity<T> {
    protected T id;

    public T getId() {
        return id;
    }

    public Object setId(T id) {
        this.id = id;
        return null;
    }
}
