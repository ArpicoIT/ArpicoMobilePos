package com.arpico.groupit.marksys.arpicomobilepos.Models;

public class AuthModel {
    private  String manage_by;

    public AuthModel(String manage_by) {
        this.manage_by = manage_by;
    }

    public String getManage_by() {
        return manage_by;
    }
}
