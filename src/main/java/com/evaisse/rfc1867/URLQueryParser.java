package com.evaisse.rfc1867;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLQueryParser {

    protected final URLQueryEncoderDecoder encoder;
    protected final URLQueryParserConfig config;

    public URLQueryParser(URLQueryParserBuilder config) {
        this.config = config != null ? config : new URLQueryParserBuilder();
        this.encoder = new URLQueryEncoderDecoder();
    }

    public HashMap<String, Object> parse(String str) {

        if (str == null || str.isEmpty()) {
            return new HashMap<>();
        }

        return this.parse(this.parseQueryStringValues(str));
    }

    protected HashMap<String, Object> parse(HashMap<String, Object> tempObj) {

        HashMap<String, Object> obj =
                this.config.isPlainObjects()
                        ? new HashMap<String, Object>()
                        : new HashMap<String, Object>();

        // Iterate over the keys and setup the new object

        for (Map.Entry<String, Object> entry : tempObj.entrySet()) {
            Object newObj = this.parseQueryStringKeys(entry.getKey(), entry.getValue());
            try {
                obj = this.merge(obj, newObj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return this.compact(obj);
    }

    public HashMap<String, Object> merge(HashMap<String, Object> a, Object b) {
        try {
            return (HashMap<String, Object>) this.mergeValues(a, b);
        } catch (Exception e) {
            e.printStackTrace();
            return a;
        }
    }

    /**
     * Merge two values into one, if it's [1,2] + {a:2}, will have {a:2, 0: 1, 1: 2} if it's [1,2] +
     * [a], will have [1,2,a]
     */
    public Object mergeValues(Object a, Object b) throws Exception {

        if (a == null) {
            return b;
        }

        if (b == null) {
            return a;
        }

        if (a instanceof Map && b instanceof Map) {
            return (Object) this.deepMerge((Map) a, (Map) b);
        }

        if (a instanceof List && b instanceof List) {
            for (int i = 0; i < ((List) b).size(); i++) {
                ((List) a).add(((List) b).get(i));
            }
            return a;
        }

        if (a instanceof HashMap && b instanceof HashMap) {
            for (Map.Entry<String, Object> entry : ((HashMap<String, Object>) b).entrySet()) {
                ((HashMap<String, Object>) a).put(entry.getKey(), entry.getValue());
            }
            return a;
        }

        // merge array & objects
        if ((a instanceof HashMap && b instanceof List)
                || (b instanceof HashMap && a instanceof List)) {
            return this.mergeValues(
                    (a instanceof HashMap) ? a : this.listToHash((List) a),
                    (b instanceof HashMap) ? b : this.listToHash((List) b));
        }

        throw new Exception("Not cool");
        //        /*
        //            If b and a are not of the same types, we add
        //         */
        //        List<Object> tmList = new ArrayList<>();
        //
        //        tmList.add(a);
        //        tmList.add(b);
        //
        //        return tmList;
    }

    protected HashMap<String, Object> listToHash(List oList) {
        HashMap<String, Object> map = new HashMap<>();
        for (int i = 0; i < oList.size(); i++) {
            map.put("" + i, oList.get(i));
        }
        return map;
    }

    protected Map deepMerge(Map original, Map newMap) {
        for (Object key : newMap.keySet()) {
            if (newMap.get(key) instanceof Map && original.get(key) instanceof Map) {
                Map originalChild = (Map) original.get(key);
                Map newChild = (Map) newMap.get(key);
                original.put(key, this.deepMerge(originalChild, newChild));
            } else if (newMap.get(key) instanceof List && original.get(key) instanceof List) {
                List originalChild = (List) original.get(key);
                List newChild = (List) newMap.get(key);
                for (Object each : newChild) {
                    if (!originalChild.contains(each) && each != null) {
                        originalChild.add(each);
                    }
                }
            } else {
                original.put(key, newMap.get(key));
            }
        }
        return original;
    }

    protected HashMap<String, Object> compact(HashMap<String, Object> map) {
        return map;
    }

    protected Object parseObject(List<String> chainOfKeys, Object val) {
        Object leaf = val;

        if (chainOfKeys.isEmpty()) {
            return leaf;
        }

        for (int i = chainOfKeys.size() - 1; i >= 0; --i) {
            HashMap<String, Object> obj = this.createNewEmptyObject();
            String root = chainOfKeys.get(i);

            if (root.equals("[]") && this.config.isParseArrays()) {
                /*
                   Parse array value
                */
                leaf = this.arrayCombine(val);
                continue; // stop here, value is an array
            }

            /*
               Parse object like value
            */
            String cleanRoot =
                    root.startsWith("[") && root.endsWith("]")
                            ? StringHelper.slice(root, 1, -1)
                            : root;

            Integer index;

            try {
                index = Integer.parseInt(cleanRoot, 10);
            } catch (NumberFormatException e) {
                index = null;
            }

            if (!this.config.isParseArrays() && cleanRoot.isEmpty()) {
                obj.put("0", leaf);
                leaf = obj;
                continue; // stop there value is an object-like array { "0": "a", "1": "c"}
            }

            if (index != null
                    && !root.equals(cleanRoot)
                    && (index.toString() + "").equals(cleanRoot)
                    && index >= 0
                    && (this.config.isParseArrays() && index <= this.config.getArrayLimit())) {
                /*
                    If index is numeric, a[1]=2
                */
                List<Object> newList = new ArrayList<Object>();
                for (int fill = 0;
                        fill < index;
                        fill++) { // fill array with null values, for futur merge
                    newList.add(fill, null);
                }
                newList.add(index, leaf);
                leaf = newList;
                continue;
            }

            obj.put(cleanRoot, leaf);
            leaf = obj; //  put obj in the new leaf
        }

        return leaf;
    }

    protected HashMap<String, Object> parseQueryStringValues(String str) {
        HashMap<String, Object> obj = new HashMap<>();

        String cleanStr = str;

        if (this.config.isIgnoreQueryPrefix() && str.indexOf("?") == 0) {
            cleanStr = str.substring(1);
        }

        int limit = this.config.getParameterLimit();
        String[] parts = cleanStr.split(this.config.getDelimiter(), limit);
        int skipIndex = -1; // Keep track of where the utf8 sentinel was found
        int i;

        String charset = this.config.getDelimiter();

        if (this.config.isCharsetSentinelProcess()) {
            for (i = 0; i < parts.length; ++i) {
                if (parts[i].indexOf("utf8=") == 0) {
                    if (parts[i].equals(this.config.getCharsetSentinel())) {
                        charset = "utf-8";
                    } else if (parts[i].equals(this.config.getIsoSentinel())) {
                        charset = "iso-8859-1";
                    }
                    skipIndex = i;
                    i = parts.length;
                }
            }
        }

        for (i = 0; i < parts.length; ++i) {
            if (i == skipIndex) {
                continue;
            }
            String part = parts[i];

            int bracketEqualsPos = part.indexOf("]=");
            int pos = bracketEqualsPos == -1 ? part.indexOf("=") : bracketEqualsPos + 1;

            String key;
            Object val;

            if (pos == -1) { // case
                key = this.encoder.decode(part);
                val = this.config.isStrictNullHandling() ? null : "";
            } else {
                key = this.encoder.decode(StringHelper.slice(part, 0, pos));
                val = this.encoder.decode(StringHelper.slice(part, pos + 1));
            }

            if (val != null
                    && this.config.isInterpretNumericEntities()
                    && charset.equals("iso-8859-1")) {
                val = interpretNumericEntitiesFromString((String) val);
            }

            if (val != null && this.config.isComma() && ((String) val).contains(",")) {
                val = ((String) val).split(",");
            }

            if (part.contains("[]=")) {
                if (val instanceof List) {
                    List<Object> nList = new ArrayList<Object>();
                    nList.add(val);
                    val = nList;
                }
            }

            /*
               Merge multiples values for key into a single value,
               ex. a[]=b&a=[]=c will give { a[] : [b,c] }
            */
            if (obj.containsKey(key)) {
                val = this.arrayCombine(obj.get(key), val);
            }

            obj.put(key, val);
        }

        return obj;
    }

    /**
     * Parse a query string and extracts all the keys with a descendance construct
     *
     * @param givenKey a given key as string
     * @param val any values
     * @return hashmap for keys & subsequents values
     */
    protected HashMap<String, Object> parseQueryStringKeys(
            final String givenKey, final Object val) {
        if (givenKey == null) {
            return null;
        }

        String key;

        if (this.config.isAllowDots()) {
            // Transform dot notation to bracket notation
            key =
                    ReplaceCallback.replace(
                            "\\.([^.[]+)",
                            givenKey,
                            new ReplaceCallback.Callback() {
                                public String matchFound(MatchResult match) {
                                    return "[" + match.group(1) + "]";
                                }
                            });
        } else {
            key = givenKey;
        }

        // The regex chunks

        String brackets = "(\\[[^\\]]*\\])";

        // Get the parent

        // parse key to extract the name before [
        List<String> segment = new ArrayList<String>();

        int parentIndex = -1;
        if (this.config.getDepth() > 0 && key.contains("[")) {
            Matcher match = Pattern.compile(brackets).matcher(key);
            if (match.find()) {
                parentIndex = match.start();
            }
        }

        String parent = parentIndex > -1 ? StringHelper.slice(key, 0, parentIndex) : key;

        // Stash the parent if it exists

        List<String> keys = new ArrayList<String>();

        if (parent != null && !parent.isEmpty()) {
            // If we aren't using plain objects, optionally prefix keys that would overwrite object
            // prototype properties
            keys.add(parent);
        }

        // Loop through children appending to the array until we hit depth
        int i = 0;

        Matcher childMatcher = Pattern.compile(brackets).matcher(key);

        while (childMatcher.find()) {
            keys.add(childMatcher.group(1));
        }

        // If there's a remainder, just add whatever is left
        if (!segment.isEmpty()) {
            keys.add('[' + StringHelper.slice(key, childMatcher.start()) + ']');
        }

        return (HashMap<String, Object>) this.parseObject(keys, val);
    }

    protected String interpretNumericEntitiesFromString(final String str) {
        String replacement =
                ReplaceCallback.replace(
                        "&#(\\d+);",
                        str,
                        new ReplaceCallback.Callback() {
                            public String matchFound(MatchResult match) {
                                return StringHelper.stringFromCharCode(
                                        Integer.parseInt(match.group(1), 10));
                            }
                        });
        return replacement;
    }

    private HashMap<String, Object> createNewEmptyObject() {
        return this.config.isPlainObjects()
                ? new HashMap<String, Object>()
                : new HashMap<String, Object>();
    }

    private List<Object> arrayCombine(final Object... items) {
        List<Object> newList = new ArrayList<>();

        for (int i = 0; i < items.length; i++) {
            Object item = items[i];
            if (item instanceof List) {
                for (int y = 0; y < ((List) item).size(); y++) {
                    newList.add(((List) item).get(y));
                }
            } else {
                newList.add(item);
            }
        }

        return newList;
    }
}
