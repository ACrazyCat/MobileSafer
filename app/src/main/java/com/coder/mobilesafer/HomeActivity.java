package com.coder.mobilesafer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.coder.mobilesafe.R;
import com.coder.mobilesafer.dialog.InterPasswordDialog;
import com.coder.mobilesafer.dialog.SetUpPasswordDialog;
import com.coder.mobilesafer.utils.MD5Utils;


public class HomeActivity extends AppCompatActivity {

    private GridView gridView;
    private long mExitTime;

    //存储设置文件
    private SharedPreferences msharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        msharedPreferences = getSharedPreferences("config",MODE_PRIVATE);

        gridView= (GridView) findViewById(R.id.gv_home);

        //填充GridView
        gridView.setAdapter(new HomeAdapter(HomeActivity.this));
        //单击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0://手机防盗
                        if(isSetUpPassword()){
                            showInterPasswordDialog();
                        }else{
                            showSetUpPasswordDialog();
                        }
                        break;
                    case 1://通讯卫士
                        break;
                    case 2://软件管家
                        break;
                    case 3://病毒查杀
                        break;
                    case 4://缓存清理
                        break;
                    case 5://进程管理
                        break;
                    case 6://流量统计
                        break;
                    case 7://高级工具
                        break;
                    case 8://设置中心
                        break;
                }
            }
        });

    }

    /**
     *
     * 设置密码对话框
     */

    public void showSetUpPasswordDialog(){
        final SetUpPasswordDialog msetPwdDialog = new SetUpPasswordDialog(HomeActivity.this);
        msetPwdDialog.setCallBack(new com.coder.mobilesafer.dialog.SetUpPasswordDialog.MyCallBack(){
            @Override
            public void ok() {
                String firstPwd = msetPwdDialog.mFirstPWDET.getText().toString().trim();
                String affirmPwd = msetPwdDialog.mAffirmET.getText().toString().trim();

                if (!TextUtils.isEmpty(firstPwd) && !TextUtils.isEmpty(affirmPwd)) {
                    //两次密码一致，则保存
                    if (firstPwd.equals(affirmPwd)) {
                        savePwd(affirmPwd);
                        //显示输入密码对话框
                        showInterPasswordDialog();
                    } else {
                        Toast.makeText(HomeActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void cancel() {
                msetPwdDialog.dismiss();
            }
        });
        msetPwdDialog.setCancelable(true);
        msetPwdDialog.show();
    }


    /**
     * 输入密码对话框
     * @return
     */
    public void showInterPasswordDialog(){
        final String password = getPassword();
        final InterPasswordDialog mInterPasswordDialog =  new InterPasswordDialog(HomeActivity.this);

        mInterPasswordDialog.setCallBack(new InterPasswordDialog.MyCallBack() {
            @Override
            public void confirm() {
                if(TextUtils.isEmpty(mInterPasswordDialog.getPassword())){
                    Toast.makeText(HomeActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }else if(password.equals(MD5Utils.encode(mInterPasswordDialog.getPassword()))){
                    //进入防盗界面
                    Toast.makeText(HomeActivity.this,"成功进入防盗界面",Toast.LENGTH_SHORT).show();
                }else{
                    mInterPasswordDialog.dismiss();
                    Toast.makeText(HomeActivity.this,"密码错误，请重新输入",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void dismiss() {
                mInterPasswordDialog.dismiss();
            }
        });

        mInterPasswordDialog.setCancelable(true);
        mInterPasswordDialog.show();
    }

    /**
     * 保存密码
     * @return
     */

    public void savePwd(String affirmPwd){
        SharedPreferences.Editor editor = msharedPreferences.edit();
        editor.putString("PhoneAntiTheftPWD", MD5Utils.encode(affirmPwd));
        editor.commit();
    }

    /**
     * 获取密码
     * @return
     */
    public String getPassword(){
        String password  = msharedPreferences.getString("PhoneAntiTheftPWD",null);
        if(TextUtils.isEmpty(password))
            return "";
        return password;
    }
    //判断是否设置过密码
    public boolean isSetUpPassword(){
        String password = msharedPreferences.getString("PhoneAntiTheftPWD", null);
        if(TextUtils.isEmpty(password)){
            return false;
        }
        return true;
    }

    //按两次返回键退出程序

    /**
     * Take care of calling onBackPressed() for pre-Eclair platforms.
     *
     * @param keyCode
     * @param event
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if((System.currentTimeMillis()-mExitTime)<2000){
                System.exit(0);
            }else{
                Toast.makeText(this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                mExitTime=System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
