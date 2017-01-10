package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.domain.Processinfo;
import com.example.lin.moblie_safe_01.engine.ProcessInfoProvider;
import com.example.lin.moblie_safe_01.util.ContastValue;
import com.example.lin.moblie_safe_01.util.SpUtil;
import com.example.lin.moblie_safe_01.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/31.
 */
public class ProcessManagerActivity extends Activity implements View.OnClickListener {

    private TextView tv_process_count;
    private Button bt_select_all;
    private TextView tv_memory_info;
    private Button bt_select_reverse;
    private Button bt_clear;
    private Button bt_setting;
    private List<Processinfo> mAppinfolist;
    private List<Processinfo> mCustomerlist;
    private List<Processinfo> mSystemlist;
    private ListView lv_process_list;
    private TextView tv_title;
    private Processinfo mProcessinfo;
    private Myadapter myadapter;
    private int mProcessCount;
    private long mAvailSpace;
    private String mStrtotalSpace;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            myadapter = new Myadapter();
            lv_process_list.setAdapter(myadapter);
            if (tv_title != null && mCustomerlist != null) {
                tv_title.setText("用户应用（" + mCustomerlist.size() + ")");
            }
        }
    };


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
            //如果进程管理设置内选中隐藏系统进程，则对返回条目总数进行判断
            boolean ischeck = SpUtil.getBoolean(getApplicationContext(), ContastValue.SHOW_SYS_PROCESS, false);
            if (ischeck) {
                return mCustomerlist.size() + mSystemlist.size() + 2;
            } else {
                return mCustomerlist.size() + 1;
            }
        }

        //1.当前位置小于用户应用时，返回用户的列表
        //2.当前位置大于用户应用时，返回系统的列表(当前位置需要减去前面用户列表占用的条目)

        @Override
        public Processinfo getItem(int position) {
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
                AppManagerActivity.ViewTitleHolder viewTitleHolder = null;
                if (convertView == null) {
                    //需要重新inflate一个布局文件
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_appinfo_item_title, null);
                    viewTitleHolder = new AppManagerActivity.ViewTitleHolder();
                    viewTitleHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    convertView.setTag(viewTitleHolder);
                } else {
                    viewTitleHolder = (AppManagerActivity.ViewTitleHolder) convertView.getTag();
                }
                //type = 0的条目位置只有两处，所以当position == 0设置为用户应用条目后，剩余一处为系统应用条目
                if (position == 0) {
                    viewTitleHolder.tv_title.setText("用户进程（" + mCustomerlist.size() + ")");
                } else {
                    viewTitleHolder.tv_title.setText("系统进程（" + mSystemlist.size() + ")");
                }

                //判断当前显示条目的标志位，如果type为1则为app应用显示条目
            } else {
                ViewHolder viewHolder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_processinfo_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                    viewHolder.tv_memory_info = (TextView) convertView.findViewById(R.id.tv_memory_info);
                    viewHolder.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
                    viewHolder.cb_box = (CheckBox) convertView.findViewById(R.id.cb_box);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                //此处显示文字，应该获取当前位置条目所包含的信息，使用getItem(position)方法，同时在前面返回集合
                viewHolder.tv_app_name.setText(getItem(position).name);
                viewHolder.iv_app_icon.setBackgroundDrawable(getItem(position).icon);
                String strsize = Formatter.formatFileSize(getApplicationContext(), getItem(position).getMemsize());
                viewHolder.tv_memory_info.setText(strsize);
                //隐藏mobile_safe的check控件显示
                if (getItem(position).getPackagename().equals(getPackageName())) {
                    viewHolder.cb_box.setVisibility(View.GONE);
                } else {
                    viewHolder.cb_box.setVisibility(View.VISIBLE);
                }
                viewHolder.cb_box.setChecked(getItem(position).ischeck);
            }
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_app_name;
        TextView tv_memory_info;
        ImageView iv_app_icon;
        CheckBox cb_box;
    }

    static class ViewTitleHolder {
        TextView tv_title;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        initUi();
        initTitle();
        initListData();
        initData();
    }

    private void initData() {
        lv_process_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mCustomerlist != null && mSystemlist != null) {
                    if (firstVisibleItem > mCustomerlist.size() + 1) {
                        tv_title.setText("系统进程（" + mSystemlist.size() + ")");
                    } else {
                        tv_title.setText("用户进程（" + mCustomerlist.size() + ")");
                    }
                }
            }
        });
        //设置list的点击事件，此处要注意将XML中clickable的属性设置成false，否则无法点击listview条目
        lv_process_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击标题是直接返回，不做处理
                if (position == 0 || position == mCustomerlist.size() + 1) {
                    return;
                } else {
                    if (position < mCustomerlist.size() + 1) {
                        mProcessinfo = mCustomerlist.get(position - 1);
                    } else {
                        mProcessinfo = mSystemlist.get(position - mCustomerlist.size() - 2);
                    }
                    if (mProcessinfo != null) {
                        if (!mProcessinfo.getPackagename().equals(getPackageName())) {
                            mProcessinfo.ischeck = !mProcessinfo.ischeck;
                            CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_box);
                            cb_box.setChecked(mProcessinfo.ischeck);
                        }
                    }
                }
            }
        });
    }

    private void initListData() {
        new Thread() {
            @Override
            public void run() {
                mAppinfolist = ProcessInfoProvider.getProcessInfo(getApplicationContext());
                mCustomerlist = new ArrayList<Processinfo>();
                mSystemlist = new ArrayList<Processinfo>();
                for (Processinfo appinfo : mAppinfolist) {
                    if (appinfo.isSystem) {
                        mSystemlist.add(appinfo);
                    } else {
                        mCustomerlist.add(appinfo);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initTitle() {
        mProcessCount = ProcessInfoProvider.getProcessCount(getApplicationContext());
        tv_process_count.setText("进程总数：" + mProcessCount);
        //       System.out.println("================="+processCount);
        mAvailSpace = ProcessInfoProvider.getAvailSpace(this);
        String stravailSpace = Formatter.formatFileSize(this, mAvailSpace);
        long totalSpace = ProcessInfoProvider.getTotalSpace();
        mStrtotalSpace = Formatter.formatFileSize(this, totalSpace);
        tv_memory_info.setText("剩余/总共：" + stravailSpace + "/" + mStrtotalSpace);
    }

    private void initUi() {
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);
        bt_select_all = (Button) findViewById(R.id.bt_select_all);
        bt_select_reverse = (Button) findViewById(R.id.bt_select_reverse);
        bt_clear = (Button) findViewById(R.id.bt_clear);
        bt_setting = (Button) findViewById(R.id.bt_setting);
        lv_process_list = (ListView) findViewById(R.id.lv_process_list);

        bt_clear.setOnClickListener(this);
        bt_select_all.setOnClickListener(this);
        bt_select_reverse.setOnClickListener(this);
        bt_setting.setOnClickListener(this);

    }

    //响应点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //全部选中list条目
            case R.id.bt_select_all:
                selectAll();
                break;
            //反选list条目
            case R.id.bt_select_reverse:
                reverseAll();
                break;
            //清理选中条目，释放内存
            case R.id.bt_clear:
                killProcess();
                break;
            case R.id.bt_setting:
                //使用startActivityForResult的方法，界面时更新adapter的值
                startActivityForResult(new Intent(ProcessManagerActivity.this, ProcessSettingActivity.class), 0);
                break;
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        if (myadapter != null) {
            myadapter.notifyDataSetChanged();
        }
        super.startActivityForResult(intent, requestCode, options);
    }

    //释放内存
    private void killProcess() {
        //新建一个list，存放选中的条目
        List<Processinfo> killProcessList = new ArrayList<>();
        //循环遍历讲chcekbox选中的应用加入到一个新建的集合中（因为不能再for循环中直接删除条目，需要在另外的循环删除）
        for (Processinfo info : mCustomerlist) {
            if (info.getPackagename().equals(getPackageName())) {
                continue;
            }
            if (info.ischeck) {
                killProcessList.add(info);
            }
        }
        for (Processinfo info : mSystemlist) {
            if (info.ischeck) {
                killProcessList.add(info);
            }
        }
        long totalReleaseSpace = 0;
        //循环遍历新建集合，删除对应在系统或用户列表中显示的内容
        for (Processinfo processinfo : killProcessList) {
            if (processinfo.getPackagename().equals(getPackageName())) {
                continue;
            }
            //如果用户列表包含相关信息，则移除
            if (mCustomerlist.contains(processinfo)) {
                mCustomerlist.remove(processinfo);
            }

            if (mSystemlist.contains(processinfo)) {
                mSystemlist.remove(processinfo);
            }
            ProcessInfoProvider.killProcess(this, processinfo);
            totalReleaseSpace += processinfo.memsize;
        }
        if (myadapter != null) {
            myadapter.notifyDataSetChanged();
        }
        //9.获取剩余进程数量,设置控件
        mProcessCount -= killProcessList.size();
        tv_process_count.setText("进程总数：" + mProcessCount);
        //10.计算剩余内存（原剩余内存加上释放总内存）
        mAvailSpace += totalReleaseSpace;
        String stravail = Formatter.formatFileSize(this, mAvailSpace);
        tv_memory_info.setText("剩余/总共" + stravail + "/" + mStrtotalSpace);
        //11.弹出土司，告诉用户系统清理情况
        String strRelease = Formatter.formatFileSize(this, totalReleaseSpace);
        String toast = String.format("关闭了%d个进程，释放了%s运行内存", killProcessList.size(), strRelease);
        ToastUtil.show(this, toast);

    }

    /**
     *
     */
    //反选操作，将当前选中的状态取反后赋值给原来的控件
    private void reverseAll() {
        for (Processinfo info : mCustomerlist) {
            if (info.getPackagename().equals(getPackageName())) {
                continue;
            }
            info.ischeck = !info.ischeck;
        }
        for (Processinfo info : mSystemlist) {
            info.ischeck = !info.ischeck;
        }
        if (myadapter != null) {
            myadapter.notifyDataSetChanged();
        }
    }

    //全选所有条目，遍历到当前moblie_safe应用时，跳出本次循环，执行下一次循环
    private void selectAll() {
        for (Processinfo info : mCustomerlist) {
            if (info.getPackagename().equals(getPackageName())) {
                continue;
            }
            info.ischeck = true;
        }
        for (Processinfo info : mSystemlist) {
            info.ischeck = true;
        }
        if (myadapter != null) {
            myadapter.notifyDataSetChanged();
        }
    }
}

