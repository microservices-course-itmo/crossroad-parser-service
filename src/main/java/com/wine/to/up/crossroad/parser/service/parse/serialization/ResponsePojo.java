package com.wine.to.up.crossroad.parser.service.parse.serialization;

import java.util.List;

public class ResponsePojo {
    public int count;
    public String html;
    public String htmlFilters;
    public String jsonTags;

    @Override
    public String toString() {
        return "ResponsePojo{" +
                "count=" + count +
                ", html='" + html.substring(0, 20) + "...\n" +
                ", htmlFilters='" + htmlFilters.substring(0, 10) + "...\n" +
                ", jsonTags='" + jsonTags + '\'' +
                '}';
    }
}
