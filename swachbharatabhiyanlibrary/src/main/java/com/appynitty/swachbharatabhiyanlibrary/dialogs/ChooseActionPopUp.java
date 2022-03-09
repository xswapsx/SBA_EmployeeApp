package com.appynitty.swachbharatabhiyanlibrary.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.appynitty.swachbharatabhiyanlibrary.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ChooseActionPopUp extends Dialog {

    private static final String TAG = "ChooseActionPopUp";
    private ChooseActionPopUpDialogListener chooseActionPopUpDialogListener;

    private Button addDetailsBtn, skipBtn;
    ImageView ivQR_image;
    private Object data;
    private String mId, mPath;
    public static final int SKIP_BUTTON_CLICKED = 0;
    public static final int ADD_DETAILS_BUTTON_CLICKED = 1;

    public ChooseActionPopUp(Context context) {
        super(context);
    }

    public void setChooseActionPopUpDialogListener(ChooseActionPopUpDialogListener chooseActionPopUpDialogListener) {
        this.chooseActionPopUpDialogListener = chooseActionPopUpDialogListener;
    }

    public void setData(String mId, String mPath) {
        this.mId = mId;
        this.mPath = mPath;
        Log.e(TAG, "setData: id:- " + this.mId + ", Path:- " + this.mPath);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_choose_option);

        initComponents();
    }

    private void initComponents() {

        generateID();
        registerEvents();
        initData();
    }

    private void generateID() {
        skipBtn = findViewById(R.id.btn_skip);
        addDetailsBtn = findViewById(R.id.btn_add_details);
        ivQR_image = findViewById(R.id.iv_QR_image);
    }

    private void initData() {
        Bitmap myBitmap = BitmapFactory.decodeFile(mPath);
        ivQR_image.setImageBitmap(myBitmap);

        Bitmap bmp = addTextToImage(myBitmap, "Swapnil Lanjewar", 200, 200, Color.GREEN, 80, 24, false);
        File f = new File(mPath);
        try {
            FileOutputStream fos = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    private void registerEvents() {

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dismissPopup();
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseActionPopUpDialogListener.onChooseActionPopUpDismissed(mId, SKIP_BUTTON_CLICKED);
                dismissPopup();
            }
        });

        addDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseActionPopUpDialogListener.onChooseActionPopUpDismissed(mId, ADD_DETAILS_BUTTON_CLICKED);
                dismissPopup();
            }
        });

    }

    public Bitmap addTextToImage(Bitmap src, String textToAddOnImage, int x, int y, int color, int alpha, int size, boolean underline) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAlpha(alpha);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setUnderlineText(underline);
        canvas.drawText(textToAddOnImage, x, y, paint);

        return result;
    }

    private void dismissPopup() {
        this.dismiss();
    }

    public interface ChooseActionPopUpDialogListener {
        void onChooseActionPopUpDismissed(String mId, int actionType);
    }

}
