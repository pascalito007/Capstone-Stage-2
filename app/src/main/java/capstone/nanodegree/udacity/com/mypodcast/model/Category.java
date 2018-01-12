package capstone.nanodegree.udacity.com.mypodcast.model;


import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by jem001 on 07/12/2017.
 */
@Parcel
public class Category {
     String title;
     Integer usage;
     String tag;
     Integer usage2;

    public Category() {
    }

    public Integer getUsage2() {
        return usage2;
    }

    public void setUsage2(Integer usage2) {
        this.usage2 = usage2;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getUsage() {
        return usage;
    }

    public void setUsage(Integer usage) {
        this.usage = usage;
    }

    @Override
    public String toString() {
        return "Category{" +
                "title='" + title + '\'' +
                ", usage='" + usage + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
