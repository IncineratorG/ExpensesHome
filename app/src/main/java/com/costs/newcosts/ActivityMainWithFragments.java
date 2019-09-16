package com.costs.newcosts;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.costs.newcosts.activities.backup.ActivityBackupData;

public class ActivityMainWithFragments extends AppCompatActivity {

    private int PREVIOUS_ACTIVITY_INDEX = -1;
    private int TARGET_TAB = -1;
    private String savedValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_fragments);

        final TabLayout mainActivityTabLayout = (TabLayout) findViewById(R.id.activity_main_tab_layout);
        mainActivityTabLayout.addTab(mainActivityTabLayout.newTab().setText("Tab 1"));
        mainActivityTabLayout.addTab(mainActivityTabLayout.newTab().setText("Tab 2"));
        mainActivityTabLayout.addTab(mainActivityTabLayout.newTab().setText("Tab 3"));
        mainActivityTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Переходим на экран чтения данных из СМС
        ImageView creditCardImageView = (ImageView) findViewById(R.id.activity_main_credit_card_imageview);
        creditCardImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("READ_SMS_CLICK");

                Intent smsExpensesReaderActivityIntent = new Intent(ActivityMainWithFragments.this, ActivitySmsExpensesReader.class);
                startActivity(smsExpensesReaderActivityIntent);
            }
        });

        // Переходим на экран сохранение данных на Google Drive
        ImageView backupDataImageView = (ImageView) findViewById(R.id.activity_main_backup_data_imageview);
        backupDataImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backupDataActivity = new Intent(ActivityMainWithFragments.this, ActivityBackupData.class);
                startActivity(backupDataActivity);
            }
        });


        TextView fragmentCurrentMonthScreenTab = (TextView) LayoutInflater.from(this).inflate(R.layout.activity_main_with_fragments_custom_tab, null);
        fragmentCurrentMonthScreenTab.setText(getResources().getString(R.string.fragmentCurrentMonthScreenTab_string));
        fragmentCurrentMonthScreenTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_mode_edit_white_24dp, 0, 0);
        mainActivityTabLayout.getTabAt(0).setCustomView(fragmentCurrentMonthScreenTab);

        TextView fragmentLastEnteredValuesTab = (TextView) LayoutInflater.from(this).inflate(R.layout.activity_main_with_fragments_custom_tab, null);
        fragmentLastEnteredValuesTab.setText(getResources().getString(R.string.fragmentLastEnteredValuesTab_string));
        fragmentLastEnteredValuesTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_history_white_24dp, 0, 0);
        mainActivityTabLayout.getTabAt(1).setCustomView(fragmentLastEnteredValuesTab);

        TextView fragmentStatisticMainScreenTab = (TextView) LayoutInflater.from(this).inflate(R.layout.activity_main_with_fragments_custom_tab, null);
        fragmentStatisticMainScreenTab.setText(getResources().getString(R.string.fragmentStatisticMainScreenTab_string));
        fragmentStatisticMainScreenTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_pie_chart_white_24dp, 0, 0);
        mainActivityTabLayout.getTabAt(2).setCustomView(fragmentStatisticMainScreenTab);



        final ViewPager mainActivityViewPager = (ViewPager) findViewById(R.id.activity_main_viewpager);
        mainActivityViewPager.setOffscreenPageLimit(mainActivityTabLayout.getTabCount());
        final AdapterMainActivityPager mainActivityViewPagerAdapter = new AdapterMainActivityPager
                (getSupportFragmentManager(), mainActivityTabLayout.getTabCount());
        mainActivityViewPager.setAdapter(mainActivityViewPagerAdapter);
        mainActivityViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mainActivityTabLayout));
        mainActivityTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mainActivityViewPager.setCurrentItem(tab.getPosition());
                // Обновляем содержимое "mainActivityViewPager" только когда
                // пользователь что-либо ввёл или изменил
                if (!Constants.mainActivityFragmentsDataIsActual)
                    mainActivityViewPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });




        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            PREVIOUS_ACTIVITY_INDEX = bundle.getInt(Constants.PREVIOUS_ACTIVITY_INDEX);
            TARGET_TAB = bundle.getInt(Constants.TARGET_TAB);
            savedValue = bundle.getString(Constants.SAVED_VALUE);

            if (savedValue != null && !"".equals(savedValue)) {
                Snackbar savedValueSnackbar = Snackbar.make(mainActivityViewPager,
                        savedValue + " " +                             // 250
                                getResources().getString(R.string.rur_string) +                                // руб
                                getResources().getString(R.string.dot_sign_string) + " " +                            // .
                                getResources().getString(R.string.savedValueSnackbar_saved_string),                           // сохранено
                        Snackbar.LENGTH_LONG);
                savedValueSnackbar.show();
            }

            TabLayout.Tab selectedTab = null;
            switch (TARGET_TAB) {
                case Constants.FRAGMENT_CURRENT_MONTH_SCREEN:
                    selectedTab = mainActivityTabLayout.getTabAt(0);
                    if (selectedTab != null)
                        selectedTab.select();
                    break;
                case Constants.FRAGMENT_LAST_ENTERED_VALUES_SCREEN:
                    selectedTab = mainActivityTabLayout.getTabAt(1);
                    if (selectedTab != null)
                        selectedTab.select();
                    break;
                case Constants.FRAGMENT_STATISTIC_MAIN_SCREEN:
                    Constants.mainActivityFragmentsDataIsActual = true;
                    selectedTab = mainActivityTabLayout.getTabAt(2);
                    if (selectedTab != null)
                        selectedTab.select();
            }
        }
    }

}
