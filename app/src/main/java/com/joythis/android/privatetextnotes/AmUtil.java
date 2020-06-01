//AmUtil.java
package com.joythis.android.privatetextnotes;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AmUtil {
    public final static String TAG_AM_UTIL = "TAG_AM_UTIL";
    Activity mActivity;

    public AmUtil (
            Activity pA
    ){
        this.mActivity = pA;
    }//AmUtil

    //generic code for requesting and checking permission
    /*
    how to use this tool?
    assuming that o is an object of type AmUtil
    result = o->getPermissionsGrantedAndDenied (
        {Manifest.permission.INTERNET,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_PHONE_STATE}
    );
    result is a Map
    at a key (PackageManager.PERMISSION_GRANTED),
        this map answers the already granted permissions, if any;
    at a key (PackageManager.PERMISSION_DENIED),
        this map answers the still denied to the activity, if any

    e.g.:
    ArrayList <String> permitidas = result.get(PackageManager.PERMISSION_GRANTED);
    ArrayList <String> negadas = result.get(PackageManager.PERMISSION_DENIED);
     */
    public Map<Integer, ArrayList<String>>
    getPermissionsGrantedAndDenied(
            String[] paPermissionToCheck
    )
    {
        Map<Integer, ArrayList<String>> ret = new HashMap<>();

        ArrayList<String> alGranted = new ArrayList<>();
        ArrayList<String> alDenied = new ArrayList<>();

        for (String strPerm : paPermissionToCheck){
            boolean bGranted =
                    ActivityCompat.checkSelfPermission(
                            //pA,
                            this.mActivity,
                            strPerm
                    ) == PackageManager.PERMISSION_GRANTED;

            boolean bDenied =
                    ActivityCompat.checkSelfPermission(
                            //pA,
                            this.mActivity,
                            strPerm
                    ) == PackageManager.PERMISSION_DENIED;

            if (bGranted) alGranted.add(strPerm);
            if (bDenied) alDenied.add(strPerm);
        }//for

        ret.put(PackageManager.PERMISSION_GRANTED, alGranted);
        ret.put(PackageManager.PERMISSION_DENIED, alDenied);

        return ret;
    }//getPermissionsGrantedAndDenied

    /*
    this tool is for requesting the necessary permissions not yet granted,
    and only those. It will NOT request permissions already granted.
     */
    public void requestTheNecessaryPermissions(
            String[] paNecessaryPermissions,
            int pRequestCode
    )
    {
        //2019-11-19
        Map<Integer, ArrayList<String>> mapPermsStatus =
                this.getPermissionsGrantedAndDenied(paNecessaryPermissions);

        ArrayList<String> alGranted =
                mapPermsStatus.get(PackageManager.PERMISSION_GRANTED);

        ArrayList<String> alDenied =
                mapPermsStatus.get(PackageManager.PERMISSION_DENIED);

        String[] aDenied = new String[alDenied.size()];
        alDenied.toArray(aDenied);

        //iterate only through the still denied permissions
        if (alDenied.size()>0){
            ActivityCompat.requestPermissions(
                    //pA,
                    this.mActivity,
                    aDenied, //does NOT accept zero length arrays
                    pRequestCode
            );
        }
    }//requestTheNecessaryPermissions

    /*
    receives the name of a file, located in the "private internal storage"
    returns the entire text content of that file
     */
    public String genericPrivateInternalStorageFileReader(
            String pFileName
    ){
        String strAll = "";
        try{
            FileInputStream fis = mActivity.openFileInput(
                    pFileName
            );
            if (fis!=null){
                InputStreamReader isr = new InputStreamReader(
                        fis,
                        StandardCharsets.UTF_8
                );
                char c; int i;
                final int END_OF_FILE = -1;
                while ((i=isr.read())!=END_OF_FILE){
                    c = (char)i; //cast the byte to a char
                    strAll+=c; //concatenate the char to the already read file contents
                }//while
                isr.close();
            }//if
            fis.close();
        }//try
        catch(Exception e){
            /*
            e.g. : file does not exist
             */
            Log.e(TAG_AM_UTIL, e.getMessage().toString());
        }//catch
        return strAll;
    }//genericPrivateInternalStorageFileReader

    /*
    receives a file name and the content, to be written into the "private internal storage"
    returns true on success, false on failure
     */
    public boolean genericPrivateInternalStorageFileWriter(
            String pFileName,
            String... pContent
    ){
        try {
            FileOutputStream fos =
                    mActivity.openFileOutput(pFileName, Activity.MODE_PRIVATE);

            if (fos!=null){
                OutputStreamWriter osw = new OutputStreamWriter(
                        fos,
                        StandardCharsets.UTF_8
                );
                for (String strPartial : pContent){
                    osw.write(strPartial);
                }//for
                osw.close();
            }//if
            fos.close();

            return true;
        }//try
        catch (Exception e){
            Log.e(TAG_AM_UTIL, e.getMessage().toString());
            return false;
        }//catch
    }//genericPrivateInternalStorageFileWriter

    /*
        receives the text for an about to be created new Button
        receives the already existing LinearLayout where the dyn created Button
        is to be added
        receives an already existing click listener to be assigned to the
        about to be created new Button
     */
    void createNewButtonInLinearLayout (
            String pStrButtonText, //text for the new Button
            LinearLayout pLayoutWhereToAddTheNewButton, //LL where to add the Button (can NOT be null)
            Button.OnClickListener pButtonClickHandler //object that handles the behavior for the new Button
    )
    {
        Button btnNewNumber = new Button(mActivity);
        btnNewNumber.setText(pStrButtonText);
        LinearLayout.LayoutParams wh = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        btnNewNumber.setLayoutParams(wh);
        btnNewNumber.setOnClickListener(pButtonClickHandler);

        pLayoutWhereToAddTheNewButton.addView(btnNewNumber);
    }//createNewButtonInLinearLayout

    public void actionQuit(){
        Intent intentQuitToMain = new Intent(Intent.ACTION_MAIN);
        intentQuitToMain.addCategory(Intent.CATEGORY_HOME);
        intentQuitToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(intentQuitToMain);
        mActivity.finish();
    }//actionQuit


    private static String
        addZeroIfNeededForHaving2Digits(
            int pSomeNumber
        )
    {
        if (pSomeNumber<10)
            return "0"+pSomeNumber;
        else
            return String.valueOf(pSomeNumber);
    }//addZeroIfNeededForHaving2Digits

    public static String CalendarToString(
        Calendar pC
    ){
        String strRet = "";

        int year, month, day, hour, minutes, seconds;
        year = pC.get(Calendar.YEAR);
        month = pC.get(Calendar.MONTH)+1;
        day = pC.get(Calendar.DATE);
        hour = pC.get(Calendar.HOUR_OF_DAY);//CTRL^Q
        minutes = pC.get(Calendar.MINUTE);
        seconds = pC.get(Calendar.SECOND);

        String strYear, strMonth, strDay,
                strHour, strMinutes, strSeconds;

        strYear = String.valueOf(year);
        strMonth = AmUtil.addZeroIfNeededForHaving2Digits(month);
        strDay = AmUtil.addZeroIfNeededForHaving2Digits(day);
        strHour = AmUtil.addZeroIfNeededForHaving2Digits(hour);
        strMinutes = AmUtil.addZeroIfNeededForHaving2Digits(minutes);
        strSeconds = AmUtil.addZeroIfNeededForHaving2Digits(seconds);

        strRet = String.format(
            "%s-%s-%s %s:%s:%s",
            strYear,
            strMonth,
            strDay,
            strHour,
            strMinutes,
            strSeconds
        );

        return strRet;
    }//CalendarToString

    public static Calendar CalendarFromString(
        String pStrCalendar //Y-M-D hh:mm:ss
    ) throws Exception //consequence: use try{}catch{Exception e} at the caller
    {
        Calendar ret = Calendar.getInstance();
        ret.clear();

        int year, month, day, hour, minutes, seconds;

        //TODO: init year .. seconds
        String[] aCalendarParts = pStrCalendar.split(" ");
        boolean bCaution = aCalendarParts.length==2;
        if (bCaution){
            String strDate = aCalendarParts[0];
            String strTime = aCalendarParts[1];

            String[] aDateParts = strDate.split("-");
            String[] aTimeParts = strTime.split(":");
            boolean bSecondCaution = aDateParts.length==3 && aTimeParts.length==3;
            if (bSecondCaution){
                try {
                    year = Integer.parseInt(aDateParts[0]);
                    month = Integer.parseInt(aDateParts[1]);
                    day = Integer.parseInt(aDateParts[2]);

                    hour = Integer.parseInt(aTimeParts[0]);
                    minutes = Integer.parseInt(aTimeParts[1]);
                    seconds = Integer.parseInt(aTimeParts[2]);

                    ret.set(Calendar.YEAR, year);
                    ret.set(Calendar.MONTH, month);
                    ret.set(Calendar.DATE, day);
                    ret.set(Calendar.HOUR_OF_DAY, hour);
                    ret.set(Calendar.MINUTE, minutes);
                    ret.set(Calendar.SECOND, seconds);

                    return ret;
                }//try
                catch(Exception e){
                    //failure in extracting numbers from the date or the time
                    String strError = e.getMessage().toString();
                    Log.e(TAG_AM_UTIL, strError);
                }//catch
            }//if second caution
        }//if first caution

        String strError = "Could NOT parse Calendar string!";
        Log.e(TAG_AM_UTIL, strError);
        throw new Exception(strError);

        //return null; //never happens
    }//CalendarFromString
}
