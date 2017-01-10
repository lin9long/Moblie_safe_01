package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.domain.Appinfo;
import com.example.lin.moblie_safe_01.engine.AppInfoProvider;
import com.example.lin.moblie_safe_01.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/30.
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {

    private TextView tv_memory;
    private TextView tv_sd_memory;
    private List<Appinfo> mAppinfolist;
    private ListView lv_app_list;
    private List<Appinfo> mSystemlist;
    private List<Appinfo> mCustomerlist;
    private TextView tv_title;
    private Appinfo mAppinfo;
    private TextView tv_share;
    private TextView tv_uninstall;
    private TextView tv_start;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Myadapter myadapter = new Myadapter();
            lv_app_list.setAdapter(myadapter);
//            if (tv_title != null && mCustomerlist != null) {
//                tv_title.setText("用户应用（" + mCustomerlist.size() + ")");
//            }
        }
    };
    private PopupWindow mPopupWindow;


    /**
     * @param v 处理popupwindow里面textview的点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_uninstall:
                //如果当前应用为系统应用时，不执行卸载
                if (mAppinfo.isSystem) {
                    ToastUtil.show(getApplicationContext(), "当前应用为系统应用,不能卸载！");
                } else {
                    //卸载应用的四步操作
                    Intent intent = new Intent(Intent.ACTION_DELETE);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + mAppinfo.getPackageName()));
                    startActivity(intent);
                }
                break;
            //启用一个应用
            case R.id.tv_start:
                PackageManager pm = getPackageManager();
                Intent launchIntentForPackage = pm.getLaunchIntentForPackage(mAppinfo.getPackageName());
                if (launchIntentForPackage != null) {
                    startActivity(launchIntentForPackage);
                } else {
                    ToastUtil.show(getApplicationContext(), "此应用不能开启！");
                }
                break;
            //分享一个应用名称（短信）
            case R.id.tv_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "大家好，我分享了一个应用，应用名称为：" + mAppinfo.getAppName());
                intent.setType("text/plain");
                startActivity(intent);
                break;
        }
        //处理完成点击事件后，将PopupWindow的窗体关闭
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    /**
     * 同时加载两个集合的方法
     * 1.数量为两个集合的数目总和
     * 2.制定索引指向条目的显示类型,一共有两种类型，标题条目、系统条目
     */
    class Myadapter extends BaseAdapter {

        //告知adapter，需要增加两个条目，此方法获取listview内显示条目的总数
        @Override
        public int getViewTypeCount() {
            return mCustomerlist.size() + mSystemlist.size() + 2;
        }

        //制定索引指向条目的显示类型,当条目在顶部
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomerlist.size() + 1) {
                //标题条目
                return 0;
            } else {
                //系统条目
                return 1;
            }
        }

        @Override
        public int getCount() {
            return mCustomerlist.size() + mSystemlist.size();
        }

        //1.当前位置小于用户应用时，返回用户的列表
        //2.当前位置大于用户应用时，返回系统的列表(当前位置需要减去前面用户列表占用的条目)

        @Override
        public Appinfo getItem(int position) {
            //获取条目需要进行条件判断
            // 1.当下述位置时，不需要填充条目，因为这是自定义显示条目，返回null
            if (position == 0 || position == mCustomerlist.size() + 1) {
                return null;
            } else {
                //2.因为当前条目总数增加2条，但显示条目未改变，所以应用对应索引值要减1
                if (position < mCustomerlist.size() + 1) {
                    return mCustomerlist.get(position - 1);
                } else {
                    return mSystemlist.get(position - mCustomerlist.size() - 2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //判断当前显示条目的标志位，如果type为0则为标题条目
            int type = getItemViewType(position);
            if (type == 0) {
                ViewTitleHolder viewTitleHolder = null;
                if (convertView == null) {
                    //需要重新inflate一个布局文件
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_appinfo_item_title, null);
                    viewTitleHolder = new ViewTitleHolder();
                    viewTitleHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    convertView.setTag(viewTitleHolder);
                } else {
                    viewTitleHolder = (ViewTitleHolder) convertView.getTag();
                }
                //type = 0的条目位置只有两处，所以当position == 0设置为用户应用条目后，剩余一处为系统应用条目
                if (position == 0) {
                    viewTitleHolder.tv_title.setText("用户应用（" + mCustomerlist.size() + ")");
                } else {
                    viewTitleHolder.tv_title.setText("系统应用（" + mSystemlist.size() + ")");
                }

                //判断当前显示条目的标志位，如果type为1则为app应用显示条目
            } else {
                ViewHolder viewHolder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_appinfo_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                    viewHolder.tv_path = (TextView) convertView.findViewById(R.id.tv_path);
                    viewHolder.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                //此处显示文字，应该获取当前位置条目所包含的信息，使用getItem(position)方法，同时在前面返回集合
                viewHolder.tv_app_name.setText(getItem(position).appName);
                viewHolder.iv_app_icon.setBackgroundDrawable(getItem(position).icon);
                if (getItem(position).isSdcard) {
                    viewHolder.tv_path.setText("SD卡应用");
                } else {
                    viewHolder.tv_path.setText("手机应用");
                }
            }
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_app_name;
        TextView tv_path;
        ImageView iv_app_icon;
    }

    static class ViewTitleHolder {
        TextView tv_title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initUi();
        initTitle();
        initData();
        //     System.out.println("mCustomerlist:"+mCustomerlist.size());
    }

    /**
     * 为了使应用列表可以刷新，当AppManagerActivity重新获取焦点时，需要刷新界面
     */
    @Override
    protected void onResume() {
        new Thread() {
            @Override
            public void run() {
                mAppinfolist = AppInfoProvider.getAppInfoList(getApplicationContext());
                mCustomerlist = new ArrayList<Appinfo>();
                mSystemlist = new ArrayList<Appinfo>();
                for (Appinfo appinfo : mAppinfolist) {
                    if (appinfo.isSystem) {
                        mSystemlist.add(appinfo);
                    } else {
                        mCustomerlist.add(appinfo);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
        super.onResume();
    }

    /**
     * 因为获取系统应用为耗时操作，添加到线程内执行
     */
    private void initData() {

        lv_app_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            //滚动过程中实现的方法
            // 1.firstVisibleItem第一个可见条目
            // 2.visibleItemCount当前一个屏幕可见条目总数
            // 3.totalItemCount总条目数量

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //此处必须要进行非空判断，否则会出现空指针报错
                if (mCustomerlist != null && mSystemlist != null) {
                    if (firstVisibleItem >= mCustomerlist.size() + 1) {
                        tv_title.setText("系统应用（" + mSystemlist.size() + ")");
                    } else {
                        tv_title.setText("用户应用（" + mCustomerlist.size() + ")");
                    }
                }
            }
        });
        //注册lv_app_list条目的点击事件，并进行判断，当点击标题时候不调用showPopupWindow的方法
        lv_app_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mCustomerlist.size() + 1) {
                    return;
                } else {
                    if (position < mCustomerlist.size() + 1) {
                        mAppinfo = mCustomerlist.get(position - 1);
                    } else {
                        mAppinfo = mSystemlist.get(position - mCustomerlist.size() - 2);
                    }
                    showPopupWindow(view);
                }
            }
        });
    }

    private void showPopupWindow(View view) {
        View popupView = View.inflate(this, R.layout.popupwindow_layout, null);

        tv_share = (TextView) popupView.findViewById(R.id.tv_share);
        tv_uninstall = (TextView) popupView.findViewById(R.id.tv_uninstall);
        tv_start = (TextView) popupView.findViewById(R.id.tv_start);

        tv_share.setOnClickListener(this);
        tv_uninstall.setOnClickListener(this);
        tv_start.setOnClickListener(this);
        //设置透明度渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(600);
        //设置缩放动画动画（后两个参数为缩放的起始x，y轴）
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(600);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);

        //设置PopupWindow的参数
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        //必须要设置背景，物理回退按键才能生效
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.showAsDropDown(view, view.getWidth() / 2, -view.getHeight());
        //PopupWindow无加载窗体动画api，所以将动画加载到popupView上
        popupView.startAnimation(animationSet);

    }

    private void initUi() {
        tv_memory = (TextView) findViewById(R.id.tv_memory);
        tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);
        tv_title = (TextView) findViewById(R.id.tv_title);
        lv_app_list = (ListView) findViewById(R.id.lv_app_list);
        mAppinfolist = new ArrayList<>();
    }

    /**
     * 初始化剩余手机、sd卡内存显示
     */
    private void initTitle() {
        //获取手机内存路径
        String path = Environment.getDataDirectory().getAbsolutePath();
        //获取SD卡的绝对路径
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //使用格式化工具，讲long型数据转换成空间大小
        String availSpace = Formatter.formatFileSize(this, getAvailSpace(path));

        String sdAvailSpace = Formatter.formatFileSize(this, getAvailSpace(sdPath));
        tv_memory.setText("磁盘可用空间：" + availSpace);
        tv_sd_memory.setText("SD卡可用空间：" + sdAvailSpace);

    }

    private long getAvailSpace(String path) {
        //系统方法获取内存大小
        StatFs statfs = new StatFs(path);
        //获取当前路径的块大小
        long size = statfs.getAvailableBlocksLong();
        //获取当前路径的块数量
        long count = statfs.getBlockCountLong();
        //当前内存空间即为块大小*快数量
        return size * count;
        //注意：此处不能返回一个int类型的数据，因为int最大值为（2147483647），最大只能表示2Gb的内存
    }


}
