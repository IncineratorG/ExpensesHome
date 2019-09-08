package com.costs.newcosts.common.types.reactive.realisation;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add a class header comment
 */
public class Subscribers {
    private List<Subscriber> mSubscribers;


    public Subscribers() {
        mSubscribers = new ArrayList<>();
    }

    public void add(Subscriber s) {
        mSubscribers.add(s);
    }

    public Subscriber get(Subscription s) {
        for (int i = 0; i < mSubscribers.size(); ++i) {
            if (mSubscribers.get(i).getSubscription().equals(s)) {
                return mSubscribers.get(i);
            }
        }

        return null;
    }

    public boolean remove(Subscription s) {
        for (int i = 0; i < mSubscribers.size(); ++i) {
            if (mSubscribers.get(i).getSubscription().equals(s)) {
                mSubscribers.remove(i);
                return true;
            }
        }

        return false;
    }

    public boolean remove(Subscriber s) {
        return remove(s.getSubscription());
    }

    public List<Subscriber> getAll() {
        return mSubscribers;
    }

    public void runAll() {
        for (int i = 0; i < mSubscribers.size(); ++i) {
            mSubscribers.get(i).getExecutable().execute();
        }
    }
}
