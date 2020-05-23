package com.chunruo.portal.vo;

/**
 * 秒杀已买数量和待支付数量
 * @author chunruo
 *
 */
public class BuyNumberVo {
	private int totalBuyNumber;
	private int waitBuyNumber;
	
	public int getTotalBuyNumber() {
		return totalBuyNumber;
	}
	
	public void setTotalBuyNumber(int totalBuyNumber) {
		this.totalBuyNumber = totalBuyNumber;
	}
	
	public int getWaitBuyNumber() {
		return waitBuyNumber;
	}
	
	public void setWaitBuyNumber(int waitBuyNumber) {
		this.waitBuyNumber = waitBuyNumber;
	}
}
