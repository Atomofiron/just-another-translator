package ru.atomofiron.translator.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.atomofiron.translator.I;
import ru.atomofiron.translator.R;
import ru.atomofiron.translator.Utils.Base;
import ru.atomofiron.translator.Utils.Node;

public class ListAdapter extends BaseAdapter {

	private Context co;
	private LayoutInflater inflater;
	private final ArrayList<Node> nodes = new ArrayList<>();
	private Base base;
	private String type;

	public ListAdapter(Context context, Base base, String type) {
		co = context;
		this.base = base;
		this.type = type;
		inflater = LayoutInflater.from(co);

		update();
	}

	@Override
	public int getCount() {
		return nodes.size();
	}

	@Override
	public Object getItem(int position) {
		return nodes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void update() {
		update(base.get(type));
	}

	private void update(List<Node> list) {
		nodes.clear();
		nodes.addAll(list);

		notifyDataSetChanged();
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_list, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);

		} else
			holder = (ViewHolder) convertView.getTag();

		holder.icon.setImageDrawable(co.getResources().getDrawable(nodes.get(
				position).isHistory ? R.drawable.ic_time :  R.drawable.ic_bookmark_border));
		holder.title.setText(nodes.get(position).title);
		holder.subtitle.setText(nodes.get(position).subtitle);

		return convertView;
	}

	class ViewHolder {
		LinearLayout layout;
		ImageView icon;
		TextView title;
		TextView subtitle;

		ViewHolder(View view) {
			layout = (LinearLayout) view.findViewById(R.id.layout);
			icon = (ImageView) view.findViewById(R.id.icon);
			title = (TextView) view.findViewById(R.id.title);
			subtitle = (TextView) view.findViewById(R.id.subtitle);
		}
	}
}
