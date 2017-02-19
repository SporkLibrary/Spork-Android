package spork.android.support.test.bindfragment.domain;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import spork.Spork;
import spork.android.BindFragment;
import spork.android.support.test.R;
import spork.android.BindLayout;

@BindLayout(R.layout.activity_fragment_binding_support)
public class TestActivitySupport extends AppCompatActivity {

    @BindFragment(R.id.testfragment)
    private TestFragmentSupport fragment;

    @BindFragment
    private TestFragmentSupport testfragment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Spork.bind(this);
    }

    public Fragment getFragmentByIdSpecified() {
        return fragment;
    }

    public Fragment getFragmentByIdImplied() {
        return testfragment;
    }

    public TestFragmentSupport getTestFragment() {
        return fragment;
    }
}
