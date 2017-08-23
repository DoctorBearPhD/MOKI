package com.example.ian.mobile_oki.view;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.widget.TextView;

import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.data.CharacterDatabase;
import com.example.ian.mobile_oki.util.StringUtil;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;

/**
 * Created to test the Timeline columns' content generation.
 * <p/>
 * Created by Ian on 8/23/2017.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    // Should learn to create Test Suite
    @Test public void columnContentsShouldNotBleedPastEndOfTimeline() throws Exception{
        selectCharacter_Balrog();
        assertEquals("BSN", CharacterDatabase.getInstance().getCurrentCharacter(false));

        selectKDMove_crouchHP1();
        assertEquals("(CH) crouch HP 1", CharacterDatabase.getInstance().getCurrentKDMove().getMoveName());

        assertEquals(StringUtil.repeat("\u00b7"+"\n", 7)+"1\n2\n3",
                this.mMainActivityTestRule.getActivity().mBodyBinding.tvBodyKd
                        .getText().toString().substring(240-1-19, 240-1));

        int row = 117;
        selectRow(row);

        selectOkiMove_dash();
        assertEquals("dash forward", CharacterDatabase.getInstance().getCurrentOkiMoveAt(1).getMove());
        assertEquals(120*2-1,
                ((TextView) mMainActivityTestRule.getActivity().findViewById(R.id.tv_body_oki_1))
                        .getText().length());
    }

    /** Have to manually scroll for some reason. */
    private void selectRow(int row) {
        // select the row
        onData(anything()).inAdapterView(withId(R.id.lv_row_selector)).atPosition(row - 1).perform(click());
    }

    private void selectCharacter_Balrog() {
        openDrawerAndPickItem(0);

        // scroll to Boxer
        onView(withId(R.id.rv_names)).perform(
                scrollTo(hasDescendant(withText("BOXER"))));

        // click Boxer
        onView(allOf(isDescendantOfA(isAssignableFrom(RecyclerView.class)),
                withText("BOXER"))).perform(click());
    }

    private void openDrawerAndPickItem(int menuItemPosition) {
        // open the drawer
        onView(withId(R.id.dl_nav_drawerlayout))
                .check(matches(isClosed(Gravity.START)))
                .perform(open());

        // click menu item (e.g. Character Select)
        onData(anything()).inAdapterView(withId(R.id.lv_nav_menu))
                .atPosition(menuItemPosition).perform(click());
    }

    private void selectKDMove_crouchHP1() {
        Matcher<android.view.View> matcher = allOf(
                hasDescendant(withText("117")),
                hasDescendant(withText("58")),
                hasDescendant(withText("63"))
        );

        onView(withId(R.id.rv_kdmoves))
                .perform( scrollTo(matcher) );

        onView(allOf(isDescendantOfA(isAssignableFrom(RecyclerView.class)), matcher))
                .perform( click() );
    }

    private void selectOkiMove_dash() {
        openDrawerAndPickItem(2);

        Matcher<android.view.View> matcher = allOf(
                hasDescendant(withText("0")),
                hasDescendant(withText("17"))
        );

        onView(withId(R.id.rv_okimoves)).perform(scrollTo(matcher));
        onView(withId(R.id.rv_okimoves)).perform(actionOnItem(matcher, click()));
    }
}