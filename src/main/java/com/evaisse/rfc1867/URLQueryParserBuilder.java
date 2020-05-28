package com.evaisse.rfc1867;

public class URLQueryParserBuilder extends URLQueryParserConfig {

    /** Private construct for builder pattern */
    public URLQueryParserBuilder() {}

    /**
     * Create parser
     *
     * @return parser configured
     */
    public URLQueryParser build() {
        return new URLQueryParser(this);
    }

    public URLQueryParserBuilder setAllowDots(boolean allowDots) {
        this.allowDots = allowDots;
        return this;
    }

    public URLQueryParserBuilder setAllowPrototypes(boolean allowPrototypes) {
        this.allowPrototypes = allowPrototypes;
        return this;
    }

    public URLQueryParserBuilder setArrayLimit(int arrayLimit) {
        this.arrayLimit = arrayLimit;
        return this;
    }

    public URLQueryParserBuilder setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public URLQueryParserBuilder setCharsetSentinelProcess(boolean charsetSentinelProcess) {
        this.charsetSentinelProcess = charsetSentinelProcess;
        return this;
    }

    public URLQueryParserBuilder setComma(boolean comma) {
        this.comma = comma;
        return this;
    }

    public URLQueryParserBuilder setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public URLQueryParserBuilder setDepth(int depth) {
        this.depth = depth;
        return this;
    }

    public URLQueryParserBuilder setIgnoreQueryPrefix(boolean ignoreQueryPrefix) {
        this.ignoreQueryPrefix = ignoreQueryPrefix;
        return this;
    }

    public URLQueryParserBuilder setInterpretNumericEntities(boolean interpretNumericEntities) {
        this.interpretNumericEntities = interpretNumericEntities;
        return this;
    }

    public URLQueryParserBuilder setParameterLimit(int parameterLimit) {
        this.parameterLimit = parameterLimit;
        return this;
    }

    public URLQueryParserBuilder setParseArrays(boolean parseArrays) {
        this.parseArrays = parseArrays;
        return this;
    }

    public URLQueryParserBuilder setPlainObjects(boolean plainObjects) {
        this.plainObjects = plainObjects;
        return this;
    }

    public URLQueryParserBuilder setStrictNullHandling(boolean strictNullHandling) {
        this.strictNullHandling = strictNullHandling;
        return this;
    }

    public URLQueryParserBuilder setIsoSentinel(String isoSentinel) {
        this.isoSentinel = isoSentinel;
        return this;
    }

    public URLQueryParserBuilder setCharsetSentinel(String charsetSentinel) {
        this.charsetSentinel = charsetSentinel;
        return this;
    }
}
