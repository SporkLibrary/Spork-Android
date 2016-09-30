package io.github.sporklibrary.android.internal.binders;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import io.github.sporklibrary.android.annotations.BindLayout;
import io.github.sporklibrary.annotations.Nullable;
import io.github.sporklibrary.exceptions.BindException;
import io.github.sporklibrary.interfaces.TypeBinder;

public class BindLayoutBinder implements TypeBinder<BindLayout> {

	@Override
	public void bind(Object object, BindLayout annotation, Class<?> annotatedType, @Nullable Object[] modules) {
		int layout_resource_id = annotation.value();

		if (Activity.class.isAssignableFrom(object.getClass())) {
			((Activity) object).setContentView(layout_resource_id);
		} else if (ViewGroup.class.isAssignableFrom(object.getClass())) {
			ViewGroup view_group = (ViewGroup) object;
			LayoutInflater.from(view_group.getContext()).inflate(layout_resource_id, view_group);
		} else {
			throw new BindException(BindLayout.class, object.getClass(), "annotation can only be used with Activity or ViewGroup");
		}
	}

	@Override
	public Class<BindLayout> getAnnotationClass() {
		return BindLayout.class;
	}
}
