package com.lalala.fangs.data.table;

/**
 * Created by FANGs on 2017/8/2.
 */

public class FinancialPayItem {
    private String Number;
    private String UserName;
    private String PayNum;
    private String FinancialType;
    private String PayType;
    private String Product;
    private String CreateTime;
    private String ManageName;
    private String Packages;
    private static int sum = 0;

    public FinancialPayItem(String number, String userName, String payNum, String financialType,
                            String payType, String product, String createTime, String manageName,
                            String packages) {
        Number = number;
        UserName = userName;
        PayNum = payNum;
        FinancialType = financialType;
        PayType = payType;
        Product = product;
        CreateTime = createTime;
        ManageName = manageName;
        Packages = packages;
    }

    public static int getSum() {
        return sum;
    }

    public static void setSum(int sum) {
        FinancialPayItem.sum = sum;
    }

    @Override
    public String toString() {
        return this.UserName+" "+this.PayNum+" "+this.CreateTime;
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

    public String getPayNum() {
        return PayNum;
    }

    public void setPayNum(String payNum) {
        PayNum = payNum;
    }

    public String getFinancialType() {
        return FinancialType;
    }

    public void setFinancialType(String financialType) {
        FinancialType = financialType;
    }

    public String getPayType() {
        return PayType;
    }

    public void setPayType(String payType) {
        PayType = payType;
    }

    public String getProduct() {
        return Product;
    }

    public void setProduct(String product) {
        Product = product;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getManageName() {
        return ManageName;
    }

    public void setManageName(String manageName) {
        ManageName = manageName;
    }

    public String getPackages() {
        return Packages;
    }

    public void setPackages(String packages) {
        Packages = packages;
    }
}
