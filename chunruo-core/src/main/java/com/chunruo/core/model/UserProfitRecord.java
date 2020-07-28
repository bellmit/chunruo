package com.chunruo.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.chunruo.core.vo.RefundVo;

/**
 * 财务记录
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_user_profit_record")
public class UserProfitRecord {
	// 状态类型
	public final static Integer DISTRIBUTION_STATUS_INIT = 1;		//进行中
	public final static Integer DISTRIBUTION_STATUS_RETURN = 2;		//退款
	public final static Integer DISTRIBUTION_STATUS_SUCC = 3;		//已结算
	public final static Integer DISTRIBUTION_STATUS_FAIL = 4;		//失败
	
	// 利润类型
	public final static Integer DISTRIBUTION_TYPE_FX = 5;			//分销
	public final static Integer DISTRIBUTION_TYPE_VIP = 6;			//k购买vip
	
	//收益归属
	public final static Integer DISTRIBUTION_MTYPE_TOP = 3;			//上线
	public final static Integer DISTRIBUTION_MTYPE_DOWN = 2;		//分享
	
    private Long recordId;			//序号
    private Long userId;			//用户ID
    private Long fromUserId;       	//来源用户ID
    private Long orderId;			//订单ID
    private String orderNo;			//订单号
    private Double income;			//收入(负值为支出)
    private Integer type;			//类型(5:分销,6购买vip)
    private Integer mtype;          //收益类型(3:上线,2:分享收益)
    private Integer status;			//状态(1:进行中;2:退款;3:已结算;4:失败)
    private Date createTime;		//创建时间
    private Date updateTime;		//更新时间
   
    @Transient
    private String nickName;
    private String fromStoreName;
    private Double orderAmount;
    private List<OrderItems> orderItemsList;
    private Double orderProfit;          //订单总收益
    private Double refundProfit;         //订单退款收益
    private Boolean isHaveRefund;        //是否有退款
    private List<RefundVo> refundVoList = new ArrayList<RefundVo>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	@Column(name="user_id")
    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@Column(name="from_user_id")
	public Long getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(Long fromUserId) {
		this.fromUserId = fromUserId;
	}

	@Column(name="order_id")
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Column(name="order_no")
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    @Column(name="income")
    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    @Column(name="type",length=1)
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Column(name="mtype",length=1)
	public Integer getMtype() {
		return mtype;
	}

	public void setMtype(Integer mtype) {
		this.mtype = mtype;
	}

	@Column(name="status",length=1)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(name="create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name="update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	
	@Transient
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	@Transient
	public List<OrderItems> getOrderItemsList() {
		return orderItemsList;
	}

	public void setOrderItemsList(List<OrderItems> orderItemsList) {
		this.orderItemsList = orderItemsList;
	}
	
	@Transient
	public Double getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(Double orderAmount) {
		this.orderAmount = orderAmount;
	}

	@Transient
	public String getFromStoreName() {
		return fromStoreName;
	}

	public void setFromStoreName(String fromStoreName) {
		this.fromStoreName = fromStoreName;
	}

	@Transient
	public Double getOrderProfit() {
		return orderProfit;
	}

	public void setOrderProfit(Double orderProfit) {
		this.orderProfit = orderProfit;
	}

	@Transient
	public Double getRefundProfit() {
		return refundProfit;
	}

	public void setRefundProfit(Double refundProfit) {
		this.refundProfit = refundProfit;
	}

	@Transient
	public Boolean getIsHaveRefund() {
		return isHaveRefund;
	}

	public void setIsHaveRefund(Boolean isHaveRefund) {
		this.isHaveRefund = isHaveRefund;
	}

	@Transient
	public List<RefundVo> getRefundVoList() {
		return refundVoList;
	}

	public void setRefundVoList(List<RefundVo> refundVoList) {
		this.refundVoList = refundVoList;
	}
}