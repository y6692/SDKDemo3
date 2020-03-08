package com.centaurstech.sdk.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.entity.PassengerEntity;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.qiwu.utils.VerifyUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.common.Const2;
import com.centaurstech.sdk.databinding.ActivityAddPassengerBinding;
import com.centaurstech.sdk.databinding.LayoutBirthBinding;
import com.centaurstech.sdk.databinding.LayoutRadioItemBinding;
import com.centaurstech.sdk.databinding.LayoutTravelerOperateBinding;
import com.centaurstech.sdk.utils.ConversionUtils;
import com.centaurstech.sdk.utils.ToastUtils;
import com.centaurstech.sdk.view.PopDialog;
import com.qiwu.ui.view.adapter.DataBindRecyclerViewAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * Time:2019/12/27
 * Author: 樊德鹏
 * Description:
 */
public class AddPassengerActivity extends BaseActivity {

    private ActivityAddPassengerBinding mBinding;

    private PassengerEntity mPassengerEntity = new PassengerEntity();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_passenger);
        initView();
        initListener();
    }

    private void initView() {

        mBinding.iHeadLayout.tvTitle.setText(R.string.add_pedestrian);

        mPassengerEntity.setiDType(1);
        mBinding.iName.etText.setLines(1);
        mBinding.iName.tvTitle.setText(R.string.name);
        mBinding.iName.etText.setHint(R.string.name_consistent);
        mBinding.iSpellSurname.tvTitle.setText(R.string.spell_surname);
        mBinding.iSpellSurname.etText.setHint(R.string.spell_surname_hint);
        mBinding.iSpellName.tvTitle.setText(R.string.spell_name);
        mBinding.iSpellName.etText.setHint(R.string.spell_name_hint);
        mBinding.iSpellName.etText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        mBinding.iSpellSurname.etText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        mBinding.iGender.tvTitle.setText(R.string.gender);
        mBinding.iCertificateType.tvTitle.setText(R.string.certificate_type);
        mBinding.iCertificateNumber.tvTitle.setText(R.string.certificate_number);
        mBinding.iCertificateNumber.etText.setHint(R.string.your_certificate_number);
        mBinding.iBirthday.tvTitle.setText(R.string.date_of_birth);
        mBinding.iBirthday.tvText.setText(R.string.date_of_birth_consistent);
        mBinding.iPhoneNumber.tvTitle.setText(R.string.phone_number);
        mBinding.iPhoneNumber.etText.setHint(R.string.use_receive_order_info);

        if (mPassengerEntity.getiDType() > 0) {
            mBinding.iCertificateType.tvText.setText(getCertificates().get(mPassengerEntity.getiDType() - 1));
        }

    }

    private void initListener() {
        mBinding.iGender.flContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGenderDialog();
            }
        });

        mBinding.iCertificateType.flContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCertificateTypeDialog();
            }
        });

        mBinding.iBirthday.flContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBirthDialog();
            }
        });

        mBinding.iCertificateNumber.etText.addTextChangedListener(new TextWatcher() {
            public String mCertificate = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mCertificate = mBinding.iCertificateNumber.etText.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                verifyCertificate(s.toString().toLowerCase(), mCertificate);
            }
        });

        mBinding.tvSaveTraveler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mBinding.iName.etText.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.show(R.string.please_input_name);
                    return;
                }
                if (name.length() < 2 || name.length() > 6) {
                    ToastUtils.show(R.string.contact_name_format_error);
                    return;
                }

                String gender = mBinding.iGender.tvText.getText().toString().trim();
                if (TextUtils.isEmpty(gender)) {
                    ToastUtils.show(R.string.please_choose_gender);
                    return;
                }

                String certificate = mBinding.iCertificateNumber.etText.getText().toString().trim();
                if (TextUtils.isEmpty(certificate)) {
                    ToastUtils.show(R.string.please_input_certificate_number);
                    return;
                }

                if (TextUtils.isEmpty(mPassengerEntity.getBirthday())) {
                    ToastUtils.show(R.string.please_choose_birth);
                    return;
                }

                String phoneNumber = mBinding.iPhoneNumber.etText.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)) {
                    ToastUtils.show(R.string.please_input_phone_number);
                    return;
                }
                if (!VerifyUtils.phoneNumber(phoneNumber)) {
                    ToastUtils.show(R.string.please_input_right_phone);
                    return;
                }

                mPassengerEntity.setContactPhone(phoneNumber);
                mPassengerEntity.setCname(name);
                mPassengerEntity.setiDNumber(certificate);
                //TODO Demo中添加出行人默认是成人，如需修改
                mPassengerEntity.setAgeType(0);
                resultActivity(new IntentExpand() {
                    @Override
                    public void extraValue(Intent intent) {
                        intent.putExtra(Const.Intent.DATA, mPassengerEntity);
                    }
                });
            }
        });

        mBinding.iHeadLayout.btnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void showGenderDialog() {
        final PopDialog popDialog = PopDialog.create(this);
        int position = mPassengerEntity.getSex() == -1 ? -1 : mPassengerEntity.getSex();
        popDialog.setContent(createView(getGender(), position, new ItemClickListener() {
            @Override
            public void onClick(String itemData, int position) {
                mPassengerEntity.setSex(position);
                mBinding.iGender.tvText.setText(itemData);
                popDialog.dismiss();
            }
        }))
                .setHideAllButton()
                .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                    @Override
                    public void onClick(View view, boolean isConfirm) {

                    }
                });
    }

    private View createView(List<String> strings, final int position, final ItemClickListener itemClickListener) {
        View view = View.inflate(this, R.layout.layout_traveler_operate, null);
        LayoutTravelerOperateBinding operateBinding = DataBindingUtil.bind(view);
        operateBinding.tvTitle.setText(strings.size() == 2 ? R.string.gender : R.string.certificate_type);
        operateBinding.rvList.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        DataBindRecyclerViewAdapter<String, LayoutRadioItemBinding> adapter = new DataBindRecyclerViewAdapter<String, LayoutRadioItemBinding>(this) {
            @Override
            public int getItemViewType(int position) {
                return R.layout.layout_radio_item;
            }

            @Override
            public void onBindHolder(LayoutRadioItemBinding binding, String itemData, int p) {
                if (position != -1) {
                    if (p == position) binding.ivRadio.setImageResource(R.mipmap.btn_radio);
                }
                binding.tvText.setText(itemData);
            }

            @Override
            public void itemClick(LayoutRadioItemBinding binding, String itemData, int position, View v) {
                super.itemClick(binding, itemData, position, v);
                itemClickListener.onClick(itemData, position);
            }
        };
        operateBinding.rvList.setAdapter(adapter);
        adapter.setData(strings);
        return view;
    }

    private void showBirthDialog() {
        try {
            View view = View.inflate(this, R.layout.layout_birth, null);
            final LayoutBirthBinding birthBinding = DataBindingUtil.bind(view);
            birthBinding.calendarView.init();
            int year, month, day;
            year = 1999;
            month = 12;
            day = 31;

            if (year > 5204) {
                year = 1999;
            }

            if (month > 12) {
                month = 12;
            }

            if (day > 31) {
                day = 28;
            }

            birthBinding.calendarView.getNumberPickerYear().setValue(year);
            birthBinding.calendarView.getNumberPickerMonth().setValue(month);
            birthBinding.calendarView.getNumberPickerDay().setMaxValue(31);
            birthBinding.calendarView.getNumberPickerDay().setValue(day);
            final PopDialog popDialog = PopDialog.create(this);

            popDialog.setContent(view)
                    .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                        @Override
                        public void onClick(View view, boolean isConfirm) {
                            if (isConfirm) {
                                final String style = "yyyy" + getString(R.string.year) + "MM" + getString(R.string.month) + "dd" + getString(R.string.day2);
                                String newBirthDay = DateUtils.timestamp2String(birthBinding.calendarView.getCalendarData().getCalendar().getTimeInMillis(), "yyyy-MM-dd");
//                                if (DateUtils.isDate2Bigger(mPassengerEntity.getBirthday(), newBirthDay)) {
                                mPassengerEntity.setBirthday(DateUtils.timestamp2String(birthBinding.calendarView.getCalendarData().getCalendar().getTimeInMillis(), "yyyy-MM-dd"));
                                mBinding.iBirthday.tvText.setTextColor(UIUtils.getColor(R.color.color292929));
                                mBinding.iBirthday.tvText.setText(DateUtils.timestamp2String(birthBinding.calendarView.getCalendarData().getCalendar().getTimeInMillis(), style));
//                                } else {
//                                    ToastUtils.show("日期不可小于身份证日期");
//                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verifyCertificate(String s, String o) {
        if (s.equals(o)) return;
        switch (mPassengerEntity.getiDType()) {
            case PassengerEntity.CERTIFICATE_IDENTITY_CARD:
                if (!TextUtils.isEmpty(s)) {
                    if (VerifyUtils.legalId(s)) {
                        if (s.length() > 18) {
                            ConversionUtils.setText(mBinding.iCertificateNumber.etText, s.substring(0, 18));
                        }
                        if (s.length() > 14) {
                            if (o.contains("x")) {
                                if (s.indexOf("x") != s.lastIndexOf("x")) {
                                    ConversionUtils.setText(mBinding.iCertificateNumber.etText, o);
                                } else {
                                    if (!s.substring(s.length() - 1, s.length()).equals("x")) {
                                        if (o.length() < s.length()) {
                                            ConversionUtils.setText(mBinding.iCertificateNumber.etText, o);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (s.contains("x")) {
                                ConversionUtils.setText(mBinding.iCertificateNumber.etText, o);
                            }
                        }
                        if (s.length() >= 14) {
                            String year = s.substring(6, 10);
                            String month = s.substring(10, 12);
                            String day = s.substring(12, 14);

                            final String birthday = year + getString(R.string.year) + month + getString(R.string.month) + day + getString(R.string.day2);
                            mPassengerEntity.setBirthday(year + "-" + month + "-" + day);
                            mBinding.iBirthday.tvText.setTextColor(UIUtils.getColor(R.color.color292929));
                            mBinding.iBirthday.tvText.setText(birthday);

                        }
                    } else {
                        ConversionUtils.setText(mBinding.iCertificateNumber.etText, o);
                    }
                }
                break;
            case PassengerEntity.CERTIFICATE_PROTECTION:
                if (!TextUtils.isEmpty(s)) {
                    if (VerifyUtils.numberAndLetter(s)) {
                        if (s.length() > 20) {
                            ConversionUtils.setText(mBinding.iCertificateNumber.etText, s.substring(0, 20));
                        }
                    } else {
                        ConversionUtils.setText(mBinding.iCertificateNumber.etText, o);
                    }
                }
                break;
            case PassengerEntity.CERTIFICATE_MTPs:
            case PassengerEntity.CERTIFICATE_HK_AND_MACAU_PASS:
            case PassengerEntity.CERTIFICATE_HK_AND_MACAU_FROM_PASS:
            case PassengerEntity.CERTIFICATE_TW_FROM_PASS:
                if (!TextUtils.isEmpty(s)) {
                    if (VerifyUtils.numberAndLetter(s)) {
                        if (s.length() > 20) {
                            ConversionUtils.setText(mBinding.iCertificateNumber.etText, s.substring(0, 20));
                        }
                    } else {
                        ConversionUtils.setText(mBinding.iCertificateNumber.etText, o);
                    }
                }
                break;
            case PassengerEntity.CERTIFICATE_OFFICER_CARD:
            case PassengerEntity.CERTIFICATE_SOLDIER_CARD:
                if (!TextUtils.isEmpty(s)) {
                    if (s.length() > 20) {
                        ConversionUtils.setText(mBinding.iCertificateNumber.etText, s.substring(0, 20));
                    }
                }
        }
    }

    private void showCertificateTypeDialog() {
        final PopDialog popDialog = PopDialog.create(this);
        popDialog.setContent(createView(getCertificates(), mPassengerEntity.getiDType() - 1, new ItemClickListener() {
            @Override
            public void onClick(String itemData, int position) {
                mBinding.iCertificateType.tvText.setText(itemData);
                mBinding.iCertificateNumber.etText.setText("");
                mPassengerEntity.setiDType(position + 1);
                showViewOfType(mPassengerEntity.getiDType());
                popDialog.dismiss();
            }
        }))
                .setHideAllButton()
                .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                    @Override
                    public void onClick(View view, boolean isConfirm) {

                    }
                });
    }

    private void showViewOfType(int position) {
        switch (position) {
            case 2:
                mBinding.llSpellName.setVisibility(View.VISIBLE);
                mBinding.llSpellSurname.setVisibility(View.VISIBLE);
                break;
            default:
                mBinding.llSpellName.setVisibility(View.GONE);
                mBinding.llSpellSurname.setVisibility(View.GONE);
                break;
        }
    }

    private List<String> getGender() {
        return Arrays.asList(Const2.DataArray.Gender);
    }

    private List<String> getCertificates() {
        return Arrays.asList(Const2.DataArray.Certificates);
    }

    public interface ItemClickListener {
        void onClick(String itemData, int position);
    }
}
