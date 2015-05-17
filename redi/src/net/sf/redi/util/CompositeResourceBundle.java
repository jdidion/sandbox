/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi.util;

import java.util.*;

/** A ResourceBundle that is a proxy to multiple ResourceBundles. */
public class CompositeResourceBundle extends ResourceBundle {
	private Locale _locale = Locale.getDefault();
	private List<String> _resources = new ArrayList<String>();

    public CompositeResourceBundle() {
    }

    public CompositeResourceBundle(Locale locale) {
        this(locale, null);
    }

	public CompositeResourceBundle(String... resources) {
        this(null, resources);
    }

    public CompositeResourceBundle(Locale locale, String... resources) {
        setLocale(locale);
		for (String resource : resources) {
			addResource(resource);
        }
	}

	public void addResource(String resource) {
		_resources.add(resource);
	}

	public List<String> getResources() {
		return Collections.unmodifiableList(_resources);
	}

	public void setLocale(Locale locale) {
		_locale = locale;
	}

	protected Object handleGetObject(String key) {
		for (String resource : _resources) {
			try {
				ResourceBundle bundle = getBndl(resource);
				return bundle.getString(key);
			} catch (MissingResourceException ex) {
			}
		}
		return key;
	}

	public Enumeration getKeys() {
		return new Enumeration() {
			private Iterator _itr = _resources.iterator();
			private Enumeration _currentEnum;

			public boolean hasMoreElements() {
				if (_currentEnum == null || !_currentEnum.hasMoreElements()) {
					if (_itr.hasNext()) {
						_currentEnum = getBndl((String)_itr.next()).getKeys();
					}
				}
				if (_currentEnum != null) {
					return _currentEnum.hasMoreElements();
				}
				return false;
			}


			public Object nextElement() {
				return _currentEnum.nextElement();
			}
		};
	}

	private ResourceBundle getBndl(String bundle) {
		return ResourceBundle.getBundle(bundle, _locale);
	}
}