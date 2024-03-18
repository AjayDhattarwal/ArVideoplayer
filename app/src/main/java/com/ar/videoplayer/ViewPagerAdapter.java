package com.ar.videoplayer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapter extends PagerAdapter {
    private final int[] layoutIds = {
            R.layout.layout_quality,
            R.layout.layout_audio_language,
            R.layout.layout_subtitle
    };

    private View[] layoutViews;

    public ViewPagerAdapter() {
        layoutViews = new View[layoutIds.length];
    }

    @Override
    public int getCount() {
        return layoutIds.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Quality";
        } else if (position == 1) {
            return "Audio Language";
        } else {
            return "Subtitles";
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (layoutViews[position] == null) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            int layoutId = layoutIds[position];
            Log.d("position" , "position:  "+position);
            layoutViews[position] = inflater.inflate(layoutId, container, false);
        }

        View layoutView = layoutViews[position];
        container.addView(layoutView);
        Log.d("container", "layout View:  " + container);

        return layoutView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}

