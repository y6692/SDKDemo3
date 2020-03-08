package com.centaurstech.sdk.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.View;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.common.User;
import com.centaurstech.qiwu.entity.TokenEntity;
import com.centaurstech.qiwu.utils.GsonUtils;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.order.OrderListActivity;
import com.centaurstech.sdk.callback.PermissionCallback;
import com.centaurstech.sdk.common.Const2;
import com.centaurstech.sdk.databinding.ActivityUserInfoBinding;
import com.centaurstech.sdk.databinding.LayoutUploadHeaderImageBinding;
import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.sdk.utils.ConversionUtils;
import com.centaurstech.sdk.utils.ImageUtils;
import com.centaurstech.sdk.utils.ToastUtils;
import com.centaurstech.sdk.utils.ViewUtils;
import com.qiwu.ui.BuildConfig;
import com.qiwu.ui.dialog.AroundDialog;

import java.io.File;
import java.util.List;

import static com.centaurstech.sdk.utils.ImageUtils.REQUEST_CODE_CROP;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {

    private ActivityUserInfoBinding mBinding;

    private static final int ALBUM_REQUEST_CODE = 11;

    public static final int CAMERA_REQUEST_CODE = 12;

    private static final int REQUEST_CODE_LOGIN = 12;

    private AroundDialog mAroundDialog;

    private View mPhotoView;

    private LayoutUploadHeaderImageBinding mHeaderBinding;

    //调用照相机返回图片文件
    private File tempFile;

    private String cropPath = "";

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_info);
        initView();
    }


    private void initView() {
        mBinding.iHeadLayout.tvTitle.setText("个人信息");
        mBinding.iUnpaid.ivIcon.setImageResource(R.mipmap.order_icon_unpaid);
        mBinding.iUnpaid.tvText.setText(R.string.unpaid);
        mBinding.iProcessing.ivIcon.setImageResource(R.mipmap.order_icon_processing);
        mBinding.iProcessing.tvText.setText(R.string.processing);
        mBinding.iNoTravel.ivIcon.setImageResource(R.mipmap.order_icon_unused);
        mBinding.iNoTravel.tvText.setText(R.string.no_use);
        mBinding.iTravel.ivIcon.setImageResource(R.mipmap.order_icon_used);
        mBinding.iTravel.tvText.setText(R.string.is_use);
        mBinding.iCancelAndRefund.ivIcon.setImageResource(R.mipmap.order_icon_cancel);
        mBinding.iCancelAndRefund.tvText.setText(R.string.cancel_refund);


        mBinding.llMeOrder.setOnClickListener(this);
        mBinding.flUnpaid.setOnClickListener(this);
        mBinding.flProcessing.setOnClickListener(this);
        mBinding.flNoTravel.setOnClickListener(this);
        mBinding.flTravel.setOnClickListener(this);
        mBinding.flCancelAndRefund.setOnClickListener(this);
        mBinding.iHeadLayout.btnNavBack.setOnClickListener(this);
        mBinding.tvNickname.setOnClickListener(this);
        mBinding.svHeaderImage.setOnClickListener(this);
        mBinding.rlDriveInfo.setOnClickListener(this);
        mBinding.tvLogout.setOnClickListener(this);
        loginChange();
    }

    private void loginChange() {
        if (User.isLogin()) {
            mBinding.tvNickname.setText(User.getNickname());
            mBinding.tvPhoneNumber.setText(User.getMobilePhone());
            mBinding.tvPhoneNumber.setVisibility(View.VISIBLE);
            ViewUtils.loadHeadImage(mBinding.svHeaderImage, User.getPhotoName());
            mBinding.tvLogout.setVisibility(View.VISIBLE);
        } else {
            ViewUtils.loadHeadImage(mBinding.svHeaderImage, R.mipmap.iv_default_avatar);
            mBinding.tvNickname.setText("未登录");
            mBinding.tvPhoneNumber.setVisibility(View.GONE);
            mBinding.tvLogout.setVisibility(View.GONE);
        }
    }

    /***
     * 弹出选择发票
     */
    private void showSelectedPhoto() {
        if (mAroundDialog != null) return;
        mPhotoView = View.inflate(this, R.layout.layout_upload_header_image, null);
        mHeaderBinding = DataBindingUtil.bind(mPhotoView);
        mAroundDialog = AroundDialog.create(this, R.style.DialogInOutBottom);
        mAroundDialog.setContent(mHeaderBinding.getRoot())
                .setHideAllButton()
                .setGravity(Gravity.BOTTOM)
                .setCanceledTouchOutside(true)
                .setOnViewClickListener(new AroundDialog.OnViewClickListener() {
                    @Override
                    public void onClick(View view, boolean isConfirm) {

                    }
                });

        mHeaderBinding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyPermission(Manifest.permission.CAMERA, new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        getPicFromCamera();
                    }

                    @Override
                    public void onDenied(List<String> permissions) {

                    }
                });
