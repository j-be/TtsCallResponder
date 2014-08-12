package org.duckdns.raven.ttscallresponder.domain.responseTemplate;

import java.util.Iterator;

import org.duckdns.raven.ttscallresponder.dataAccess.SettingsManager;
import org.duckdns.raven.ttscallresponder.domain.common.AbstractPersistentList;

import android.content.Context;

/**
 * Persistent list for {@link ResponseTemplate} objects.
 * 
 * FIXME: License
 * 
 * @see AbstractPersistentList
 * 
 * @author Juri Berlanda
 * 
 */
public class PersistentResponseTemplateList extends AbstractPersistentList<ResponseTemplate> {
	private static final String TAG = "PersistentResponseTemplateList";

	private static PersistentResponseTemplateList singleton = null;

	private final SettingsManager settingsManager;

	public static synchronized void initSingleton(Context context) {
		if (PersistentResponseTemplateList.singleton == null) {
			PersistentResponseTemplateList.singleton = new PersistentResponseTemplateList(context);
			PersistentResponseTemplateList.singleton.loadPersistentList();
		}
	}

	public static PersistentResponseTemplateList getSingleton() {
		return PersistentResponseTemplateList.singleton;
	}

	/**
	 * Default constructor
	 * 
	 * @param parent
	 *            the {@link Context} the list is running in
	 */
	private PersistentResponseTemplateList(Context parent) {
		super(parent.getFilesDir());
		this.settingsManager = new SettingsManager(parent);
	}

	/**
	 * Getter for the currently active {@link ResponseTemplate}
	 * 
	 * @return the currently active {@link ResponseTemplate}
	 */
	public ResponseTemplate getCurrentResponseTemplate() {
		Iterator<ResponseTemplate> iter = this.getPersistentList().iterator();
		ResponseTemplate item = null;

		while (iter.hasNext()) {
			item = iter.next();
			if (item.getId() == this.settingsManager.getCurrentResponseTemplateId())
				return item;
		}

		return null;
	}

	@Override
	protected String getFileName() {
		return "responseTemplateList";
	}

	/**
	 * Custom list load function. This is necessary for setting the highest
	 * known ID on {@link ResponseTemplate}.
	 */
	@Override
	public void loadPersistentList() {
		super.loadPersistentList();
		long maxId = -1;

		Iterator<ResponseTemplate> iter = this.getPersistentList().iterator();
		while (iter.hasNext())
			maxId = Math.max(maxId, iter.next().getId());
		ResponseTemplate.setHighestUsedId(maxId);
	}

}
