package com.evaisse.rfc1867;

public abstract class URLQueryParserConfig {

    // protected boolean decoder = utils.decode;
    protected boolean allowDots = false;
    protected boolean allowPrototypes = false;
    protected int arrayLimit = 20;
    protected String charset = "utf-8";
    protected boolean charsetSentinelProcess = false;
    protected boolean comma = false;

    protected String delimiter = "&";
    protected int depth = 5;
    protected boolean ignoreQueryPrefix = false;
    protected boolean interpretNumericEntities = false;
    protected int parameterLimit = 1000;
    protected boolean parseArrays = true;
    protected boolean plainObjects = false;
    protected boolean strictNullHandling = false;

    // This is what browsers will submit when the ✓ character occurs in an
    // application/x-www-form-urlencoded body and the encoding of the page containing
    // the form is iso-8859-1, or when the submitted form has an accept-charset
    // attribute of iso-8859-1. Presumably also with other charsets that do not contain
    // the ✓ character, such as us-ascii.
    protected String isoSentinel = "utf8=%26%2310003%3B"; // encodeURIComponent('&#10003;')

    // These are the percent-encoded utf-8 octets representing a checkmark, indicating that the
    // request actually is utf-8 encoded.
    protected String charsetSentinel = "utf8=%E2%9C%93"; // encodeURIComponent('✓')

    public boolean isAllowDots() {
        return allowDots;
    }

    public boolean isAllowPrototypes() {
        return allowPrototypes;
    }

    public int getArrayLimit() {
        return arrayLimit;
    }

    public String getCharset() {
        return charset;
    }

    public boolean isCharsetSentinelProcess() {
        return charsetSentinelProcess;
    }

    public boolean isComma() {
        return comma;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isIgnoreQueryPrefix() {
        return ignoreQueryPrefix;
    }

    public boolean isInterpretNumericEntities() {
        return interpretNumericEntities;
    }

    public int getParameterLimit() {
        return parameterLimit;
    }

    public boolean isParseArrays() {
        return parseArrays;
    }

    public boolean isPlainObjects() {
        return plainObjects;
    }

    public boolean isStrictNullHandling() {
        return strictNullHandling;
    }

    public String getIsoSentinel() {
        return isoSentinel;
    }

    public String getCharsetSentinel() {
        return charsetSentinel;
    }
}
