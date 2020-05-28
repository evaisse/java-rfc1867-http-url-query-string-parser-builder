package com.evaisse.rfc1867;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.MatchResult;

public class URLQueryEncoderDecoder {

    protected String charset = "utf-8";

    public URLQueryEncoderDecoder() {}

    public URLQueryEncoderDecoder(String charset) {
        if (charset != null) {
            this.charset = charset;
        }
    }

    public static String encode(String str) {
        return "";
    }

    public String decode(String str) {
        String strWithoutPlus = str.replace('+', ' ');

        if (charset.equals("iso-8859-1")) {
            // unescape never throws, no try...catch needed:
            return ReplaceCallback.replace(
                    "/%[0-9a-f]{2}/gi",
                    str,
                    new ReplaceCallback.Callback() {
                        public String matchFound(MatchResult match) {
                            try {
                                return URLDecoder.decode(match.group(1), "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                return "";
                            }
                        }
                    });
        }

        return this.unescape(strWithoutPlus);
    };

    /**
     * @param str
     * @param charset
     * @return
     */
    public String unescape(String str) {
        // utf-8
        try {
            return URLDecoder.decode(str, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return str;
        }
    }
}
