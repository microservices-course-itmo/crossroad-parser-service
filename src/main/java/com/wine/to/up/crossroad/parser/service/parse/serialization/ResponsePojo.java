package com.wine.to.up.crossroad.parser.service.parse.serialization;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ResponsePojo {
    private int count;
    private String html;
    private String htmlFilters;
    private String jsonTags;
}
