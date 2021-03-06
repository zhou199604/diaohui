package com.arcgis.mymap.Geological;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;

import com.arcgis.mymap.DataLineActivity;
import com.arcgis.mymap.DataLineExportActivity;
import com.arcgis.mymap.Export.ExprotLineUtils;
import com.arcgis.mymap.Export.GeoExportLineUtils;
import com.arcgis.mymap.Export.GeoWirteLineGpx;
import com.arcgis.mymap.Export.GeoWriteLineCass;
import com.arcgis.mymap.Export.GeoWriteLineKml;
import com.arcgis.mymap.Export.WriteLineCass;
import com.arcgis.mymap.Export.WriteLineGpx;
import com.arcgis.mymap.Export.WriteLineKml;
import com.arcgis.mymap.R;
import com.arcgis.mymap.adapter.ExportLineAdapter;
import com.arcgis.mymap.adapter.GeoExportLineAdapter;
import com.arcgis.mymap.contacts.Lines;
import com.arcgis.mymap.contacts.MoreLines;
import com.arcgis.mymap.contacts.MyDatabaseHelper;
import com.arcgis.mymap.contacts.NewProject;
import com.arcgis.mymap.utils.GetTable;
import com.arcgis.mymap.utils.ToastNotRepeat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/5/3.
 */

