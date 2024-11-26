package com.kdan.demo.dto;

public class UserDTO {
	private int userId;
    private String userName;
    private double cashBalance;
    private double transactionSum;
    
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public double getCashBalance() {
		return cashBalance;
	}
	public void setCashBalance(double cashBalance) {
		this.cashBalance = cashBalance;
	}
	public double getTransactionSum() {
		return transactionSum;
	}
	public void setTransactionSum(double transactionSum) {
		this.transactionSum = transactionSum;
	}
}
