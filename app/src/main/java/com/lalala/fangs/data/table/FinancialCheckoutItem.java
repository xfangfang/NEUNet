package com.lalala.fangs.data.table;

/**
 * Created by FANGs on 2017/8/1.
 */

public class FinancialCheckoutItem{
    private String Number;
    private String UserName;
    private String Money;
    private String Product;
    private String Packages;
    private String Flux;
    private String Minutes;
    private String SumTimes;
    private String CreateTime;
    private String Remark;
    private static int sum = 0;


    public FinancialCheckoutItem(String number, String userName, String money, String product,
                                 String packages, String flux, String minutes, String sumTimes,
                                 String createTime, String remark) {
        Number = number;
        UserName = userName;
        Money = money;
        Product = product;
        Packages = packages;
        Flux = flux;
        Minutes = minutes;
        SumTimes = sumTimes;
        CreateTime = createTime;
        Remark = remark;
    }


    @Override
    public String toString() {
        return UserName+" "+CreateTime+" "+Money;
    }


    public static int getSum() {
        return sum;
    }

    public static void setSum(int sum) {
        FinancialCheckoutItem.sum = sum;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getMoney() {
        return Money;
    }

    public void setMoney(String money) {
        Money = money;
    }

    public String getProduct() {
        return Product;
    }

    public void setProduct(String product) {
        Product = product;
    }

    public String getPackages() {
        return Packages;
    }

    public void setPackages(String packages) {
        Packages = packages;
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

    public String getSumTimes() {
        return SumTimes;
    }

    public void setSumTimes(String sumTimes) {
        SumTimes = sumTimes;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }
}
