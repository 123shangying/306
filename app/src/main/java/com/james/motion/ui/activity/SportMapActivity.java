package com.james.motion.ui.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.blankj.utilcode.util.SPUtils;
import com.james.motion.R;
import com.james.motion.commmon.bean.PathRecord;
import com.james.motion.commmon.bean.SportMotionRecord;
import com.james.motion.commmon.utils.CountTimerUtil;
import com.james.motion.commmon.utils.DateUtils;
import com.james.motion.commmon.utils.LogUtils;
import com.james.motion.commmon.utils.MySp;
import com.james.motion.commmon.utils.UIHelper;
import com.james.motion.commmon.utils.Utils;
import com.james.motion.db.DataManager;
import com.james.motion.db.RealmHelper;
import com.james.motion.sport_motion.MotionUtils;
import com.james.motion.sport_motion.PathSmoothTool;
import com.james.motion.ui.BaseActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ??????: ????????????
 * ??????: james
 * ??????: 2019/2/27 14:58
 * ??????: SportMapActivity
 */
public class SportMapActivity extends BaseActivity {

    @BindView(R.id.sport_content)
    RelativeLayout sportContent;
    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.rlMap)
    RelativeLayout rlMap;
    @BindView(R.id.tv_mode)
    TextView tvMode;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv2)
    TextView tv2;
    @BindView(R.id.tv3)
    TextView tv3;
    @BindView(R.id.cm_passtime)
    Chronometer cmPasstime;
    @BindView(R.id.tvMileage)
    TextView tvMileage;
    @BindView(R.id.tvSpeed)
    TextView tvSpeed;
    @BindView(R.id.fl_count_timer)
    FrameLayout flCountTimer;
    @BindView(R.id.tv_number_anim)
    TextView tvNumberAnim;

    private Dialog tipDialog = null;

    //??????????????????
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

//    private boolean isBind = false;
//    private LocationService mService = null;
//    private ServiceConnection mConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mService = null;
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            // ??????????????????IBinder???????????????IBinder??????????????????????????????????????????Service??????
//            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
//            mService = binder.getService();
//            mService.setInterfaceLocationed(aMapLocation -> {
//                Message msg = Message.obtain();
//                msg.what = LOCATION;
//                msg.obj = aMapLocation;
//                mHandler.sendMessage(msg);
//            });
//        }
//    };

    //?????????????????????
    private OnLocationChangedListener mListener = null;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private final Long interval = 4000L;//??????????????????

    private PolylineOptions polylineOptions;
    private Polyline mOriginPolyline;
    private PathRecord record;
    private DataManager dataManager = null;
    private PathSmoothTool mpathSmoothTool = null;
    private List<LatLng> mSportLatLngs = new ArrayList<>(0);

    private long seconds = 0;//??????(??????)
    private long mStartTime = 0;
    private long mEndTime = 0;
    private double distance;//??????
//    private float calorie;//?????????
//    private float speed;//??????

    private boolean ISSTARTUP = false;

    private ValueAnimator apperaAnim1;
    private ValueAnimator hiddenAnim1;

    private ValueAnimator apperaAnim2;
    private ValueAnimator hiddenAnim2;

    private ValueAnimator apperaAnim3;
    private ValueAnimator hiddenAnim3;

    private AMap aMap;

    private boolean mode = true;

    private final int LOCATION = 0;
    private Handler mHandler = new Handler(Looper.getMainLooper())
