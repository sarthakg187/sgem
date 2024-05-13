package com.example.cloudspiritualgemapp;

public class ReadWriteDataDetails {
    public String group_name,gid,group_description;
    public ReadWriteDataDetails(){};
    public ReadWriteDataDetails(String group_name,String gid ,String group_description){
        this.group_name = group_name;
        this.gid = gid;
        this.group_description = group_description;
    }
    public ReadWriteDataDetails(String group_name ,String group_description){
        this.group_name = group_name;
        this.group_description = group_description;
    }
}
