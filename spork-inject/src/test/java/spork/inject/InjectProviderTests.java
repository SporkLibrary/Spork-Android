package spork.inject;

import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Provider;

import spork.inject.internal.ObjectGraphBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class InjectProviderTests {

	/**
	 * A module that increases a counter every time a method is called.
	 */
	private static class Module {
		private int counter = 0;

		@Provides
		public Integer integerValue() {
			return ++counter;
		}
	}


	private static class Parent {
		@Inject
		Provider<Integer> provider;
	}

	/**
	 * Test that when Provider.get() is called multiple times,
	 * a new instance is returned every time.
	 */
	@Test
	public void multipleGet() {
		// given
		Parent parent = new Parent();

		// when
		new ObjectGraphBuilder()
				.module(new Module())
				.build()
				.inject(parent);

		Integer first = parent.provider.get();
		Integer second = parent.provider.get();

		// then
		assertThat(first, is(1));
		assertThat(second, is(2));
	}
}
