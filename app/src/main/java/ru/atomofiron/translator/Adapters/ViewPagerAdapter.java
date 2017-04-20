package ru.atomofiron.translator.Adapters;


import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

import ru.atomofiron.translator.R;

public class ViewPagerAdapter extends PagerAdapter {

	Context co;
	List<View> pages = null;

	public ViewPagerAdapter(Context co, List<View> pages){
		this.co = co;
		this.pages = pages;
	}

	@Override
	public Object instantiateItem(View collection, int position){
		View v = pages.get(position);
		((ViewPager) collection).addView(v, 0);
		return v;
	}

	@Override
	public void destroyItem(View collection, int position, Object view){
		((ViewPager) collection).removeView((View) view);
	}

	@Override
	public int getCount(){
		return pages.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object){
		return view.equals(object);
	}

	@Override
	public void finishUpdate(View arg0){
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1){
	}

	@Override
	public Parcelable saveState(){
		return null;
	}

	@Override
	public void startUpdate(View arg0){
	}

	int[] titles = new int[] { R.string.history, R.string.translate, R.string.favorite };
	public CharSequence getPageTitle(int position) {
		return co.getString(titles[position]);
	}
}
