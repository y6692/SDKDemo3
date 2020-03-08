package com.centaurstech.sdk.activity.order;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.centaurstech.qiwu.QiWu;
import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.BookingFlightTicketEntity;
import com.centaurstech.qiwu.entity.FlightInfoEntity;
import com.centaurstech.qiwu.entity.FlightTicketEntity;
import com.centaurstech.qiwu.entity.PassengerEntity;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.qiwu.utils.FileUtils;
import com.centaurstech.qiwu.utils.GsonUtils;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.qiwu.utils.VerifyUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.AddPassengerActivity;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.activity.PaymentActivity;
import com.centaurstech.sdk.activity.order.OrderListActivity;
import com.centaurstech.sdk.adapter.AirPriceDetailAdapter;
import com.centaurstech.sdk.adapter.PassengerSlidingAdapter;
import com.centaurstech.sdk.databinding.ActivityConfirmAirOrderBinding;
import com.centaurstech.sdk.databinding.ItemFlightTicketInfoTabBinding;
import com.centaurstech.sdk.databinding.LayoutTicketInfoSuperpositionBinding;
import com.centaurstech.sdk.entity.AirPriceDetailsEntity;
import com.centaurstech.sdk.listener.OnPassengerSlidingListener;
import com.centaurstech.sdk.utils.AnimatorUtils;
import com.centaurstech.sdk.utils.ConversionUtils;
import com.centaurstech.sdk.utils.ToastUtils;
import com.centaurstech.sdk.view.PopDialog;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Leon(黄长亮)
 * @describe 下单预定
 * @date 2019/6/24
 */
public class ConfirmFlightOrderActivity extends BaseActivity implements View.OnClickListener {

    FlightTicketEntity mDepartureTicket;
    FlightTicketEntity mReturnTicket;
    String orderType = "";
    ArrayList<ArrayList<FlightInfoEntity>> mFlightInfoEntities;
    private ActivityConfirmAirOrderBinding mBinding;

    private ArrayList<FlightTicketEntity> flightTicketEntities = new ArrayList<>();

    private PassengerSlidingAdapter mPassengerSlidingAdapter;

    private BookingFlightTicketEntity mBookingAirTicketEntity;

    private int mPriceViewHeight = 0;

    private int mPosition = 0;

    private boolean selectInsurance = false;

    private String amount;

    private static final int REQUEST_CODE_TRAVELER = 35;

    private boolean mShowAirTicketDetail = false;

