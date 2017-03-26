package spork.inject.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Provider;
import javax.inject.Qualifier;

import spork.inject.internal.lang.Annotations;
import spork.inject.internal.lang.Nullability;

@ThreadSafe
class InjectSignatureCache {
	/**
	 * Map values may be null.
	 */
	private final Map<Field, InjectSignature> fieldInjectSignatureMap;

	/**
	 * Map values may be null.
	 */
	private final Map<Method, InjectSignature[]> methodInjectSignatureMap;

	private final QualifierFactory qualifierFactory;

	/**
	 * Primary constructor.
	 */
	InjectSignatureCache(Map<Field, InjectSignature> fieldInjectSignatureMap, Map<Method, InjectSignature[]> methodInjectSignatureMap, QualifierFactory qualifierFactory) {
		this.fieldInjectSignatureMap = fieldInjectSignatureMap;
		this.methodInjectSignatureMap = methodInjectSignatureMap;
		this.qualifierFactory = qualifierFactory;
	}

	/**
	 * Secondary constructor.
	 */
	InjectSignatureCache(Map<Field, InjectSignature> fieldInjectSignatureMap, Map<Method, InjectSignature[]> methodInjectSignatureMap) {
		this(fieldInjectSignatureMap, methodInjectSignatureMap, new QualifierFactory());
	}

	/**
	 * Secondary constructor.
	 */
	InjectSignatureCache() {
		this(new HashMap<Field, InjectSignature>(), new HashMap<Method, InjectSignature[]>());
	}

	// region Fields

	/**
	 * Get an InjectSignature instance.
	 * It's either retrieved from cache or it is created and stored in cache.
	 * @param field the target field
	 * @param targetType the real target type (not a {@link Provider})
	 * @return an InjectSignature
	 */
	InjectSignature getInjectSignature(Field field, Class<?> targetType) {
		synchronized (fieldInjectSignatureMap) {
			if (fieldInjectSignatureMap.containsKey(field)) {
				return fieldInjectSignatureMap.get(field);
			} else {
				InjectSignature injectSignature = createInjectSignature(field, targetType);
				fieldInjectSignatureMap.put(field, injectSignature);
				return injectSignature;
			}
		}
	}

	private InjectSignature createInjectSignature(Field field, Class<?> targetType) {
		Annotation qualifierAnnotation = Annotations.findAnnotationAnnotatedWith(Qualifier.class, field);
		Nullability nullability = Nullability.create(field);
		String qualifier = qualifierAnnotation != null
				? getQualifier(qualifierAnnotation)
				: null;
		return new InjectSignature(targetType, nullability, qualifier);
	}

	// endregion

	// region Methods

	/**
	 * Get the InjectSignature instanaces for the given Method's parameters.
	 * It is either retrieved from cache or otherwise created, cached and returned.
	 * @param method the method to analyze
	 * @return an array of 1 or more InjectSignature instances or null (never an empty array!)
	 */
	@Nullable
	InjectSignature[] getInjectSignatures(Method method) {
		synchronized (fieldInjectSignatureMap) {
			if (methodInjectSignatureMap.containsKey(method)) {
				return methodInjectSignatureMap.get(method);
			} else {
				InjectSignature[] signatures = createInjectSignatures(method);
				methodInjectSignatureMap.put(method, signatures);
				return signatures;
			}
		}
	}

	/**
	 * Cache the InjectSignature instances for the given Method's parameters
	 * @param method the method to analyze
	 * @return an array of 1 or more InjectSignature instances or null (never an empty array!)
	 */
	@Nullable
	private InjectSignature[] createInjectSignatures(Method method) {
		if (method.getParameterTypes().length == 0) {
			return null;
		}

		int parameterCount = method.getParameterTypes().length;
		InjectSignature[] injectSignatures = new InjectSignature[parameterCount];
		for (int i = 0; i < parameterCount; ++i) {
			injectSignatures[i] = createInjectSignature(method, i);
		}
		return injectSignatures;
	}

	/**
	 * Create the InjectSignature for a specific Method parameter.
	 */
	private InjectSignature createInjectSignature(Method method, int parameterIndex) {
		Class<?> parameterClass = method.getParameterTypes()[parameterIndex];

		Annotation[] annotations = method.getParameterAnnotations()[parameterIndex];
		Nullability nullability = Nullability.create(annotations);
		Class<?> targetType = (parameterClass == Provider.class)
				? (Class<?>) ((ParameterizedType) method.getGenericParameterTypes()[parameterIndex]).getActualTypeArguments()[0]
				: parameterClass;

		Annotation qualifierAnnotation = Annotations.findAnnotationAnnotatedWith(Qualifier.class, annotations);
		String qualifier = qualifierAnnotation != null
				? getQualifier(qualifierAnnotation)
				: null;

		return new InjectSignature(targetType, nullability, qualifier);
	}

	// endregion

	// region Qualifiers

	String getQualifier(Annotation qualifierAnnotation) {
		return qualifierFactory.create(qualifierAnnotation);
	}

	// endregion
}
