package com.lalala.fangs.data.table;

/**
 * Created by FANGs on 2017/8/1.
 */

public class DetailLogItem {
    private String UserName;
    private String SignTime;
    private String DropTime;
    private String IP;
    private String OS;
    private String Flux;
    private String Minutes;
    private String Charge;
    private static int sum = 0;

    public DetailLogItem(String userName, String signTime, String dropTime, String IP,
                         String OS, String flux, String minutes, String charge) {
        UserName = userName;
        SignTime = signTime;
        DropTime = dropTime;
        this.IP = IP;
        this.OS = OS;
        Flux = flux;
        Minutes = minutes;
        Charge = charge;
    }

    public static int getSum() {
        return sum;
    }


    @Override
    public String toString() {
        return UserName+" "+DropTime+" "+Flux;
    }

    public static void setSum(int sum) {
        DetailLogItem.sum = sum;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getSignTime() {
        return SignTime;
    }

    public void setSignTime(String signTime) {
        SignTime = signTime;
    }

    public String getDropTime() {
        return DropTime;
    }

    public void setDropTime(String dropTime) {
        DropTime = dropTime;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getOS() {
        return OS;
    }

    public void setOS(String OS) {
        this.OS = OS;
    }

    public String getFlux() {
        return Flux;
    }

    public void setFlux(String flux) {
        Flux = flux;
    }

    public String getMinutes() {
        return Minutes;
    }

    public void setMinutes(String minutes) {
        Minutes = minutes;
    }

    public String getCharge() {
        return Charge;
    }

    public void setCharge(String charge) {
        Charge = charge;
    }
}
