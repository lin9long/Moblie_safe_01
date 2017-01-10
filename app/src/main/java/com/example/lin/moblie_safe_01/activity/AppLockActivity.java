package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.domain.Appinfo;
import com.example.lin.moblie_safe_01.engine.AppInfoProvider;
import com.example.lin.moblie_safe_01.engine.AppLockDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/4.
 */
public class AppLockActivity extends Activity {

    private Button btn_lock;
    private Button btn_unlock;
    private TextView tv_lock_app;
    private TextView tv_unlock_app;
    private ListView lv_lock;
    private ListView lv_unlock;
    private LinearLayout ll_lock;
    private LinearLayout ll_unlock;
    private List<Appinfo> mAppinfoList;
    private List<Appinfo> mLockappList;
    private List<Appinfo> mUnLockappList;
    private AppLockDao mDao;
    private Myadapter mUnLockadapter;
    private Myadapter mLockadapter;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //传入构造方法的标志位，分别获取两个adapter，给不同的listview显示条目
            mLockadapter = new Myadapter(true);
            lv_lock.setAdapter(mLockadapter);
            mUnLockadapter = new Myadapter(false);
            lv_unlock.setAdapter(mUnLockadapter);
        }
    };
    private TranslateAnimation mTtranslateanimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        initUi();
        initData();
        initAnimation();
    }

    private void initAnimation() {
        mTtranslateanimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1
                , Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        mTtranslateanimation.setDuration(500);
    }

    /**
     * 获取加锁及未加锁应用列表，放到线程内执行
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                mLockappList = new ArrayList<>();
                mUnLockappList = new ArrayList<>();
                mAppinfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mDao = AppLockDao.getInstance(getApplicationContext());
                //将加锁程序包名获取后，与所有程序的包名进行对比，如果相同则为加锁程序
                List<String> lockPackageList = mDao.findAll();
                for (Appinfo appinfo : mAppinfoList) {
                    if (lockPackageList.contains(appinfo.packageName)) {
                        mLockappList.add(appinfo);
                    } else {
                        mUnLockappList.add(appinfo);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    private void initUi() {
        btn_lock = (Button) findViewById(R.id.btn_lock);
        btn_unlock = (Button) findViewById(R.id.btn_unlock);

        tv_lock_app = (TextView) findViewById(R.id.tv_lock_app);
        tv_unlock_app = (TextView) findViewById(R.id.tv_unlock_app);

        lv_lock = (ListView) findViewById(R.id.lv_lock);
        lv_unlock = (ListView) findViewById(R.id.lv_unlock);

        ll_lock = (LinearLayout) findViewById(R.id.ll_lock);
        ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
        //设置两个按钮的点击事件，切换显示不同的listview，对listview所在LinearLayout设置可见/不可见属性
        btn_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_unlock.setVisibility(View.GONE);
                ll_lock.setVisibility(View.VISIBLE);

                btn_lock.setBackgroundResource(R.drawable.tab_right_pressed);
                btn_unlock.setBackgroundResource(R.drawable.tab_left_default);
            }
        });
        btn_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_unlock.setVisibility(View.VISIBLE);
                ll_lock.setVisibility(View.GONE);

                btn_lock.setBackgroundResource(R.drawable.tab_right_default);
                btn_unlock.setBackgroundResource(R.drawable.tab_left_pressed);

            }
        });
    }

    class Myadapter extends BaseAdapter {
        //添加一个标志位，判断现在显示的是加锁还是未加锁列表
        private boolean islock;

        public Myadapter(boolean islock) {
            this.islock = islock;
        }


        @Override
        public int getCount() {
            //如果是上锁的程序，则返回上锁列表内的条目
            if (islock) {
                tv_lock_app.setText("已加锁程序总数" + mLockappList.size());
                return mLockappList.size();
            } else {
                tv_unlock_app.setText("未加锁程序总数" + mUnLockappList.size());
                return mUnLockappList.size();
            }
        }

        @Override
        public Appinfo getItem(int position) {
            if (islock) {
                return mLockappList.get(position);
            } else {
                return mUnLockappList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //使用viewHolder复用布局
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(getApplicationContext(), R.layout.listview_applock_item, null);
                //注意此处要在convertView中findViewById
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.iv_lock = (ImageView) convertView.findViewById(R.id.iv_lock);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Appinfo appinfo = getItem(position);
            viewHolder.tv_name.setText(appinfo.appName);
            viewHolder.iv_icon.setBackgroundDrawable(appinfo.icon);
            //注册点击锁定图片的点击事件，执行动画
            final View animationView = convertView;

            //如果标志位为ture，显示锁定图标，false显示未锁定图标
            if (islock) {
                viewHolder.iv_lock.setBackgroundResource(R.drawable.setup_lock);
            } else {
                viewHolder.iv_lock.setBackgroundResource(R.drawable.setup_unlock);
            }
            viewHolder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //设置convertView的动画事件
                    animationView.startAnimation(mTtranslateanimation);
                    //要在动画执行后进行添加删除操作，需要注册动画的监听事件
                    mTtranslateanimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        //动画执行完成后，再进行添加删除操作
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (islock) {
                                //1.锁定列表内条目删除一条
                                mLockappList.remove(appinfo);
                                //2.未锁定列表内条目增加一条
                                mUnLockappList.add(appinfo);
                                //3.数据库内删除一条数据
                                mDao.delete(appinfo.packageName);
                                //4.通知数据适配器更新界面
                                mLockadapter.notifyDataSetChanged();
                            } else {
                                //2.锁定列表内条目增加一条
                                mLockappList.add(appinfo);
                                //1.未锁定列表内条目删除一条
                                mUnLockappList.remove(appinfo);
                                //3.数据库内增加一条数据
                                mDao.insert(appinfo.packageName);
                                //4.通知数据适配器更新界面
                                mUnLockadapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        ImageView iv_lock;
        TextView tv_name;
    }
}
