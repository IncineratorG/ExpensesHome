package com.costs.newcosts.common.types.reactive.realisation;

import java.util.UUID;

/**
 * TODO: Add a class header comment
 */
public class Subscription {
    private String mUuid = null;

    public Subscription() {
        mUuid = UUID.randomUUID().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscription that = (Subscription) o;

        return mUuid != null ? mUuid.equals(that.mUuid) : that.mUuid == null;
    }

    @Override
    public int hashCode() {
        return mUuid != null ? mUuid.hashCode() : 0;
    }
}
