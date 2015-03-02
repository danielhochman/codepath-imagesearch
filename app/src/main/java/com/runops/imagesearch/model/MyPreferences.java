package com.runops.imagesearch.model;

import android.util.Log;

import java.io.Serializable;

public class MyPreferences implements Serializable, Cloneable {
    public String size;
    public Integer sizeIndex;
    public String color;
    public Integer colorIndex;
    public String type;
    public Integer typeIndex;

    public String site;


    public MyPreferences() {
        this.sizeIndex = 0;
        this.colorIndex = 0;
        this.typeIndex = 0;
        this.site = "";
        this.color = "";
        this.type = "";
        this.size = "";
    }

    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof MyPreferences) {
            MyPreferences that = (MyPreferences) other;
            result = (
                    this.size.equals(that.size)
                    && this.color.equals(that.color)
                    && this.type.equals(that.type)
                    && this.site.equals(that.site)
            );
        }
        return result;
    }
}
