package com.centaurstech.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.UUID;

import static com.qiwu.ui.util.UIUtils.getContext;

public class ImageUtils {

    public static final int REQUEST_CODE_CROP = 22;

    public static String cropPhoto(Activity context, Uri uri, boolean isAvatar) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        File cropPhotoFile = null;
        try {
            String imgNameCrop = UUID.randomUUID().toString();
            File pictureDirCrop = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/CropPicture");
            if (!pictureDirCrop.exists()) {
                pictureDirCrop.mkdirs();
            }
            cropPhotoFile = File.createTempFile(
                    imgNameCrop,         /* prefix */
                    ".jpg",             /* suffix */
                    pictureDirCrop      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (cropPhotoFile != null) {
            //7.0 安全机制下不允许保存裁剪后的图片
            //所以仅仅将File Uri传入MediaStore.EXTRA_OUTPUT来保存裁剪后的图像
            Uri imgUriCrop = Uri.fromFile(cropPhotoFile);
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", true);
            if (isAvatar) {
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 300);
                intent.putExtra("outputY", 300);
            }
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUriCrop);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            context.startActivityForResult(intent, REQUEST_CODE_CROP);
        }
        if (cropPhotoFile == null) {
            return "";
        }
        return cropPhotoFile.getAbsolutePath();
    }

    /**
     * 质量压缩方法
     *
     * @param
     * @return
     */
    public static String compressReSave(String srcPath, Context context) {
        String filePath = "";
        Bitmap image = BitmapFactory.decodeFile(srcPath);
        if (image == null){
            return "";
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 300) { // 循环判断如果压缩后图片是否大于300kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            if (options > 10){
                options -= 10;// 每次都减少10
            } else {
                options -= 5;
            }

            if (options == 5){
                break;
            }
        }
        FileOutputStream outStream = null;
        filePath = createCameraFile(context);
        try {
            outStream = new FileOutputStream(filePath);
            // 把数据写入文件
            outStream.write(baos.toByteArray());
            // 记得要关闭流！
            outStream.close();
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (outStream != null) {
                    // 记得要关闭流！
                    outStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建拍照照片保存路径
     */
    public static String createCameraFile(Context context) {
        String path = "";
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "cameraPic");
        } else {
            file = new File(context.getFilesDir().getPath() + File.separator + "cameraPic");
        }
        if (file != null) {
            if (!file.exists()) {
                file.mkdir();
            }
            File output = new File(file, System.currentTimeMillis() + ".png");
            try {
                if (output.exists()) {
                    output.delete();
                } else {
                    output.createNewFile();
                }
                path = output.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    /**
     * 生成二维码，默认大小为500*500
     *
     * @param text 需要生成二维码的文字、网址等
     * @return bitmap
     */
    public static Bitmap createQRCode(String text) {
        return createQRCode(text, 170);
    }

    /**
     * 生成二维码
     *
     * @param text 需要生成二维码的文字、网址等
     * @param size 需要生成二维码的大小（）
     * @return bitmap
     */
    public static Bitmap createQRCode(String text, int size) {
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, size, size, hints);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * size + x] = 0xff000000;
                    } else {
                        pixels[y * size + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
