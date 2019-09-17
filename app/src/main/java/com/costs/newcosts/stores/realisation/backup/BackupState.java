package com.costs.newcosts.stores.realisation.backup;

import com.costs.newcosts.common.types.reactive.realisation.ObservableProperty;
import com.costs.newcosts.stores.realisation.backup.types.BackupContentBundle;
import com.costs.newcosts.stores.realisation.backup.types.DriveServiceBundle;
import com.google.api.services.drive.model.FileList;

/**
 * TODO: Add a class header comment
 */
public class BackupState implements com.costs.newcosts.stores.abstraction.State {
    public ObservableProperty<BackupContentBundle> backupContentBundle = new ObservableProperty<>();
    public ObservableProperty<DriveServiceBundle> googleDriveServiceBundle = new ObservableProperty<>();
    public ObservableProperty<Boolean> hasInternetConnection = new ObservableProperty<>();
    public ObservableProperty<Boolean> signedIn = new ObservableProperty<>();
    public ObservableProperty<String> rootFolderId = new ObservableProperty<>();
    public ObservableProperty<FileList> backupFilesList = new ObservableProperty<>();
}
