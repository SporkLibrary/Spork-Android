package spork.android;

import spork.Spork;
import spork.android.interfaces.ContextResolver;
import spork.android.interfaces.FragmentResolver;
import spork.android.interfaces.Registry;
import spork.android.interfaces.SporkAndroidExtension;
import spork.android.interfaces.ViewResolver;
import spork.android.internal.ContextResolverManager;
import spork.android.internal.DefaultContextResolver;
import spork.android.internal.DefaultFragmentResolver;
import spork.android.internal.DefaultViewResolver;
import spork.android.internal.FragmentResolverManager;
import spork.android.internal.ViewResolverManager;
import spork.android.internal.binders.BindClickBinder;
import spork.android.internal.binders.BindFragmentBinder;
import spork.android.internal.binders.BindLayoutBinder;
import spork.android.internal.binders.BindResourceBinder;
import spork.android.internal.binders.BindViewBinder;
import spork.BinderRegistry;
import spork.SporkExtension;

/**
 * Extension that adds binders that provide Android-specific features.
 * This extension is automatically resolved by the spork-android module.
 */
public final class SporkAndroid implements SporkExtension {
	private final FragmentResolverManager fragmentResolverManager = new FragmentResolverManager();
	private final ViewResolverManager viewResolverManager = new ViewResolverManager();
	private final ContextResolverManager contextResolverManager = new ContextResolverManager();

	public SporkAndroid() {
		fragmentResolverManager.register(new DefaultFragmentResolver());
		viewResolverManager.register(new DefaultViewResolver());
		contextResolverManager.register(new DefaultContextResolver());
	}

	@Override
	public void initialize(Spork spork) {
		BinderRegistry binderRegistry = spork.getBinderRegistry();

		binderRegistry.register(new BindLayoutBinder()); // layouts must be bound before views
		binderRegistry.register(new BindViewBinder(viewResolverManager));
		binderRegistry.register(new BindFragmentBinder(fragmentResolverManager));
		binderRegistry.register(new BindClickBinder(viewResolverManager));
		binderRegistry.register(new BindResourceBinder(contextResolverManager));

		initializeExtension("spork.android.support.SporkAndroidSupport");
	}

	/**
	 * Try to initialize a SporkAndroidExtension.
	 * Fails without throwing an exception if the extension is not present.
	 *
	 * @param extensionClassName the SporkAndroidExtension class name
	 */
	@SuppressWarnings("PMD.EmptyCatchBlock")
	private void initializeExtension(String extensionClassName) {
		try {
			Class<?> extensionClass = Class.forName(extensionClassName);
			Object extensionObject = extensionClass.newInstance();
			if (extensionObject instanceof SporkAndroidExtension) {
				SporkAndroidExtension extension = (SporkAndroidExtension) extensionObject;
				extension.initialize(this);
			}
		} catch (ClassNotFoundException e) {
			// no-op
		} catch (IllegalAccessException e) {
			System.out.println("SporkAndroid: extension " + extensionClassName + "found, but initialization failed because of IllegalAccessException: " + e.getMessage());
		} catch (InstantiationException e) {
			System.out.println("SporkAndroid: extension " + extensionClassName + "found, but failed to create instance: " + e.getMessage());
		}
	}

	/**
	 * @return the Registry to register new FragmentResolver instances
	 */
	public Registry<FragmentResolver> getFragmentResolverRegistry() {
		return fragmentResolverManager;
	}

	/**
	 * @return the Registry to register new ViewResolver instances
	 */
	public Registry<ViewResolver> getViewResolverRegistry() {
		return viewResolverManager;
	}

	/**
	 * @return the Registry to register new ContextResolver instances
	 */
	public Registry<ContextResolver> getContextResolverRegistry() {
		return contextResolverManager;
	}
}
