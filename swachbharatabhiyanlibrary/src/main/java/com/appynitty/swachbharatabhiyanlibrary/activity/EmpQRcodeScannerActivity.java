package com.appynitty.swachbharatabhiyanlibrary.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.appynitty.retrofitconnectionlibrary.pojos.ResultPojo;
import com.appynitty.swachbharatabhiyanlibrary.R;
import com.appynitty.swachbharatabhiyanlibrary.adapters.connection.EmpQrLocationAdapterClass;
import com.appynitty.swachbharatabhiyanlibrary.dialogs.ChooseActionPopUp;
import com.appynitty.swachbharatabhiyanlibrary.pojos.QrLocationPojo;
import com.appynitty.swachbharatabhiyanlibrary.repository.EmpSyncServerRepository;
import com.appynitty.swachbharatabhiyanlibrary.utils.AUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pixplicity.easyprefs.library.Prefs;
import com.riaylibrary.custom_component.MyProgressDialog;
import com.riaylibrary.utils.LocaleHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import io.github.kobakei.materialfabspeeddial.FabSpeedDial;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class EmpQRcodeScannerActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {

    private final static String TAG = "EmpQRcodeScannerActivity";

    private Context mContext;
    private Toolbar toolbar;
    private ZBarScannerView scannerView;
    private FabSpeedDial fabSpeedDial;
    private TextInputLayout idIpLayout;
    private AutoCompleteTextView idAutoComplete;
    private RadioGroup collectionRadioGroup;
    private String radioSelection, mHouse_id, mImagePath, encodedImage;
    private Button submitBtn, permissionBtn;
    private View contentView;
    private Boolean isScanQr;
    private ChooseActionPopUp chooseActionPopUp;
    private EmpQrLocationAdapterClass empQrLocationAdapter;
    private QrLocationPojo qrLocationPojo;
    private static final int REQUEST_CAMERA = 22;
    private EmpSyncServerRepository empSyncServerRepository;
    private Gson gson;

    private MyProgressDialog myProgressDialog;
    private ArrayList<Integer> mSelectedIndices;

    @Override
    protected void attachBaseContext(Context newBase) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            super.attachBaseContext(LocaleHelper.onAttach(newBase));
        } else {
            super.attachBaseContext(newBase);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponents();

    }