//    {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case LOCATION://???handler????????????
//                    updateLocation((AMapLocation) msg.obj);
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
    ;

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            cmPasstime.setText(formatseconds());
            mHandler.postDelayed(this, 1000);
        }
    }

    private MyRunnable mRunnable = null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_sportmap;
    }

    @Override
    public void initData(Bundle savedInstanceState) {

        mapView.onCreate(savedInstanceState);// ?????????????????????

        record = new PathRecord();

        dataManager = new DataManager(new RealmHelper());

        //???????????????
        CountTimerUtil.start(tvNumberAnim, new CountTimerUtil.AnimationState() {
            @Override
            public void start() {

            }

            @Override
            public void repeat() {

            }

            @Override
            public void end() {
                flCountTimer.setVisibility(View.GONE);
                hiddenAnim1.start();
//                apperaAnim2.start_bg();
                hiddenAnim3.start();

                ISSTARTUP = true;

                seconds = 0;
                cmPasstime.setBase(SystemClock.elapsedRealtime());

                mStartTime = System.currentTimeMillis();
                if (record == null)
                    record = new PathRecord();
                record.setStartTime(mStartTime);

                if (mRunnable == null)
                    mRunnable = new MyRunnable();
                mHandler.postDelayed(mRunnable, 0);

                startUpLocation();

            }
        });

        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }

        initPolyline();

        setMode();
    }

    private void initPolyline() {
        polylineOptions = new PolylineOptions();
        polylineOptions.color(getResources().getColor(R.color.colorAccent));
        polylineOptions.width(20f);
        polylineOptions.useGradient(true);

        mpathSmoothTool = new PathSmoothTool();
        mpathSmoothTool.setIntensity(4);
    }

    private void startUpLocation() {
        //????????????
//        isBind = bindService(new Intent(this, LocationService.class), mConnection, Service.BIND_AUTO_CREATE);

        //??????????????????
        if (null != mapView)
            sportContent.setKeepScreenOn(true);

        startLocation();
    }

    private void unBindService() {
        //??????????????????
//        if (isBind && null != mService) {
//            unbindService(mConnection);
//            isBind = false;
//        }
//        mService = null;

        //??????????????????
        if (null != mapView)
            sportContent.setKeepScreenOn(false);

        //????????????
        if (null != mLocationClient) {
            mLocationClient.stopLocation();
            mLocationClient.unRegisterLocationListener(aMapLocationListener);
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    private void setMode() {
        if (mode) {
            tvMode.setText("????????????");
            UIHelper.setLeftDrawable(tvMode, R.mipmap.map_mode);
            rlMap.setVisibility(View.GONE);
        } else {
            tvMode.setText("????????????");
            UIHelper.setLeftDrawable(tvMode, R.mipmap.run_mode);
            rlMap.setVisibility(View.VISIBLE);
        }
        mode = !mode;
    }

    /**
     * ???????????????
     */
    private void startLocation() {
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            //??????????????????
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//????????????????????????????????????????????????????????????????????????????????????????????????????????????
            mLocationOption.setGpsFirst(false);//?????????????????????gps??????????????????????????????????????????????????????
            mLocationOption.setHttpTimeOut(30000);//???????????????????????????????????????????????????30?????????????????????????????????
            mLocationOption.setInterval(interval);//???????????????????????????????????????2???
            mLocationOption.setNeedAddress(false);//????????????????????????????????????????????????????????????true
            mLocationOption.setOnceLocation(false);//?????????????????????????????????????????????false
            mLocationOption.setOnceLocationLatest(false);//???????????????????????????wifi??????????????????false.???????????????true,?????????????????????????????????????????????????????????
            AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//????????? ????????????????????????????????????HTTP??????HTTPS????????????HTTP
            mLocationOption.setSensorEnable(false);//????????????????????????????????????????????????false
            mLocationOption.setWifiScan(true); //???????????????????????????wifi??????????????????true??????????????????false??????????????????????????????????????????????????????????????????????????????????????????????????????
            mLocationOption.setLocationCacheEnable(true); //???????????????????????????????????????????????????true
            mLocationOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.ZH);//??????????????????????????????????????????????????????????????????????????????????????????????????????
            mLocationClient.setLocationOption(mLocationOption);

            // ??????????????????
            mLocationClient.setLocationListener(aMapLocationListener);
            //????????????
            mLocationClient.startLocation();
        }
    }

    @Override
    public void initListener() {
//        cmPasstime.setOnChronometerTickListener(chronometer -> cmPasstime.setText(formatseconds()));
    }

    @OnClick({R.id.tv_mode, R.id.tv1, R.id.tv2, R.id.tv3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_mode:
                setMode();
                break;
            case R.id.tv1:
                ISSTARTUP = true;

                mHandler.removeCallbacks(mRunnable);
                mRunnable = null;

                unBindService();

                hiddenAnim1.start();
//                apperaAnim2.start_bg();
                hiddenAnim3.start();

                //????????????
                if (null != record && null != record.getPathline() && !record.getPathline().isEmpty()) {
                    saveRecord();
                } else {
                    Utils.showToast(this, "?????????????????????!");
                    finish();
                }
                break;
            case R.id.tv2:
                ISSTARTUP = false;

                if (null != mRunnable) {
                    mHandler.removeCallbacks(mRunnable);
                    mRunnable = null;
                }

                unBindService();

                mEndTime = System.currentTimeMillis();

                aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(mSportLatLngs), 20));

                apperaAnim1.start();
                hiddenAnim2.start();
                apperaAnim3.start();
                break;
            case R.id.tv3:
                ISSTARTUP = true;

                if (mRunnable == null)
                    mRunnable = new MyRunnable();
                mHandler.postDelayed(mRunnable, 0);

                startUpLocation();

                hiddenAnim1.start();
                apperaAnim2.start();
                hiddenAnim3.start();
                break;
            default:
                break;
        }
    }

    private void saveRecord() {

        showLoadingView(false);
        showToast("????????????????????????!");

        try {
            SportMotionRecord sportMotionRecord = new SportMotionRecord();

            List<LatLng> locations = record.getPathline();
            LatLng firstLocaiton = locations.get(0);
            LatLng lastLocaiton = locations.get(locations.size() - 1);

            sportMotionRecord.setId(System.currentTimeMillis());
            sportMotionRecord.setMaster(Integer.parseInt(SPUtils.getInstance().getString(MySp.USERID, "0")));
            sportMotionRecord.setDistance(distance);
            sportMotionRecord.setDuration(seconds);
            sportMotionRecord.setmStartTime(mStartTime);
            sportMotionRecord.setmEndTime(mEndTime);
            sportMotionRecord.setStratPoint(MotionUtils.amapLocationToString(firstLocaiton));
            sportMotionRecord.setEndPoint(MotionUtils.amapLocationToString(lastLocaiton));
            sportMotionRecord.setPathLine(MotionUtils.getLatLngPathLineString(locations));
            double sportMile = distance / 1000d;
            //????????????120???
            sportMotionRecord.setCalorie(MotionUtils.calculationCalorie(60, sportMile));
            sportMotionRecord.setSpeed(sportMile / ((double) seconds / 3600));
            sportMotionRecord.setDistribution(record.getDistribution());
            sportMotionRecord.setDateTag(DateUtils.getStringDateShort(mEndTime));

//                record.setId(sportMotionRecord.getId());
//                record.setDistance(sportMotionRecord.getDistance());
//                record.setDuration(sportMotionRecord.getDuration());
//                record.setStartTime(sportMotionRecord.getMStartTime());
//                record.setEndTime(sportMotionRecord.getMEndTime());
//                record.setStartpoint(firstLocaiton);
//                record.setEndpoint(lastLocaiton);
//                record.setCalorie(sportMotionRecord.getCalorie());
//                record.setSpeed(sportMotionRecord.getSpeed());
//                record.setDistribution(sportMotionRecord.getDistribution());
//                record.setDateTag(sportMotionRecord.getDateTag());

            dataManager.insertSportRecord(sportMotionRecord);
        } catch (Exception e) {
            LogUtils.e("????????????????????????", e);
        }

        mHandler.postDelayed(() -> {
            dismissLoadingView();
            setResult(RESULT_OK);

            SportResultActivity.StartActivity(this, mStartTime, mEndTime);

            finish();
        }, 1500);

    }

    /**
     * ????????????amap?????????
     */
    private void setUpMap() {
        aMap.setLocationSource(locationSource);// ??????????????????
        // ??????????????????????????????
        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
//                .fromResource(R.drawable.mylocation_point));// ????????????????????????
        myLocationStyle.strokeColor(Color.TRANSPARENT);// ???????????????????????????
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// ???????????????????????????
        // myLocationStyle.anchor(int,int)//????????????????????????
        // ???????????????????????????????????? ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
//        myLocationStyle.interval(interval);//???????????????????????????????????????
//        myLocationStyle.showMyLocation(true);//????????????????????????????????????true ?????????false?????????
        myLocationStyle.strokeWidth(1.0f);// ???????????????????????????
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// ????????????????????????????????????
        aMap.getUiSettings().setZoomControlsEnabled(false);// ????????????????????????????????????
        aMap.getUiSettings().setCompassEnabled(false);// ?????????????????????????????????
        aMap.setMyLocationEnabled(true);// ?????????true??????????????????????????????????????????false??????????????????????????????????????????????????????false
    }

    private LocationSource locationSource = new LocationSource() {
        @Override
        public void activate(OnLocationChangedListener onLocationChangedListener) {
            mListener = onLocationChangedListener;
            startLocation();
        }

        @Override
        public void deactivate() {
            mListener = null;
            if (mLocationClient != null) {
                mLocationClient.stopLocation();
                mLocationClient.onDestroy();
            }
            mLocationClient = null;
        }
    };

    /**
     * ??????????????????
     *
     * @param aMapLocation ???????????????
     */
    private AMapLocationListener aMapLocationListener = aMapLocation -> {
        if (null == aMapLocation)
            return;
        if (aMapLocation.getErrorCode() == 0) {
            //???????????????????????????????????????????????????List???
            LogUtils.d("???????????????" + aMapLocation.getLatitude() + "\n???????????????" + aMapLocation.getLongitude());
//            LatLng locationValue = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

            //????????????
            updateLocation(aMapLocation);

        } else {
            String errText = "????????????," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
            LogUtils.e("AmapErr", errText);
        }
    };

    private void updateLocation(AMapLocation aMapLocation) {
        //????????????
//        if (mOriginList != null && mOriginList.size() > 0) {
//            mOriginPolyline = aMap.addPolyline(new PolylineOptions().addAll(mOriginList).color(Color.GREEN));
//            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(mOriginList), 200));
//        }


        record.addpoint(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));

        //????????????
        distance = getDistance(record.getPathline());

        double sportMile = distance / 1000d;
        //??????????????????0.2?????????????????????
        if (seconds > 0 && sportMile > 0.2) {
            double distribution = (double) seconds / 60d / sportMile;
            record.setDistribution(distribution);
            tvSpeed.setText(decimalFormat.format(distribution));
            tvMileage.setText(decimalFormat.format(sportMile));
        } else {
            record.setDistribution(0d);
            tvSpeed.setText(String.valueOf("0.00"));
            tvMileage.setText(String.valueOf("0.00"));
        }

        mSportLatLngs.clear();
        //??????????????????
        mSportLatLngs = new ArrayList<>(mpathSmoothTool.pathOptimize(record.getPathline()));
        //??????
