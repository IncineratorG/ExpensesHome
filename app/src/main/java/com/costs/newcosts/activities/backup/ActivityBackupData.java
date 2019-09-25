package com.costs.newcosts.activities.backup;


import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.costs.newcosts.ActivityMainWithFragments;
import com.costs.newcosts.Constants;
import com.costs.newcosts.DB_Costs;
import com.costs.newcosts.R;
import com.costs.newcosts.common.types.reactive.abstraction.Executable;
import com.costs.newcosts.common.types.reactive.realisation.Subscription;
import com.costs.newcosts.services.realisation.backup.tasks.TaskRunner;
import com.costs.newcosts.stores.abstraction.Action;
import com.costs.newcosts.stores.realisation.backup.types.BackupContentBundle;
import com.costs.newcosts.stores.realisation.backup.types.DriveServiceBundle;
import com.costs.newcosts.stores.common.Payload;
import com.costs.newcosts.stores.abstraction.Store;
import com.costs.newcosts.stores.realisation.backup.BackupActionsFactory;
import com.costs.newcosts.stores.realisation.Stores;
import com.costs.newcosts.stores.realisation.backup.BackupState;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * TODO: Add a class header comment
 */
public class ActivityBackupData extends AppCompatActivity {
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
    private BackupState mBackupState;

    private Subscription mBackupFilesListSubscription;
    private Subscription mHasInternetConnectionSubscription;
    private Subscription mGoogleDriveServiceBundleSubscription;
    private Subscription mRootFolderIdSubscription;
    private Subscription mBackupContentSubscription;
    private Subscription mRestoreStatusSubscription;

    private AlertDialog mRestorationProgressDialog;
    private Executable mRestorationDialogCancelAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_data);

        calendar = new GregorianCalendar();

        linearLayoutManager = new LinearLayoutManager(this);
        backupListRecyclerView = (RecyclerView) findViewById(R.id.backup_data_recycler_view);

        // Кнопка создания резервной копии
        createBackupDataButton = (Button) findViewById(R.id.backup_data_backup_button);
        createBackupDataButton.setEnabled(true);
        createBackupDataButton.setTextColor(ContextCompat.getColor(this, R.color.lightGrey));
        createBackupDataButton.setOnClickListener((v) -> {
            test();
        });


//        createBackupDataButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createBackupData_V2();
//                createBackupDataButton.setEnabled(false);
//                createBackupDataButton.setTextColor(ContextCompat.getColor(ActivityBackupData.this, R.color.lightGrey));
//                if (backupDataRecyclerViewAdapter != null)
//                    backupDataRecyclerViewAdapter.setClickListener(null);
//            }
//        });

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
//        selectGoogleAccountImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectGoogleAccount();
//            }
//        });


