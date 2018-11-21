package com.example.wujue.util;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private List<View> listView;

    public ViewPagerAdapter(Context context,List<View> listView){
        this.context = context;
        this.listView = listView;
    }
//   返回要滑动的view的个数
    @Override
    public int getCount(){
        return listView.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object){
        return view == object;
    }
//   将当前视图添加到container中，返回当前view
    @Override
    public Object instantiateItem(ViewGroup container, int position){
        container.addView(listView.get(position));
        return listView.get(position);
    }
//    从当前container中删除指定位置的view
    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        View view = (View)object;
        container.removeView(view);
    }

}
