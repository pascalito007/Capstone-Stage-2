package capstone.nanodegree.udacity.com.mypodcast;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import capstone.nanodegree.udacity.com.mypodcast.activity.EpisodeActivity;

/**
 * Created by jem001 on 12/01/2018.
 */

@RunWith(AndroidJUnit4.class)
public class EpisodeActivityInstrumentedTest {
    @Rule
    public ActivityTestRule<EpisodeActivity> mActivityTestRule = new ActivityTestRule<>(EpisodeActivity.class);
    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
    }



    @Test
    public void idlingResourceTest() {
        Espresso.onView(ViewMatchers.withId(R.id.rv_episode)).perform(RecyclerViewActions.actionOnItemAtPosition(1, ViewActions.click()));
        Espresso.onView(ViewMatchers.withId(R.id.tv_description)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
