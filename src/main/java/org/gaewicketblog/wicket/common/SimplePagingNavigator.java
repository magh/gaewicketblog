package org.gaewicketblog.wicket.common;

import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigationIncrementLink;
import org.apache.wicket.markup.html.panel.Panel;

@SuppressWarnings("serial")
public class SimplePagingNavigator extends Panel {

	private final IPageable pageable;

	public SimplePagingNavigator(final String id, final IPageable pageable) {
		super(id);
		this.pageable = pageable;
	}

	@Override
	protected void onBeforeRender() {
		PagingNavigationIncrementLink<Void> prev = (PagingNavigationIncrementLink<Void>) get("prev");
		PagingNavigationIncrementLink<Void> next = (PagingNavigationIncrementLink<Void>) get("next");
		if (prev == null) {
			prev = new PagingNavigationIncrementLink<Void>("prev", pageable, -1);
			add(prev);
			next = new PagingNavigationIncrementLink<Void>("next", pageable, 1);
			add(next);
		}
		prev.setVisible(!prev.isFirst());
		next.setVisible(!next.isLast());
		super.onBeforeRender();
	}

}
