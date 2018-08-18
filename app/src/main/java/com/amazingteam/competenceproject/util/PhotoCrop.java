package com.amazingteam.competenceproject.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.amazingteam.competenceproject.R;

public class PhotoCrop {
    private String photoPath;
    private int templateID;

    public PhotoCrop(String photoPath, String templateType) {
        this.photoPath = photoPath;
        switch (templateType) {
            case "tshirt": {
                this.templateID = R.drawable.clothtemp_tshirt_1;
                break;
            }
            case "trousers": {
                this.templateID = R.drawable.clothtemp_trousers_1;
                break;
            }
            case "winterhat": {
                this.templateID = R.drawable.clothtemp_winterhat_1;
                break;
            }
            case "winterboots": {
                this.templateID = R.drawable.clothtemp_winterboots_1;
                break;
            }
            case "shirt": {
                this.templateID = R.drawable.clothtemp_shirt_1;
                break;
            }
            case "shorttrousers": {
                this.templateID = R.drawable.clothtemp_shorttrousers_1;
                break;
            }
            default: {
                this.templateID = R.drawable.clothtemp_tshirt_1;
                break;
            }
        }
    }

    public Bitmap getCroppedImage(Resources resources, String clothType) {
        Bitmap templateBitmap = BitmapFactory.decodeResource(resources, templateID);
        Bitmap largePhoto = BitmapLoader.load(photoPath);
        Bitmap photoBitmap = Bitmap.createScaledBitmap(
                largePhoto,
                templateBitmap.getWidth() / 2,
                templateBitmap.getHeight() / 2,
                false);
        Bitmap result = Bitmap.createBitmap(
                templateBitmap.getWidth() / 2,
                templateBitmap.getHeight() / 2,
                Bitmap.Config.ARGB_8888);
        Bitmap templateSmall = Bitmap.createScaledBitmap(
                templateBitmap,
                result.getWidth(),
                result.getHeight(),
                false);
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCanvas.drawBitmap(photoBitmap, 0, 0, paint);
        PorterDuff.Mode mode = PorterDuff.Mode.DST_OUT;
        paint.setXfermode(new PorterDuffXfermode(mode));
        mCanvas.drawBitmap(templateSmall, 0, 0, paint);
        int resultWidth = result.getWidth();
        Bitmap resizedBitmap = result;
        if (clothType.equals("tshirt")) {
            resizedBitmap = Bitmap.createBitmap(
                    result, 0, 80, resultWidth, 640);
        }
        if (clothType.equals("trousers")) {
            resizedBitmap = Bitmap.createBitmap(
                    result, 0, 10, resultWidth, 1030);
        }
        if (clothType.equals("winterhat")) {
            resizedBitmap = Bitmap.createBitmap(
                    result, 20, 290, resultWidth - 20, 420);
            resizedBitmap = Bitmap.createScaledBitmap(
                    resizedBitmap, resizedBitmap.getWidth() / 2, resizedBitmap.getHeight() / 2, false);
        }
        if (clothType.equals("winterboots")) {
            resizedBitmap = Bitmap.createBitmap(
                    result, 110, 320, 550, 380);
        }
        if (templateBitmap != null) {
            templateBitmap.recycle();
            templateBitmap = null;
        }
        if (largePhoto != null) {
            largePhoto.recycle();
            largePhoto = null;
        }
        if (photoBitmap != null) {
            photoBitmap.recycle();
            photoBitmap = null;
        }
        return resizedBitmap;
    }
}
