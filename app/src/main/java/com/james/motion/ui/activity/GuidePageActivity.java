package com.james.motion.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnPageChangeListener;
import com.james.motion.R;
import com.james.motion.add.activity.Denglu;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * author : zlf
 * date   : 2018/11/26
 * blog   :https://www.jianshu.com/u/281e9668a5a6
 */
public class GuidePageActivity extends AppCompatActivity {

    Unbinder unbinder;
    @BindView(R.id.cb_test)
    ConvenientBanner cbTest;
    @BindView(R.id.btn_test)
    Button btnTest;

    private ArrayList<Integer> arrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_page);
        unbinder = ButterKnife.bind(this);
        initView();
        initGuidePage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();

    }

    private void initView() {
        arrayList = new ArrayList<>();
        arrayList.add(R.mipmap.q);
        arrayList.add(R.mipmap.w);
        arrayList.add(R.mipmap.e);
        arrayList.add(R.mipmap.r);
        arrayList.add(R.mipmap.t);

    }

    private void initGuidePage() {
        cbTest.setPages(new CBViewHolderCreator() {
            @Override
            public Holder createHolder(View itemView) {
                return new LocalImageHolderView(itemView);
            }

            @Override
            public int getLayoutId() {
                //????????????????????????
                return R.layout.item_guide_page;
            }

        }, arrayList)
                .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setPointViewVisible(true)
                .setCanLoop(false)
                .setOnPageChangeListener(new OnPageChangeListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    }

                    @Override
                    public void onPageSelected(int index) {
                        //????????????????????????????????????index???2??????????????????????????????????????????????????????????????????????????????????????????
                        if (index == 4) {
                            btnTest.setVisibility(View.VISIBLE);
                            cbTest.setPointViewVisible(false);
                        } else {
                            btnTest.setVisibility(View.GONE);
                            cbTest.setPointViewVisible(true);

                        }
                    }
                });
    }

    @OnClick(R.id.btn_test)
    public void onViewClicked() {
        //????????????activity
        Intent intent = new Intent(GuidePageActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * ?????????2 ?????????holder
     */
    public class LocalImageHolderView extends Holder<Integer> {
        private ImageView mImageView;

        //?????????
        public LocalImageHolderView(View itemView) {
            super(itemView);
        }

        @Override
        protected void initView(View itemView) {
            mImageView = itemView.findViewById(R.id.iv_guide_page);
            mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        @Override
        public void updateUI(Integer data) {
            mImageView.setImageResource(data);
        }
    }
}
