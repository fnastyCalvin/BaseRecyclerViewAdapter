package com.calvin.baserecyclerviewadapter.model;

import com.calvin.base.section.Section;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by jiangtao on 2016/6/6 11:10.
 */
public class SectionModel extends Section{
    public String name;
    public String date;
    public long dateMill;
    private int month;

    public SectionModel(String name,String date){
        this.name = name;
        this.date = date;
        this.dateMill = getDateMill(date);
        this.sectionName = getSection();
    }

    public long getDateMill(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd",Locale.CHINA);
        try {
            this.month = sdf.parse(date).getMonth()+1;
            return sdf.parse(date).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public String getSection(){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(),Locale.CHINA);
        int month = calendar.get(Calendar.MONTH)+1;
        if (month == this.month){
            return "本月";
        }
        return this.month+"月";
    }
}
