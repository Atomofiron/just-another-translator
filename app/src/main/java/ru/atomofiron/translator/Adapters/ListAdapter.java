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

import ru.atomofiron.translator.R;
import ru.atomofiron.translator.Utils.Base;
import ru.atomofiron.translator.Utils.Node;

public class ListAdapter extends BaseAdapter implements View.OnClickListener {

	private Context co;
	private LayoutInflater inflater;
	private final ArrayList<Node> nodes = new ArrayList<>();
	private Base base;
	private Node.TYPE type;

	public ListAdapter(Context context, Base base, Node.TYPE type) {
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
	public Node getItem(int position) {
		return nodes.get(getCount() - 1 - position);
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_list, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);

			holder.icon.setOnClickListener(this);
		} else
			holder = (ViewHolder) convertView.getTag();

		Node node = nodes.get(getCount() - 1 - position);

		holder.node = node;
		holder.icon.setImageDrawable(co.getResources().getDrawable(
				node.isHistory() ? R.drawable.ic_trashbox : R.drawable.ic_bookmark_selector));
		holder.icon.setActivated(true);
		holder.title.setText(node.getPhrase());
		holder.subtitle.setText(node.getTranslation());

		return convertView;
	}

	@Override
	public void onClick(View v) {
		Node node = ((ViewHolder)((View)v.getParent()).getTag()).node;

		if (type.equals(Node.TYPE.HISTORY)) {
			base.remove(node);
			nodes.remove(node);
			notifyDataSetChanged();
		} else if (v.isActivated()) {
			base.remove(node);
			v.setActivated(false);
		} else {
			base.put(node);
			v.setActivated(true);
		}
	}

	private class ViewHolder {
		LinearLayout layout;
		ImageView icon;
		TextView title;
		TextView subtitle;
		Node node;

		ViewHolder(View view) {
			layout = (LinearLayout) view.findViewById(R.id.layout);
			icon = (ImageView) view.findViewById(R.id.icon);
			title = (TextView) view.findViewById(R.id.title);
			subtitle = (TextView) view.findViewById(R.id.subtitle);
		}
	}
}