//set format
//public void setupFormats() {
//    List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
//    if(mSelectedIndices == null || mSelectedIndices.isEmpty()) {
//        mSelectedIndices = new ArrayList<Integer>();
//        for(int i = 0; i < BarcodeFormat.ALL_FORMATS.size(); i++) {
//            mSelectedIndices.add(i);
//        }
//    }
//
//    for(int index : mSelectedIndices) {
//        formats.add(BarcodeFormat.ALL_FORMATS.get(index));
//    }
//    if(scannerView != null) {
//       scannerView.setFormats(formats);
//    }
//}


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AUtils.MY_PERMISSIONS_REQUEST_CAMERA) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                checkCameraPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.CAMERA)) {

                AUtils.showPermissionDialog(mContext, "CAMERA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, AUtils.MY_PERMISSIONS_REQUEST_CAMERA);
                        }
                    }
                });
            }
        } else if (requestCode == AUtils.MY_PERMISSIONS_REQUEST_LOCATION) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                checkLocationPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {

                AUtils.showPermissionDialog(mContext, "Location Service", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AUtils.MY_PERMISSIONS_REQUEST_LOCATION);
                        }
                    }
                });
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPreview();
    }

    @Override
    protected void onPause() {
        stopPreview();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!isScanQr) {
            scanQR();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (AUtils.isInternetAvailable()) {
            AUtils.hideSnackBar();
        } else {
            AUtils.showSnackBar(findViewById(R.id.parent));
        }
    }

    private void initComponents() {
        generateId();
        registerEvents();
        initData();
//        setupFormats();
    }

    protected void generateId() {
        setContentView(R.layout.emp_activity_qrcode_scanner);
        toolbar = findViewById(R.id.toolbar);
        mHouse_id = "";
        mContext = EmpQRcodeScannerActivity.this;
        AUtils.currentContextConstant = mContext;
        myProgressDialog = new MyProgressDialog(mContext, R.drawable.progress_bar, false);

        empSyncServerRepository = new EmpSyncServerRepository(mContext);
        gson = new Gson();

        fabSpeedDial = findViewById(R.id.flash_toggle);
        idAutoComplete = findViewById(R.id.txt_id_auto);
        idAutoComplete.setThreshold(0);
        idAutoComplete.setDropDownBackgroundResource(R.color.white);
        idAutoComplete.setSingleLine();

        idIpLayout = findViewById(R.id.txt_id_layout);
        collectionRadioGroup = findViewById(R.id.collection_radio_group);
        radioSelection = AUtils.RADIO_SELECTED_HP;

        submitBtn = findViewById(R.id.submit_button);
        permissionBtn = findViewById(R.id.grant_permission);
        contentView = findViewById(R.id.scanner_view);

        isScanQr = true;

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.qr_scanner);
        scannerView = new ZBarScannerView(mContext);
        scannerView.setAutoFocus(true);
        scannerView.setLaserColor(getResources().getColor(R.color.colorPrimary));
        scannerView.setBorderColor(getResources().getColor(R.color.colorPrimary));
        contentFrame.addView(scannerView);


        initToolbar();

        chooseActionPopUp = new ChooseActionPopUp(mContext);
        empQrLocationAdapter = new EmpQrLocationAdapterClass();
        qrLocationPojo = new QrLocationPojo();
    }

    protected void initToolbar() {
        toolbar.setTitle(getResources().getString(R.string.title_activity_qrcode_scanner));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    protected void registerEvents() {

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (!idAutoComplete.getText().toString().matches("")) {
                        submitQRcode(idAutoComplete.getText().toString());
                    } else {
                        if (getGCType().equals(AUtils.HP_AREA_TYPE_ID))
                            AUtils.error(mContext, mContext.getResources().getString(R.string.hp_area_validation_emp));
                        else if (getGCType().equals(AUtils.GP_AREA_TYPE_ID))
                            AUtils.error(mContext, mContext.getResources().getString(R.string.gp_area_validation_emp));
                        else
                            AUtils.error(mContext, mContext.getResources().getString(R.string.dy_area_validation_emp));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        collectionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int radioGroupId = radioGroup.getCheckedRadioButtonId();

                idAutoComplete.setText("");
                AUtils.showKeyboard((Activity) mContext);

                idAutoComplete.setText("");

                if (radioGroupId == R.id.house_collection_radio) {
                    idIpLayout.setHint(getResources().getString(R.string.house_number_hint));
                    radioSelection = AUtils.RADIO_SELECTED_HP;
                    idAutoComplete.setText("HPSBA");
                    idAutoComplete.setSelection(5);
                }

                /*if (radioGroupId == R.id.point_collection_radio) {
                    idIpLayout.setHint(getResources().getString(R.string.gp_id_hint));
                    radioSelection = AUtils.RADIO_SELECTED_GP;
                    idAutoComplete.setText("GPSBA");
                    idAutoComplete.setSelection(5);
                }*/

                if (radioGroupId == R.id.dump_yard_radio) {
                    idIpLayout.setHint(getResources().getString(R.string.dy_id_hint));
                    radioSelection = AUtils.RADIO_SELECTED_DY;
                    idAutoComplete.setText("DYSBA");
                    idAutoComplete.setSelection(5);
                }
            }
        });

        idAutoComplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (isFocused)
                    AUtils.showKeyboard((Activity) mContext);

                if (isFocused && isScanQr) {
                    hideQR();
                    idAutoComplete.setText("HPSBA");
                    idAutoComplete.setSelection(4);
                    AUtils.showKeyboard((Activity) mContext);
                } else {
                    idAutoComplete.clearListSelection();
                }
            }
        });

        idAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AUtils.hideKeyboard((Activity) mContext);
            }
        });

        permissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermission();
            }
        });

        fabSpeedDial.getMainFab().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scannerView.getFlash()) {
                    scannerView.setFlash(false);
                    fabSpeedDial.getMainFab().setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on_indicator));
                } else {
                    scannerView.setFlash(true);
                    fabSpeedDial.getMainFab().setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off));
                }
            }
        });

        chooseActionPopUp.setChooseActionPopUpDialogListener(new ChooseActionPopUp.ChooseActionPopUpDialogListener() {
            @Override
            public void onChooseActionPopUpDismissed(String data, int actionType) {
                String mId = data;
                switch (actionType) {
                    case ChooseActionPopUp.ADD_DETAILS_BUTTON_CLICKED:
                        if (AUtils.isInternetAvailable() && AUtils.isConnectedFast(mContext)) {
                            submitOnDetails(mId, getGCType(mId));
                        } else {
                            if (!AUtils.isConnectedFast(mContext)) {
                                AUtils.warning(mContext, getResources().getString(R.string.feature_unavailable_error), Toast.LENGTH_LONG);
                            } else {
                                AUtils.info(mContext, getResources().getString(R.string.no_internet_error), Toast.LENGTH_LONG);
                            }

                            restartPreview();
                        }
                        break;
                    case ChooseActionPopUp.SKIP_BUTTON_CLICKED:
                        submitOnSkip(mHouse_id);
                        Log.e(TAG, "onChooseActionPopUpDismissed: " + mId);
                        break;
                }
            }
        });

        empQrLocationAdapter.setEmpQrLocationListner(new EmpQrLocationAdapterClass.EmpQrLocationListner() {
            @Override
            public void onSuccessCallback(ResultPojo resultPojo) {
                myProgressDialog.dismiss();
                String message = "";

                if (Prefs.getString(AUtils.LANGUAGE_NAME, AUtils.DEFAULT_LANGUAGE_ID).equals("2")) {
                    message = resultPojo.getMessageMar();
                } else {
                    message = resultPojo.getMessage();
                }

                if (/*resultPojo.getStatus().equals(AUtils.STATUS_SUCCESS) ||*/ AUtils.isNullString(resultPojo.getStatus())) {
//                    AUtils.success(mContext, message, Toast.LENGTH_LONG);
                    AUtils.error(mContext, "No response!", Toast.LENGTH_LONG);
                    finish();
                } else {
//                    AUtils.error(mContext, message, Toast.LENGTH_LONG);
                    AUtils.success(mContext, message, Toast.LENGTH_LONG);
                    finish();
                }
            }

            @Override
            public void onFailureCallback() {
                myProgressDialog.dismiss();
                AUtils.error(mContext, getResources().getString(R.string.something_error), Toast.LENGTH_LONG);
            }

            @Override
            public void onErrorCallback() {
                myProgressDialog.dismiss();
                AUtils.error(mContext, getResources().getString(R.string.serverError), Toast.LENGTH_LONG);
            }
        });

    }

    protected void initData() {

        checkCameraPermission();

    }

    private void submitQRcode(String houseid) {
        Log.e(TAG, "submitQRcode: " + houseid);
        mHouse_id = houseid;
        if (validSubmitId(houseid.toLowerCase())) {
            takePhotoImageViewOnClick();
//            showActionPopUp(houseid);

        } else {
            AUtils.warning(EmpQRcodeScannerActivity.this, mContext.getResources().getString(R.string.qr_error));
            restartPreview();
        }
    }

    private Boolean validSubmitId(String id) {

        Log.e(TAG, "validSubmitId: " + id);
        return id.substring(0, 2).matches("^[HhPp]+$")
                || id.matches("gpsba[0-9]+$")
                || id.matches("lwsba[0-9]+$")
                || id.matches("sssba[0-9]+$")
                || id.matches("dysba[0-9]+$");
    }

    private void checkCameraPermission() {
        if (AUtils.isCameraPermissionGiven(mContext)) {
            startPreview();
            contentView.setVisibility(View.VISIBLE);
            permissionBtn.setVisibility(View.GONE);
            checkLocationPermission();
        } else {
            contentView.setVisibility(View.GONE);
            permissionBtn.setVisibility(View.VISIBLE);
        }
    }

    private void scanQR() {
        isScanQr = true;
        startCamera();
        contentView.setVisibility(View.VISIBLE);
        submitBtn.setVisibility(View.GONE);
        collectionRadioGroup.setVisibility(View.GONE);
        idAutoComplete.clearFocus();
        idAutoComplete.setText("");
        idIpLayout.setHint(getResources().getString(R.string.hp_gp_id_hint));

    }

    private void hideQR() {
        isScanQr = false;
        stopCamera();
        contentView.setVisibility(View.GONE);
        submitBtn.setVisibility(View.VISIBLE);
        collectionRadioGroup.setVisibility(View.VISIBLE);

        if (radioSelection.equals(AUtils.RADIO_SELECTED_HP)) {
            idIpLayout.setHint(getResources().getString(R.string.house_number_hint));
        } else {
            idIpLayout.setHint(getResources().getString(R.string.gp_id_hint));
        }
    }

    @SuppressLint("MissingPermission")
    private void checkLocationPermission() {

        if (AUtils.isLocationPermissionGiven(mContext)) {
            //You already have the permission, just go ahead.
            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!GpsStatus) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            } //else {
            //AUtils.saveLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            //}
        }
    }

    private void takePhotoImageViewOnClick() {
//        hideQR();
        setContentView(R.layout.layout_blank);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    public void handleResult(Result result) {
//        scannerView.stopCameraPreview();
        Log.e(TAG, "handleResult: " + result.getContents());
        mHouse_id = result.getContents();
        takePhotoImageViewOnClick();
//        showActionPopUp(result.getContents());
//        restartPreview();
    }

    private void startPreview() {
        scannerView.startCamera();
        scannerView.resumeCameraPreview(this);
    }

    private void stopPreview() {
        scannerView.stopCameraPreview();
        scannerView.stopCamera();
    }

    private void startCamera() {
        scannerView.startCamera();

    }

    private void stopCamera() {
        scannerView.stopCamera();
    }

    private void restartPreview() {
        stopPreview();
        startPreview();
    }

    private void showActionPopUp(String id) {

        if (validSubmitId(id.toLowerCase())) {
            chooseActionPopUp.setData(id, mImagePath);
            chooseActionPopUp.setCanceledOnTouchOutside(false);
            chooseActionPopUp.show();


        } else
            AUtils.error(mContext, getResources().getString(R.string.invalid_qr_error));
    }

    private void submitOnSkip(String id) {
        try {
            qrLocationPojo.setReferanceId(id);
            qrLocationPojo.setLat(Prefs.getString(AUtils.LAT, ""));
            qrLocationPojo.setLong(Prefs.getString(AUtils.LONG, ""));

            qrLocationPojo.setName("");
            qrLocationPojo.setNameMar("");
            qrLocationPojo.setAddress("");
            qrLocationPojo.setZoneId("");
            qrLocationPojo.setWardId("");
            qrLocationPojo.setAreaId("");
            qrLocationPojo.setHouseNumber("");
            qrLocationPojo.setMobileno("");

            qrLocationPojo.setGcType(getGCType(id));
            qrLocationPojo.setDate(AUtils.getServerDateTime());
            qrLocationPojo.setQRCodeImage("data:image/jpeg;base64," + encodedImage);
            startSubmitQRAsyncTask(qrLocationPojo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void submitOnDetails(String refId, String gctype) {
        Intent intent = new Intent(mContext, EmpAddLocationDetailsActivity.class);
        intent.putExtra(AUtils.ADD_DETAILS_TYPE_KEY, AUtils.NondaniLocation.OPEN_FORM_TYPE);
        intent.putExtra(AUtils.NondaniLocation.REFERENCE_ID, refId);
        intent.putExtra(AUtils.NondaniLocation.SUBMIT_TYPE, gctype);
        startActivityForResult(intent, AUtils.ADD_DETAILS_REQUEST_KEY);
    }

    private void startSubmitQRAsyncTask(QrLocationPojo pojo) {
        myProgressDialog.show();
        pojo.setUserId(Prefs.getString(AUtils.PREFS.USER_ID, ""));
        if (AUtils.isInternetAvailable() && AUtils.isConnectedFast(mContext)) {
            empQrLocationAdapter.saveQrLocation(pojo);
            stopCamera();
        } else {
            insertToDB(pojo);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        AUtils.currentContextConstant = mContext;

        if (requestCode == AUtils.ADD_DETAILS_REQUEST_KEY && resultCode == RESULT_OK) {
            finish();
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        }


        super.onActivityResult(requestCode, resultCode, data);

    }

    private String getGCType() {
        String areaType = AUtils.GP_AREA_TYPE_ID;
        if (radioSelection.equals(AUtils.RADIO_SELECTED_HP)) {
            areaType = AUtils.HP_AREA_TYPE_ID;
        } else if (radioSelection.equals(AUtils.RADIO_SELECTED_DY)) {
            areaType = AUtils.DY_AREA_TYPE_ID;
        }
        return areaType;
    }

    private String getGCType(String refId) {
        if (refId.substring(0, 2).toLowerCase().matches("^[hp]+$"))
            return "1";
        else if (refId.substring(0, 2).toLowerCase().matches("^[gp]+$"))
            return "2";
        else if (refId.substring(0, 2).toLowerCase().matches("^[dy]+$"))
            return "3";
        else if (refId.substring(0, 2).toLowerCase().matches("^[lw]+$"))
            return "4";
        else if (refId.substring(0, 2).toLowerCase().matches("^[ss]+$"))
            return "5";
        else
            return "0";
    }

    private void insertToDB(QrLocationPojo pojo) {

        Type type = new TypeToken<QrLocationPojo>() {
        }.getType();
        empSyncServerRepository.insertEmpSyncServerEntity(gson.toJson(pojo, type));

        myProgressDialog.dismiss();
        AUtils.success(mContext, getString(R.string.success_message), Toast.LENGTH_LONG);
        finish();
    }

    private void onCaptureImageResult(Intent data) {

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        File destination;

        OutputStream fos;
        try {

            final File dir;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {  //added by swapnil for android version 10 and above

                ContentResolver resolver = mContext.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "Gram Panchayat");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(imageUri);

                Log.e(TAG, "onCaptureImageResult:- width:- " + thumbnail.getWidth() + ", height:- " + thumbnail.getHeight());
//                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, fos);
                destination = new File(String.valueOf(contentValues), System.currentTimeMillis() + ".jpg");

            } else {

                dir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM), "Gram Panchayat");

                if (!dir.exists()) {
                    dir.mkdirs();
                }
                destination = new File(dir, System.currentTimeMillis() + ".jpg");
                fos = new FileOutputStream(destination);
                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }

            fos.flush();
            fos.close();

        } catch (Exception e) {

            e.printStackTrace();

            AUtils.error(mContext, "Unable to add image", Toast.LENGTH_SHORT);

        }

        Uri tempUri;
        String finalPath;
        tempUri = getImageUri(getApplicationContext(), thumbnail);
        finalPath = getRealPathFromURI(tempUri);
        Log.e(TAG, "onCaptureImageResult: imagePath:- " + finalPath);
        mImagePath = finalPath;
        showActionPopUp(mHouse_id);

        Bitmap bm = BitmapFactory.decodeFile(finalPath);
        Bitmap newBitmap = AUtils.writeOnImage(AUtils.getDateAndTime(), mHouse_id, mImagePath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();

        encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        Log.d(TAG, "onCaptureImageResult: Base64:- " + encodedImage);

    }

    /**
     * Added by swapnil 22-12-21
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    /**
     * End of addition
     */
}