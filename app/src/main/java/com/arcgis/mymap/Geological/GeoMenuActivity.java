package com.arcgis.mymap.Geological;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.arcgis.mymap.BaiduMapActivity;
import com.arcgis.mymap.ConvertTools;
import com.arcgis.mymap.R;
import com.arcgis.mymap.UpdateActivity;
import com.arcgis.mymap.contacts.MyDatabaseHelper;
import com.arcgis.mymap.contacts.NewProject;
import com.arcgis.mymap.utils.GeoReadExcelUtils;
import com.arcgis.mymap.utils.MyImageButton;
import com.arcgis.mymap.utils.ToastNotRepeat;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/4/18.
 */

public class GeoMenuActivity  extends AppCompatActivity {
    private LinearLayout la=null;
    private LinearLayout lb=null;
    private LinearLayout lc=null;
    private LinearLayout ld=null;
    private LinearLayout le=null;
    private LinearLayout lf=null;
    //private LinearLayout lg=null;
    //private LinearLayout lh=null;
    private MyImageButton ba=null;
    private MyImageButton bb=null;
    private MyImageButton bc=null;
    private MyImageButton bd=null;
    private MyImageButton be=null;
    private MyImageButton bf=null;
    //private MyImageButton bg=null;
    //private MyImageButton bh=null;
    private ConvenientBanner convenientBanner;
    private List<Integer> imgs=new ArrayList<>();
    private List<NewProject> projects=new ArrayList<>();
    public int pposition;
    public MyDatabaseHelper dbHelper;
    public SQLiteDatabase db;
    public String time;
    public long t1;
    public long t2;
    public RadioButton bt1;
    public RadioButton bt2;
    public RadioButton bt3;
    private static final int REQUEST_CHOOSER = 1234;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_menu);
        verifyStoragePermissions(this);
        intView();
        setListenet();
        setConvenientBanner();
    }
    /**
     * 设置广告栏
     */
    private void setConvenientBanner() {
        convenientBanner.setPages(new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return new LocalImageHolderView();
            }
        },imgs).setPointViewVisible(true)//设置指示器是否可见
                .setPageIndicator(new int[]{R.mipmap.yuandianbantou,R.mipmap.yuandian});//设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
        convenientBanner.setManualPageable(true);//设置手动影响（设置了该项无法手动切换）
        convenientBanner.startTurning(2000);     //设置自动切换（同时设置了切换时间间隔）
        convenientBanner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);//设置指示器位置（左、中、右）
    }
    public class LocalImageHolderView implements Holder<Integer> {
        private ImageView imageView;
        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }
        @Override
        public void UpdateUI(Context context, int position, Integer data) {
            imageView.setImageResource(data);
        }
    }
    private void intView() {
        dbHelper=new MyDatabaseHelper(this, "pointsStore.db", null, 5);
        db = dbHelper.getWritableDatabase();
        ba=new MyImageButton(this,R.mipmap.xiangmu,R.string.xiangmu);
        bb=new MyImageButton(this,R.mipmap.shujuchakan,R.string.shujv);
        bc=new MyImageButton(this,R.mipmap.shijing,R.string.shijing);
        bd=new MyImageButton(this,R.mipmap.erwei,R.string.erwei);
        //be=new MyImageButton(this,R.mipmap.daoru,R.string.daoru);
        //bf=new MyImageButton(this,R.mipmap.daochu,R.string.daochu);
        be=new MyImageButton(this,R.mipmap.gongju,R.string.gongju);
        bf=new MyImageButton(this,R.mipmap.gengxin,R.string.gengxin);
        //获取包裹本按钮的容器
        //将我们自定义的Button添加进这个容器
        la = (LinearLayout) findViewById(R.id.la);
        lb = (LinearLayout) findViewById(R.id.lb);
        lc = (LinearLayout) findViewById(R.id.lc);
        ld = (LinearLayout) findViewById(R.id.ld);
        le = (LinearLayout) findViewById(R.id.le);
        lf = (LinearLayout) findViewById(R.id.lf);
        //lg = (LinearLayout) findViewById(R.id.lg);
        //lh = (LinearLayout) findViewById(R.id.lh);
        la.addView(ba);
        lb.addView(bb);
        lc.addView(bc);
        ld.addView(bd);
        le.addView(be);
        lf.addView(bf);
        //lg.addView(bg);
        //lh.addView(bh);
        convenientBanner= (ConvenientBanner) findViewById(R.id.convenientBanner);
        imgs.add(R.mipmap.city5);
        imgs.add(R.mipmap.city6);
        imgs.add(R.mipmap.city7);
        imgs.add(R.mipmap.city8);
        Cursor cursor=db.query("Geonewproject",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                //遍历cursor对象，取出数据
                NewProject newProject=new NewProject();
                String name=cursor.getString(cursor.getColumnIndex("gpname"));
                String url=cursor.getString(cursor.getColumnIndex("gsname"));
                String time=cursor.getString(cursor.getColumnIndex("timestamp"));
                newProject.setProjectname(name);
                newProject.setStrname(url);
                newProject.setDatetime(time);
                projects.add(newProject);
            }while (cursor.moveToNext());
        }
        cursor.close();
        pposition= Integer.parseInt(getIntent().getStringExtra("pposition"));
    }
    private void setListenet() {
        ba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a=new Intent(GeoMenuActivity.this,GeoProjectAttributes.class);
                startActivity(a);
            }
        });
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent c=new Intent(GeoMenuActivity.this, GeoDataManagerActivity.class);
                c.putExtra("pposition",String.valueOf(pposition));
                startActivity(c);
            }
        });
        bc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor=db.query("Newtimes",null,null,null,null,null,null);
                if (cursor.moveToFirst()){
                    time=cursor.getString(cursor.getColumnIndex("time"));
                }
                cursor.close();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                String nowtime=simpleDateFormat.format(date);
                try {
                    Date dt1=simpleDateFormat.parse(time);
                    Date dt2=simpleDateFormat.parse(nowtime);
                    t1=dt1.getTime()+1728000000;
                    t2=dt2.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (t2<=t1){
                    Intent a=new Intent(GeoMenuActivity.this,GeologicalMapActivity.class);
                    a.putExtra("pposition",String.valueOf(pposition));
                    startActivity(a);
                }else {
                    ToastNotRepeat.show(GeoMenuActivity.this,"权限到期，请联系QQ:443712225");
                }
            }
        });
        bd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent b=new Intent(GeoMenuActivity.this,BaiduMapActivity.class);
                startActivity(b);
            }
        });