public class GeoExportLineActivity extends Activity {
    private ListView lv;
    public List<MoreLines> list,list2,listExport;
    private GeoExportLineAdapter adapter;
    public MoreLines line;
    private Button bt_selectall,bt_cancel,bt_close,bt_export,bt_delate;
    private ImageButton back;
    private SQLiteDatabase db;
    private MyDatabaseHelper dbHelper;
    public String pposition;
    public List<NewProject> projects=new ArrayList<>();
    private GeoDataLineActivity geoDataLineActivity;
    public EditText editText;
    public RadioButton bt1,bt2,bt3,bt4,radioButton1,radioButton2,radioButton3,radioButton4;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datalineexport);
        intview();
        setlistener();
        Cursor cursor = db.query("Geomorelines"+pposition, null, null, null, null, null, null);
        try {
            list=geoDataLineActivity.getData(list,cursor);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        list2.addAll(list);
        adapter=new GeoExportLineAdapter(list,GeoExportLineActivity.this,R.layout.exportitem);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                GeoExportLineAdapter.Viewholder holder= (GeoExportLineAdapter.Viewholder) view.getTag();
                // 改变CheckBox的状态
                holder.getCheckBox().toggle();
                // 将CheckBox的选中状况记录下来
                adapter.getIsSelected().put(position,holder.getCheckBox().isChecked());
                line=list.get(position);
                if (holder.getCheckBox().isChecked()==true){
                    listExport.add(line);
                }else {
                    if (listExport.contains(line)){
                        listExport.remove(line);
                    }
                }
            }
        });
    }
    //添加监听
    private void setlistener() {
        ClickListener listener=new ClickListener();
        bt_selectall.setOnClickListener(listener);
        bt_cancel.setOnClickListener(listener);
        bt_close.setOnClickListener(listener);
        bt_export.setOnClickListener(listener);
        bt_delate.setOnClickListener(listener);
        back.setOnClickListener(listener);
    }
    //初始化控件
    private void intview() {
        geoDataLineActivity = new GeoDataLineActivity();
        dbHelper=new MyDatabaseHelper(this, "pointsStore.db", null, 5);
        db=dbHelper.getWritableDatabase();
        //获取表位置
        GeoGetTable getTable=new GeoGetTable();
        pposition=getTable.getTableposition(GeoExportLineActivity.this,db,dbHelper,projects);
        lv= (ListView) findViewById(R.id.listview);
        bt_selectall= (Button) findViewById(R.id.selectall);
        bt_cancel= (Button) findViewById(R.id.cancel);
        bt_close= (Button) findViewById(R.id.closeall);
        bt_export= (Button) findViewById(R.id.export);
        bt_delate= (Button) findViewById(R.id.delate);
        back= (ImageButton) findViewById(R.id.back);
        list=new ArrayList<>();
        list2=new ArrayList<>();
        listExport=new ArrayList<>();
        line=new MoreLines();
    }
    //时间单击监听功能
    private class ClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.delate:
                    for (int x=0;x<listExport.size();x++){
                        db.delete("Geomorelines"+pposition, "id=?", new String[]{String.valueOf(listExport.get(x).getId())});
                        list.remove(listExport.get(x));
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    },400);
                    for (int i=0;i<list.size();i++){
                        adapter.getIsSelected().put(i, false);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case R.id.back:
                    finish();
                    break;
                case R.id.selectall:
                    for (int i=0;i<list.size();i++){
                        adapter.getIsSelected().put(i, true);
                    }
                    listExport.clear();
                    listExport.addAll(list2);
                    adapter.notifyDataSetChanged();
                    break;
                case R.id.cancel:
                    for (int i=0;i<list.size();i++){
                        if (adapter.getIsSelected().get(i)){
                            adapter.getIsSelected().put(i,false);
                        }
                    }
                    listExport.clear();
                    adapter.notifyDataSetChanged();
                    break;
                case R.id.closeall:
                    finish();
                    break;
                case R.id.export:
                    LinearLayout linearLayout= (LinearLayout) getLayoutInflater().inflate(R.layout.alert_dialogexport,null);
                    AlertDialog dialog=new AlertDialog.Builder(GeoExportLineActivity.this)
                            .setTitle("导出数据:")
                            .setView(linearLayout)
                            .setNegativeButton("取消",null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String filename=editText.getText().toString();
                                    if (filename.equals("")){
                                        filename="未命名";
                                    }
                                    if (bt4.isChecked()){
                                        try{
                                            //GeoExportLineUtils.writelineExcel(GeoExportLineActivity.this,listExport,filename);
                                            ToastNotRepeat.show(GeoExportLineActivity.this,"导出成功！");
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }else if (bt1.isChecked()){
                                        GeoWirteLineGpx wirteLineGpx = new GeoWirteLineGpx();
                                        try{
                                            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            Date curDate =  new Date(System.currentTimeMillis());
                                            String  str  =  formatter.format(curDate);
                                            //wirteLineGpx.createlineGpx(filename,listExport,str);
                                            ToastNotRepeat.show(GeoExportLineActivity.this,"导出成功！");
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }else if (bt2.isChecked()){
                                        GeoWriteLineKml writeLineKml = new GeoWriteLineKml();
                                        try{
                                            //writeLineKml.createKml(filename,listExport);
                                            ToastNotRepeat.show(GeoExportLineActivity.this,"导出成功！");
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }else {
                                        LinearLayout linearLayout1= (LinearLayout) getLayoutInflater().inflate(R.layout.choosesystem,null);
                                        final String finalFilename = filename;
                                        AlertDialog dialog1=new AlertDialog.Builder(GeoExportLineActivity.this)
                                                .setTitle(" 选择坐标系统：")
                                                .setView(linearLayout1)
                                                .setNegativeButton("取消",null)
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (radioButton1.isChecked()){
                                                            GeoWriteLineCass writeLineCass = new GeoWriteLineCass();
                                                            try{
                                                                //writeLineCass.creatWgs84(finalFilename,listExport);
                                                                ToastNotRepeat.show(GeoExportLineActivity.this,"导出成功！");
                                                            }catch(Exception e){
                                                                e.printStackTrace();
                                                            }
                                                        }else if (radioButton2.isChecked()){
                                                            GeoWriteLineCass writeLineCass = new GeoWriteLineCass();
                                                            try{
                                                                //writeLineCass.createbeijing54(finalFilename,listExport);
                                                                ToastNotRepeat.show(GeoExportLineActivity.this,"导出成功！");
                                                            }catch(Exception e){
                                                                e.printStackTrace();
                                                            }
                                                        }else if (radioButton3.isChecked()){
                                                            GeoWriteLineCass writeLineCass = new GeoWriteLineCass();
                                                            try{
                                                                //writeLineCass.createxian80(finalFilename,listExport);
                                                                ToastNotRepeat.show(GeoExportLineActivity.this,"导出成功！");
                                                            }catch(Exception e){
                                                                e.printStackTrace();
                                                            }
                                                        }else {
                                                            GeoWriteLineCass writeLineCass = new GeoWriteLineCass();
                                                            try{
                                                                //writeLineCass.createguojia2000(finalFilename,listExport);
                                                                ToastNotRepeat.show(GeoExportLineActivity.this,"导出成功！");
                                                            }catch(Exception e){
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                })
                                                .show();
                                        radioButton1= (RadioButton) dialog1.findViewById(R.id.radiobt1);
                                        radioButton2= (RadioButton) dialog1.findViewById(R.id.radiobt2);
                                        radioButton3= (RadioButton) dialog1.findViewById(R.id.radiobt3);
                                        radioButton4= (RadioButton) dialog1.findViewById(R.id.radiobt4);
                                        Button btnpositive=dialog1.getButton(AlertDialog.BUTTON_POSITIVE);
                                        Button btnnegative=dialog1.getButton(AlertDialog.BUTTON_NEGATIVE);
                                        btnpositive.setTextColor(getResources().getColor(R.color.color29));
                                        btnnegative.setTextColor(getResources().getColor(R.color.color29));
                                    }
                                }
                            })
                            .show();
                    editText= (EditText) dialog.findViewById(R.id.filename);
                    bt1= (RadioButton) dialog.findViewById(R.id.gpx);
                    bt2= (RadioButton) dialog.findViewById(R.id.kml);
                    bt3= (RadioButton) dialog.findViewById(R.id.cass);
                    bt4= (RadioButton) dialog.findViewById(R.id.excel);
                    Button btnpositive=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    Button btnnegative=dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    btnpositive.setTextColor(getResources().getColor(R.color.color29));
                    btnnegative.setTextColor(getResources().getColor(R.color.color29));
                    break;
            }
        }
    }
}
