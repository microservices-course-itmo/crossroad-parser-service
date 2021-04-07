package com.wine.to.up.crossroad.parser.service.proxy;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Proxy {
    private Long id;
    private String ip;
    private Integer port;
    private Date createDate;
}