//        requestSignIn();

        statusTextView = (TextView) findViewById(R.id.backup_data_status_textview);
        statusTextView.setText(getResources().getString(R.string.abd_statusTextView_noConnection_string));


        mBackupStore = Stores.getInstance().getStore(Stores.BackupStore);
        mBackupState = (BackupState) mBackupStore.getState();
        if (mBackupState == null) {
            Log.d(TAG, CLASS_NAME + "->onCreate()->mBackupState_IS_NULL");
        } else {
            // Очищаем всю информацию в хранилище.
            Action clearStoreAction = mBackupStore.getActionFactory().getAction(BackupActionsFactory.ClearStore);

            mBackupStore.dispatch(clearStoreAction);

            // Подписываемся на необходимые параметры хранилища.
            setSubscriptions();
        }


        AlertDialog.Builder restorationProgressDialogBuilder = new AlertDialog.Builder(ActivityBackupData.this);
        restorationProgressDialogBuilder.setCancelable(false);
        restorationProgressDialogBuilder.setTitle(getResources().getString(R.string.atrd_restoringProgressDialogBuilder_Title_string));
        restorationProgressDialogBuilder.setNegativeButton(getResources().getString(R.string.atrd_restoringProgressDialogBuilder_Cancel_string), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "DIALOG_CANCEL_BUTTON_CLICKED");
                if (mRestorationDialogCancelAction != null) {
                    mRestorationDialogCancelAction.execute();
                }
            }
        });

        mRestorationProgressDialog = restorationProgressDialogBuilder.create();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Проверяем соединение с интернетом.
        Payload payload = new Payload();
        payload.set("context", this);

        Action checkInternetConnectionAction = mBackupStore.getActionFactory().getAction(BackupActionsFactory.CheckInternetConnection);
        checkInternetConnectionAction.setPayload(payload);

        mBackupStore.dispatch(checkInternetConnectionAction);

        if (mBackupState.hasInternetConnection.get()) {
            // Если пользователь не залогинился - логинимся и получаем Drive сервис.
            if (!mBackupState.signedIn.get()) {
                requestSignIn();
            }
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

    private void requestSignIn() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

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
        Action signInAction = mBackupStore.getActionFactory().getAction(BackupActionsFactory.SetSignIn);

        mBackupStore.dispatch(signInAction);

        // Создаём Drive сервис.
        Payload payload = new Payload();
        payload.set("result_intent", result);
        payload.set("context", this);
        payload.set("appLabel", getAppLabel(this));

        Action buildGoogleDriveServiceAction = mBackupStore.getActionFactory().getAction(BackupActionsFactory.BuildGoogleDriveService);
        buildGoogleDriveServiceAction.setPayload(payload);

        mBackupStore.dispatch(buildGoogleDriveServiceAction);
    }

    void test() {

    }


    @Override
    public void onBackPressed() {
        returnToPreviousActivity();
    }

    // Возвращаемся к предыдущему экрану
    private void returnToPreviousActivity() {
//        if (asyncTaskRestoreData != null)
//            asyncTaskRestoreData.cancel(true);
        Intent mainActivityWithFragmentsIntent = new Intent(ActivityBackupData.this, ActivityMainWithFragments.class);
        mainActivityWithFragmentsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainActivityWithFragmentsIntent);
    }

    private void setSubscriptions() {
        mBackupFilesListSubscription = mBackupState.backupFilesList.subscribe(()-> {
            processBackupFiles(mBackupState.backupFilesList.get());
        });

        mHasInternetConnectionSubscription = mBackupState.hasInternetConnection.subscribe(() -> {
            if (!mBackupState.hasInternetConnection.get()) {
                statusTextView.setText(getResources().getString(R.string.abd_statusTextView_noConnection_string));
            } else {
                statusTextView.setText(getResources().getString(R.string.abd_statusTextView_acquiringConnection_string));
            }
        });

        mGoogleDriveServiceBundleSubscription = mBackupState.googleDriveServiceBundle.subscribe(() -> {
            switch (mBackupState.googleDriveServiceBundle.get().getDriveServiceStatus()) {
                case DriveServiceBundle.Set: {
                    statusTextView.setText(getResources().getString(R.string.abd_statusTextView_connectionAcquired_string));

                    // Получаем ID корневой папки с файлами резервной копии.
                    Action getRootFolderIdAction = mBackupStore.getActionFactory().getAction(BackupActionsFactory.GetRootFolder);
                    getRootFolderIdAction.setPayload(mBackupState.googleDriveServiceBundle.get().getDriveService());

                    mBackupStore.dispatch(getRootFolderIdAction);

                    break;
                }

                case DriveServiceBundle.Setting: {
                    statusTextView.setText(getResources().getString(R.string.abd_statusTextView_acquiringConnection_string));
                    break;
                }

                case DriveServiceBundle.NotSet: {
                    statusTextView.setText(getResources().getString(R.string.abd_statusTextView_noConnection_string));
                    break;
                }
            }
        });

        mRootFolderIdSubscription = mBackupState.rootFolderId.subscribe(() -> {
            if (!mBackupState.rootFolderId.get().isEmpty()) {
                // Получаем список файлов резервных копий.
                Action getBackupListAction = mBackupStore.getActionFactory().getAction(BackupActionsFactory.GetBackupList);

                Payload payload = new Payload();
                payload.set("googleDriveService", mBackupState.googleDriveServiceBundle.get().getDriveService());
                payload.set("rootFolderId", mBackupState.rootFolderId.get());

                getBackupListAction.setPayload(payload);

                mBackupStore.dispatch(getBackupListAction);
            } else {
                Log.d(TAG, "NO_BACKUP_ROOT_FOLDERS_FOUND");
            }
        });

        mBackupContentSubscription = mBackupState.backupContentBundle.subscribe(() -> {
            switch (mBackupState.backupContentBundle.get().getContentStatus()) {
                case BackupContentBundle.Setting: {
                    mRestorationDialogCancelAction = () -> {
                        Action cancelLoadingBackupContent = mBackupStore.getActionFactory().getAction(BackupActionsFactory.StopAsyncTask);

                        Payload payload = new Payload();
                        payload.set("taskType", TaskRunner.GetBackupFolderContentTask);

                        cancelLoadingBackupContent.setPayload(payload);

                        mBackupStore.dispatch(cancelLoadingBackupContent);

                        enableBackground();
                    };

                    mRestorationProgressDialog.setMessage(getResources().getString(R.string.atrd_restoringProgressDialogBuilder_Message_string));
                    mRestorationProgressDialog.show();

                    break;
                }

                case BackupContentBundle.Set: {
                    mRestorationDialogCancelAction = () -> {
                        Action cancelRestoreDataBase = mBackupStore.getActionFactory().getAction(BackupActionsFactory.StopAsyncTask);

                        Payload payload = new Payload();
                        payload.set("taskType", TaskRunner.RestoreDataBaseTask);

                        cancelRestoreDataBase.setPayload(payload);

                        mBackupStore.dispatch(cancelRestoreDataBase);

                        enableBackground();
                    };
                    mRestorationProgressDialog.setMessage("Восстановление");

                    if (mBackupState.backupContentBundle.get().getCostNamesInputStream() == null ||
                            mBackupState.backupContentBundle.get().getCostValuesInputStream() == null) {
                        Log.d(TAG, "ActivityBackupData->BAD_BACKUP_CONTENT");
                        return;
                    }

                    Action restoreDbFromBackup = mBackupStore.getActionFactory().getAction(BackupActionsFactory.RestoreDbFromBackup);

                    Payload payload = new Payload();
                    payload.set("costNamesStream", mBackupState.backupContentBundle.get().getCostNamesInputStream());
                    payload.set("costValuesStream", mBackupState.backupContentBundle.get().getCostValuesInputStream());
                    payload.set("costsDb", DB_Costs.getInstance(this));

                    restoreDbFromBackup.setPayload(payload);

                    mBackupStore.dispatch(restoreDbFromBackup);

                    break;
                }
            }
        });

        mRestoreStatusSubscription = mBackupState.restoreStatus.subscribe(() -> {
            String restoreStatus = mBackupState.restoreStatus.get().getStatus();

            if (!mRestorationProgressDialog.isShowing()) {
                Log.d(TAG, "PROGRESS_DIALOG_NOT_SHOWING");
                return;
            }

            mRestorationProgressDialog.setMessage(restoreStatus);

            if (restoreStatus.equals(TaskRunner.TaskCompletedStatus) ||
                restoreStatus.equals(TaskRunner.TaskInterruptedStatus) ||
                restoreStatus.equals(TaskRunner.TaskErrorOccurredStatus)) {
                mRestorationProgressDialog.dismiss();

                Toast dataRestoredToast;
                if (restoreStatus.equals(TaskRunner.TaskCompletedStatus)) {
                    statusTextView.setText(getResources().getString(R.string.abd_dataRestoredSuccessful_string));
                    dataRestoredToast = Toast.makeText(this, getResources().getString(R.string.abd_dataRestoredSuccessful_string), Toast.LENGTH_SHORT);
                } else {
                    statusTextView.setText(getResources().getString(R.string.abd_dataNotRestored_string));
                    dataRestoredToast = Toast.makeText(this, getResources().getString(R.string.abd_dataNotRestored_string), Toast.LENGTH_SHORT);
                }
                dataRestoredToast.show();

                enableBackground();
            }
        });
    }

    private void unsubscribeAll() {
        mHasInternetConnectionSubscription.unsubscribe();
        mRootFolderIdSubscription.unsubscribe();
        mGoogleDriveServiceBundleSubscription.unsubscribe();
        mBackupFilesListSubscription.unsubscribe();
        mBackupContentSubscription.unsubscribe();
        mRestoreStatusSubscription.unsubscribe();
    }

    private void processBackupFiles(FileList files) {
        Log.d(TAG, CLASS_NAME + "setSubscriptions()->BACKUP_FILE_LIST_SIZE: " + files.size());

        existingDeviceBackupFolders = new ArrayList<>();
        for (File file : files.getFiles()) {
            DataUnitBackupFolder backupTitle = new DataUnitBackupFolder();
            backupTitle.setTitle(file.getName());
            backupTitle.setDriveId(file.getId());

            existingDeviceBackupFolders.add(backupTitle);
        }

        // Отображаем полученный список резервных копий
        backupListRecyclerView.setLayoutManager(linearLayoutManager);
        backupDataRecyclerViewAdapter = new AdapterActivityBackupDataRecyclerView(ActivityBackupData.this, existingDeviceBackupFolders);
        backupDataRecyclerViewAdapter.setClickListener(new AdapterActivityBackupDataRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                onBackupItemClick(position);
            }
        });
        backupListRecyclerView.setAdapter(backupDataRecyclerViewAdapter);

        if (existingDeviceBackupFolders.size() > 0) {
            statusTextView.setText(getResources().getString(R.string.abd_statusTextView_backupDataFound_string));
        } else {
            statusTextView.setText(getResources().getString(R.string.abd_statusTextView_noBackupDataFound_string));
        }
    }

    // При нажатии на элемент списка резервных копий - отображаем диалоговое окно,
    // предлагающее восстановить данные из резервной копии или удалить выбранную резервную копию
    private void onBackupItemClick(final int position) {
        DataUnitBackupFolder selectedBackupItem = existingDeviceBackupFolders.get(position);

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(selectedBackupItem.getMilliseconds());

        AlertDialog.Builder chosenBackupItemDialogBuilder = new AlertDialog.Builder(ActivityBackupData.this);
        LayoutInflater inflater = LayoutInflater.from(ActivityBackupData.this);
        View dialogView = inflater.inflate(R.layout.edit_cost_value_dialog, null);
        chosenBackupItemDialogBuilder.setView(dialogView);

        // Устанавливаем дату выбранной резервной копии
        TextView dateTextView = (TextView) dialogView.findViewById(R.id.edit_cost_value_dialog_costDate);
        dateTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                selectedBackupItem.getDay() + " " +
                Constants.DECLENSION_MONTH_NAMES[selectedBackupItem.getMonth()] + " " +
                selectedBackupItem.getYear() + ", " +
                calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                calendar.get(Calendar.MINUTE));

        // Устанавливаем название устройства, на котром была создана выбранная резервная копия
        TextView descriptionTextView = (TextView) dialogView.findViewById(R.id.edit_cost_value_dialog_costName);
        descriptionTextView.setText(selectedBackupItem.getDeviceManufacturer() + " " +
                selectedBackupItem.getDeviceModel());

        // Скрываем ненужные элементы
        TextView invisTextView_1 = (TextView) dialogView.findViewById(R.id.edit_cost_value_dialog_costValue);
        invisTextView_1.setVisibility(View.GONE);
        TextView invisTextView_2 = (TextView) dialogView.findViewById(R.id.edit_cost_value_dialog_costNote);
        invisTextView_2.setVisibility(View.GONE);

        // Запускаем диалоговое окно
        final AlertDialog chosenBackupItemDialog = chosenBackupItemDialogBuilder.create();
        chosenBackupItemDialog.show();

        // Устанавливаем слушатели на кнопки созданного диалогового окна
        // При нажатии кнопки "Восстановить" появляется диалоговое окно, запрашивающее подтверждение восстановления
        Button restoreButton = (Button) dialogView.findViewById(R.id.edit_cost_value_dialog_editButton);
        restoreButton.setText(getResources().getString(R.string.abd_restoreButton_string));
        restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenBackupItemDialog.dismiss();

                AlertDialog.Builder restoreFromChosenBackupItemDialogBuilder = new AlertDialog.Builder(ActivityBackupData.this);
                restoreFromChosenBackupItemDialogBuilder.setTitle(getResources().getString(R.string.abd_restoreFromChosenBackupItemDialogBuilder_Title_string));
                restoreFromChosenBackupItemDialogBuilder.setMessage(getResources().getString(R.string.abd_restoreFromChosenBackupItemDialogBuilder_Message_string));
                restoreFromChosenBackupItemDialogBuilder.setPositiveButton(getResources().getString(R.string.abd_restoreFromChosenBackupItemDialogBuilder_continue_button_string), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        disableBackground();

                        restoreDataFromBackup(position);
                    }
                });
                restoreFromChosenBackupItemDialogBuilder.setNegativeButton(getResources().getString(R.string.abd_restoreFromChosenBackupItemDialogBuilder_cancel_button_string), null);

                AlertDialog restoreFromChosenBackupItemDialog = restoreFromChosenBackupItemDialogBuilder.create();
                restoreFromChosenBackupItemDialog.show();
            }
        });

        // При нажатии кнопки "Удалить" появляется диалоговое окно, запрашивающее подтверждение удаления
        Button deleteButton = (Button) dialogView.findViewById(R.id.edit_cost_value_dialog_deleteButton);
        deleteButton.setText(getResources().getString(R.string.abd_deleteButton_string));
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenBackupItemDialog.dismiss();

                AlertDialog.Builder deleteBackupItemDialogBuilder = new AlertDialog.Builder(ActivityBackupData.this);
                deleteBackupItemDialogBuilder.setTitle(getResources().getString(R.string.abd_deleteBackupItemDialogBuilder_Title_string));
                deleteBackupItemDialogBuilder.setMessage(getResources().getString(R.string.abd_deleteBackupItemDialogBuilder_Message_string));
                deleteBackupItemDialogBuilder.setPositiveButton(getResources().getString(R.string.abd_deleteBackupItemDialogBuilder_delete_button_string), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        deleteBackupItem(position);
                    }
                });
                deleteBackupItemDialogBuilder.setNegativeButton(getResources().getString(R.string.abd_deleteBackupItemDialogBuilder_cancel_button_string), null);

                AlertDialog deleteBackupItemDialog = deleteBackupItemDialogBuilder.create();
                deleteBackupItemDialog.show();
            }
        });

        Button cancelButton = (Button) dialogView.findViewById(R.id.edit_cost_value_dialog_cancelButton);
        cancelButton.setText(getResources().getString(R.string.abd_cancelButton_string));
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenBackupItemDialog.dismiss();
            }
        });
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

    private void restoreDataFromBackup(int position) {
        if (existingDeviceBackupFolders.size() == 0) {
            Log.i(TAG, "NO BACKUP FILES FOUND");
            return;
        }

        String backupFolderId = existingDeviceBackupFolders.get(position).getDriveId();

        Action getBackupFolderContent = mBackupStore.getActionFactory().getAction(BackupActionsFactory.GetBackupFolderContent);

        Payload payload = new Payload();
        payload.set("googleDriveService", mBackupState.googleDriveServiceBundle.get().getDriveService());
        payload.set("backupFolderId", backupFolderId);

        getBackupFolderContent.setPayload(payload);

        mBackupStore.dispatch(getBackupFolderContent);
    }

    private void enableBackground() {
        backupDataRecyclerViewAdapter.setClickListener(new AdapterActivityBackupDataRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                onBackupItemClick(position);
            }
        });

        createBackupDataButton.setEnabled(true);
        createBackupDataButton.setBackgroundResource(R.drawable.keyboard_buttons_custom);
        createBackupDataButton.setTextColor(getResources().getColorStateList(R.color.button_text_color));

        arrowBackImageView.setEnabled(true);
    }

    private void disableBackground() {
        backupDataRecyclerViewAdapter.setClickListener(null);

        createBackupDataButton.setEnabled(false);
        createBackupDataButton.setTextColor(ContextCompat.getColor(ActivityBackupData.this, R.color.lightGrey));

        arrowBackImageView.setEnabled(false);
    }
}