package com.example.ian.mobile_oki.util;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;

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

        /* To show colors, must use Html.fromHtml(source)
         * which is deprecated as of API Level 24 (Android N);
         *
         * but getColor(id) is also deprecated as of API Level 23 (Android M),
         * so must use getColor(id, theme)
         */
        String color = getWakeupColor();

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

    private static String makeOneKDAColumn(int kda) {
        int maxFrames = MainActivity.MAX_TIMELINE_FRAMES;
        String column = makeDots(kda)
                .concat(getColoredWakeupNumbers(maxFrames - kda))
                .concat(makeDots(maxFrames - (kda + 10)));

        // remove last newline and return string
        return column.substring(0, column.length() - 5); // <br/> = 5 characters
    }

    public static SpannedString generateOkiColumnContent(int okiRowIndex, OkiMoveListItem okiMove) {

        return new SpannedString(fromHtml(makeOneOkiColumn(
                okiMove.getStartup(),
                okiMove.getActive(),
                okiMove.getRecovery(),
                okiRowIndex)));
    }

    private static String makeOneOkiColumn(int startup, int active, int recovery, int currentRow) {
        startup = (startup == 0) ? 0 : (startup - 1); // don't adjust if startup is 0 (e.g. dash)
        int total = startup + active + recovery;

        // fill with dots until "current row"
        String column = makeDots(currentRow);
        // color the frame data
        column = column.concat("<font color=" + getFrameDataColor() + ">");
        // fill with "s" for each startup frame
        column = column.concat(StringUtil.repeat("s"+"<br/>",startup));
        // fill with "A" for each active frame
        column = column.concat(StringUtil.repeat("A"+"<br/>",active));
        // fill with "r" for each recovery frame
        column = column.concat(StringUtil.repeat("r"+"<br/>",recovery));
        // stop coloring and fill remaining with dots
        column = column.concat("</font>" +
                makeDots((MainActivity.MAX_TIMELINE_FRAMES - (currentRow + total) - 1)));

        return column.substring(0, column.length() - 5);
    }

    private static String makeDots(int amount) {
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
                .substring(3); // 0 = "#" , 1&2=alpha channel
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