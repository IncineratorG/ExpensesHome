package com.costs.newcosts.activities.backup_v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.costs.newcosts.ActivityMainWithFragments;
import com.costs.newcosts.R;
import com.costs.newcosts.activities.backup.AdapterActivityBackupDataRecyclerView;
import com.costs.newcosts.activities.backup.DataUnitBackupFolder;
import com.costs.newcosts.common.types.reactive.abstraction.Executable;
import com.costs.newcosts.common.types.reactive.realisation.Subscription;
import com.costs.newcosts.stores.abstraction.Action;
import com.costs.newcosts.stores.abstraction.Store;
import com.costs.newcosts.stores.common.Payload;
import com.costs.newcosts.stores.realisation.Stores;
import com.costs.newcosts.stores.realisation.backup_v2.BackupActionsFactory_v2;
import com.costs.newcosts.stores.realisation.backup_v2.BackupState_v2;
import com.costs.newcosts.stores.realisation.backup_v2.types.DriveServiceBundle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.drive.DriveScopes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * TODO: Add a class header comment
 */
public class ActivityBackupData_v2 extends AppCompatActivity {
    private static final String TAG = "tag";
    private static final String CLASS_NAME = "ActivityBackupData";

    private ImageView arrowBackImageView;

    private Button createBackupDataButton;

    private String TABLE_COST_NAMES_FILE_NAME = "cost_names_data.xml";
    private String TABLE_COST_VALUES_FILE_NAME = "cost_values_data.xml";
    private String DEVICE_BACKUP_FOLDER_NAME;
    private String REFERENCE_FILE_NAME = "reference_file";

    private String ROOT_BACKUP_FOLDER_NAME = "EXPENSES_BACKUP";
    private List<DataUnitBackupFolder> existingDeviceBackupFolders = new ArrayList<>();

    private Calendar calendar;
    private String BACKUP_USER_COMMENT = "";

    private RecyclerView backupListRecyclerView;
    private AdapterActivityBackupDataRecyclerView backupDataRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;

    private TextView statusTextView;

    private ImageView selectGoogleAccountImageView;

    private static final int REQUEST_CODE_SIGN_IN = 1;

    private Store mBackupStore;
    private BackupState_v2 mBackupState;

    private Subscription mBackupFilesListSubscription;
    private Subscription mHasInternetConnectionSubscription;
    private Subscription mGoogleDriveServiceBundleSubscription;
    private Subscription mRootFolderIdSubscription;
    private Subscription mBackupContentSubscription;
    private Subscription mRestoreStatusSubscription;
    private Subscription mCreateBackupSubscription;

    private AlertDialog mRestorationProgressDialog;
    private Executable mRestorationDialogCancelAction;

    private ProgressDialog mProgressDialog;
    private Executable mProgressDialogCancelAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_data);

        calendar = new GregorianCalendar();

        linearLayoutManager = new LinearLayoutManager(this);
        backupListRecyclerView = (RecyclerView) findViewById(R.id.backup_data_recycler_view);

        // Кнопка создания резервной копии
        createBackupDataButton = (Button) findViewById(R.id.backup_data_backup_button);
        createBackupDataButton.setEnabled(false);
        createBackupDataButton.setTextColor(ContextCompat.getColor(this, R.color.lightGrey));
        createBackupDataButton.setOnClickListener((v) -> {
//            createDeviceBackup();
        });


        // При нажатии стрелки назад - возвращаемся к предыдущему экрану
        arrowBackImageView = (ImageView) findViewById(R.id.backup_data_arrow_back_imageview);
        arrowBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToPreviousActivity();
            }
        });

        // Открываем диалоговое окно, в котором можно выбрать аккаунт Google
        selectGoogleAccountImageView = (ImageView) findViewById(R.id.backup_data_account_imageview);
        selectGoogleAccountImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                signOut();
            }
        });

        statusTextView = (TextView) findViewById(R.id.backup_data_status_textview);
        statusTextView.setText(getResources().getString(R.string.abd_statusTextView_noConnection_string));

        mBackupStore = Stores.getInstance().getStore(Stores.BackupStore_v2);
        mBackupState = (BackupState_v2) mBackupStore.getState();

        // Очищаем всю информацию в хранилище.
        Action clearStoreAction = mBackupStore.getActionFactory().getAction(BackupActionsFactory_v2.ClearStore);
        mBackupStore.dispatch(clearStoreAction);

        // Подписываемся на необходимые параметры хранилища.
        setSubscriptions();

        mProgressDialog = new ProgressDialog(this);

