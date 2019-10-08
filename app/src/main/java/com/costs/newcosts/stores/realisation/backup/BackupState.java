package com.costs.newcosts.stores.realisation.backup;

import com.costs.newcosts.common.types.reactive.realisation.ObservableProperty;
import com.costs.newcosts.stores.realisation.backup.types.BackupData;
import com.costs.newcosts.stores.realisation.backup.types.CreateDeviceBackupStatus;
import com.costs.newcosts.stores.realisation.backup.types.DeleteDeviceBackupStatus;
import com.costs.newcosts.stores.realisation.backup.types.DriveServiceBundle;
import com.costs.newcosts.stores.realisation.backup.types.RestoreStatus;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

/**
 * TODO: Add a class header comment
 */
public class BackupState implements com.costs.newcosts.stores.abstraction.State {
    public ObservableProperty<Boolean> hasInternetConnection = new ObservableProperty<>(false);
    public ObservableProperty<Boolean> signedIn = new ObservableProperty<>(false);
    public ObservableProperty<DriveServiceBundle> driveServiceBundle = new ObservableProperty<>();
    public ObservableProperty<GoogleSignInClient> googleSignInClient = new ObservableProperty<>();
    public ObservableProperty<BackupData> backupData = new ObservableProperty<>();
    public ObservableProperty<RestoreStatus> restoreStatus = new ObservableProperty<>();
    public ObservableProperty<CreateDeviceBackupStatus> createDeviceBackupStatus = new ObservableProperty<>();
    public ObservableProperty<DeleteDeviceBackupStatus> deleteDeviceBackupStatus = new ObservableProperty<>();
}
