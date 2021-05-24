package com.example.diseasetracker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final EditText ID = findViewById(R.id.edtID);
        final EditText Name = findViewById(R.id.edtName);
        final EditText Surname = findViewById(R.id.edtSurname);
        final RadioGroup Gender = findViewById(R.id.radGender);
        final RadioGroup Diagnosed = findViewById(R.id.radDiagnosed);

        Button b = findViewById(R.id.btnNext);
        b.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String sID = ID.getText().toString();
                String sName = Name.getText().toString();
                String sSurname = Surname.getText().toString();
                String sGender =getGender(Gender);
                final String sAge = getDate();
                String sDiagnosed = getDiagnosed(Diagnosed);
                if (sID.length() == 0 || sName.length() == 0 || sSurname.length() == 0
                                || sGender.length() == 0 || sAge.length() == 0 || sDiagnosed.length() == 0){
                    SignUp.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignUp.this,
                                    "You have not filled in all the required information.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    Intent info = passInfo(sID, sName, sSurname, sGender, sAge, sDiagnosed);
                    startActivity(info);
                }
            }
        });
    }

    public String getDate(){
        DatePicker Age = findViewById(R.id.dpDoB);
        int day = Age.getDayOfMonth();
        int month = Age.getMonth()+1;
        int year = Age.getYear();

        String Year = Integer.toString(year);
        String Month = "";
        if (month < 10) {
            Month = "0" + month;
        } else {
            Month = Integer.toString(month);
        }
        String Day = "";
        if (day < 10) {
            Day = "0" + day;
        } else {
            Day = Integer.toString(day);
        }

        return Year + "-" + Month + "-" + Day;
    }

    public String getGender(final RadioGroup Gender){
        if (Gender.getCheckedRadioButtonId() == -1){
            return "";
        }

        int selectedId = Gender.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        String gender = radioButton.getText().toString();
        if (gender.equals("Male")) {
            return "M";
        }
        else if(gender.equals("Female")){
            return "F";
        }
        else return "N";
    }

    public String getDiagnosed(final RadioGroup Diagnosed){
        if (Diagnosed.getCheckedRadioButtonId() == -1){
            return "";
        }

        // get selected radio button from radioGroup
        int selectedId = Diagnosed.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        return radioButton.getText().toString();
    }

    public Intent passInfo(String sID, String sName, String sSurname, String sGender, String sAge, String sDiagnosed){
        Intent i = new Intent(SignUp.this, Sign_Up2.class);
        i.putExtra("ID", sID);
        i.putExtra("Name", sName);
        i.putExtra("Surname", sSurname);
        i.putExtra("Gender", sGender);
        i.putExtra("Age", sAge);
        i.putExtra("Diagnosed", sDiagnosed);
        return i;
    }
}