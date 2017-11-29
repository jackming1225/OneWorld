package com.world.one.oneworld.model;


import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;
import java.util.List;


@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class RegionalBlocs implements Serializable {

    private String acronym;
    private String name;
    private String otherAcronyms;
    private String otherNames;

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOtherAcronyms() {
        return otherAcronyms;
    }

    public void setOtherAcronyms(String otherAcronyms) {
        this.otherAcronyms = otherAcronyms;
    }

    public String getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(String otherNames) {
        this.otherNames = otherNames;
    }
}
