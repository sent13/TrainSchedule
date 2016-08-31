package io.sent.trainschedule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

/**
 * Created by sent13 on 16/08/24.
 */
public class SettingActivity extends AppCompatActivity{

    ScheduleApplication application;

    RadioButton settingThemeRadioButton1;
    RadioButton settingThemeRadioButton2;
    EditText shortMostFastEdit;
    EditText shortFastEdit;
    EditText shortSlowEdit;
    EditText longMostFastEdit;
    EditText longFastEdit;
    EditText longSlowEdit;
    Button settingSaveButton;
    Button settingDefaultButton;
    Button settingCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        application=(ScheduleApplication)this.getApplication();
        if(application.getThemeNum()!=1) setTheme(R.style.NoActionBarThemeDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        findViews();
        initViews();
    }

    private void findViews(){
        settingThemeRadioButton1=(RadioButton)findViewById(R.id.setting_theme_rdo_btn1);
        settingThemeRadioButton2=(RadioButton)findViewById(R.id.setting_theme_rdo_btn2);
        shortMostFastEdit=(EditText)findViewById(R.id.short_most_fast_edit);
        shortFastEdit=(EditText)findViewById(R.id.short_fast_edit);
        shortSlowEdit=(EditText)findViewById(R.id.short_slow_edit);
        longMostFastEdit=(EditText)findViewById(R.id.long_most_fast_edit);
        longFastEdit=(EditText)findViewById(R.id.long_fast_edit);
        longSlowEdit=(EditText)findViewById(R.id.long_slow_edit);
        settingSaveButton=(Button)findViewById(R.id.setting_save_btn);
        settingDefaultButton=(Button)findViewById(R.id.setting_default_btn);
        settingCancelButton=(Button)findViewById(R.id.setting_cancel_btn);
    }

    private void initViews(){
        shortMostFastEdit.setText(application.getTrainShuruiStr(application.MOST_FAST_SHORT));
        shortFastEdit.setText(application.getTrainShuruiStr(application.FAST_SHORT));
        shortSlowEdit.setText(application.getTrainShuruiStr(application.SLOW_SHORT));
        longMostFastEdit.setText(application.getTrainShuruiStr(application.MOST_FAST_LONG));
        longFastEdit.setText(application.getTrainShuruiStr(application.FAST_LONG));
        longSlowEdit.setText(application.getTrainShuruiStr(application.SLOW_LONG));
        int themeNum=application.getThemeNum();
        if(themeNum==1){
            settingThemeRadioButton1.setChecked(true);
        }else{
            settingThemeRadioButton2.setChecked(true);
        }

        settingCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        settingDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingThemeRadioButton1.setChecked(true);
                shortMostFastEdit.setText("新");
                shortFastEdit.setText("快");
                shortSlowEdit.setText("普");
                longMostFastEdit.setText("新快速");
                longFastEdit.setText("快速");
                longSlowEdit.setText("普通");
            }
        });

        settingSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkShuruiEditor()){
                    if(settingThemeRadioButton1.isChecked()){
                        application.setThemeNum(application.THEME_LIGHT);
                    }else{
                        application.setThemeNum(application.THEME_DARK);
                    }
                    application.setTrainSaveShuruiStr(collectShuruiStr());
                    setResult(RESULT_OK);
                    finish();
                }else{
                    toast("未入力の項目があります");
                }

            }
        });
    }

    private boolean checkShuruiEditor(){
        if(shortMostFastEdit.getText().toString().trim().equals("")) return false;
        if(shortFastEdit.getText().toString().trim().equals("")) return false;
        if(shortSlowEdit.getText().toString().trim().equals("")) return false;
        if(longMostFastEdit.getText().toString().trim().equals("")) return false;
        if(longFastEdit.getText().toString().trim().equals("")) return false;
        if(longSlowEdit.getText().toString().trim().equals("")) return false;
        return true;
    }

    private String collectShuruiStr(){
        StringBuffer str=new StringBuffer();
        str.append(shortMostFastEdit.getText().toString().trim()+",");
        str.append(shortFastEdit.getText().toString().trim()+",");
        str.append(shortSlowEdit.getText().toString().trim()+",");
        str.append(longMostFastEdit.getText().toString().trim()+",");
        str.append(longFastEdit.getText().toString().trim()+",");
        str.append(longSlowEdit.getText().toString().trim());
        return str.toString();
    }

    private void toast(String text){
        if(text==null) text="";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
