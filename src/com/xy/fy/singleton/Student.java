package com.xy.fy.singleton;

public class Student {
  private String account;// 账号，主键,最长十位
  private String password;// 密码
  private String name;// 姓名

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return "Student [account=" + account + ", password=" + password + "]";
  }

}
