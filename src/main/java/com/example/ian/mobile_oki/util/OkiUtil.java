package com.example.ian.mobile_oki.util;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.util.Log;

import com.example.ian.mobile_oki.OkiApp;
import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.data.KDMoveListItem;
import com.example.ian.mobile_oki.data.OkiMoveListItem;
import com.example.ian.mobile_oki.view.MainActivity;

/**
 * Utility class for handling operations regarding the Oki Timeline.
 * Created by Ian on 7/31/2017.
 */
public class OkiUtil {
    public static SpannedString[] generateKDAdvColumnContent(KDMoveListItem kdData) {

        int     kda  = kdData.getKda(),
                kdr  = kdData.getKdra(),
                kdbr = kdData.getKdbra();

        /* create columns with dots until the start of the opponent's wakeup,
           then add wakeup frames, then add dots until max frames is reached */
        return new SpannedString[]{new SpannedString(fromHtml(makeOneKDAColumn(kda))),
                                   new SpannedString(fromHtml(makeOneKDAColumn(kdr))),
                                   new SpannedString(fromHtml(makeOneKDAColumn(kdbr)))
                                  };
    }

    public static SpannedString generateOkiColumnContent(int okiRowIndex, OkiMoveListItem okiMove) {
        /*
         * To show colors, must use Html.fromHtml(source)
         * which is deprecated as of API Level 24 (Android N);
         */
        return new SpannedString(fromHtml(makeOneOkiColumn(
                okiMove.getStartup(),
                okiMove.getActive(),
                okiMove.getRecovery(),
                okiRowIndex)));
    }

    public static SpannedString generateEmptyColumnContent() {
        String dots = makeDots(MainActivity.MAX_TIMELINE_FRAMES);
        dots = dots.substring(0, dots.length() - 5); // remove last "<br/>"

        return new SpannedString(fromHtml(dots));
    }

    private static String makeOneKDAColumn(int kda) {
        int maxFrames = MainActivity.MAX_TIMELINE_FRAMES;
        String column;

        // If kda is greater than maxFrames (120), then just make maxFrames (120) dots.
        if (kda >= maxFrames)
            column = makeDots(maxFrames);
        else
            column = makeDots(kda)
                    .concat(getColoredWakeupNumbers(maxFrames - kda))
                    .concat(makeDots(maxFrames - (kda + 10)));

        // remove last newline and return string
        return column.substring(0, column.length() - 5); // <br/> = 5 characters
    }

    private static String makeOneOkiColumn(int startup, String active, int recovery, int currentRowIndex) {
        int combinedActive, amountToFill, remaining = MainActivity.MAX_TIMELINE_FRAMES;

        // Moves with multiple hits are displayed differently.
        combinedActive = (isInteger(active)) ? Integer.valueOf(active) : combineActive(active);

        // Adjust startup value to display [first active frame] on [last startup frame].
        // If the move is not an attack (e.g. dash; i.e. something not listed as having
        //   startup, active, and recovery), then don't adjust.
        startup = startup - (combinedActive > 0 || (recovery > 0 && combinedActive == 0) ? 1 : 0);
        int total = startup + combinedActive + recovery;

        // fill with dots until "current row index" (until just before the selected row)
        String column = makeDots(currentRowIndex);
        remaining -= currentRowIndex;

        // color the frame data
        column = column.concat("<font color=" + getFrameDataColor() + ">");

        // fill with "s" for each startup frame
        if (startup > 0){
            if (remaining - startup >= 0)
                 amountToFill = startup;
            else amountToFill = remaining;

            column = column.concat(StringUtil.repeat("s" + "<br/>", amountToFill));

            remaining -= amountToFill;
            if (remaining == 0)
                return column.substring(0, column.length() - 5).concat("</font>");
        }

        // fill with "A" for each active frame
        if (combinedActive > 0) { // if there's something to show,
            if (remaining - combinedActive >= 0)  // if there's enough space to show all the [active frames],
                 amountToFill = combinedActive;
            else amountToFill = remaining;        // else, if there's not enough space,

            if (String.valueOf(combinedActive).equals(active))
                column = column.concat(StringUtil.repeat("A" + "<br/>", amountToFill));
            else
                column = column.concat(parseActive(active, amountToFill));

            remaining -= amountToFill;
            if (remaining == 0) { // if there's no more room,
                return column.substring(0, column.length() - 5).concat("</font>");
            }
        }

        // fill with "r" for each recovery frame
        if (recovery > 0) {

            if (remaining - recovery >= 0) amountToFill = recovery;
            else amountToFill = remaining;

            column = column.concat(StringUtil.repeat("r" + "<br/>", amountToFill));

            remaining -= amountToFill;
        }
        // stop coloring and fill remaining with dots
        if (remaining > 0) {
            column = column.concat("</font>" + makeDots(remaining));
            return column.substring(0, column.length() - 5);
        } else
            return column.substring(0, column.length() - 5).concat("</font>");
    }

