package com.jsj.wifiupdaterdemo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 扫描完毕接收器
     */
    private WifiReceiver receiverWifi;

    /**
     * WifiManager对象
     */
    private WifiManager mWifiManager;
    /**
     * 存放wifi列表的集合
     */
    private ArrayList<ScanResult> wifiList;

    /**
     * 存放wifi列表的集合
     */
    private ArrayList<ScanResult> newWifiList;

    private Dialog dialog;
    private WifiAdapter mWifiAdapter;
    private ListView lv_wifi;
    private String TAG;

    /**
     * 记录当前连接的wifi的ssid
     */
    private String mCurrentSsid;

    private TextView tv_connect_name;

    private CheckBox cb_toggle;
    /**
     *  保存的wifi配置列表
     */
    private List<WifiConfiguration> mWifiConfigList;
    /**
     * 点击要连接的wifi
     */
    private String mOnclickSSid;

    /**
     * 无效值 相当于false
     */
    private int minvalid = -100;

    private DialogUtil mDialogUtil;

    private Dialog mPasswordDialog;

    private Dialog mConnWifiDialog;

    private LinearLayout ll_conncet_wifi;

    private ProgressBar pb_wifi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialogUtil = new DialogUtil();
        setContentView(R.layout.activity_main);
        lv_wifi = (ListView) findViewById(R.id.lv_wifi);
        cb_toggle = (CheckBox) findViewById(R.id.cb_toggle);
        tv_connect_name = (TextView) findViewById(R.id.tv_connect_name);
        ll_conncet_wifi = (LinearLayout) findViewById(R.id.ll_conncet_wifi);
        pb_wifi = (ProgressBar)findViewById(R.id.pb_wifi);
        cb_toggle.setOnClickListener(this);

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mWifiConfigList = mWifiManager.getConfiguredNetworks();//获取已有的网络配置
        receiverWifi = new WifiReceiver();

        wifiList = new ArrayList<ScanResult>();
        newWifiList = new ArrayList<ScanResult>();
        //判断wifi是否是打开状态
        if (mWifiManager.isWifiEnabled()) {//打开状态
            mWifiManager.startScan();//开始扫描wifi
//            startScan();
//            dialog = ProgressDialog.show(this, "", "正在扫描WIFI热点,请稍候");
            pb_wifi.setVisibility(View.VISIBLE);

        } else {//关闭状态

        }

        TAG = this.getLocalClassName();
    }

    /**
     * 判断要连接的wifi信息，是否已经保存过,如果存在返回networkId，不存在返回无效码
     *
     * @return
     */
    public int isConfiguration() {
        mWifiConfigList = mWifiManager.getConfiguredNetworks();
        if (mWifiConfigList == null) return minvalid;
        for (int i = 0; i < mWifiConfigList.size(); i++) {
            if (mWifiConfigList.get(i).SSID.equals("\"" + mOnclickSSid + "\"")) {//地址相同
                return mWifiConfigList.get(i).networkId;
            }
        }
        return minvalid;
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiverWifi, filter);// 注册广播
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverWifi);// 注销广播
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cb_toggle:
                if (mWifiManager.isWifiEnabled()) {//打开状态
                    dialog = ProgressDialog.show(this, "", "正在关闭wifi");
                    closeWifi();
                } else {//关闭状态
                    dialog = ProgressDialog.show(this, "", "正在打开wifi");
                    openWifi();
                }

                break;
        }
    }

    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                refreshWifiList();
            } else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {//监听wifi 的打开和关闭
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                Log.e("H3c", "wifiState" + wifiState);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED://关闭完成
//                        Toast.makeText(MainActivity.this, "wifi为关闭状态", Toast.LENGTH_LONG).show();
                        cb_toggle.setChecked(false);
                        ll_conncet_wifi.setVisibility(View.GONE);
                        if (dialog != null) dialog.dismiss();
                        break;
                    case WifiManager.WIFI_STATE_DISABLING://正在关闭
                        Toast.makeText(MainActivity.this, "DISABLING", Toast.LENGTH_LONG).show();
                        break;
                    case WifiManager.WIFI_STATE_ENABLED://打开成功
//                        Toast.makeText(MainActivity.this, "wifi为打开状态", Toast.LENGTH_LONG).show();
                        cb_toggle.setChecked(true);
                        String sSid = mWifiManager.getConnectionInfo().getSSID();
                        Log.d(TAG, "当前连接的wifi信息 ＝＝＝ " + mWifiManager.getConnectionInfo().toString());
                        if (!TextUtils.isEmpty(sSid)) {
                            mCurrentSsid = sSid.substring(1, sSid.length() - 1);
                            tv_connect_name.setText(mCurrentSsid);
                            ll_conncet_wifi.setVisibility(View.VISIBLE);
                        }
                        break;
                    case WifiManager.WIFI_STATE_ENABLING://正在打开
                        Toast.makeText(MainActivity.this, "ENABLING", Toast.LENGTH_LONG).show();
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN:
                        Toast.makeText(MainActivity.this, "UNKNOWN", Toast.LENGTH_LONG).show();
                        break;
                }
            } else if (WifiManager.EXTRA_SUPPLICANT_CONNECTED.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra("connected", true);
                Toast.makeText(MainActivity.this, "connected ＝＝ " + connected, Toast.LENGTH_LONG).show();
                Log.e("wifi", "CONNECTED::" + "connected ==" + connected);
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {//wifi 连接状态已经改变，第二个ssid已经接入
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
//                    boolean isConnected = networkInfo.isAvailable();
                    if (networkInfo.isConnected() && networkInfo.isAvailable()) {//当wifi发生改变时会回掉
                        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                        String sSid = wifiInfo.getSSID();
                        mCurrentSsid = sSid.substring(1, sSid.length() - 1);
                        Log.d(TAG, "NETWORK_STATE_CHANGED_ACTION  -----" + mCurrentSsid + "连接成功");
                        if (mOnclickSSid != null && !mOnclickSSid.equals("unknown ssid")) {
                            if (mOnclickSSid.equals(mCurrentSsid)) {
//                                Toast.makeText(MainActivity.this, mCurrentSsid + "连接成功", Toast.LENGTH_SHORT).show();
                                tv_connect_name.setText(mCurrentSsid);
                                mConnWifiDialog.dismiss();
                                refreshWifiList();
                            }
                        }
                    } else if (!networkInfo.isConnected() && networkInfo.isAvailable()) {
                        Log.d(TAG, "显示连接已保存，但标题栏没有，即没有实质连接上");
//                        Toast.makeText(MainActivity.this, "显示连接已保存，但标题栏没有，即没有实质连接上", Toast.LENGTH_SHORT).show();
                    } else if (!networkInfo.isConnected() && !networkInfo.isAvailable()) {//wifi 处于关闭关闭状态会回掉
                        Log.d(TAG, "选择连接，在正在获取IP地址时 ");
                        Toast.makeText(MainActivity.this, "选择连接，在正在获取IP地址时 ", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION.equals(intent.getAction())) {
                Toast.makeText(MainActivity.this, "SUPPLICANT_CONNECTION_CHANGE_ACTION", Toast.LENGTH_LONG).show();
                Log.e("wifi", "SUPPLICANT_CONNECTION_CHANGE_ACTION");
            }
        }
    }

    /**
     * 获取当前系统连接的wifi信息
     *
     * @return
     */
    private int getConnectWifiNetworkId() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        String currentSid = wifiInfo.getSSID();
        mCurrentSsid = currentSid.substring(1, currentSid.length() - 1);
        Log.d(TAG, "current wifi info " + wifiInfo.toString());
        return wifiInfo.getNetworkId();
    }

    /**
     * 打开WIFI
     */
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {//判断WIFI设备是否打开
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭WIFI
     */
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 连接wifi
     *
     * @param netId
     */
    private void connectingWifi(int netId) {
        Log.d(TAG, "连接网络———>" + netId);
        boolean enable = mWifiManager.enableNetwork(netId, true);
        mConnWifiDialog = ProgressDialog.show(this, "", "正在连接" + mOnclickSSid);
        //如果广播没有接收到连接wifi成功的消息，10秒后将dialog dismiss掉
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mConnWifiDialog.dismiss();
            }
        }, 15000);
    }

    /**
     * 输入密码的Dialog
     *
     * @return
     */
    public void showPasswordDialog(final int networkId) {
        mPasswordDialog = mDialogUtil.createPasswordDialog(MainActivity.this, "输入密码", "输入密码:", "加入", "取消", new DialogUtil.OnButtonViewListener() {
            @Override
            public void enterButton(View view) {
                String password = ((EditText) view).getText().toString();
                addWifiConfig(password);
                connectingWifi(networkId);
            }

            @Override
            public void cancelButton() {

            }
        });
    }

    /**
     * 删除网络对话框
     *
     * @return
     */
    public void delectWifiDialog(final int networkId) {
        mPasswordDialog = mDialogUtil.createSetHintDialog(MainActivity.this, "删除网络", "删除网络:", true, "取消", "确定", new DialogUtil.OnButtonListener() {
            @Override
            public void enterButton() {
                deleteSavedConfigs(networkId);
            }

            @Override
            public void cancelButton() {

            }
        });
    }

    /**
     * 删除已保存的wifi配置
     */
    private void deleteSavedConfigs(int networkId) {
        if (mWifiConfigList == null) {
            return;
        }
        Log.d(TAG, "删除wifi配置 === " + networkId);
        for (int i = 0; i < mWifiConfigList.size(); i++) {
            WifiConfiguration config = mWifiConfigList.get(i);
            if (config.networkId == networkId) {
                Log.d(TAG, "删除失败的wifi配置 === " + config.SSID);
                config.priority = i + 2;
                mWifiManager.removeNetwork(config.networkId);
                break;
            }
        }
        mWifiManager.saveConfiguration();
    }

    /**
     * 断开指定ID的网络
     *
     * @param netId
     */
    public void disconnectWifi(int netId) {
        mWifiManager.disconnect();//断开当前网络
        mWifiManager.removeNetwork(netId);
    }

    /**
     * 添加wifi配置信息，连接wifi时需要先添加到系统配置
     *
     * @param pwd
     * @return
     */
    public int addWifiConfig(String pwd) {
        WifiConfiguration wifiCong = new WifiConfiguration();
        wifiCong.SSID = "\"" + mOnclickSSid + "\"";
        wifiCong.preSharedKey = "\"" + pwd + "\"";
        wifiCong.hiddenSSID = true;
        wifiCong.status = WifiConfiguration.Status.ENABLED;
        return mWifiManager.addNetwork(wifiCong);
    }

    /**
     * 刷新wifi列表
     */
    private void refreshWifiList() {
        wifiList = (ArrayList<ScanResult>) mWifiManager.getScanResults();
//        dialog.dismiss();
        pb_wifi.setVisibility(View.INVISIBLE);
        boolean isExists;//记录是否相同
        Log.d("＝＝＝＝", "扫描完毕");
        newWifiList.clear();

        for (ScanResult wifi : wifiList) {
            Log.d(TAG, "wifi＝＝" + wifi.SSID);
            if (wifi.SSID.equals(mCurrentSsid)) {
                Log.d(TAG, "过滤掉已经连接的wifi＝＝" + mCurrentSsid);
                continue;
            }
            isExists = false;
            for (int i = 0; i < newWifiList.size(); i++) {
                ScanResult newWifi = newWifiList.get(i);
                if (wifi.SSID.equals(newWifi.SSID)) {
                    isExists = true;
                    newWifiList.set(i, wifi);
                }
            }
            if (!isExists) {//如果不相同再添加再集合中
                newWifiList.add(wifi);
            }
        }

        if (mWifiAdapter == null) {
            mWifiAdapter = new WifiAdapter(MainActivity.this, newWifiList, new WifiAdapter.OnClickDeleteWifiListener() {
                @Override
                public void deleteWifiConfig() {
                    //判断sSid是否已配置，如果没有配置提示输入密码，反之连接
                    int networkId = isConfiguration();
                    if (networkId != minvalid) {//没有保存的wifi信息，需要输入用户名密码
                        delectWifiDialog(networkId);
                    }
                }

                @Override
                public void connectWifi(View view) {
                    mOnclickSSid = ((TextView) view.findViewById(R.id.tv_sSid)).getText().toString();
                    //判断sSid是否已配置，如果没有配置提示输入密码，反之连接
                    int networkId = isConfiguration();
                    if (networkId == minvalid) {//没有保存的wifi信息，需要输入用户名密码
                        showPasswordDialog(networkId);
                    } else {
                        connectingWifi(networkId);
                    }
                }
            });
            lv_wifi.setAdapter(mWifiAdapter);
        } else {
            mWifiAdapter.notifyDataSetChanged();
        }

        mWifiManager.startScan();//再次扫描wifi
    }

    /**
     * 每秒调用一次startScan
     */
    public void startScan() {
        new Thread() {// 新建线程，每隔1秒发送一次广播，同时把i放进intent传出
            public void run() {
                while (true) {
                    mWifiManager.startScan();
                    pb_wifi.setVisibility(View.VISIBLE);
                    try {
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
