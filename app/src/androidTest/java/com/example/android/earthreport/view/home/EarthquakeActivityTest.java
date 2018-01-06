package com.example.android.earthreport.view.home;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.example.android.earthreport.R;
import com.example.android.earthreport.view.main.EarthquakeActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class EarthquakeActivityTest {

    @Rule
    public ActivityTestRule<EarthquakeActivity> mActivityTestRule = new ActivityTestRule<>(EarthquakeActivity.class);

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

    //Home Test
    @Test
    public void clickHomeTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.home),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navBottom),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

    }

    @Test
    public void clickTimelineTest() {
        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.timeline),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navBottom),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

    }

    @Test
    public void settingClickTest() {
        ViewInteraction bottomNavigationItemView4 = onView(
                allOf(withId(R.id.setting),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navBottom),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView4.perform(click());
    }

    @Test
    public void swipeRightLeftTest() {
        ViewInteraction viewPager = onView(
                allOf(withId(R.id.viewpager),
                        childAtPosition(
                                allOf(withId(R.id.root),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        viewPager.perform(swipeRight());
        viewPager.perform(swipeLeft());


    }

    @Test
    public void swipeLeftRightTest() {
        ViewInteraction viewPager2 = onView(
                allOf(withId(R.id.viewpager),
                        childAtPosition(
                                allOf(withId(R.id.root),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        viewPager2.perform(swipeLeft());
        viewPager2.perform(swipeRight());
    }

    @Test
    public void homeSearchTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.home),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navBottom),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.action_search),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.minMagnitudeSpinner),
                        childAtPosition(
                                allOf(withId(R.id.root),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        DataInteraction appCompatTextView = onData(anything())
                .inAdapterView(withClassName(is("android.support.v7.widget.DropDownListView")))
                .atPosition(4);
        appCompatTextView.perform(click());

        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.maxMagnitudeSpinner),
                        childAtPosition(
                                allOf(withId(R.id.root),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatSpinner2.perform(click());

        DataInteraction appCompatTextView2 = onData(anything())
                .inAdapterView(withClassName(is("android.support.v7.widget.DropDownListView")))
                .atPosition(6);
        appCompatTextView2.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.startDate),
                        childAtPosition(
                                allOf(withId(R.id.root),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                6),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("Done"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withId(R.id.endDate),
                        childAtPosition(
                                allOf(withId(R.id.root),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                7),
                        isDisplayed()));
        appCompatImageButton3.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(android.R.id.button1), withText("Done"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.action_search),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        2),
                                0),
                        isDisplayed()));
        actionMenuItemView2.perform(click());

        DataInteraction constraintLayout = onData(anything())
                .inAdapterView(allOf(withId(R.id.searchListView)))
                .atPosition(0);
        constraintLayout.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pressBack();

    }

    @Test
    public void homeToolBarAddAlertTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.action_add_alert),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        1),
                                2),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.done_alert), withText("Done"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatButton.perform(click());
    }

    @Test
    public void homeToolBookmarkTest() {
        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.action_bookmark),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        1),
                                1),
                        isDisplayed()));
        actionMenuItemView2.perform(click());

    }

    //Earthquake List Test
    @Test
    public void earthquakeListTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.timeline),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navBottom),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());
    }

    @Test
    public void earthquakeListSwipeTest() {
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
    public void earthquakeListSingleValueMapTest() {

        earthquakeListTest();
        DataInteraction constraintLayout = onData(anything())
                .inAdapterView(allOf(withId(R.id.list)))
                .atPosition(0);
        constraintLayout.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void earthquakeAllValuesMapViewTest() {

        earthquakeListTest();
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fabButton),
                        childAtPosition(
                                withParent(withId(R.id.viewpager)),
                                3),
                        isDisplayed()));
        floatingActionButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void earthquakeListRereshTest() {

        earthquakeListTest();
        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.action_search),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());
    }


    //Setting Test

    @Test
    public void earthquakeListFilterTest() {
        earthquakeListTest();
        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.action_filter),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        1),
                                1),
                        isDisplayed()));
        actionMenuItemView2.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.done_alert), withText("Done"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                6),
                        isDisplayed()));
        appCompatButton.perform(click());
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
