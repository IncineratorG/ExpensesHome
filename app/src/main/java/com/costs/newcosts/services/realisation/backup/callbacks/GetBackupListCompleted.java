package com.costs.newcosts.services.realisation.backup.callbacks;

import com.google.api.services.drive.model.FileList;

/**
 * TODO: Add a class header comment
 */
public interface GetBackupListCompleted {
    void complete(FileList files);
}
