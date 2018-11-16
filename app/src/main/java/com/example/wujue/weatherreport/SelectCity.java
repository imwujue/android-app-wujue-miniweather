package com.example.wujue.weatherreport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wujue.app.MyApplication;
import com.example.wujue.bean.City;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectCity extends Activity implements View.OnClickListener{
    private ImageView mBackBtn;

    private ListView listView;
    private TextView cityselected;

    private List<City> listcity = MyApplication.getInstance().getCityList();
    private int listsize = listcity.size();
    private String[] city = new String[listsize];

    private ArrayList<String> mSearchResult = new ArrayList<>();

    private Map<String, String> nametoCode = new HashMap<>();
    private Map<String, String> NametoPinYin = new HashMap<>();

    private SearchView mSearch;
    String  returnCode;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

//        监听事件
        mBackBtn = (ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

//        打印listcity中的城市名数据
        cityselected = (TextView)findViewById(R.id.title_name);
        Log.d("City", listcity.get(1).getCity());
        for(int i =0; i<listsize; i++){
            city[i] = listcity.get(i).getCity();
            Log.d("City", city[i]);
        }

//        建立name-code,name-pinyin映射
        String strName;
        String strNamePinyin;
        String strCode;
        for(City city1:listcity){
            strCode = city1.getNumber();
            strName = city1.getCity();
            strNamePinyin = city1.getAllPY().toLowerCase();
            Log.d("myTest", strName);
            Log.d("myTest", strNamePinyin);
            nametoCode.put(strName, strCode);
            NametoPinYin.put(strName,strNamePinyin);
            mSearchResult.add(strName);
        }

//      监听listview
        final ArrayAdapter<String> arrayAdapter;
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, mSearchResult);
        listView = findViewById(R.id.list_view);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String returnCityName = mSearchResult.get(position);
                Toast.makeText(SelectCity.this,"您已选择："+returnCityName, Toast.LENGTH_SHORT).show();
                returnCode = nametoCode.get(returnCityName);
                Log.d("returnCode", returnCode);
                cityselected.setText("当前城市:" + returnCityName);
            }
        });

//        监听searchview
        mSearch = (SearchView)findViewById(R.id.search_edit);
        setSearchView(mSearch);
        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(SelectCity.this,"检索中",Toast.LENGTH_SHORT).show();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText)){
                    if(mSearchResult!=null){
                        mSearchResult.clear();
                    }
                    for(String str: NametoPinYin.keySet()){
                        Log.d("myTest", newText);
                        Log.d("myTest", str);
                        if(str.contains(newText)||NametoPinYin.get(str).contains(newText)){

                            mSearchResult.add(str);
                            Log.d("myTest", mSearchResult.get(0));
                        }
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });
        Log.d("myTest", mSearchResult.get(0));
        listView.setAdapter(arrayAdapter);
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.title_back){
            Log.d("myTest", "点击返回");
            int position = listView.getCheckedItemPosition();
            String selectCityCode = listcity.get(position).getNumber();
            Intent i = new Intent();
            i.putExtra("cityCode",returnCode);
            setResult(RESULT_OK,i);
            finish();
        }
    }

    protected void setSearchView(SearchView mSearch){
        mSearch.setQueryHint("搜索全国城市");
        int id = mSearch.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) mSearch.findViewById(id);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setHintTextColor(getResources().getColor(android.R.color.white));
    }
}
