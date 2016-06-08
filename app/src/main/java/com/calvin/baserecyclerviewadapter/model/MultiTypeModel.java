package com.calvin.baserecyclerviewadapter.model;

/**
 * Created by jiangtao on 2016/6/2 15:20.
 */
public class MultiTypeModel {
    public String title;
    public String detail;
    public String avatorPath;
    public int type;

    public MultiTypeModel(String title, String detail, String avatorPath,int type) {
        this.title = title;
        this.detail = detail;
        this.avatorPath = avatorPath;
        this.type = type;
    }
}
