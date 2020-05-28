# java-http-query-string-parser-builder

An [RFC1867](https://tools.ietf.org/html/rfc1867) compliant Java URL query string parser/builder

Which allow to build URLs query arguements with multidimensional array on popular forms like

```javascript
queryBuilder({
    "search": {
        "categories": [
            1,
            2
        ],
        "criteria": {
            "foo": 1,
            "bar": "other"
        },
        "order": {
            "date": "desc",
            "name": "desc"
        }
    }
})
 // => encoded = "search%5Bcategories%5D%5B0%5D=1&search%5Bcategories%5D%5B1%5D=2&search%5Bcriteria%5D%5Bfoo%5D=1&search%5Bcriteria%5D%5Bbar%5D=other&search%5Border%5D%5Bdate%5D=desc&search%5Border%5D%5Bname%5D=desc"
 // => decoded = "search[categories][0]=1&search[categories][1]=2&search[criteria][foo]=1&search[criteria][bar]=other&search[order][date]=desc&search[order][name]=desc"
```

Tests & logic based on : 

 - [NodeJS 'qs', qs.stringify(query)](https://www.npmjs.com/package/qs)
 - [PHP http_build_query(query)](https://www.php.net/manual/en/function.http-build-query.php)
 - [jQuery params encoding $.get(url, query)](https://api.jquery.com/jQuery.param/#entry-longdesc)

Projects struct based on : 

 - https://github.com/codecov/example-gradle
 
