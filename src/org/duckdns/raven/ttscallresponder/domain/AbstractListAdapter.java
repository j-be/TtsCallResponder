package org.duckdns.raven.ttscallresponder.domain;

import java.util.List;

import android.app.Activity;
import android.widget.BaseAdapter;

public abstract class AbstractListAdapter<ModelType> extends BaseAdapter {

	private List<ModelType> list;
	private final Activity parent;

	public AbstractListAdapter(Activity parent, List<ModelType> list) {
		this.parent = parent;
		this.setModel(list);
	}

	protected Activity getParent() {
		return this.parent;
	}

	public void setModel(List<ModelType> list) {
		this.list = list;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		return this.list.get(position);
	}

}
