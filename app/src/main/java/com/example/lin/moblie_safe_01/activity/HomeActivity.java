package com.example.lin.moblie_safe_01.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lin.moblie_safe_01.R;
import com.example.lin.moblie_safe_01.util.ContastValue;
import com.example.lin.moblie_safe_01.util.MD5Util;
import com.example.lin.moblie_safe_01.util.SpUtil;
import com.example.lin.moblie_safe_01.util.ToastUtil;

import net.youmi.android.normal.banner.BannerManager;
import net.youmi.android.normal.banner.BannerViewListener;
import net.youmi.android.normal.spot.SpotManager;

/**
 * Created by lin on 2016/12/7.
 */
public class HomeActivity extends Activity {
    private GridView gv_root;
    private String[] mtitleStr;
    private int[] drawableIds;
    private TextView tv_marquee;
    private Myadapter adadpter;

    /**
     * @param savedInstanceState 测试github上能否做出更新
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUi();
        initData();
        // 获取广告条
        View bannerView = BannerManager.getInstance(this)
                .getBannerView(this, new BannerViewListener() {
                    @Override
                    public void onRequestSuccess() {

                    }

                    @Override
                    public void onSwitchBanner() {

                    }

                    @Override
                    public void onRequestFailed() {

                    }
                });

// 获取要嵌入广告条的布局
        LinearLayout bannerLayout = (LinearLayout) findViewById(R.id.ll_banner);

// 将广告条加入到布局中
        bannerLayout.addView(bannerView);
    }

    private void initData() {
        mtitleStr = new String[]{
                "手机防盗", "通信卫士", "软件管理", "进程管理",
                "流量统计", "手机杀毒", "缓存清理", "高级工具", "系统设置"
        };
        drawableIds = new int[]{
                R.drawable.home_app, R.drawable.home_app2, R.drawable.home_app3, R.drawable.home_app4,
                R.drawable.home_app5, R.drawable.home_app6, R.drawable.home_app7, R.drawable.home_app8,
                R.drawable.home_app9
        };
        gv_root.setAdapter(new Myadapter());
        gv_root.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        //手机防盗功能，登陆显示输入密码选项
                        showDialog();
                        break;
                    case 1:
                        //通信卫士功能（黑名单拦截）
                        Intent intent2 = new Intent(getApplicationContext(), BlackNumberActivity.class);
                        startActivity(intent2);
                        break;
                    case 2:
                        //软件管理（软件卸载、开启、分享）
                        Intent intent3 = new Intent(getApplicationContext(), AppManagerActivity.class);
                        startActivity(intent3);
                        break;
                    case 3:
                        //进程管理（手机内存进程管理）
                        Intent intent4 = new Intent(getApplicationContext(), ProcessManagerActivity.class);
                        startActivity(intent4);
                        break;
                    case 4:
                        //进程管理（手机内存进程管理）
                        Intent intent7 = new Intent(getApplicationContext(), TrafficAcivity.class);
                        startActivity(intent7);
                        break;
                    case 5:
                        //病毒查杀（查杀病毒）
                        Intent intent5 = new Intent(getApplicationContext(), AntiVirusActivity.class);
                        startActivity(intent5);
                        break;
                    case 6:
                        //  Intent intent6 = new Intent(getApplicationContext(),CacheClearActivity.class);
                        Intent intent6 = new Intent(getApplicationContext(), BaseCacheActivity.class);
                        startActivity(intent6);
                        break;

                    case 7:
                        Intent intent1 = new Intent(getApplicationContext(), AToolsActivity.class);
                        startActivity(intent1);
                        break;
                    case 8:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void showDialog() {
        String psd = SpUtil.getString(this, ContastValue.MOBLI_SAFE_PSD, "");
        if (TextUtils.isEmpty(psd)) {
            showSetpsdDialog();
        } else {
            showConfirmpsdDialog();
        }
    }

    private void showConfirmpsdDialog() {
        //当前密码已存在的情况下，跳转至确认密码界面，代码与设置密码类似
        AlertDialog.Builder bulider = new AlertDialog.Builder(this);
        final AlertDialog dialog = bulider.create();
        //使用布局文件自定义dialog的界面
        View view = View.inflate(this, R.layout.dialog_confirm_psd, null);
        dialog.setView(view);
        //维护低版本界面
        // dialog.setView(view,0,0,0,0);
        dialog.show();
        Button btn_comfirm = (Button) view.findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        final EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
        btn_comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comfirm_psd = et_confirm_psd.getText().toString();
                String md5_comfirm_psd = MD5Util.encoder(comfirm_psd);
                //将输入确认密码进行Md5转换后，与sp内的md5密码进行比对
                if (!TextUtils.isEmpty(comfirm_psd)) {
                    //输入及确认密码两者都不为空，进入下一个逻辑
                    String pass_psd = SpUtil.getString(getApplicationContext(), ContastValue.MOBLI_SAFE_PSD, "");
                    if (pass_psd.equals(md5_comfirm_psd)) {
                        //Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    } else {
                        ToastUtil.show(getApplicationContext(), "输入密码有误，请重新输入。");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入密码。");
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void showSetpsdDialog() {
        //当前系统不存在密码，需要先设置密码后使用
        AlertDialog.Builder bulider = new AlertDialog.Builder(this);
        final AlertDialog dialog = bulider.create();
        //使用布局文件自定义dialog的界面
        View view = View.inflate(this, R.layout.dialog_set_psd, null);
        dialog.setView(view);
        //维护低版本界面
        // dialog.setView(view,0,0,0,0);
        dialog.show();
        Button btn_comfirm = (Button) view.findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        final EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
        final EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
        btn_comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String set_psd = et_set_psd.getText().toString();
                String comfirm_psd = et_confirm_psd.getText().toString();
                if (!TextUtils.isEmpty(set_psd) && !TextUtils.isEmpty(comfirm_psd)) {
                    //输入及确认密码两者都不为空，进入下一个逻辑
                    if (set_psd.equals(comfirm_psd)) {
                        SpUtil.putString(getApplicationContext(), ContastValue.MOBLI_SAFE_PSD, MD5Util.encoder(set_psd));
                        // Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    } else {
                        ToastUtil.show(getApplicationContext(), "输入密码有误，请重新输入。");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入密码。");
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void initUi() {
        gv_root = (GridView) findViewById(R.id.gv_root);
//        tv_marquee = (TextView) findViewById(R.id.tv_marquee);
//        tv_marquee.setMovementMethod(LinkMovementMethod.getInstance());
    }

    //初始化主页面的adapter
    private class Myadapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mtitleStr.length;
        }

        @Override
        public Object getItem(int i) {
            return mtitleStr[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View mview = View.inflate(getApplicationContext(), R.layout.adapter_gv, null);
            TextView tv_title = (TextView) mview.findViewById(R.id.tv_title);
            ImageView tv_icon = (ImageView) mview.findViewById(R.id.tv_icon);
            tv_title.setText(mtitleStr[i]);
            tv_icon.setBackgroundResource(drawableIds[i]);
            return mview;
        }
    }
}