//                launchActivity(TestAsrActivity.class);
            }
        });

        mHeaderBinding.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionCallback() {
                            @Override
                            public void onGranted() {
                                getPicFromAlbm();
                            }

                            @Override
                            public void onDenied(List<String> permissions) {
                                ToastUtils.show(R.string.photo_album_access_denied);
                            }
                        });
            }
        });

        mHeaderBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAroundDialog.dismiss();
            }
        });

        mAroundDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mAroundDialog = null;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1000:
                mBinding.tvNickname.setText(User.getNickname());
                mBinding.tvPhoneNumber.setText(User.getMobilePhone());
                break;
            case ALBUM_REQUEST_CODE:
                if (data != null) {
                    Uri uri = data.getData();
                    cropPath = ImageUtils.cropPhoto(this, uri, true);//开始对图片进行裁剪处理
                }
                break;
            case CAMERA_REQUEST_CODE:
                //用相机返回的照片去调用剪裁也需要对Uri进行处理
                if (null == BitmapFactory.decodeFile(tempFile.getAbsolutePath())) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", tempFile);
                    cropPath = ImageUtils.cropPhoto(this, contentUri, true);//开始对图片进行裁剪处理
                } else {
                    cropPath = ImageUtils.cropPhoto(this, Uri.fromFile(tempFile), true);//开始对图片进行裁剪处理
                }

                break;
            case REQUEST_CODE_CROP:
                if (null == BitmapFactory.decodeFile(cropPath)) {
                    return;
                }
                String url = ImageUtils.compressReSave(cropPath, this);
                ViewUtils.loadHeadImage(mBinding.svHeaderImage, url);
                break;
        }
    }

    /**
     * 权限申请结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:   //相册选择照片权限申请返回
                getPicFromAlbm();
                break;
        }
    }

    /**
     * 从相册获取图片
     */
    private void getPicFromAlbm() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, ALBUM_REQUEST_CODE);
    }

    /**
     * 从相机获取图片
     */
    private void getPicFromCamera() {
        //用于保存调用相机拍照后所生成的文件
        tempFile = new File(Environment.getExternalStorageDirectory().getPath(), System.currentTimeMillis() + ".png");
        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {   //如果在Android7.0以上,使用FileProvider获取Uri
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".fileProvider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            LogUtils.sf("getPicFromCamera" + contentUri.toString());
        } else {    //否则使用Uri.fromFile(file)方法获取Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onEvent(EventBusEntity event) {
        super.onEvent(event);
        switch (event.getMessage()) {
            case Const2.login_success:
                loginChange();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNavBack:
                finish();
                break;
            case R.id.tvNickname:
                if (!User.isLogin()) {
                    launchActivity(LoginActivity.class);
                    return;
                }
                break;
            case R.id.svHeaderImage:
                if (!User.isLogin()) {
                    launchActivity(LoginActivity.class);
                    return;
                }
                applyPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , new PermissionCallback() {
                            @Override
                            public void onGranted() {
                                showSelectedPhoto();
                            }

                            @Override
                            public void onDenied(List<String> permissions) {

                            }
                        });
                break;
            case R.id.tvLogout:
                QiWuAPI.account.logout(new APICallback<TokenEntity>() {
                    @Override
                    public void onSuccess(TokenEntity response) {
                        User.logout(true);
                        finish();
                        LogUtils.sf(GsonUtils.toJson(response));
                    }
                });
                break;
            case R.id.rlDriveInfo:
                launchActivity(DeviceInfoActivity.class);
                break;
            case R.id.llMeOrder:
                toOrderActivity(0);
                break;
            case R.id.flUnpaid:
                toOrderActivity(1);
                break;
            case R.id.flProcessing:
                toOrderActivity(2);
                break;
            case R.id.flNoTravel:
                toOrderActivity(3);
                break;
            case R.id.flTravel:
                toOrderActivity(4);
                break;
            case R.id.flCancelAndRefund:
                toOrderActivity(5);
                break;
        }
    }

    private void toOrderActivity(final int position) {
        if (ConversionUtils.checkLogin(this)) {
            launchActivity(OrderListActivity.class, new IntentExpand() {
                @Override
                public void extraValue(Intent intent) {
                    intent.putExtra("position", position);
                }
            });
        }
    }

}
