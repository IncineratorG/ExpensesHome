package com.costs.newcosts.activities.backup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.costs.newcosts.common.types.reactive.realisation.Subscription;
import com.costs.newcosts.services.backup.tasks.TaskRunner;
import com.costs.newcosts.stores.abstraction.Action;
import com.costs.newcosts.stores.abstraction.Store;
import com.costs.newcosts.stores.common.Payload;
import com.costs.newcosts.stores.realisation.Stores;
import com.costs.newcosts.stores.realisation.backup.BackupActionsFactory;
import com.costs.newcosts.stores.realisation.backup.BackupState;
import com.costs.newcosts.stores.realisation.backup.types.BackupData;
import com.costs.newcosts.stores.realisation.backup.types.CreateDeviceBackupStatus;
import com.costs.newcosts.stores.realisation.backup.types.DeleteDeviceBackupStatus;
import com.costs.newcosts.stores.realisation.backup.types.DriveServiceBundle;
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
public class ActivityBackupData extends AppCompatActivity {
    private static final String TAG = "tag";
    private static final String CLASS_NAME = "ActivityBackupData";

    private ImageView arrowBackImageView;

    private Button createBackupDataButton;

    private List<DataUnitBackupFolder> existingDeviceBackupFolders = new ArrayList<>();

    private Calendar calendar;

    private RecyclerView backupListRecyclerView;
    private AdapterActivityBackupDataRecyclerView backupDataRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;

    private TextView statusTextView;

    private ImageView selectGoogleAccountImageView;

    private static final int REQUEST_CODE_SIGN_IN = 1;

    private Store mBackupStore;
    private BackupState mBackupState;

    private Subscription mHasInternetConnectionSubscription;
    private Subscription mGoogleDriveServiceBundleSubscription;
    private Subscription mBackupDataSubscription;
    private Subscription mRestoreStatusSubscription;
    private Subscription mCreateDeviceBackupSubscription;
    private Subscription mDeleteDeviceBackupSubscription;

    private ProgressDialog mProgressDialog;


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
        createBackupDataButton.setOnClickListener((v) -> createDeviceBackup());

        // При нажатии стрелки назад - возвращаемся к предыдущему экрану
        arrowBackImageView = (ImageView) findViewById(R.id.backup_data_arrow_back_imageview);
        arrowBackImageView.setOnClickListener((v) -> returnToPreviousActivity());

        // Открываем диалоговое окно, в котором можно выбрать аккаунт Google
        selectGoogleAccountImageView = (ImageView) findViewById(R.id.backup_data_account_imageview);
        selectGoogleAccountImageView.setOnClickListener((v) -> signOut());

        statusTextView = (TextView) findViewById(R.id.backup_data_status_textview);
        statusTextView.setText(getResources().getString(R.string.abd_statusTextView_noConnection_string));

        mBackupStore = Stores.getInstance().getStore(Stores.BackupStore);
        mBackupState = (BackupState) mBackupStore.getState();

        // Очищаем всю информацию в хранилище.
        Action clearStoreAction = mBackupStore.getActionFactory().getAction(BackupActionsFactory.ClearStore);
        mBackupStore.dispatch(clearStoreAction);

        // Подписываемся на необходимые параметры хранилища.
        setSubscriptions();

        // Все кнопки, кроме кнопки "Назад" делаем неактивными до момента получения данных обфайлах резервных копий.
        disableBackground();

        mProgressDialog = new ProgressDialog(ActivityBackupData.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.atrd_restoringProgressDialogBuilder_Cancel_string), (d, w) -> {
            mBackupStore.dispatch(mBackupStore.getActionFactory().getAction(BackupActionsFactory.StopCurrentAsyncTask));
        });
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
            if (!mBackupState.signedIn.get()) {
                requestSignIn();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unsubscribeAll();
    }

    @Override
    public void onBackPressed() {
        returnToPreviousActivity();
    }

    // Возвращаемся к предыдущему экрану
    private void returnToPreviousActivity() {
        Intent mainActivityWithFragmentsIntent = new Intent(ActivityBackupData.this, ActivityMainWithFragments.class);
        mainActivityWithFragmentsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainActivityWithFragmentsIntent);
    }

    private void requestSignIn() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
