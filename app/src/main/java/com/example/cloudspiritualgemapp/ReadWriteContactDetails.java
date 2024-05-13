package com.example.cloudspiritualgemapp;

import android.net.Uri;
import java.io.Serializable;

public class ReadWriteContactDetails implements Serializable{
     public String uri,path, uid,name ,number,whatsapp , age ,occupation ,address ,stayingwith ,chantinground ,nativ ,sg ,residency ,sgBoy,frProbable,remarks ;
    public ReadWriteContactDetails(){};
    public ReadWriteContactDetails(String uri, String path ,String uid,String  name ,String number,String whatsapp , String age ,String occupation ,String address ,String stayingwith ,String chantinground ,String nativ ,String sg ,String residency,String sgBoy,String frProbable   ,String remarks){
        this.uri =uri;
        this.path = path;
        this.uid = uid;
        this.name = name;
        this.number = number;
        this.whatsapp = whatsapp;
        this.age = age;
        this.occupation = occupation;
        this.address = address;
        this.stayingwith = stayingwith;
        this.chantinground = chantinground;
        this.nativ = nativ;
        this.sg = sg;
        this.residency = residency;
        this.sgBoy = residency;
        this.frProbable = residency;
        this.remarks = remarks;
    }

}
