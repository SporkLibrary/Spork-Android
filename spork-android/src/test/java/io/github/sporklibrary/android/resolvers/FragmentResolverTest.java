package io.github.sporklibrary.android.resolvers;

import org.junit.Test;

import static org.junit.Assert.assertNull;

public class FragmentResolverTest {

	@Test
	public void testFaulty() {
		DefaultFragmentResolver resolver = new DefaultFragmentResolver();
		assertNull(resolver.resolveFragment("Nothing", 1));
		assertNull(resolver.resolveFragment("Nothing", "anything"));
	}
}