    private ArrayList<PassengerEntity> mPassengerEntitys = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_air_order);
        mDepartureTicket = (FlightTicketEntity) getIntent().getSerializableExtra(Const.Intent.DATA);
        mReturnTicket = (FlightTicketEntity) getIntent().getSerializableExtra(Const.Intent.DATA2);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        mBinding.iHeadLayout.rlRoot.setBackgroundColor(UIUtils.getColor(R.color.colorWhite));
        mBinding.iHeadLayout.tvTitle.setText(UIUtils.getString(R.string.confirm_order));
        mBinding.iHeadLayout.tvTitle.setTextColor(UIUtils.getColor(R.color.colorFF000000));
        mBinding.iHeadLayout.btnNavBack.setImageResource(R.mipmap.nav_btn_back);

        mBinding.iInsurance.tvTitle.setText(R.string.aviation_accident_insurance);
        mBinding.rvPassengers.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        mPassengerSlidingAdapter = new PassengerSlidingAdapter(this);
        mBinding.rvPassengers.setAdapter(mPassengerSlidingAdapter);
        mBinding.iExpenseAccount.llExpenseAccount.setVisibility(View.GONE);
        mBinding.iExpenseAccount.iInvoiceEnterpriseName.tvTitle.setText(R.string.name2);
        mBinding.iExpenseAccount.iInvoiceEnterpriseName.etText.setHint(R.string.please_input_enterprise_name);
        mBinding.iExpenseAccount.iInvoiceEnterpriseName.tvStar.setVisibility(View.VISIBLE);
        mBinding.iExpenseAccount.iInvoiceEnterpriseName.ivRightImage.setVisibility(View.VISIBLE);
        mBinding.iExpenseAccount.iInvoiceEnterpriseName.ivRightImage.setImageResource(R.mipmap.icon_invoice);
        mBinding.iExpenseAccount.iInvoiceEnterpriseTaxpayerNumber.tvTitle.setText(R.string.taxpayer_number);
        mBinding.iExpenseAccount.iInvoiceEnterpriseTaxpayerNumber.etText.setHint(R.string.please_input_taxpayer_number);
        mBinding.iExpenseAccount.iInvoiceEnterpriseTaxpayerNumber.tvStar.setVisibility(View.VISIBLE);
        mBinding.iExpenseAccount.iInvoiceEnterpriseEmail.tvTitle.setText(R.string.email);
        mBinding.iExpenseAccount.iInvoiceEnterpriseEmail.etText.setHint(R.string.receive_electron_invoice);
        mBinding.iExpenseAccount.iInvoiceEnterpriseEmail.tvStar.setVisibility(View.VISIBLE);
        mBinding.iExpenseAccount.iInvoiceEnterprisePhone.tvTitle.setText(R.string.phone_number);
        mBinding.iExpenseAccount.iInvoiceEnterprisePhone.etText.setHint(R.string.please_input_phone_number);
        mBinding.iExpenseAccount.iInvoiceRegisterAddress.tvTitle.setText(R.string.register_address);
        mBinding.iExpenseAccount.iInvoiceRegisterAddress.etText.setHint(R.string.please_input_company_register_address);
        mBinding.iExpenseAccount.iInvoiceRegisterTel.tvTitle.setText(R.string.register_tel);
        mBinding.iExpenseAccount.iInvoiceRegisterTel.etText.setHint(R.string.please_input_company_register_phone);
        mBinding.iExpenseAccount.iInvoiceOpenBank.tvTitle.setText(R.string.make_account_bank);
        mBinding.iExpenseAccount.iInvoiceOpenBank.etText.setHint(R.string.please_input_company_mack_account_bank);
        mBinding.iExpenseAccount.iInvoiceOpenAccount.tvTitle.setText(R.string.make_account);
        mBinding.iExpenseAccount.iInvoiceOpenAccount.etText.setHint(R.string.please_input_company_mack_account);


        mBinding.iExpenseAccount.iInvoicePeopleName.tvTitle.setText(R.string.name2);
        mBinding.iExpenseAccount.iInvoicePeopleName.etText.setHint(R.string.please_input_name2);
        mBinding.iExpenseAccount.iInvoicePeopleName.tvStar.setVisibility(View.VISIBLE);
        mBinding.iExpenseAccount.iInvoicePeopleName.ivRightImage.setVisibility(View.VISIBLE);
        mBinding.iExpenseAccount.iInvoicePeopleName.ivRightImage.setImageResource(R.mipmap.icon_invoice);
        mBinding.iExpenseAccount.iInvoicePeopleEmail.tvTitle.setText(R.string.email);
        mBinding.iExpenseAccount.iInvoicePeopleEmail.etText.setHint(R.string.receive_electron_invoice);
        mBinding.iExpenseAccount.iInvoicePeopleEmail.tvStar.setVisibility(View.VISIBLE);
        mBinding.iExpenseAccount.iInvoicePeoplePhone.tvTitle.setText(R.string.phone_number);
        mBinding.iExpenseAccount.iInvoicePeoplePhone.etText.setHint(R.string.please_input_phone);

        mBinding.iExpenseAccount.iInvoiceEnterpriseName.etText.setFilters(new InputFilter[]{mCompanyFilter});
        ConversionUtils.numberAndLetter(mBinding.iExpenseAccount.iInvoiceEnterpriseTaxpayerNumber.etText, 20);
        ConversionUtils.setPhoneTextLimit(mBinding.iExpenseAccount.iInvoiceEnterprisePhone.etText);
        ConversionUtils.setPhoneTextLimit(mBinding.iExpenseAccount.iInvoicePeoplePhone.etText);
        ConversionUtils.email(mBinding.iExpenseAccount.iInvoiceEnterpriseEmail.etText, Integer.MAX_VALUE);
        ConversionUtils.email(mBinding.iExpenseAccount.iInvoicePeopleEmail.etText, Integer.MAX_VALUE);
        ConversionUtils.setNameLimit(mBinding.iExpenseAccount.iInvoicePeopleName.etText);


        mBinding.iContactName.tvTitle.setText(R.string.contact);
        mBinding.iContactName.ivRightImage.setVisibility(View.VISIBLE);
        mBinding.iContactName.ivRightImage.setImageResource(R.mipmap.icon_conact);
        mBinding.iContactName.etText.setHint(R.string.please_input_contact_name);
        mBinding.iContactPhone.tvTitle.setText(R.string.phone_number);
        mBinding.iContactPhone.etText.setHint(R.string.receive_order_feedback);
        mBinding.iContactPhone.ivRightImage.setVisibility(View.INVISIBLE);
        mPassengerSlidingAdapter.setOnPassengerSlidingListener(new OnPassengerSlidingListener() {
            @Override
            public void onOperation(int type, int position) {
                mPassengerEntitys.remove(position);
                setPersonCount(mPassengerSlidingAdapter.getData().size());
            }
        });

        mBinding.flPriceDetail.measure(0, 0);
        mPriceViewHeight = mBinding.flPriceDetail.getMeasuredHeight();
        AnimatorUtils.setMargin(mBinding.flPriceDetail, 0, -mPriceViewHeight, 4);
        mBinding.flPriceDetailBG.setVisibility(View.GONE);
        mBinding.iInsurance.ivRadio.setImageResource(R.mipmap.btn_radio_pre);
    }


    private void initData() {
        showLoadingDialog();
        if (mDepartureTicket != null) {
            orderType = "机票";
            if (mReturnTicket != null) {
                flightTicketEntities.add(mReturnTicket);
                orderType = "往返票";
                QiWuAPI.flight.getInfo(mDepartureTicket, mReturnTicket, new APICallback<APIEntity<ArrayList<ArrayList<FlightInfoEntity>>>>() {
                    @Override
                    public void onSuccess(APIEntity<ArrayList<ArrayList<FlightInfoEntity>>> response) {
                        hideLoadingDialog();
                        if (response.isSuccess()) {
                            FileUtils.byte2File(GsonUtils.toJson(response).getBytes(), QiWu.getContext().getExternalFilesDir("txt").toString(), "FlightInfoEntity.txt");
                            mFlightInfoEntities = response.getData();
                            if (mFlightInfoEntities.size() > 0) {
                                BookingFlightTicketEntity bookingAirTicketEntity = new BookingFlightTicketEntity();
                                bookingAirTicketEntity.setPayload(response.getData());
                                mBookingAirTicketEntity = bookingAirTicketEntity;
                                setData();
                            }
                        }
                    }
                });
            } else {
                QiWuAPI.flight.getInfo(mDepartureTicket, new APICallback<APIEntity<ArrayList<ArrayList<FlightInfoEntity>>>>() {
                    @Override
                    public void onSuccess(APIEntity<ArrayList<ArrayList<FlightInfoEntity>>> response) {
                        hideLoadingDialog();
                        switch (response.getRetcode()) {
                            case 0:
                                mFlightInfoEntities = response.getData();
                                LogUtils.sf(GsonUtils.toJson(response));
                                if (mFlightInfoEntities.size() > 0) {
                                    BookingFlightTicketEntity bookingAirTicketEntity = new BookingFlightTicketEntity();
                                    bookingAirTicketEntity.setPayload(response.getData());
                                    mBookingAirTicketEntity = bookingAirTicketEntity;
                                    setData();
                                }
                                break;
                            default:
                                ToastUtils.show(response.getMsg());
                                break;
                        }
                        if (response.isSuccess()) {

                        }
                    }
                });
            }
        }
    }

    private void initListener() {
        mBinding.ivAdd.setOnClickListener(this);
        mBinding.iExpenseAccount.llOtherInfoContainer.setOnClickListener(this);
        mBinding.iOrderFooter.llShowTicketDetail.setOnClickListener(this);
        mBinding.iOrderFooter.tvSubmitOrder.setOnClickListener(this);
        mBinding.flPriceDetailBG.setOnClickListener(this);
        mBinding.tvChildTicketRule.setOnClickListener(this);
        mBinding.tvUpdateLuggage.setOnClickListener(this);
        mBinding.iExpenseAccount.ivCompany.setOnClickListener(this);
        mBinding.iExpenseAccount.ivPersonageOrOther.setOnClickListener(this);
        mBinding.iContactName.ivRightImage.setOnClickListener(this);
        mBinding.iFlightTicketInfoTab1.rlOrderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAirTicketDetail(0);
            }
        });
        mBinding.iFlightTicketInfoTab2.rlOrderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAirTicketDetail(1);
            }
        });

        mBinding.iFlightTicketInfoTab1.flChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition = 0;
                mBinding.iFlightTicketInfoTab1.ivRadio.setImageResource(R.mipmap.btn_radio);
                mBinding.iFlightTicketInfoTab2.ivRadio.setImageResource(R.mipmap.btn_radio_pre);
                setTicketPrice();
                setPersonCount(mPassengerSlidingAdapter.getData().size());
            }
        });

        mBinding.iFlightTicketInfoTab2.flChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition = 1;
                mBinding.iFlightTicketInfoTab1.ivRadio.setImageResource(R.mipmap.btn_radio_pre);
                mBinding.iFlightTicketInfoTab2.ivRadio.setImageResource(R.mipmap.btn_radio);
                setTicketPrice();
                setPersonCount(mPassengerSlidingAdapter.getData().size());
            }
        });

        mBinding.swExpenseAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                mBinding.iExpenseAccount.llExpenseAccount.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        mBinding.iHeadLayout.btnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBackHintDialog();
            }
        });
    }

    private void showBackHintDialog() {
        PopDialog.create(this)
                .setContent(R.string.hint_give_up_order)
                .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                    @Override
                    public void onClick(View view, boolean isConfirm) {
                        if (isConfirm) finish();
                    }
                });
    }

    /***
     * 设置数据
     */
    private void setData() {
        if (mBookingAirTicketEntity.getPayload().size() == 1) {
            setFlightTicketInfo(mBinding.iFlightTicketInfoTab1, mBookingAirTicketEntity.getPayload().get(0));
            mBinding.vLine.setVisibility(View.GONE);
            mBinding.iFlightTicketInfoTab2.llContainer.setVisibility(View.GONE);
        } else if (mBookingAirTicketEntity.getPayload().size() == 2) {
            mBinding.vLine.setVisibility(View.VISIBLE);
            mBinding.iFlightTicketInfoTab2.llContainer.setVisibility(View.VISIBLE);
            setFlightTicketInfo(mBinding.iFlightTicketInfoTab1, mBookingAirTicketEntity.getPayload().get(0));
            setFlightTicketInfo(mBinding.iFlightTicketInfoTab2, mBookingAirTicketEntity.getPayload().get(1));
            mBinding.iFlightTicketInfoTab2.llSpeedMakeTicket.setVisibility(View.VISIBLE);
            mBinding.iFlightTicketInfoTab2.ivRadio.setImageResource(R.mipmap.btn_radio_pre);

            mPosition = 1;
            mBinding.iFlightTicketInfoTab1.ivRadio.setImageResource(R.mipmap.btn_radio_pre);
            mBinding.iFlightTicketInfoTab2.ivRadio.setImageResource(R.mipmap.btn_radio);
            setTicketPrice();

        }
    }


    /***
     * 乘机人数可变
     * @param count
     */
    private void setPersonCount(int count) {
        FlightInfoEntity p1 = null;
        FlightInfoEntity p2 = null;
        List<FlightInfoEntity> payloadBeanList = mBookingAirTicketEntity.getPayload().get(mPosition);
        if (payloadBeanList.size() == 1) {
            p1 = payloadBeanList.get(0);
        } else if (payloadBeanList.size() == 2) {
            p1 = payloadBeanList.get(0);
            p2 = payloadBeanList.get(1);
        }
        double orderAmount = 0.0;
        mBinding.rvPriceDetail.setLayoutManager(new LinearLayoutManager(this));
        List<AirPriceDetailsEntity> detailsEntities = new ArrayList<>();
        FlightInfoEntity.InsurancesBean insurances = null;
        if (p1 != null) {
            FlightInfoEntity.PriceInfoBean priceInfo = p1.getPriceInfo();
            FlightInfoEntity.ExtInfoBean extInfo = p1.getExtInfo();
            orderAmount = Double.valueOf(extInfo.getRy_price()) * count;
            orderAmount = orderAmount + (Double.valueOf(priceInfo.getTof()) + Double.valueOf(priceInfo.getArf())) * count;


            List<PassengerEntity> passengers = mPassengerEntitys;
            int adultCount = 0, childCount = 0, babyCount = 0;

            for (PassengerEntity passenger : passengers) {
                switch (passenger.getAgeType()) { // 0成人，1儿童，2婴儿
                    case 0:
                        adultCount++;
                        break;
                    case 1:
                        childCount++;
                        break;
                    case 2:
                        babyCount++;
                        break;
                }
            }

            AirPriceDetailsEntity adultTicket = new AirPriceDetailsEntity();
            adultTicket.setCount(count);
            adultTicket.setText("成人机票" + " x" + adultCount);
            adultTicket.setType(1);
            adultTicket.setTripType(p2 == null ? "" : getString(R.string.to_trip));
            adultTicket.setPrice(ConversionUtils.getMoney(Double.valueOf(extInfo.getRy_price()) * adultCount));
            detailsEntities.add(adultTicket);

            if (childCount > 0) {
                AirPriceDetailsEntity childTicket = new AirPriceDetailsEntity();
                childTicket.setCount(count);
                childTicket.setText("儿童机票" + " x" + childCount);
                childTicket.setTripType(p2 == null ? "" : getString(R.string.to_trip));
                childTicket.setPrice(ConversionUtils.getMoney(Double.valueOf(extInfo.getRy_price()) * childCount * 0.5));
                detailsEntities.add(childTicket);
            }

            if (babyCount > 0) {
                AirPriceDetailsEntity babyTicket = new AirPriceDetailsEntity();
                babyTicket.setCount(count);
                babyTicket.setText("婴儿机票" + " x" + babyCount);
                babyTicket.setTripType(p2 == null ? "" : getString(R.string.to_trip));
                babyTicket.setPrice(ConversionUtils.getMoney(Double.valueOf(extInfo.getRy_price()) * babyCount * 0.1));
                detailsEntities.add(babyTicket);
            }

            AirPriceDetailsEntity adultFuelOil = new AirPriceDetailsEntity();
            adultFuelOil.setCount(count);
            adultFuelOil.setText("（成人）机建+燃油" + " x" + adultCount);
            adultFuelOil.setTripType(p2 == null ? "" : getString(R.string.to_trip));
            adultFuelOil.setPrice(ConversionUtils.getMoney((Double.valueOf(priceInfo.getTof()) + Double.valueOf(priceInfo.getArf())) * adultCount));
            detailsEntities.add(adultFuelOil);

            if (childCount > 0) {
                AirPriceDetailsEntity childFuelOil = new AirPriceDetailsEntity();
                childFuelOil.setCount(count);
                childFuelOil.setText("（儿童）机建+燃油" + " x" + childCount);
                childFuelOil.setTripType(p2 == null ? "" : getString(R.string.to_trip));
                childFuelOil.setPrice(ConversionUtils.getMoney((Double.valueOf(priceInfo.getTof()) + Double.valueOf(priceInfo.getArf())) * childCount * 0.5));
                detailsEntities.add(childFuelOil);
            }

            if (babyCount > 0) {
                AirPriceDetailsEntity babyTicket = new AirPriceDetailsEntity();
                babyTicket.setCount(count);
                babyTicket.setText("（婴儿）机建+燃油" + " x" + babyCount);
                babyTicket.setTripType(p2 == null ? "" : getString(R.string.to_trip));
                babyTicket.setPrice(ConversionUtils.getMoney((Double.valueOf(priceInfo.getTof()) + Double.valueOf(priceInfo.getArf())) * babyCount * 0));
                detailsEntities.add(babyTicket);
            }
        }

        if (p2 != null) {
            FlightInfoEntity.PriceInfoBean priceInfo = p2.getPriceInfo();
            FlightInfoEntity.ExtInfoBean extInfo = p2.getExtInfo();
            orderAmount = orderAmount + Double.valueOf(extInfo.getRy_price()) * count;
            orderAmount = orderAmount + (Double.valueOf(priceInfo.getTof()) + Double.valueOf(priceInfo.getArf())) * count;

            List<PassengerEntity> passengers = mPassengerSlidingAdapter.getData();
            int adultCount = 0, childCount = 0, babyCount = 0;
            for (PassengerEntity passenger : passengers) {
                switch (passenger.getAgeType()) { // 0成人，1儿童，2婴儿
                    case 0:
                        adultCount++;
                        break;
                    case 1:
                        childCount++;
                        break;
                    case 2:
                        babyCount++;
                        break;
                }
            }

            AirPriceDetailsEntity adultTicket = new AirPriceDetailsEntity();
            adultTicket.setCount(count);
            adultTicket.setText("成人机票" + " x" + adultCount);
            adultTicket.setType(1);
            adultTicket.setTripType(getString(R.string.return_trip));
            adultTicket.setPrice(ConversionUtils.getMoney(Double.valueOf(extInfo.getRy_price()) * adultCount));
            detailsEntities.add(adultTicket);

            if (childCount > 0) {
                AirPriceDetailsEntity childTicket = new AirPriceDetailsEntity();
                childTicket.setCount(count);
                childTicket.setText("儿童机票" + " x" + childCount);
                childTicket.setTripType(getString(R.string.return_trip));
                childTicket.setPrice(ConversionUtils.getMoney(Double.valueOf(extInfo.getRy_price()) * childCount * 0.5));
                detailsEntities.add(childTicket);
            }

            if (babyCount > 0) {
                AirPriceDetailsEntity babyTicket = new AirPriceDetailsEntity();
                babyTicket.setCount(count);
                babyTicket.setText("婴儿机票" + " x" + babyCount);
                babyTicket.setTripType(getString(R.string.return_trip));
                babyTicket.setPrice(ConversionUtils.getMoney(Double.valueOf(extInfo.getRy_price()) * babyCount * 0.1));
                detailsEntities.add(babyTicket);
            }

            AirPriceDetailsEntity adultFuelOil = new AirPriceDetailsEntity();
            adultFuelOil.setCount(count);
            adultFuelOil.setText("（成人）机建+燃油" + " x" + adultCount);
            adultFuelOil.setTripType(getString(R.string.return_trip));
            adultFuelOil.setPrice(ConversionUtils.getMoney((Double.valueOf(priceInfo.getTof()) + Double.valueOf(priceInfo.getArf())) * adultCount));
            detailsEntities.add(adultFuelOil);

            if (childCount > 0) {
                AirPriceDetailsEntity childFuelOil = new AirPriceDetailsEntity();
                childFuelOil.setCount(count);
                childFuelOil.setText("（儿童）机建+燃油" + " x" + childCount);
                childFuelOil.setTripType(getString(R.string.return_trip));
                childFuelOil.setPrice(ConversionUtils.getMoney((Double.valueOf(priceInfo.getTof()) + Double.valueOf(priceInfo.getArf())) * childCount * 0.5));
                detailsEntities.add(childFuelOil);
            }

            if (babyCount > 0) {
                AirPriceDetailsEntity babyTicket = new AirPriceDetailsEntity();
                babyTicket.setCount(count);
                babyTicket.setText("（婴儿）机建+燃油" + " x" + babyCount);
                babyTicket.setTripType(getString(R.string.return_trip));
                babyTicket.setPrice(ConversionUtils.getMoney((Double.valueOf(priceInfo.getTof()) + Double.valueOf(priceInfo.getArf())) * babyCount * 0));
                detailsEntities.add(babyTicket);
            }
        }

        if (selectInsurance && insurances != null) {
            AirPriceDetailsEntity insurance = new AirPriceDetailsEntity();
            if (p2 != null) {
                orderAmount = orderAmount + Double.valueOf(insurances.getPayment_amt()) * count * 2;
                insurance.setPrice(ConversionUtils.getMoney(Double.valueOf(insurances.getPayment_amt()) * count * 2));
                insurance.setText(getString(R.string.aviation_accident_insurance) + " x" + count * 2);
            } else {
                orderAmount = orderAmount + Double.valueOf(insurances.getPayment_amt()) * count;
                insurance.setPrice(ConversionUtils.getMoney(Double.valueOf(insurances.getPayment_amt()) * count));
                insurance.setText(getString(R.string.aviation_accident_insurance) + " x" + count);
            }
            insurance.setCount(count);
            insurance.setTripType("");
            detailsEntities.add(insurance);
        }

        AirPriceDetailAdapter airPriceDetailAdapter = new AirPriceDetailAdapter(this);
        mBinding.rvPriceDetail.setAdapter(airPriceDetailAdapter);
        airPriceDetailAdapter.setData(detailsEntities);

        ViewGroup.LayoutParams lp = mBinding.rvPriceDetail.getLayoutParams();
        if (detailsEntities.size() > 5) {
            lp.height = UIUtils.dip2Px(40) * 5;
        } else {
            lp.height = UIUtils.dip2Px(40) * detailsEntities.size();
        }
        mBinding.rvPriceDetail.setLayoutParams(lp);
        amount = String.valueOf(orderAmount);
        if (orderAmount >= 300) {
            mBinding.swExpenseAccount.setEnabled(true);
            mBinding.tvInvoiceExplain.setText(R.string.flight_after_7_day_send_email);
        } else {
            mBinding.swExpenseAccount.setEnabled(false);
            mBinding.tvInvoiceExplain.setText(R.string.under_300_please_to_invoice_control);
        }
        mBinding.iOrderFooter.tvOrderAmount.setText(getString(R.string.money_sign) + ConversionUtils.getMoney(orderAmount));
        mBinding.iOrderFooter.tvPersonCount.setText("（共" + count + getString(R.string.person) + "）");
    }

    InputFilter mCompanyFilter = new InputFilter() {

        Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\u4E00-\\u9FA5_\\-()]");

        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            Matcher matcher = pattern.matcher(charSequence);
            if (matcher.matches()) {
                ToastUtils.show("非法字符！");
                return "";
            } else {
                return null;
            }

        }
    };

    private void showAirTicketDetail(int index) {
        try {
            View view = View.inflate(this, R.layout.layout_ticket_info_superposition, null);
            LayoutTicketInfoSuperpositionBinding binding = DataBindingUtil.bind(view);
            binding.iTicketInfo1.vLine.setVisibility(View.GONE);
            binding.iTicketInfo2.vLine.setVisibility(View.GONE);
            if (mBookingAirTicketEntity != null) {
                FlightInfoEntity p1 = null;
                FlightInfoEntity p2 = null;
                List<FlightInfoEntity> payloadBeanList = mBookingAirTicketEntity.getPayload().get(index);
                if (payloadBeanList.size() == 1) {
                    p1 = payloadBeanList.get(0);
                } else if (payloadBeanList.size() == 2) {
                    p1 = payloadBeanList.get(0);
                    p2 = payloadBeanList.get(1);
                }
                if (p1 != null) {
                    FlightInfoEntity.FlightInfoBean flightInfoBean = p1.getFlightInfo().get(0);
                    String date = DateUtils.timestamp2String(DateUtils.date2Timestamp(flightInfoBean.getDptDate(), "yyyy-MM-dd"), "M" + UIUtils.getString(R.string.month) + "d" + UIUtils.getString(R.string.day2));

                    binding.iTicketInfo1.tvDate.setText(date);
                    binding.iTicketInfo1.tvRoute.setText(flightInfoBean.getDptCity() + "-" + flightInfoBean.getArrCity());
                    binding.iTicketInfo1.tvCabinInfo.setText(flightInfoBean.getCbcn());
                    binding.iTicketInfo1.tvOriginAisle.setText(flightInfoBean.getDptAirport() + isEmpty(flightInfoBean.getDptTerminal()));
                    binding.iTicketInfo1.tvOriginTime.setText(flightInfoBean.getDptTime());
                    binding.iTicketInfo1.tvPassAddress.setText((TextUtils.isEmpty(flightInfoBean.getStopCity()) ? "" : UIUtils.getString(R.string.transit) + " " + flightInfoBean.getStopCity()));
                    binding.iTicketInfo1.tvDestinationAisle.setText(flightInfoBean.getArrAirport() + isEmpty(flightInfoBean.getArrTerminal()));
                    binding.iTicketInfo1.tvDestinationTime.setText(flightInfoBean.getArrTime());
                    binding.iTicketInfo1.tvVehicleType.setText(flightInfoBean.getCarrierName() + flightInfoBean.getFlightNum());
                    if (flightInfoBean.getFlightTimes().contains("分钟")) {
                        binding.iTicketInfo1.tvDuration.setText(UIUtils.getString(R.string.voyage) + flightInfoBean.getFlightTimes().replace("小时", "h").replace("分钟", "m"));
                    } else {
                        binding.iTicketInfo1.tvDuration.setText(UIUtils.getString(R.string.voyage) + flightInfoBean.getFlightTimes().replace("小时", "h"));
                    }
//                    binding.iTicketInfo1.tvDuration.setText((UIUtils.getString(R.string.voyage)+String.format("%.1f",(mAirTicketDetailsEntity.getDuration() *1.0f / 60))  + "H"));
                }

                if (p2 != null) {

                    binding.vLine.setVisibility(View.VISIBLE);

                    FlightInfoEntity.FlightInfoBean flightInfoBean = p2.getFlightInfo().get(0);
                    FlightInfoEntity.PriceInfoBean priceInfo = p2.getPriceInfo();
                    FlightInfoEntity.ExtInfoBean extInfo = p2.getExtInfo();
                    String date = DateUtils.timestamp2String(DateUtils.date2Timestamp(flightInfoBean.getDptDate(), "yyyy-MM-dd"), "M" + UIUtils.getString(R.string.month) + "d" + UIUtils.getString(R.string.day2));
                    String time = " " + flightInfoBean.getDptTime();


                    binding.iTicketInfo2.llContainer.setVisibility(View.VISIBLE);
                    binding.iTicketInfo2.tvDate.setText(date);
                    binding.iTicketInfo2.tvRoute.setText(flightInfoBean.getDptCity() + "-" + flightInfoBean.getArrCity());
                    binding.iTicketInfo2.tvCabinInfo.setText(flightInfoBean.getCbcn());
                    binding.iTicketInfo2.tvOriginAisle.setText(flightInfoBean.getDptAirport() + isEmpty(flightInfoBean.getDptTerminal()));
                    binding.iTicketInfo2.tvOriginTime.setText(flightInfoBean.getDptTime());
                    binding.iTicketInfo2.tvPassAddress.setText((TextUtils.isEmpty(flightInfoBean.getStopCity()) ? "" : UIUtils.getString(R.string.transit) + " " + flightInfoBean.getStopCity()));
                    binding.iTicketInfo2.tvDestinationAisle.setText(flightInfoBean.getArrAirport() + isEmpty(flightInfoBean.getArrTerminal()));
                    binding.iTicketInfo2.tvDestinationTime.setText(flightInfoBean.getArrTime());
                    binding.iTicketInfo2.tvVehicleType.setText(flightInfoBean.getCarrierName() + flightInfoBean.getFlightNum());
                    if (flightInfoBean.getFlightTimes().contains("分钟")) {
                        binding.iTicketInfo2.tvDuration.setText(UIUtils.getString(R.string.voyage) + flightInfoBean.getFlightTimes().replace("小时", "h").replace("分钟", "m"));
                    } else {
                        binding.iTicketInfo2.tvDuration.setText(UIUtils.getString(R.string.voyage) + flightInfoBean.getFlightTimes().replace("小时", "h"));
                    }
                }
            }
            PopDialog.create(this)
                    .setContent(view)
                    .setHideAllButton()
                    .setCanceledTouchOutside(true)
                    .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                        @Override
                        public void onClick(View view, boolean isConfirm) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTicketPrice() {

        FlightInfoEntity p1 = null;
        FlightInfoEntity p2 = null;
        List<FlightInfoEntity> payloadBeanList = mBookingAirTicketEntity.getPayload().get(mPosition);
        if (payloadBeanList.size() == 1) {
            p1 = payloadBeanList.get(0);
        } else if (payloadBeanList.size() == 2) {
            p1 = payloadBeanList.get(0);
            p2 = payloadBeanList.get(1);
        }

        if (p1 != null) {
            FlightInfoEntity.PriceInfoBean priceInfo = p1.getPriceInfo();
            FlightInfoEntity.ExtInfoBean extInfo = p1.getExtInfo();
            mBinding.tvTicketPrice.setText(getString(R.string.money_sign) + extInfo.getRy_price());
            mBinding.tvFuelOidPrice.setText(getString(R.string.money_sign) + (Double.valueOf(priceInfo.getTof()) + Double.valueOf(priceInfo.getArf())));
        }

        if (p2 != null) {
            FlightInfoEntity.PriceInfoBean priceInfo = p2.getPriceInfo();
            FlightInfoEntity.ExtInfoBean extInfo = p2.getExtInfo();

            mBinding.tvTicketPrice.setText(mBinding.tvTicketPrice.getText() + "+" + getString(R.string.money_sign) + extInfo.getRy_price());
            mBinding.tvFuelOidPrice.setText(mBinding.tvFuelOidPrice.getText() + "+" + getString(R.string.money_sign) + (Double.valueOf(priceInfo.getTof()) + Double.valueOf(priceInfo.getArf())));
        }
    }

    /***
     *
     * @param flightTicketInfo
     * @param payloadBeanList
     */
    private void setFlightTicketInfo(ItemFlightTicketInfoTabBinding flightTicketInfo, List<FlightInfoEntity> payloadBeanList) {
        LogUtils.sf(GsonUtils.toJson(payloadBeanList));
        if (payloadBeanList != null) {
            FlightInfoEntity p1 = null;
            FlightInfoEntity p2 = null;
            if (payloadBeanList.size() == 1) {
                p1 = payloadBeanList.get(0);
            } else if (payloadBeanList.size() == 2) {
                p1 = payloadBeanList.get(0);
                p2 = payloadBeanList.get(1);
            }

            if (p1 != null) {
                FlightInfoEntity.FlightInfoBean flightInfoBean = p1.getFlightInfo().get(0);
                FlightInfoEntity.PriceInfoBean priceInfo = p1.getPriceInfo();
                FlightInfoEntity.ExtInfoBean extInfo = p1.getExtInfo();
                String date = DateUtils.timestamp2String(DateUtils.date2Timestamp(flightInfoBean.getDptDate(), "yyyy-MM-dd"), "yyyy.MM.dd");
                String time = " " + flightInfoBean.getDptTime();

                String cabin;
                if (flightInfoBean.getCbcn().contains("(")) {
                    cabin = flightInfoBean.getCbcn().substring(0, flightInfoBean.getCbcn().indexOf("("));
                } else {
                    cabin = flightInfoBean.getCbcn();
                }
                if (!TextUtils.isEmpty(cabin)) {
                    if (cabin.length() > 3) {
                        flightTicketInfo.tvTimeAndCabinType1.setText(date + time + "  |  " + cabin.substring(0, 3) + "...");
                    } else {
                        flightTicketInfo.tvTimeAndCabinType1.setText(date + time + "  |  " + cabin);
                    }
                }
                flightTicketInfo.tvFlightChannel1.setText(flightInfoBean.getDptAirport() + isEmpty(flightInfoBean.getDptTerminal())
                        + " - " + isEmpty(flightInfoBean.getArrAirport()) + isEmpty(flightInfoBean.getArrTerminal()));
                mBinding.tvTicketPrice.setText(getString(R.string.money_sign) + extInfo.getRy_price());
                mBinding.tvFuelOidPrice.setText(getString(R.string.money_sign) + (Double.valueOf(priceInfo.getTof()) + Double.valueOf(priceInfo.getArf())));
            }

            if (p2 != null) {
                FlightInfoEntity.FlightInfoBean flightInfoBean = p2.getFlightInfo().get(0);
                FlightInfoEntity.PriceInfoBean priceInfo = p2.getPriceInfo();
                FlightInfoEntity.ExtInfoBean extInfo = p2.getExtInfo();
                String date = DateUtils.timestamp2String(DateUtils.date2Timestamp(flightInfoBean.getDptDate(), "yyyy-MM-dd"), "yyyy.MM.dd");
                String time = " " + flightInfoBean.getDptTime();
                String cabin;
                if (flightInfoBean.getCbcn().contains("(")) {
                    cabin = flightInfoBean.getCbcn().substring(0, flightInfoBean.getCbcn().indexOf("("));
                } else {
                    cabin = flightInfoBean.getCbcn();
                }
                if (!TextUtils.isEmpty(cabin)) {
                    if (cabin.length() > 3) {
                        flightTicketInfo.tvTimeAndCabinType2.setText(date + time + "  |  " + cabin.substring(0, 3) + "...");
                    } else {
                        flightTicketInfo.tvTimeAndCabinType2.setText(date + time + "  |  " + cabin);
                    }
                }
                flightTicketInfo.tvFlightChannel2.setText(flightInfoBean.getDptAirport() + isEmpty(flightInfoBean.getDptTerminal())
                        + " - " + flightInfoBean.getArrAirport() + isEmpty(flightInfoBean.getArrTerminal()));
                flightTicketInfo.llFlightInfo2.setVisibility(View.VISIBLE);
                flightTicketInfo.tvTicketTab1.setVisibility(View.VISIBLE);
                mBinding.tvTicketPrice.setText(mBinding.tvTicketPrice.getText() + "+" + getString(R.string.money_sign) + extInfo.getRy_price());
                mBinding.tvFuelOidPrice.setText(mBinding.tvFuelOidPrice.getText() + "+" + getString(R.string.money_sign) + (Double.valueOf(priceInfo.getTof()) + Double.valueOf(priceInfo.getArf())));
            }
        }
    }

    private String isEmpty(String s) {
        if (TextUtils.isEmpty(s) || s.equals("null")) {
            return "";
        } else {
            return s;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivAdd:
                launchActivity(AddPassengerActivity.class, REQUEST_CODE_TRAVELER);
                break;
            case R.id.tvChildTicketRule:

                break;
            case R.id.tvUpdateLuggage:

                break;
            case R.id.llShowTicketDetail:
            case R.id.flPriceDetailBG:
                showOrHideAirTicketDetailView();
                break;
            case R.id.ivRadio:
                selectInsurance = !selectInsurance;
                mBinding.iInsurance.ivRadio.setImageResource(selectInsurance ? R.mipmap.btn_radio : R.mipmap.btn_radio_pre);
                setPersonCount(mPassengerSlidingAdapter.getData().size());
                break;
            case R.id.tvSubmitOrder:
                showCreatingOrderDialog();
                break;
        }
    }

    /***
     * 确认下单
     */
    private void showCreatingOrderDialog() {
        String contactName = mBinding.iContactName.etText.getText().toString();
        String contactPhone = mBinding.iContactName.etText.getText().toString();
        if (mPassengerSlidingAdapter.getData().size() <= 0) {
            ToastUtils.show(R.string.please_choose_air_passenger);
            return;
        }
        if (TextUtils.isEmpty(mBinding.iContactName.etText.getText())) {
            ToastUtils.show(R.string.please_write_contact_name);
            return;
        }

        if (TextUtils.isEmpty(mBinding.iContactPhone.etText.getText())) {
            ToastUtils.show(R.string.please_write_phone);
            return;
        }
        if (!VerifyUtils.phoneNumber(mBinding.iContactPhone.etText.getText().toString())) {
            ToastUtils.show(R.string.please_write_right_phone);
            return;
        }

        FlightInfoEntity p1 = null;
        FlightInfoEntity p2 = null;
        List<FlightInfoEntity> payloadBeanList = mBookingAirTicketEntity.getPayload().get(mPosition);
        showLoadingDialog();
        if (payloadBeanList.size() == 1) {
            p1 = payloadBeanList.get(0);
            QiWuAPI.flight.booking(p1, mPassengerEntitys, contactName, contactPhone, null, new APICallback<APIEntity<String>>() {
                @Override
                public void onSuccess(APIEntity<String> response) {
                    hideLoadingDialog();
                    if (response.isSuccess()) {
                        try {
                            JSONArray array = new JSONArray(response.getData());
                            ArrayList<String> orderIds = new ArrayList<String>();
                            for (int i = 0; i < array.length(); i++) {
                                orderIds.add(array.getString(i));
                            }
                            launchActivity(OrderListActivity.class);
                            launchActivity(PaymentActivity.class, new IntentExpand() {
                                @Override
                                public void extraValue(Intent intent) {
                                    intent.putExtra(Const.Intent.DATA, orderIds.get(0));
                                    intent.putExtra(Const.Intent.TYPE, 2);
                                }
                            });
                            finish();
                        } catch (Exception e) {

                        }
                    } else {
                        UIUtils.showToast(response.getMsg());
                    }
                }
            });
        } else if (payloadBeanList.size() == 2) {
            p1 = payloadBeanList.get(0);
            p2 = payloadBeanList.get(1);
            QiWuAPI.flight.booking(p1, p2, mPassengerEntitys, contactName, contactPhone, null, new APICallback<APIEntity<String>>() {
                @Override
                public void onSuccess(APIEntity<String> response) {
                    hideLoadingDialog();
                    if (response.isSuccess()) {
                        try {
                            JSONArray array = new JSONArray(response.getData());
                            ArrayList<String> orderIds = new ArrayList<String>();
                            for (int i = 0; i < array.length(); i++) {
                                orderIds.add(array.getString(i));
                            }
                            launchActivity(OrderListActivity.class);
                            launchActivity(PaymentActivity.class, new IntentExpand() {
                                @Override
                                public void extraValue(Intent intent) {
                                    intent.putExtra(Const.Intent.DATA, orderIds.get(0));
                                    intent.putExtra(Const.Intent.TYPE, 2);
                                }
                            });
                            finish();
                        } catch (Exception e) {

                        }
                    } else {
                        UIUtils.showToast(response.getMsg());
                    }
                }
            });
        }
    }

    private void showOrHideAirTicketDetailView() {
        if (mShowAirTicketDetail) {
            mBinding.iOrderFooter.ivShowTicketPop.setImageResource(R.mipmap.btn_pop2);
            AnimatorUtils.hide(mBinding.flPriceDetailBG, 200);
            AnimatorUtils.setMargin(mBinding.flPriceDetail, 200, -mPriceViewHeight, 4);
        } else {
            mBinding.iOrderFooter.ivShowTicketPop.setImageResource(R.mipmap.btn_pop_pre);
            AnimatorUtils.show(mBinding.flPriceDetailBG, 200);
            AnimatorUtils.setMargin(mBinding.flPriceDetail, 200, 0, 4);
        }
        mShowAirTicketDetail = !mShowAirTicketDetail;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_TRAVELER:
                    PassengerEntity entity = (PassengerEntity) data.getSerializableExtra(Const.Intent.DATA);
                    if (entity == null) return;
                    mPassengerEntitys.add(entity);
                    if (mPassengerEntitys != null) {
                        mPassengerSlidingAdapter.setData(mPassengerEntitys);
                        setPersonCount(mPassengerEntitys.size());
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
