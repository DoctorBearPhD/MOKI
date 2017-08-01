package com.example.ian.mobile_oki.data;

import android.annotation.SuppressLint;
import android.support.annotation.ColorRes;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;

import com.example.ian.mobile_oki.OkiApp;
import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.util.StringUtil;
import com.example.ian.mobile_oki.view.MainActivity;

/**
 * Utility class for handling operations regarding the Oki Timeline.
 * Created by Ian on 7/31/2017.
 */
public class OkiUtil {
    //TODO: be sure to remove the last newline character
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

        // get symbol representing "one frame" from the resources
        String frameSymbol = OkiApp.getContext().getResources()
                .getString(R.string.timeline_frame_symbol);

        /* create columns with dots until the start of the opponent's wakeup,
           then add wakeup frames, then add dots until max frames is reached */
        return new SpannedString[]{new SpannedString(fromHtml(makeOneColumn(kda, frameSymbol))),
                                   new SpannedString(fromHtml(makeOneColumn(kdr, frameSymbol))),
                                   new SpannedString(fromHtml(makeOneColumn(kdbr, frameSymbol)))
                                  };
    }

    private static String makeOneColumn(int kda, String frameSymbol) {
        int maxFrames = MainActivity.MAX_TIMELINE_FRAMES;
        String column = StringUtil.repeat(frameSymbol + '\n', kda)
                .concat(getColoredWakeupNumbers())
                .concat(StringUtil.repeat(frameSymbol + '\n', maxFrames - (kda + 10)));

        // remove last newline and return string
        return column.substring(0, column.length());
    }

    private static String getColoredWakeupNumbers() {
        // open a font color tag
        String result = "<font color=" + getWakeupColor() + ">";

        // add numbers 1 to 0 (1 to 10)
        // add and underline the first frame of wakeup
        result = result.concat("<u>1</u>\n");

        for (int i = 2; i <= 10; i++) {
            result = result.concat(String.valueOf(i % 10) + '\n');
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
}