//                        .requestScopes(new Scope(DriveScopes.DRIVE))
                        .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        Action setGoogleSignInClient = mBackupStore.getActionFactory().getAction(BackupActionsFactory.SetGoogleSignInClient);

        Payload payload = new Payload();
        payload.set("googleSignInClient", client);

        setGoogleSignInClient.setPayload(payload);

        mBackupStore.dispatch(setGoogleSignInClient);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private void signOut() {
        disableBackground();

        mBackupState.googleSignInClient.get().signOut().addOnCompleteListener((task) -> {
            requestSignIn();
        });
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

    private void setSubscriptions() {
        final String METHOD_NAME = ".setSubscriptions()";

        mHasInternetConnectionSubscription = mBackupState.hasInternetConnection.subscribe(() -> {
            if (!mBackupState.hasInternetConnection.get()) {
                statusTextView.setText(getResources().getString(R.string.abd_statusTextView_noConnection_string));
            } else {
                statusTextView.setText(getResources().getString(R.string.abd_statusTextView_connectionAcquired_string));
            }
        });

        mGoogleDriveServiceBundleSubscription = mBackupState.driveServiceBundle.subscribe(() -> {
            switch (mBackupState.driveServiceBundle.get().getDriveServiceStatus()) {
                case DriveServiceBundle.Set: {
                    mProgressDialog.dismiss();

                    // Получаем данные резервной копии.
                    Payload payload = new Payload();
                    payload.set("googleDriveService", mBackupState.driveServiceBundle.get().getDriveService());

                    Action getBackupData = mBackupStore.getActionFactory().getAction(BackupActionsFactory.GetBackupData);
                    getBackupData.setPayload(payload);

                    mBackupStore.dispatch(getBackupData);

                    break;
                }

                case DriveServiceBundle.Setting: {
                    mProgressDialog.setTitle(null);
                    mProgressDialog.setMessage("Подключение к серверам Google");
                    if (!isFinishing()) {
                        mProgressDialog.show();
                    }
                    break;
                }

                case DriveServiceBundle.NotSet: {
                    mProgressDialog.dismiss();
                    break;
                }
            }
        });

        mBackupDataSubscription = mBackupState.backupData.subscribe(() -> {
            switch (mBackupState.backupData.get().getBackupDataStatus()) {
                case BackupData.Set: {
                    mProgressDialog.dismiss();

                    // Отображаем полученный список резервных копий
                    existingDeviceBackupFolders = mBackupState.backupData.get().getDeviceBackupFolders();
                    backupListRecyclerView.setLayoutManager(linearLayoutManager);
                    backupDataRecyclerViewAdapter = new AdapterActivityBackupDataRecyclerView(ActivityBackupData.this, existingDeviceBackupFolders);
                    backupListRecyclerView.setAdapter(backupDataRecyclerViewAdapter);

                    enableBackground();

                    break;
                }

                case BackupData.Setting: {
                    mProgressDialog.setTitle("");
                    mProgressDialog.setMessage("Получение резервных копий");
                    if (!isFinishing()) {
                        mProgressDialog.show();
                    }
                    break;
                }

                case BackupData.NotSet: {
                    mProgressDialog.dismiss();
                    break;
                }
            }
        });

        mRestoreStatusSubscription = mBackupState.restoreStatus.subscribe(() -> {
            final String restoreStatus = mBackupState.restoreStatus.get().getStatus();

            switch (restoreStatus) {
                case TaskRunner.TaskStartedStatus: {
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }

                    mProgressDialog.setMessage(getResources().getString(R.string.atrd_restoringProgressDialogBuilder_Title_string));
                    if (!isFinishing()) {
                        mProgressDialog.show();
                    }
                    break;
                }

                case TaskRunner.TaskCompletedStatus:
                case TaskRunner.TaskInterruptedStatus:
                case TaskRunner.TaskErrorOccurredStatus: {
                    mProgressDialog.dismiss();

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

                    break;
                }

                default: {
                    mProgressDialog.setMessage(restoreStatus);
                }
            }
        });

        mCreateDeviceBackupSubscription = mBackupState.createDeviceBackupStatus.subscribe(() -> {
            final String createDeviceBackupStatus = mBackupState.createDeviceBackupStatus.get().getStatus();

            switch (createDeviceBackupStatus) {
                case CreateDeviceBackupStatus.Complete:
                case CreateDeviceBackupStatus.NotComplete: {
                    mProgressDialog.dismiss();

                    // Получаем список резервных копий.
                    Payload payload = new Payload();
                    payload.set("googleDriveService", mBackupState.driveServiceBundle.get().getDriveService());

                    Action getBackupData = mBackupStore.getActionFactory().getAction(BackupActionsFactory.GetBackupData);
                    getBackupData.setPayload(payload);

                    mBackupStore.dispatch(getBackupData);

                    break;
                }

                case CreateDeviceBackupStatus.InProgress: {
                    mProgressDialog.setTitle("");
                    mProgressDialog.setMessage("Создание резервной копии");
                    if (!isFinishing()) {
                        mProgressDialog.show();
                    }
                    break;
                }
            }
        });

        mDeleteDeviceBackupSubscription = mBackupState.deleteDeviceBackupStatus.subscribe(() -> {
            final String deleteDeviceBackupStatus = mBackupState.deleteDeviceBackupStatus.get().getStatus();

            switch (deleteDeviceBackupStatus) {
                case DeleteDeviceBackupStatus.Complete:
                case DeleteDeviceBackupStatus.NotComplete: {
                    mProgressDialog.dismiss();

                    // Получаем данные резервной копии.
                    Payload payload = new Payload();
                    payload.set("googleDriveService", mBackupState.driveServiceBundle.get().getDriveService());

                    Action getBackupData = mBackupStore.getActionFactory().getAction(BackupActionsFactory.GetBackupData);
                    getBackupData.setPayload(payload);

                    mBackupStore.dispatch(getBackupData);

                    break;
                }

                case DeleteDeviceBackupStatus.InProgress: {
                    mProgressDialog.setTitle("");
                    mProgressDialog.setMessage("Удаление резервной копии");
                    if (!isFinishing()) {
                        mProgressDialog.show();
                    }
                    break;
                }
            }
        });
    }

    private void unsubscribeAll() {
        mHasInternetConnectionSubscription.unsubscribe();
        mGoogleDriveServiceBundleSubscription.unsubscribe();
        mBackupDataSubscription.unsubscribe();
        mRestoreStatusSubscription.unsubscribe();
        mCreateDeviceBackupSubscription.unsubscribe();
        mDeleteDeviceBackupSubscription.unsubscribe();
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
        restoreButton.setOnClickListener((v) -> {
            chosenBackupItemDialog.dismiss();

            AlertDialog.Builder restoreFromChosenBackupItemDialogBuilder = new AlertDialog.Builder(ActivityBackupData.this);
            restoreFromChosenBackupItemDialogBuilder.setTitle(getResources().getString(R.string.abd_restoreFromChosenBackupItemDialogBuilder_Title_string));
            restoreFromChosenBackupItemDialogBuilder.setMessage(getResources().getString(R.string.abd_restoreFromChosenBackupItemDialogBuilder_Message_string));
            restoreFromChosenBackupItemDialogBuilder.setPositiveButton(getResources().getString(R.string.abd_restoreFromChosenBackupItemDialogBuilder_continue_button_string), (d, w) -> {
                disableBackground();
                restoreDataFromBackup(position);
            });
            restoreFromChosenBackupItemDialogBuilder.setNegativeButton(getResources().getString(R.string.abd_restoreFromChosenBackupItemDialogBuilder_cancel_button_string), null);

            AlertDialog restoreFromChosenBackupItemDialog = restoreFromChosenBackupItemDialogBuilder.create();
            restoreFromChosenBackupItemDialog.show();
        });

        // При нажатии кнопки "Удалить" появляется диалоговое окно, запрашивающее подтверждение удаления
        Button deleteButton = (Button) dialogView.findViewById(R.id.edit_cost_value_dialog_deleteButton);
        deleteButton.setText(getResources().getString(R.string.abd_deleteButton_string));
        deleteButton.setOnClickListener((v) -> {
            chosenBackupItemDialog.dismiss();

            AlertDialog.Builder deleteBackupItemDialogBuilder = new AlertDialog.Builder(ActivityBackupData.this);
            deleteBackupItemDialogBuilder.setTitle(getResources().getString(R.string.abd_deleteBackupItemDialogBuilder_Title_string));
            deleteBackupItemDialogBuilder.setMessage(getResources().getString(R.string.abd_deleteBackupItemDialogBuilder_Message_string));
            deleteBackupItemDialogBuilder.setPositiveButton(getResources().getString(R.string.abd_deleteBackupItemDialogBuilder_delete_button_string), (d, w) -> {
                deleteBackupItem(position);
            });
            deleteBackupItemDialogBuilder.setNegativeButton(getResources().getString(R.string.abd_deleteBackupItemDialogBuilder_cancel_button_string), null);

            AlertDialog deleteBackupItemDialog = deleteBackupItemDialogBuilder.create();
            deleteBackupItemDialog.show();
        });

        Button cancelButton = (Button) dialogView.findViewById(R.id.edit_cost_value_dialog_cancelButton);
        cancelButton.setText(getResources().getString(R.string.abd_cancelButton_string));
        cancelButton.setOnClickListener((v) -> chosenBackupItemDialog.dismiss());
    }

    private void restoreDataFromBackup(int position) {
        if (existingDeviceBackupFolders.size() == 0) {
            Log.i(TAG, "NO BACKUP FILES FOUND");
            return;
        }

        String backupFolderId = existingDeviceBackupFolders.get(position).getDriveId();

        Payload payload = new Payload();
        payload.set("googleDriveService", mBackupState.driveServiceBundle.get().getDriveService());
        payload.set("backupFolderId", backupFolderId);
        payload.set("costsDb", DB_Costs.getInstance(this));

        Action restoreFromBackup = mBackupStore.getActionFactory().getAction(BackupActionsFactory.RestoreFromBackup);
        restoreFromBackup.setPayload(payload);

        mBackupStore.dispatch(restoreFromBackup);
    }

    private void createDeviceBackup() {
        Payload payload = new Payload();
        payload.set("googleDriveService", mBackupState.driveServiceBundle.get().getDriveService());
        payload.set("rootFolderId", mBackupState.backupData.get().getRootFolderId());
        payload.set("costsDb", DB_Costs.getInstance(this));

        Action createDeviceBackup = mBackupStore.getActionFactory().getAction(BackupActionsFactory.CreateDeviceBackup);
        createDeviceBackup.setPayload(payload);

        mBackupStore.dispatch(createDeviceBackup);
    }

    private void deleteBackupItem(int position) {
        if (existingDeviceBackupFolders.size() == 0) {
            Log.i(TAG, "NO BACKUP FILES FOUND");
            return;
        }

        String backupFolderId = existingDeviceBackupFolders.get(position).getDriveId();

        Payload payload = new Payload();
        payload.set("googleDriveService", mBackupState.driveServiceBundle.get().getDriveService());
        payload.set("backupFolderId", backupFolderId);

        Action deleteDeviceBackup = mBackupStore.getActionFactory().getAction(BackupActionsFactory.DeleteDeviceBackup);
        deleteDeviceBackup.setPayload(payload);

        mBackupStore.dispatch(deleteDeviceBackup);
    }

    private void enableBackground() {
        if (backupDataRecyclerViewAdapter != null) {
            backupDataRecyclerViewAdapter.setClickListener((v, p) -> onBackupItemClick(p));
        }

        createBackupDataButton.setEnabled(true);
        createBackupDataButton.setBackgroundResource(R.drawable.keyboard_buttons_custom);
        createBackupDataButton.setTextColor(getResources().getColorStateList(R.color.button_text_color));

        selectGoogleAccountImageView.setEnabled(true);
    }

    private void disableBackground() {
        if (backupDataRecyclerViewAdapter != null) {
            backupDataRecyclerViewAdapter.setClickListener(null);
        }

        createBackupDataButton.setEnabled(false);
        createBackupDataButton.setTextColor(ContextCompat.getColor(ActivityBackupData.this, R.color.lightGrey));

        selectGoogleAccountImageView.setEnabled(false);
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