package com.costs.newcosts.services.realisation.backup;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.costs.newcosts.DB_Costs;
import com.costs.newcosts.services.realisation.backup.callbacks.GetBackupFolderContentCompleted;
import com.costs.newcosts.services.realisation.backup.callbacks.GetBackupListCompleted;
import com.costs.newcosts.services.realisation.backup.callbacks.GetRootFolderCompleted;
import com.costs.newcosts.services.realisation.backup.callbacks.RestoreDataBaseProgress;
import com.costs.newcosts.services.realisation.backup.tasks.GetBackupFolderContentTask;
import com.costs.newcosts.services.realisation.backup.tasks.GetBackupListTask;
import com.costs.newcosts.services.realisation.backup.tasks.GetRootFolderTask;
import com.costs.newcosts.services.realisation.backup.tasks.RestoreDataBaseTask;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.InputStream;
import java.util.Collections;

/**
 * TODO: Add a class header comment
 */
public class BackupService {
    public BackupService() {

    }

    public boolean hasInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null;
    }

    public void getSignInAccount(Intent result, OnSuccessListener<GoogleSignInAccount> onSuccess, OnFailureListener onFailure) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public Drive getGoogleDriveService(GoogleSignInAccount signInAccount, Context context, String appName) {
        // Use the authenticated account to sign in to the Drive service.
        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        context, Collections.singleton(DriveScopes.DRIVE_APPDATA));
        credential.setSelectedAccount(signInAccount.getAccount());

        return new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential)
                .setApplicationName(appName)
                .build();
    }

    public void getBackupList(Drive googleDriveService, String rootFolderId, GetBackupListCompleted callback) {
        GetBackupListTask getBackupListTask = new GetBackupListTask(googleDriveService, rootFolderId, callback);
        getBackupListTask.execute();
    }

    public void getRootFolder(Drive googleDriveService, GetRootFolderCompleted callback) {
        GetRootFolderTask getRootFolderTask = new GetRootFolderTask(googleDriveService, callback);
        getRootFolderTask.execute();
    }

    public void getBackupFolderContent(Drive googleDriveService, String folderId, GetBackupFolderContentCompleted callback) {
        GetBackupFolderContentTask getBackupFolderContentTask = new GetBackupFolderContentTask(googleDriveService, folderId, callback);
        getBackupFolderContentTask.execute();
    }

    public void restoreDataBase(DB_Costs costsDb, InputStream costValuesStream, InputStream costNamesStream, RestoreDataBaseProgress progressCallback) {
        RestoreDataBaseTask restoreDataBaseTask = new RestoreDataBaseTask(costsDb, costValuesStream, costNamesStream, progressCallback);
        restoreDataBaseTask.execute();
    }
}
