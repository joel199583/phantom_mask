package com.kdan.demo.dto;

public class MaskTransactionDTO {
	private int maskCount;
	private double transactionSum;
	
	public int getMaskCount() {
		return maskCount;
	}
	public void setMaskCount(int maskCount) {
		this.maskCount = maskCount;
	}
	public double getTransactionSum() {
		return transactionSum;
	}
	public void setTransactionSum(double transactionSum) {
		this.transactionSum = transactionSum;
	}
}
