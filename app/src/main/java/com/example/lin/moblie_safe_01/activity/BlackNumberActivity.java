package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.engine.BlackNumberDao;
import com.example.lin.moblie_safe_01.domain.BlackNumberInfo;
import com.example.lin.moblie_safe_01.util.ToastUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/12/28.
 */
//listview优化思路
//1.优化listview显示过程
//2.优化findviewbyid的次数
public class BlackNumberActivity extends Activity {

    private Button bt_add;
    private ListView lv_black_number;
    private BlackNumberDao mBlacknumber;
    private List<BlackNumberInfo> mBlackNumberList;
    private int mode = -1;
    private Myadapter myadapter;
    private boolean mIsload = false;
    private int mCount;
    //实例化adapter，listview赋值显示
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //加入容错处理，当系统adapter为空而创建，如果已经生成了，更新里面数据即可
            if (myadapter == null) {
                myadapter = new Myadapter();
                lv_black_number.setAdapter(myadapter);
            } else {
                myadapter.notifyDataSetChanged();
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_number);
        initUi();
        initData();
    }

    //查询数据表库为耗时操作，将其放进子线程内执行
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                mBlacknumber = BlackNumberDao.getInstance(getApplicationContext());
                  //  mBlackNumberList = mBlacknumber.findAll();
                //对数据进行分页处理，只显示20条数据
                mBlackNumberList = mBlacknumber.find(0);
                mCount = mBlacknumber.getCount();
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUi() {
        bt_add = (Button) findViewById(R.id.bt_add);
        lv_black_number = (ListView) findViewById(R.id.lv_black_number);
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        lv_black_number.setOnScrollListener(new AbsListView.OnScrollListener() {
            //监听滚动状态变化时的事件
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //判断需要继续加载数据的条件
                // 1.当前滚动状态为空闲状态
                // 2.已经滚动到当前加载数据的最后一条
                // 3.一个总控制变量，控制程序不会因为满足上述两个条件而重复加载数据
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&
                        lv_black_number.getLastVisiblePosition() >= mBlackNumberList.size() - 1 &&
                        !mIsload) {
                    //加入容错处理，当前显示条目总数小于数据库总量才进行分页数据加载，否则加载完成
                    if (mCount > mBlackNumberList.size()) {
                        new Thread() {
                            @Override
                            public void run() {
                                mBlacknumber = BlackNumberDao.getInstance(getApplicationContext());
                                List<BlackNumberInfo> loadmore = mBlacknumber.find(mBlackNumberList.size());
                                mBlackNumberList.addAll(loadmore);
                                mHandler.sendEmptyMessage(0);
                            }
                        }.start();
                    }
                }
            }

            //监听滚动过程中的事件
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void showDialog() {
        //获取一个builder，此处构造方法不能使用getapplicationcontent()方法，因传入this
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        //加载自定义的dialog对象
        View view = View.inflate(getApplicationContext(), R.layout.dialog_add_blacknumber, null);
        //设置对象的四个边距
        dialog.setView(view, 0, 0, 0, 0);
        //实例化dialog内的控件
        final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
        RadioGroup rd_group = (RadioGroup) view.findViewById(R.id.rd_group);
        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        //设置多选组的选择条件，分别给选择短信/电话/全部的赋值1/2/3
        rd_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    //拦截短信
                    case R.id.rb_sms:
                        mode = 1;
                        break;
                    //拦截电话
                    case R.id.rb_phone:
                        mode = 2;
                        break;
                    //拦截所有
                    case R.id.rb_all:
                        mode = 3;
                        break;
                }
            }
        });
        //点击确定按钮事件
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_phone.getText().toString();
                //进行非空判断，提示用户输入电话号码
                if (!TextUtils.isEmpty(phone)) {
                    //更新数据库
                    mBlacknumber.insert(phone, mode + "");
                    //实时更新listview里面的内容，不需要重新读取数据库
                    BlackNumberInfo blacknumber = new BlackNumberInfo();
                    blacknumber.phone = phone;
                    blacknumber.mode = mode + "";
                    //将更新后的数据加载到list集合的最顶部
                    mBlackNumberList.add(0, blacknumber);
                    if (myadapter != null) {
                        myadapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入电话号码！！");
                }

            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //初始化adapter
    private class Myadapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mBlackNumberList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackNumberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            //1.convertView为空，代表当前条目为首次构建，就需要查找控件，为后续的控件复用做准备
            //2.创建u一个ViewHolder，去存储所有控件的id，并由convertView的settag()方法存储起来
            //3.复用convertView的时候，找到之前设置过的tag，找回ViewHolder中包含的控件
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
                viewHolder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
                viewHolder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
                //将viewholder存储在convertview中
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //数据库中删除
                    mBlacknumber.delete(mBlackNumberList.get(position).phone);
                    //在集合中删除
                    mBlackNumberList.remove(position);
                    //通知adapter更新listview中的内容
                    myadapter.notifyDataSetChanged();
                }
            });
            viewHolder.tv_phone.setText(mBlackNumberList.get(position).phone);
            int mode = Integer.parseInt(mBlackNumberList.get(position).mode);
            //根据不同的mode显示不同的信息
            switch (mode) {
                case 1:
                    viewHolder.tv_mode.setText("拦截短信");
                    break;
                case 2:
                    viewHolder.tv_mode.setText("拦截电话");
                    break;
                case 3:
                    viewHolder.tv_mode.setText("拦截短信及电话");
                    break;
            }
            return convertView;
        }

    }

    static class ViewHolder {
        TextView tv_mode;
        TextView tv_phone;
        ImageView iv_delete;
    }
}
