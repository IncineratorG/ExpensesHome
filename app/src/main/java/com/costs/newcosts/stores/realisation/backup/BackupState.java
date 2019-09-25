package com.costs.newcosts.stores.realisation.backup;

import com.costs.newcosts.common.types.reactive.realisation.ObservableProperty;
import com.costs.newcosts.stores.realisation.backup.types.BackupContentBundle;
import com.costs.newcosts.stores.realisation.backup.types.CreateBackupStatus;
import com.costs.newcosts.stores.realisation.backup.types.DriveServiceBundle;
import com.costs.newcosts.stores.realisation.backup.types.RestoreStatus;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.services.drive.model.FileList;

/**
 * TODO: Add a class header comment
 */
public class BackupState implements com.costs.newcosts.stores.abstraction.State {
    public ObservableProperty<CreateBackupStatus> createBackupStatus = new ObservableProperty<>();
    public ObservableProperty<RestoreStatus> restoreStatus = new ObservableProperty<>();
    public ObservableProperty<BackupContentBundle> backupContentBundle = new ObservableProperty<>();
    public ObservableProperty<GoogleSignInClient> googleSignInClient = new ObservableProperty<>();
    public ObservableProperty<DriveServiceBundle> googleDriveServiceBundle = new ObservableProperty<>();
    public ObservableProperty<Boolean> hasInternetConnection = new ObservableProperty<>();
    public ObservableProperty<Boolean> signedIn = new ObservableProperty<>();
    public ObservableProperty<String> rootFolderId = new ObservableProperty<>();
    public ObservableProperty<FileList> backupFilesList = new ObservableProperty<>();
}
