package com.ramprosmart.wheelview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ramprosmart.widget.WheelView;

public class MainActivity extends AppCompatActivity
{
    final String TAG = MainActivity.class.getSimpleName();

    final String[] MONTHS = new String[] {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    WheelView monthSelectorView;
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        monthSelectorView = (WheelView) findViewById(R.id.month_selector);
        submitBtn = (Button) findViewById(R.id.submit_btn);

        monthSelectorView.setItems(MONTHS);

        submitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int selectedIndex = monthSelectorView.getSelectedIndex();
                String selectedItem = MONTHS[selectedIndex];
                Log.e(TAG, "selectedIndex: "+selectedIndex+", selectedItem: "+selectedItem);
            }
        });
    }
}
