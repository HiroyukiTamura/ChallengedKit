package com.cks.hiroyuki2.worksupport3.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.cks.hiroyuki2.worksupport3.R;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by hiroyuki2 on 2017/11/04.
 */
public class AboutVPAdapter extends PagerAdapter {

    private static final int page = 2;
    private Context context;
    private LayoutInflater inflater;
    private String title0;
    private String title1;
    private IAboutVPAdapter listener;

    public AboutVPAdapter(@NonNull Context context, IAboutVPAdapter listener){
        this.context = context;
        this.listener = listener;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        title0 = context.getString(R.string.about_vp_title0);
        title1 = context.getString(R.string.about_vp_title1);
    }

    public interface IAboutVPAdapter{
        void onClickLibItem();
        void onClickLauncher();
    }

    public class Item0{
        @OnCheckedChanged(R.id.toggle)
        void onCheckChange0(){

        }
        @OnCheckedChanged(R.id.toggle1)
        void onCheckChange1(){

        }
    }

    public class Item1{
        @OnClick(R.id.ll0)
        void onClickLL0(){
            listener.onClickLibItem();
        }

        @OnClick(R.id.ll1)
        void onClickLL1(){
            listener.onClickLauncher();
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = new View(context);
        switch (position){
            case 0:
                view = inflater.inflate(R.layout.about_vp_item0, null);
                ButterKnife.bind(new Item0(), view);
                break;
            case 1:
                view = inflater.inflate(R.layout.about_vp_item1, null);
                ButterKnife.bind(new Item1(), view);
                break;
        }
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return page;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return title0;
            case 1:
                return title1;
        }
        return null;
    }
}
