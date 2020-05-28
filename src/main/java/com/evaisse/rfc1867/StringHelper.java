package com.evaisse.rfc1867;

public class StringHelper {

    /**
     * Alias for javascript method "".slice()
     *
     * @param str  input string
     * @param from from index, can be negative to index from the end of the string
     * @param to   end index, can be negative to index from the end of the string
     * @return a given sliced string
     */
    public static String slice(String str, int from, int to) {
        from = Math.min(from, str.length());

        if (to < 0) {
            // handle negative index
            to = str.length() + to;
            to = Math.max(to, 0);
        }

        if (from < 0) { // handle negative index
            from = str.length() + from;
            from = Math.max(from, 0);
        }

        to = to < from
            ? from
            : (Math.min(to, str.length())); // reset TO to minimum from index.

        return str.substring(from, to);
    }

    public static String slice(String str, int from) {
        return slice(str, from, str.length());
    }

    public static String stringFromCharCode(int... codePoints) {
        return new String(codePoints, 0, codePoints.length);
    }
}
