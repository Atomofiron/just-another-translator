package ru.atomofiron.translator.Adapters;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

import ru.atomofiron.translator.R;

/**
 * Отображения перевода и списков истории и избранного.
 */
public class ViewPagerAdapter extends PagerAdapter {
	private static final int[] titles = new int[] { R.string.history, R.string.translation, R.string.favorite };

	private Context co;
	private List<View> pages = null;

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

	public CharSequence getPageTitle(int position) {
		return co.getString(titles[position]);
	}
}
