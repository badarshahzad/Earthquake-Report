package com.example.android.earthreport.controller.home;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.example.android.earthreport.R;
import com.example.android.earthreport.controller.main.EarthquakeActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SettingTest {

    @Rule
    public ActivityTestRule<EarthquakeActivity> mActivityTestRule = new ActivityTestRule<>(EarthquakeActivity.class);

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @Test
    public void settingTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.setting),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navBottom),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());
    }

    @Test
    public void settingSwipeTest() {
        ViewInteraction viewPager = onView(
                allOf(withId(R.id.viewpager),
                        childAtPosition(
                                allOf(withId(R.id.root),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        viewPager.perform(swipeLeft());
    }

    @Test
    public void settingAlertNotificationTest() {
        settingTest();
        DataInteraction linearLayout = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list)))
                .atPosition(1);
        linearLayout.perform(click());
    }

    @Test
    public void settingPeriodTest() {
        settingTest();
        DataInteraction linearLayout2 = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list)))
                .atPosition(3);
        linearLayout2.perform(click());

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(allOf(withClassName(is("com.android.internal.app.AlertController$RecycleListView"))))
                .atPosition(1);
        appCompatCheckedTextView.perform(click());
    }

    @Test
    public void settingFilterTest() {
        settingTest();
        DataInteraction linearLayout3 = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list)))
                .atPosition(4);
        linearLayout3.perform(click());

        DataInteraction appCompatCheckedTextView2 = onData(anything())
                .inAdapterView(allOf(withClassName(is("com.android.internal.app.AlertController$RecycleListView"))))
                .atPosition(4);
        appCompatCheckedTextView2.perform(click());
    }

    @Test
    public void settingSortByTest() {
        settingTest();
        DataInteraction linearLayout4 = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list)))
                .atPosition(5);
        linearLayout4.perform(click());


    DataInteraction appCompatCheckedTextView3 = onData(anything())
            .inAdapterView(allOf(withClassName(is("com.android.internal.app.AlertController$RecycleListView"))))
            .atPosition(1);
        appCompatCheckedTextView3.perform(
    click());
    }

    //This test is not working and effecting other test
    /*
        @Test
        public void settingNotificaitonToneTest() {
        settingTest();
        DataInteraction linearLayout6 = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list)))
                .atPosition(7);
        linearLayout6.perform(click());

    }*/

    @Test
    public void settingVibrteTest() {
        settingTest();
        DataInteraction linearLayout5 = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list)))
                .atPosition(8);
        linearLayout5.perform(

                click());

    }
}
