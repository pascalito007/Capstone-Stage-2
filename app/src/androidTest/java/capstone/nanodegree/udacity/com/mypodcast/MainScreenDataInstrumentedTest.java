package capstone.nanodegree.udacity.com.mypodcast;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import capstone.nanodegree.udacity.com.mypodcast.IdlingResource.SimpleIdlingResource;

/**
 * Created by jem001 on 02/01/2018.
 */

@RunWith(AndroidJUnit4.class)
public class MainScreenDataInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
    }


    @Test
    public void idlingResourceTest() {
        Espresso.onView(ViewMatchers.withId(R.id.rv_recommendations)).perform(RecyclerViewActions.actionOnItemAtPosition(3, ViewActions.click()));
        Espresso.onView(ViewMatchers.withId(R.id.podcast_img_clean)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