//        AlertDialog.Builder infoDialogBuilder = new AlertDialog.Builder(ActivityBackupData_v2.this);
//        infoDialogBuilder.setCancelable(false);
//        infoDialogBuilder.setTitle("No Title");
//        infoDialogBuilder.setNegativeButton(getResources().getString(R.string.atrd_restoringProgressDialogBuilder_Cancel_string), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Log.d(TAG, "DIALOG_CANCEL_BUTTON_CLICKED");
//                if (mInfoDialogCancelAction != null) {
//                    mInfoDialogCancelAction.execute();
//                }
//            }
//        });
//
//        mInfoDialog = infoDialogBuilder.create();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Проверяем соединение с интернетом.
        Payload payload = new Payload();
        payload.set("context", this);

        Action checkInternetConnectionAction = mBackupStore.getActionFactory().getAction(BackupActionsFactory_v2.CheckInternetConnection);
        checkInternetConnectionAction.setPayload(payload);

        mBackupStore.dispatch(checkInternetConnectionAction);

        if (mBackupState.hasInternetConnection.get()) {
            if (!mBackupState.signedIn.get()) {
                requestSignIn();
            }
        } else {
            Log.d(TAG, "NO_INTERNET_CONNECTION");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        unsubscribeAll();


//        if (asyncTaskRestoreData != null)
//            asyncTaskRestoreData.cancel(true);
//        disconnectFromGoogleDrive();
    }

    @Override
    public void onBackPressed() {
        returnToPreviousActivity();
    }

    // Возвращаемся к предыдущему экрану
    private void returnToPreviousActivity() {
//        if (asyncTaskRestoreData != null)
//            asyncTaskRestoreData.cancel(true);
        Intent mainActivityWithFragmentsIntent = new Intent(ActivityBackupData_v2.this, ActivityMainWithFragments.class);
        mainActivityWithFragmentsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainActivityWithFragmentsIntent);
    }

    private void requestSignIn() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE))
                        .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        Action setGoogleSignInClient = mBackupStore.getActionFactory().getAction(BackupActionsFactory_v2.SetGoogleSignInClient);

        Payload payload = new Payload();
        payload.set("googleSignInClient", client);

        setGoogleSignInClient.setPayload(payload);

        mBackupStore.dispatch(setGoogleSignInClient);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }

    private void handleSignInResult(Intent result) {
        // Устанавливаем признак того, что пользователь залогинился.
        Action signInAction = mBackupStore.getActionFactory().getAction(BackupActionsFactory_v2.SetSignIn);

        mBackupStore.dispatch(signInAction);

        // Создаём Drive сервис.
        Payload payload = new Payload();
        payload.set("result_intent", result);
        payload.set("context", this);
        payload.set("appLabel", getAppLabel(this));

        Action buildGoogleDriveServiceAction = mBackupStore.getActionFactory().getAction(BackupActionsFactory_v2.BuildGoogleDriveService);
        buildGoogleDriveServiceAction.setPayload(payload);

        mBackupStore.dispatch(buildGoogleDriveServiceAction);
    }

    private void setSubscriptions() {
        mHasInternetConnectionSubscription = mBackupState.hasInternetConnection.subscribe(() -> {
            if (!mBackupState.hasInternetConnection.get()) {
                statusTextView.setText(getResources().getString(R.string.abd_statusTextView_noConnection_string));

                createBackupDataButton.setEnabled(false);
                createBackupDataButton.setBackgroundResource(R.drawable.keyboard_buttons_custom);
                createBackupDataButton.setTextColor(ContextCompat.getColor(ActivityBackupData_v2.this, R.color.lightGrey));
            } else {
                statusTextView.setText(getResources().getString(R.string.abd_statusTextView_connectionAcquired_string));

                createBackupDataButton.setEnabled(true);
                createBackupDataButton.setBackgroundResource(R.drawable.keyboard_buttons_custom);
                createBackupDataButton.setTextColor(getResources().getColorStateList(R.color.button_text_color));

            }
        });

        mGoogleDriveServiceBundleSubscription = mBackupState.driveServiceBundle.subscribe(() -> {
            Log.d(TAG, mBackupState.driveServiceBundle.get().getDriveServiceStatus());

            switch (mBackupState.driveServiceBundle.get().getDriveServiceStatus()) {
                case DriveServiceBundle.Set: {
                    mProgressDialog.dismiss();

                    // Получаем данные резервной копии.
                    Action getBackupData = mBackupStore.getActionFactory().getAction(BackupActionsFactory_v2.GetBackupData);
                    mBackupStore.dispatch(getBackupData);

                    break;
                }

                case DriveServiceBundle.Setting: {
                    mProgressDialog.setMessage("Подключение к серверам Google");
                    mProgressDialog.show();
                    break;
                }

                case DriveServiceBundle.NotSet: {
                    mProgressDialog.dismiss();
                    break;
                }
            }
        });
    }

    private void unsubscribeAll() {
        mHasInternetConnectionSubscription.unsubscribe();
        mGoogleDriveServiceBundleSubscription.unsubscribe();
    }

    private String getAppLabel(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            Log.d(TAG, CLASS_NAME + "->getAppLabel->NameNotFoundException");
        }

        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "Unknown");
    }
}