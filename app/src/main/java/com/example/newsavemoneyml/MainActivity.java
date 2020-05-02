package com.example.newsavemoneyml;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    private File mainsheetDir;
    private File mainsheet;
    private File sms_extracted_csv;
    private ArrayList<MainSheet> sheetSamples = new ArrayList<>();
    private ArrayList<SMS_Extracted> smsExtracted = new ArrayList<>();
    private HashMap<String, List> finaldata = new HashMap<>();

    String selectedDate;
    SharedPreferences sharedpreferences;

    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int SMS_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createFileMainSheet();
        sharedpreferences = getSharedPreferences("Time", Context.MODE_PRIVATE);
        selectedDate = sharedpreferences.getString("time","01-01-2020" + "T" + "00:00:00");
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_SMS}, SMS_PERMISSION_CODE);
        }

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        refreshSmsInbox();
        createFileSMS_ExtractedCSV();
        readMainSheet();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        if(sp.getString("AVL_BAL", "0.0") == "0.0"){
            editor.putString("AVL_BAL", "500000.0");
            editor.commit();
        }
        mainFn();
        printSMS_ExtractedCSV();

    }


    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { permission },
                    requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == SMS_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) ==  PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.SEND_SMS}, 1);
                Toast.makeText(MainActivity.this, "No Permission granted", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
            } else {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


    public void refreshSmsInbox() {

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy'T'hh:mm:ss");
        Log.i("heyy",selectedDate);

        Date dateStart = null;
        try {
            dateStart = formatter.parse(selectedDate);
        }  catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        String filter = "date>=" + dateStart.getTime();
        Log.i("Hey",millisToDate( dateStart.getTime()));

        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, filter, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        int indexDate =smsInboxCursor.getColumnIndex("date");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) {
            return;
        }

        do {
            String date=millisToDate(smsInboxCursor.getLong(indexDate));
            String date1 = ""+ date;
            date1 = date1.replace(" ", ",");
            date1 = date1.replace("/", "-");
            String b = ""+smsInboxCursor.getString(indexBody);
            b = b.replace(",", " ");
            b = b.replace("\n", " ");
            String str = date1 +"," + smsInboxCursor.getString(indexAddress)+ "," + b + "\n";
            writeMainSheet(str);
        } while (smsInboxCursor.moveToNext());


        selectedDate= formatter.format(new Date());
        sharedpreferences.edit().putString("time",selectedDate).apply();
    }

    public static String millisToDate(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Date date = calendar.getTime();
        return dateFormat.format(date);
    }


    private void clearMainSheet(){
        if (mainsheet.exists()) {
            try {
                FileWriter fileWriter = new FileWriter(mainsheet);
                BufferedWriter bfWriter = new BufferedWriter(fileWriter);
                bfWriter.write("");
                bfWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Function to create a MainSheet.csv file.
    private void createFileMainSheet(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ) {
                mainsheetDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "SaveMoneyFiles");
                if (!mainsheetDir.exists()) {
                    try {
                        mainsheetDir.mkdir();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mainsheet = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "SaveMoneyFiles" + File.separator + "mainsheet.csv");
                if (!mainsheet.exists()) {
                    try {
                        mainsheet.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        else {
            mainsheetDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "SaveMoneyFiles");
            if (!mainsheetDir.exists()) {
                try {
                    mainsheetDir.mkdir();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mainsheet = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "SaveMoneyFiles" + File.separator + "mainsheet.csv");
            if (!mainsheet.exists()) {
                try {
                    mainsheet.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    // Function to create a SMS_Extracted.csv file.
    private void createFileSMS_ExtractedCSV(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ) {
                mainsheetDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "SaveMoneyFiles");
                if (!mainsheetDir.exists()) {
                    try {
                        mainsheetDir.mkdir();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                sms_extracted_csv = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "SaveMoneyFiles" + File.separator + "sms_extracted.csv");
                if (!sms_extracted_csv.exists()) {
                    try {
                        sms_extracted_csv.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        else {
            mainsheetDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "SaveMoneyFiles");
            if (!mainsheetDir.exists()) {
                try {
                    mainsheetDir.mkdir();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sms_extracted_csv = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "SaveMoneyFiles" + File.separator + "sms_extracted.csv");
            if (!sms_extracted_csv.exists()) {
                try {
                    sms_extracted_csv.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    // Function to write to MainSheet.csv file
    private void writeMainSheet(String msg){

        if (mainsheet.exists()) {
            try {
                FileWriter fileWriter = new FileWriter(mainsheet,true);
                BufferedWriter bfWriter = new BufferedWriter(fileWriter);
                bfWriter.write(msg);
                bfWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Function to append to SMS_Extracted.csv file
    private void writeSMS_ExtractedCSV(String data){

        if (sms_extracted_csv.exists()) {
            try {
                FileOutputStream fileinput = new FileOutputStream(sms_extracted_csv); // Remove true to avoid append.
                PrintStream printstream = new PrintStream(fileinput);
                printstream.print(data+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private ArrayList<String> readSMS_ExtractedCSV(){
        String line;
        ArrayList<String> temp = new ArrayList<>();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(sms_extracted_csv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(fis));

            while ((line = r.readLine()) != null) {
                String[] tokens = line.split(",");
                temp.add(tokens[0] + "," + tokens[9]);
            }

        } catch (IOException e) {
            // Logs error with priority level
            Log.wtf("MyActivity", "Error reading data file on line" , e);

            // Prints throwable details
            e.printStackTrace();
        }
        return temp;
    }

    // Function to read sms_extracted.csv and store the values into sms_extracted.
    private void printSMS_ExtractedCSV(){
        String line;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(sms_extracted_csv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(fis));

            while ((line = r.readLine()) != null) {
                Log.d("SMS_Extracted ","Line: " + line);
            }

        } catch (IOException e) {
            // Logs error with priority level
            Log.wtf("MyActivity", "Error reading data file on line" , e);

            // Prints throwable details
            e.printStackTrace();
        }
    }

    // Function to read MainSheet and store the values into sheetSamples.
    private void readMainSheet(){

        String line;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(mainsheet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(fis));

            while ((line = r.readLine()) != null) {
                Log.d("MyActivity","Line: " + line);
                String[] tokens = line.split(",");
                MainSheet sample = new MainSheet();

                // Setters
                sample.setDate(tokens[0]);
                sample.setTime(tokens[1]);
                sample.setHead(tokens[2]);
                sample.setBody(tokens[3]);

                // Adding object to a class
                sheetSamples.add(sample);

                // Log the object
                Log.d("My Activity", "Just created: " + sample);
            }

        } catch (IOException e) {
            // Logs error with priority level
            Log.wtf("MyActivity", "Error reading data file on line" , e);

            // Prints throwable details
            e.printStackTrace();
        }
    }


    // Function for checking head value is BANK/MARCHANT/OTHER
    private String headcheck(String a){

        Datasheet datasheet = new Datasheet(); // Separate Class for merchant.csv & Autocorrect.csv files
        List<List<String>> obj = datasheet.createMerchant(); // For merchant.csv File.
        List<String> bnk = obj.get(0); // For Bank Column from merchant.csv File.
        List<String> mname = obj.get(1); // For Merchant Column from merchant.csv File.

        for (int i = 0;i<bnk.size();i++){
            if(a.toLowerCase().contains(bnk.get(i))) //Pattern to check BANK
                return "BANK";
        }
        for (int i = 0;i<mname.size();i++){
            if(a.toLowerCase().contains(mname.get(i))) // Pattern to check MARCHANT
                return "MERCHANT";
        }
        return "OTHER";
    }

    private boolean is_index(int n){

        try {
            sheetSamples.get(n);
        }catch (IndexOutOfBoundsException e){
            return false;
        }
        return true;
    }


    private double is_indexsheet(String n){

        try {

            smsExtracted.get(smsExtracted.size()-1);

        }catch (Exception e){
            return -1;
        }
        return smsExtracted.get(smsExtracted.size()-1).getAVL_BAL();
    }

    // Line 207 - 219
    private ArrayList<Integer> getIndex(ArrayList<String> l, String rs){
        ArrayList<Integer> indexPosList = new ArrayList<>();
        for(int i = 0; i < l.size(); i++){
            if(l.get(i).equals(rs))
                indexPosList.add(i);
        }
        return indexPosList;
    }

    // function to convert String to Date format.
    public static Date getDateFromString(String date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  myDate;
    }


    private void mainFn(){

        Datasheet datasheet = new Datasheet(); // Separate Class for merchant.csv & Autocorrect.csv files
        List<List<String>> objMer = datasheet.createMerchant(); // For merchant.csv File.
        List<String> bnk = objMer.get(0); // For Bank Column from merchant.csv File.
        List<String> mname = objMer.get(1); // For Merchant Column from merchant.csv File.

        List<List<String>> objAuto = datasheet.createAutocorrect();
        List<String> unigram = datasheet.createUnigram();

        // Line 88 - 129
        for(int i = 0; i < sheetSamples.size(); i++) {
            MainSheet sms = new MainSheet();
            sms.setDate(sheetSamples.get(i).getDate());
            sms.setTime(sheetSamples.get(i).getTime());

            if (headcheck(sheetSamples.get(i).getHead()).equals("BANK") && is_index(i + 1) == true) {
                if (headcheck(sheetSamples.get(i + 1).getHead()).equals("MERCHANT")) {
                    sms.setHead(sheetSamples.get(i).getHead());
                    sms.setBody(sheetSamples.get(i).getBody());
                } else {
                    sms.setHead(sheetSamples.get(i).getHead());
                    sms.setBody(sheetSamples.get(i).getBody());
                }
            }

            if (headcheck(sheetSamples.get(i).getHead()).equals("BANK") && is_index(i + 1) == false) {
                sms.setHead(sheetSamples.get(i).getHead());
                sms.setBody(sheetSamples.get(i).getBody());
            }

            if ((headcheck(sheetSamples.get(i).getHead()) == "OTHER") || headcheck(sheetSamples.get(i).getHead()).equals("MERCHANT")) {
                sms.setHead("OTHER");
                sms.setBody("OTHER");
            }

            if((!sms.getHead().equals("OTHER")) && (!sms.getBody().equals("OTHER"))) {
                // Line 131 - 142
                String bank = "";
                for (int j = 0; j < bnk.size(); j++) {
                    if (sms.getHead().toLowerCase().contains(bnk.get(j))) {
                        bank = bnk.get(j);
                        break;
                    }
                }

                // Line 143
                String txt = sms.getBody().toLowerCase();
                // Line 154
                txt = txt.replace("."," ");
                txt = txt.replaceAll("(?<=\\D)(?=\\d)", " ");

                String[] z = txt.split(" ");
                String txt1 = "";

                for (int j = 0; j < z.length; j++)
                    txt1 = txt1 + " " + z[j];

                for (int j = 0; j < unigram.size(); j++)
                    txt1 = txt1.replaceAll(unigram.get(j), " " + unigram.get(j) + " ");

                z = txt1.split(" ");


                // Line 167
                ArrayList<String> s = new ArrayList<>();
                for (int j = 0; j < unigram.size(); j++) {
                    s.add(unigram.get(j));
                }
                for (int j = 0; j < z.length; j++) {
                    if (z[j].matches("-?\\d+(\\.\\d+)?")) {
                        s.add(z[j]);
                    }
                }

                // Line 173
                ArrayList<String> t = new ArrayList<>();
                for (int j = 0; j < z.length; j++) {
                    for (int k = 0; k < s.size(); k++) {
                        if (z[j].equalsIgnoreCase(s.get(k))) {
                            t.add(z[j]);
                        }
                    }
                }

                // Line 179
                List<String> otp = objMer.get(10);
                List<String> ess = objMer.get(11);

                // Line 186
                List<String> crd = objAuto.get(0);
                List<String> dbt = objAuto.get(1);
                List<String> rs = objAuto.get(2);
                List<String> bln = objAuto.get(3);

                ArrayList<String> l = new ArrayList<>();
                for (int j = 0; j < t.size(); j++) {
                    for (int k = 0; k < crd.size(); k++) {
                        if (crd.get(k).equals(t.get(j)))
                            l.add(crd.get(k));
                    }
                    for (int k = 0; k < dbt.size(); k++) {
                        if (dbt.get(k).equals(t.get(j)))
                            l.add(dbt.get(k));
                    }
                    for (int k = 0; k < rs.size(); k++) {
                        if (rs.get(k).equals(t.get(j)))
                            l.add(rs.get(k));
                    }
                    for (int k = 0; k < bln.size(); k++) {
                        if (bln.get(k).equals(t.get(j)))
                            l.add(bln.get(k));
                    }
                    for (int k = 0; k < mname.size(); k++) {
                        if (mname.get(k).equals(t.get(j)))
                            l.add(mname.get(k));
                    }
                    for (int k = 0; k < otp.size(); k++) {
                        if (otp.get(k).equals(t.get(j)))
                            l.add(otp.get(k));
                    }

                    // Line 205 to be added after is_number() fn is made.
                    try {
                        Double.parseDouble(t.get(j));
                        l.add(t.get(j));
                    } catch (Exception e) {
                    }
                }

                //
                // Line 207 - 219 contains function getIndex() which is declared just before this mainFn().
                //

                // Line 222 - 224 not neaded as we already have date and time as menbers of sms object.

                // Line 227 Using SharedPreferences for storing the last available balance.
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                String av1 = sp.getString("AVL_BAL", "0.0");
                double av = Double.parseDouble(av1);
                // Line 231 Creating a Object of SMS_Extracted class.
                SMS_Extracted data = new SMS_Extracted();
                data.setDATE(sms.getDate());
                data.setTIME(sms.getTime());
                data.setBANK(bank);

                ArrayList<String> p = new ArrayList<>();

                // Renaming j as jl as j is used for looping
                ArrayList<Integer> jl;

                // Line 236
                for (int j = 0; j < l.size(); j++) {
                    if (l.get(j).equals("CREDIT"))
                        data.setCREDIT(1);
                    if (l.get(j).equals("DEBIT"))
                        data.setDEBIT(1);
                    if (l.get(j).equals("ATM"))
                        data.setATM(1);
                    else
                        data.setNET_BANKING(1);
                    if (l.get(j).equals("rs"))
                        p.add(l.get(j));
                }

                data.setMERCHANT(" ");
                // Line 246 (Can be merged with above for loop.)
                for (int j = 0; j < l.size(); j++) {
                    for (int k = 0; k < mname.size(); k++) {
                        if (l.get(j).equals(mname.get(k)))
                            data.setMERCHANT(mname.get(k));
                    }
                    for (int k = 0; k < objMer.get(2).size(); k++) {
                        if (l.get(j).equals(objMer.get(2).get(k)))
                            data.setFOOD(1);
                    }
                    for (int k = 0; k < objMer.get(3).size(); k++) {
                        if (l.get(j).equals(objMer.get(3).get(k)))
                            data.setSHOPPING(1);
                    }
                    for (int k = 0; k < objMer.get(4).size(); k++) {
                        if (l.get(j).equals(objMer.get(4).get(k)))
                            data.setGROCERY(1);
                    }
                    for (int k = 0; k < objMer.get(5).size(); k++) {
                        if (l.get(j).equals(objMer.get(5).get(k)))
                            data.setTRAVEL(1);
                    }
                    for (int k = 0; k < objMer.get(6).size(); k++) {
                        if (l.get(j).equals(objMer.get(6).get(k)))
                            data.setMEDICAL(1);
                    }
                    for (int k = 0; k < objMer.get(7).size(); k++) {
                        if (l.get(j).equals(objMer.get(7).get(k)))
                            data.setBILLS(1);
                    }
                    for (int k = 0; k < objMer.get(8).size(); k++) {
                        if (l.get(j).equals(objMer.get(8).get(k)))
                            data.setSUBSCRIPTION(1);
                    }
                    for (int k = 0; k < objMer.get(9).size(); k++) {
                        if (l.get(j).equals(objMer.get(9).get(k)))
                            data.setEMI(1);
                    }
                }
                SharedPreferences.Editor editor = sp.edit();
                // Line 264
                jl = getIndex(l, "rs");

                data.setAVL_BAL(av);

                if (p.size() > 2) {
                    data.setTRX_AMT(Double.parseDouble(l.get(jl.get(0) + 1)));
                    data.setAVL_BAL(Double.parseDouble(l.get(jl.get(1) + 1)));
                    editor.putString("AVL_BAL", l.get(jl.get(1) + 1));
                    editor.commit();

                }
                if (p.size() == 2) {
                    if (jl.get(0) == jl.get(1)) {
                        data.setTRX_AMT(Double.parseDouble(l.get(jl.get(0) + 1)));
                        if (data.getCREDIT() == 1) {
                            data.setAVL_BAL(av + Double.parseDouble(l.get(jl.get(0) + 1)));
                            editor.putString("AVL_BAL", Double.toString(av + Double.parseDouble(l.get(jl.get(0) + 1))));
                            editor.commit();
                        } else {
                            editor.putString("AVL_BAL", Double.toString(av - Double.parseDouble(l.get(jl.get(0) + 1))));
                            editor.commit();
                            data.setAVL_BAL(av - Double.parseDouble(l.get(jl.get(0) + 1)));
                        }
                    } else {
                        data.setTRX_AMT(Double.parseDouble(l.get(jl.get(0) + 1)));
                        data.setAVL_BAL(Double.parseDouble(l.get(jl.get(1) + 1)));
                        editor.putString("AVL_BAL", l.get(jl.get(1) + 1));
                        editor.commit();
                    }
                }
                if (p.size() == 1) {
                    data.setTRX_AMT(Double.parseDouble(l.get(jl.get(0) + 1)));
                    if (data.getCREDIT() == 1) {
                        data.setAVL_BAL(av + Double.parseDouble(l.get(jl.get(0) + 1)));
                        editor.putString("AVL_BAL", Double.toString(av + Double.parseDouble(l.get(jl.get(0) + 1))));
                        editor.commit();
                    } else {
                        data.setAVL_BAL(av - Double.parseDouble(l.get(jl.get(0) + 1)));
                        editor.putString("AVL_BAL", Double.toString(av - Double.parseDouble(l.get(jl.get(0) + 1))));
                        editor.commit();
                    }
                }


                // Line 290 above to be done.

                Date today = getDateFromString(sms.getDate()); // Conversion of Data from String to Date format.
                Calendar lmonth = Calendar.getInstance();
                lmonth.setTime(today);
                lmonth.add(Calendar.DAY_OF_YEAR, -30); // today - timedelta(days=30)

                ArrayList<String> temp = readSMS_ExtractedCSV();
                int count = 0;

                //Line 296-300 has to implement by other ways because there is no function in java to compare to Dates.
                for (int j = 0; j < temp.size(); j++) {

                    try {
                        String[] tokens = temp.get(j).split(",");
                        String tempDate = tokens[0];
                        String mername = tokens[1];
                        Date dtst = getDateFromString(tempDate);
                        Calendar current = Calendar.getInstance();
                        current.setTime(dtst);
                        if (lmonth.getTimeInMillis() - current.getTimeInMillis() <= 0 && data.getMERCHANT().equals(mername)) {
                            count += 1;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                if (count >= 2) {
                    data.setREC(1);
                } else {
                    data.setNON_REC(1);
                }

                for (int j = 0; j < ess.size(); j++) {
                    if (data.getMERCHANT() == ess.get(j)) {
                        data.setESS(1);
                    } else {
                        data.setNON_ESS(1);
                    }
                }

                // Line 312 Format data variable from dictionary to pandans frame format
                // Line 315-319 a function to add a new data entry from data to SMS_Extracted.xlsx file
                // Not necessary for us, we just have to store data variable values
                String t1 = "";
                for (int j = 0; j < t.size(); j++) {
                    t1 = t1 + t.get(j);
                }
                String o = "";
                for (int j = 0; j < otp.size(); j++) {
                    if (Pattern.matches(otp.get(j), t1) == true) {
                        o = otp.get(j);
                        break;
                    }
                }
                if (o.isEmpty() == true) {
                    Log.d("Data " + i, data.printData());
                    writeSMS_ExtractedCSV(data.printData());
                    smsExtracted.add(data);
                }
                // Line 334-345 no need to implement these line of codes because we aren't storing data.
            }
        }

        printSMS_ExtractedCSV();

        if(smsExtracted.size()>0) {
            ArrayList<Integer> smsMedical = new ArrayList<>();
            ArrayList<Integer> smsBills = new ArrayList<>();
            ArrayList<Integer> smsSubscription = new ArrayList<>();
            ArrayList<Integer> smsEMI = new ArrayList<>();
            ArrayList<Integer> smsRec = new ArrayList<>();
            ArrayList<Integer> smsNonRec = new ArrayList<>();
            ArrayList<Integer> smsESS = new ArrayList<>();
            ArrayList<Integer> smsNonESS = new ArrayList<>();
            ArrayList<Integer> smsCredit = new ArrayList<>();
            ArrayList<Integer> smsDebit = new ArrayList<>();
            ArrayList<Integer> smsATM = new ArrayList<>();
            ArrayList<Integer> smsNetBanking = new ArrayList<>();
            ArrayList<Integer> smsFood = new ArrayList<>();
            ArrayList<Integer> smsShopping = new ArrayList<>();
            ArrayList<Integer> smsGrocery = new ArrayList<>();
            ArrayList<Integer> smsTravel = new ArrayList<>();
            ArrayList<String> smsDate = new ArrayList<>();
            ArrayList<String> smsTime = new ArrayList<>();
            ArrayList<String> smsBank = new ArrayList<>();
            ArrayList<String> smsMerchant = new ArrayList<>();
            ArrayList<Double> smsTrxAmt = new ArrayList<>();
            ArrayList<Double> smsAvlBal = new ArrayList<>();

            for (int j = 0; j < smsExtracted.size(); j++) {
                smsMedical.add(smsExtracted.get(j).getMEDICAL());
                smsBills.add(smsExtracted.get(j).getBILLS());
                smsSubscription.add(smsExtracted.get(j).getSUBSCRIPTION());
                smsEMI.add(smsExtracted.get(j).getEMI());
                smsRec.add(smsExtracted.get(j).getREC());
                smsNonRec.add(smsExtracted.get(j).getNON_REC());
                smsESS.add(smsExtracted.get(j).getESS());
                smsNonESS.add(smsExtracted.get(j).getNON_ESS());
                smsCredit.add(smsExtracted.get(j).getCREDIT());
                smsDebit.add(smsExtracted.get(j).getDEBIT());
                smsATM.add(smsExtracted.get(j).getATM());
                smsNetBanking.add(smsExtracted.get(j).getNET_BANKING());
                smsFood.add(smsExtracted.get(j).getFOOD());
                smsShopping.add(smsExtracted.get(j).getSHOPPING());
                smsGrocery.add(smsExtracted.get(j).getGROCERY());
                smsTravel.add(smsExtracted.get(j).getTRAVEL());
                smsDate.add(smsExtracted.get(j).getDATE());
                smsTime.add(smsExtracted.get(j).getTIME());
                smsBank.add(smsExtracted.get(j).getBANK());
                smsMerchant.add(smsExtracted.get(j).getMERCHANT());
                smsTrxAmt.add(smsExtracted.get(j).getTRX_AMT());
                smsAvlBal.add(smsExtracted.get(j).getAVL_BAL());
            }
            finaldata.put("Medical", smsMedical);
            finaldata.put("Bills", smsBills);
            finaldata.put("subscription", smsSubscription);
            finaldata.put("EMI", smsEMI);
            finaldata.put("REC", smsRec);
            finaldata.put("NON-REC", smsNonRec);
            finaldata.put("ESS", smsESS);
            finaldata.put("NON-ESS", smsNonESS);
            finaldata.put("Credit", smsCredit);
            finaldata.put("Debit", smsDebit);
            finaldata.put("ATM", smsATM);
            finaldata.put("Net Banking", smsNetBanking);
            finaldata.put("Food", smsFood);
            finaldata.put("Shopping", smsShopping);
            finaldata.put("Grocery", smsGrocery);
            finaldata.put("Travel", smsTravel);
            finaldata.put("Date", smsDate);
            finaldata.put("Time", smsTime);
            finaldata.put("Bank", smsBank);
            finaldata.put("Merchant", smsMerchant);
            finaldata.put("TRX_AMT", smsTrxAmt);
            finaldata.put("AVL_BAL", smsAvlBal);
        }
    }

}
