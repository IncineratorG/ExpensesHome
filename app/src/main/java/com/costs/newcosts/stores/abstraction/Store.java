package com.costs.newcosts.stores.abstraction;

/**
 * TODO: Add a class header comment
 */
public abstract class Store {
    public void dispatch(Action action) {
        reduce(action);
        effect(action);
    }

    public abstract State getState();
    public abstract ActionsFactory getActionFactory();

    protected abstract void reduce(Action action);
    protected abstract void effect(Action action);
}
