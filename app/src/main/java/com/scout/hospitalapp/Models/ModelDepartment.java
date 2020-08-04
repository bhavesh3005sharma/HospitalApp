package com.scout.hospitalapp.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelDepartment implements Serializable {
    @SerializedName("department_Name")
    String departmentName;
    @SerializedName("description")
    String description;

    public ModelDepartment(String departmentName, String description) {
        this.departmentName = departmentName;
        this.description = description;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getDescription() {
        return description;
    }
}