//        mSportLatLngs = new ArrayList<>(mpathSmoothTool.reducerVerticalThreshold(MotionUtils.parseLatLngList(record.getPathline())));
        //????????????
//        mSportLatLngs = new ArrayList<>(MotionUtils.parseLatLngList(record.getPathline()));

        if (!mSportLatLngs.isEmpty()) {
            polylineOptions.add(mSportLatLngs.get(mSportLatLngs.size() - 1));
            if (mListener != null)
                mListener.onLocationChanged(aMapLocation);// ?????????????????????
//            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getBounds(mSportLatLngs), 18));
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()), 18));
        }
        mOriginPolyline = aMap.addPolyline(polylineOptions);
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();

        // ????????????????????????
        if (tvNumberAnim != null) tvNumberAnim.clearAnimation();
        if (tv1 != null) tv1.clearAnimation();
        if (tv2 != null) tv2.clearAnimation();
        if (tv3 != null) tv3.clearAnimation();

        if (null != mRunnable) {
            mHandler.removeCallbacks(mRunnable);
            mRunnable = null;
        }

        unBindService();

        if (null != dataManager)
            dataManager.closeRealm();

        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            setApperaAnimationView();
            setHiddenAnimationView();
        }
        super.onWindowFocusChanged(hasFocus);
    }

    /**
     * ????????????
     */
    public void setApperaAnimationView() {

        apperaAnim1 = ValueAnimator.ofFloat(tv1.getHeight() * 2, 0);
        apperaAnim1.setDuration(500);
        apperaAnim1.setTarget(tv1);
        apperaAnim1.addUpdateListener(animation -> tv1.setTranslationY((Float) animation.getAnimatedValue()));
        apperaAnim1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                tv1.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tv1.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                tv1.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        apperaAnim2 = ValueAnimator.ofFloat(tv2.getHeight() * 2, 0);
        apperaAnim2.setDuration(500);
        apperaAnim2.setTarget(tv2);
        apperaAnim2.addUpdateListener(animation -> tv2.setTranslationY((Float) animation.getAnimatedValue()));
        apperaAnim2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                tv2.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tv2.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                tv2.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        apperaAnim3 = ValueAnimator.ofFloat(tv3.getHeight() * 2, 0);
        apperaAnim3.setDuration(500);
        apperaAnim3.setTarget(tv3);
        apperaAnim3.addUpdateListener(animation -> tv3.setTranslationY((Float) animation.getAnimatedValue()));
        apperaAnim3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                tv3.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tv3.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                tv3.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * ????????????
     */
    public void setHiddenAnimationView() {

        hiddenAnim1 = ValueAnimator.ofFloat(0, tv1.getHeight() * 2);
        hiddenAnim1.setDuration(500);
        hiddenAnim1.setTarget(tv1);
        hiddenAnim1.addUpdateListener(animation -> tv1.setTranslationY((Float) animation.getAnimatedValue()));
        hiddenAnim1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                tv1.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tv1.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                tv1.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        hiddenAnim2 = ValueAnimator.ofFloat(0, tv2.getHeight() * 2);
        hiddenAnim2.setDuration(500);
        hiddenAnim2.setTarget(tv2);
        hiddenAnim2.addUpdateListener(animation -> tv2.setTranslationY((Float) animation.getAnimatedValue()));
        hiddenAnim2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                tv2.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tv2.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                tv2.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        hiddenAnim3 = ValueAnimator.ofFloat(0, tv3.getHeight() * 2);
        hiddenAnim3.setDuration(500);
        hiddenAnim3.setTarget(tv3);
        hiddenAnim3.addUpdateListener(animation -> tv3.setTranslationY((Float) animation.getAnimatedValue()));
        hiddenAnim3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                tv3.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tv3.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                tv3.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public String formatseconds() {
        String hh = seconds / 3600 > 9 ? seconds / 3600 + "" : "0" + seconds
                / 3600;
        String mm = (seconds % 3600) / 60 > 9 ? (seconds % 3600) / 60 + ""
                : "0" + (seconds % 3600) / 60;
        String ss = (seconds % 3600) % 60 > 9 ? (seconds % 3600) % 60 + ""
                : "0" + (seconds % 3600) % 60;

        seconds++;

        return hh + ":" + mm + ":" + ss;
    }

    private LatLngBounds getBounds(List<LatLng> pointlist) {
        LatLngBounds.Builder b = LatLngBounds.builder();
        if (pointlist == null) {
            return b.build();
        }
        for (LatLng latLng : pointlist) {
            b.include(latLng);
        }
        return b.build();

    }

    //????????????
    private float getDistance(List<LatLng> list) {
        float distance = 0;
        if (list == null || list.size() == 0) {
            return distance;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            LatLng firstLatLng = list.get(i);
            LatLng secondLatLng = list.get(i + 1);
            double betweenDis = AMapUtils.calculateLineDistance(firstLatLng,
                    secondLatLng);
            distance = (float) (distance + betweenDis);
        }
        return distance;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) { // ?????????????????? ????????????
                //??????????????????????????????
                if (ISSTARTUP) {
                    showToast("??????????????????????????????????????????!");
                    return true;

                }
                //?????????????????????
                if (null != record && null != record.getPathline() && !record.getPathline().isEmpty()) {
                    showTipDialog("?????????????",
                            "?????????????????????????????????,????????????????????????,???????????????!",
                            new TipCallBack() {
                                @Override
                                public void confirm() {
                                    finish();
                                }

                                @Override
                                public void cancle() {

                                }
                            });
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        //??????????????????????????????
        if (ISSTARTUP) {
            showToast("?????????????????????????????????????????????!");
            return;
        }
        //?????????????????????
        if (null != record && null != record.getPathline() && !record.getPathline().isEmpty()) {
            showTipDialog("?????????????",
                    "?????????????????????????????????,????????????????????????,???????????????!",
                    new TipCallBack() {
                        @Override
                        public void confirm() {
                            finish();
                        }

                        @Override
                        public void cancle() {

                        }
                    });
            return;
        }
        super.onBackPressed();
    }

    private void showTipDialog(String title, String tips, TipCallBack tipCallBack) {
        tipDialog = new Dialog(context, R.style.matchDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.tip_dialog_layout, null);
        ((TextView) (view.findViewById(R.id.title))).setText(title);
        ((TextView) (view.findViewById(R.id.tips))).setText(tips);
        view.findViewById(R.id.cancelTV).setOnClickListener(
                v -> {
                    tipCallBack.cancle();
                    tipDialog.dismiss();
                });
        view.findViewById(R.id.confirmTV).setOnClickListener(v -> {
            tipCallBack.confirm();
            tipDialog.dismiss();
        });
        tipDialog.setContentView(view);
        tipDialog.show();
    }

    private interface TipCallBack {
        void confirm();

        void cancle();
    }
}
