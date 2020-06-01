package com.joythis.android.privatetextnotes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ActivityThatDisplaysTheTSVContents extends AppCompatActivity {
    TextView mTvTSVContents;
    MyNotes mNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_that_displays_the_tsvcontents);

        init();
    }//onCreate

    void init(){
        //assocs
        mTvTSVContents = findViewById(R.id.idTvTSVContents);

        //app specific ops
        mNotes = new MyNotes(this, MainActivity.TSV_DB_NAME);
        String strEntireTSV = mNotes.readFromTSV();
        mTvTSVContents.setText(strEntireTSV);
    }//init

}//ActivityThatDisplaysTheTSVContents