//        be.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LinearLayout linearLayout= (LinearLayout) getLayoutInflater().inflate(R.layout.alert_inport_layout2,null);
//                AlertDialog dialog=new AlertDialog.Builder(GeoMenuActivity.this)
//                        .setTitle("导入Excel")
//                        .setView(linearLayout)
//                        .setNegativeButton("取消",null)
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                FileUtils.mFileFileterBySuffixs.acceptSuffixs("xls");
//                                Intent intent=new Intent(GeoMenuActivity.this,FileChooserActivity.class);
//                                startActivityForResult(intent, REQUEST_CHOOSER);
//                            }
//                        }).show();
//                bt1= (RadioButton) dialog.findViewById(R.id.excel1);
//                bt2= (RadioButton) dialog.findViewById(R.id.excel2);
//                bt3= (RadioButton) dialog.findViewById(R.id.excel3);
//                Button btnpositive=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                Button btnnegative=dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//                btnpositive.setTextColor(getResources().getColor(R.color.color29));
//                btnnegative.setTextColor(getResources().getColor(R.color.color29));
//            }
//        });
//        bf.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i=new Intent(GeoMenuActivity.this, GeoDataManagerExportActivity.class);
//                startActivity(i);
//            }
//        });
        bf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(GeoMenuActivity.this, UpdateActivity.class);
                startActivity(i);
            }
        });
        be.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(GeoMenuActivity.this,ConvertTools.class);
                i.putExtra("data","dikan");
                i.putExtra("position",String.valueOf(pposition));
                startActivity(i);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQUEST_CHOOSER:
                if (resultCode == RESULT_OK) {
                    final Uri uri = data.getData();
                    String path = FileUtils.getPath(this, uri);
                    if (path != null && FileUtils.isLocal(path)) {
                        File file = new File(path);
                        String f=file.toString();
                        GeoReadExcelUtils excelUtils = new GeoReadExcelUtils();
                        if (bt1.isChecked()){
                            excelUtils.writePointExcel(GeoMenuActivity.this,String.valueOf(pposition),f);
                            ToastNotRepeat.show(GeoMenuActivity.this,"导入成功");
                        }else if (bt2.isChecked()){
                            excelUtils.writeLineExcel(GeoMenuActivity.this,String.valueOf(pposition),f);
                            ToastNotRepeat.show(GeoMenuActivity.this,"导入成功");
                        }else {
                            excelUtils.writeSurfaceExcel(GeoMenuActivity.this,String.valueOf(pposition),f);
                            ToastNotRepeat.show(GeoMenuActivity.this,"导入成功");
                        }
                    }
                }
                break;
        }
    }
}
