package com.chunruo.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * 银行列表
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_bank")
public class Bank implements Serializable{
	private static final long serialVersionUID = -1577514225902443202L;
	private Long bankId;			//序号
    private String name;			//银行名称
    private Boolean status;			//状态
    private Boolean isQuickCard;	//是否快捷支付
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getBankId() {
        return bankId;
    }
    
    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }
    
    @Column(name="name")
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
    
    @Column(name="status")
    public Boolean getStatus() {
        return status;
    }
    
    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Column(name="is_quick_pay")
	public Boolean getIsQuickCard() {
		return isQuickCard;
	}

	public void setIsQuickCard(Boolean isQuickCard) {
		this.isQuickCard = isQuickCard;
	}
}