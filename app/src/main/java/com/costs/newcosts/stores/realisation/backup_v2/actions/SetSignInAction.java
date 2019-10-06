package com.costs.newcosts.stores.realisation.backup_v2.actions;

import com.costs.newcosts.stores.abstraction.Action;
import com.costs.newcosts.stores.realisation.backup_v2.BackupActionsFactory_v2;

/**
 * TODO: Add a class header comment
 */
public class SetSignInAction implements Action {
    private Object mPayload;


    @Override
    public int getType() {
        return BackupActionsFactory_v2.SetSignIn;
    }

    @Override
    public Object getPayload() {
        return mPayload;
    }

    @Override
    public void setPayload(Object payload) {
        mPayload = payload;
    }
}