    private static boolean isInteger(String active) {
        if (active.isEmpty()) return false;

        for (char c : active.toCharArray()) {
            if( ! Character.isDigit(c) ) return false;
        }

        return true;
    }

    private static String parseActive(String active, int remaining) {
        String[] numberStrings = active.split("[()]");
        // ? - handle blank?
        String result = "";
        try {
            for (int i = 0; i < numberStrings.length; i++) {
                // form oki visualization based on index
                if (remaining > 0) {
                    result = result.concat(StringUtil.repeat((i % 2 == 0) ? "A<br/>" : "r<br/>",
                            Integer.valueOf(numberStrings[i]))
                    );
                    remaining--;
                } else break; // break out of loop if there's no more room
            } // end for loop
        } catch (NumberFormatException e) {
            Log.e("OkiUtil", "parseActive: Not a number! Either a parenthesis wasn't found, or a number wasn't found...");
        }
//        String oneLineResult = "";
//        for (String s : result.split("<br/>")){
//            oneLineResult = oneLineResult.concat(s);
//        }
//        Log.d("OkiUtil", "parseActive: result = " + oneLineResult);
        return result;
    }

    private static int combineActive(String active) {
        String[] numbers = active.split("[()]");
        int sum = 0;

        try {
            for (String s : numbers) {
                sum += Integer.parseInt(s);
            } //end for loop
        } catch (NumberFormatException e) {
            Log.e("OkiUtil", "combineActive: Not a number! Either a parenthesis wasn't found, or a number wasn't found...");
        }
        return sum;
    }

    private static String makeDots(int amount) {
        // If the amount of remaining frames is < 1, there's nothing to do here!
        if (amount < 1) return "";

        // get the symbol representing "one frame" from the resources
        String frameSymbol = OkiApp.getContext().getResources()
                .getString(R.string.timeline_frame_symbol);

        return StringUtil.repeat(frameSymbol+"<br/>", amount);
    }

    private static String getColoredWakeupNumbers(final int remaining) {
        // open a font color tag
        String result = "<font color=" + getWakeupColor() + ">";

        // add numbers 1 to 0 (1 to 10)
        // add and underline the first frame of wakeup
        result = result.concat("<u>1</u><br/>");

        for (int i = 2; i <= 10 && i <= remaining; i++) {
            result = result.concat(String.valueOf(i % 10) + "<br/>");
        }

        // close font color tag and return the string
        return result.concat("</font>");
    }

    /**
     * @return a {@link String} representation of the color used to highlight wakeup frames
     */
    @SuppressLint("ResourceType")
    private static String getWakeupColor() {
        return "#"+OkiApp.getContext().getResources().getString(R.color.secAccent)
                .substring(3); // 0 = "#" , 1&2=alpha channel
    }

    /**
     * @return a {@link String} representation of the color used to highlight frame data on the Timeline
     */
    @SuppressLint("ResourceType")
    private static String getFrameDataColor() {
        return "#"+OkiApp.getContext().getResources().getString(R.color.terLight)
                .substring(3); // index 0 = "#" , 1 & 2 = alpha channel
    }

    /**
     * Html Compat method to transform HTML string into {@code Spanned} using
     * version-safe methods.
     *
     * @param source the HTML String to transform
     * @return {@link Spanned} form of HTML string for display in
     * {@link android.widget.TextView TextViews} etc.
     */
    private static Spanned fromHtml(String source) {
        Spanned result;

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
            // version is below N
            result = Html.fromHtml(source);
        } else {
            // version is N or above
            result = Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        }

        return result;
    }

    /**
     * Version-safe implementation of getColor().
     *
     * <p>getColor(id) is deprecated as of API Level 23 (Android M),
     * so {@code getColor(id, theme)} must be used on later versions.</p>
     *
     * @param resId resource id of the desired color resource
     * @return color resource's value
     */
    public static int getColor(int resId) {
        if (Build.VERSION.SDK_INT < 23)
            return OkiApp.getContext().getResources().getColor(resId);
        else
            return OkiApp.getContext().getResources().getColor(resId, null);
    }
}
