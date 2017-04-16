package spork.internal;

import java.util.List;

import spork.Binder;

/**
 * This {@link Binder} implementation is the main (public) entry point for binding instances.
 */
public class BinderImpl implements Binder {
	private final BinderCache binderCache;

	public BinderImpl(BinderCache binderCache) {
		this.binderCache = binderCache;
	}

	@Override
	public void bind(Object object, Object... parameters) {
		Class<?> objectClass = object.getClass();

		while (objectClass != null && objectClass != Object.class) {
			List<Binder> binders = binderCache.getBinders(objectClass);
			bind(object, binders, parameters);
			objectClass = objectClass.getSuperclass();
		}
	}

	/**
	 * Bind all annotations for an object instance for one specific class at a single level of inheritance.
	 *
	 * @param object  the instance to bind annotations for
	 * @param binders the list of cached binders
	 * @param parameters optional parameters
	 */
	private void bind(Object object, List<Binder> binders, Object... parameters) {
		for (Binder binder : binders) {
			binder.bind(object, parameters);
		}
	}
}
