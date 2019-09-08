package com.costs.newcosts.stores.realisation.backup;

import com.costs.newcosts.common.types.reactive.realisation.ObservableProperty;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

/**
 * TODO: Add a class header comment
 */
public class BackupState implements com.costs.newcosts.stores.abstraction.State {
    public ObservableProperty<Drive> googleDriveService = new ObservableProperty<>();
    public ObservableProperty<Boolean> googleDriveServiceSet = new ObservableProperty<>();
    public ObservableProperty<String> googleDriveServiceStatus = new ObservableProperty<>();
    public ObservableProperty<Boolean> hasInternetConnection = new ObservableProperty<>();
    public ObservableProperty<Boolean> signedIn = new ObservableProperty<>();
    public ObservableProperty<String> rootFolderId = new ObservableProperty<>();
    public ObservableProperty<FileList> backupFilesList = new ObservableProperty<>();
}
