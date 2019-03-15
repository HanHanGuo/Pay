package com.xianguo.pay.entity;

import com.xianguo.hotmapper.annotation.FieldInfo;
import com.xianguo.hotmapper.annotation.ReTable;
import com.xianguo.hotmapper.annotation.Table;
import com.xianguo.hotmapper.enums.FieldIsNull;

import lombok.Data;

/**
 * 退款日志
 * @author:鲜果
 * @date:2018年9月4日
 */
@Data
@Table("m_refund_log")//表名
@ReTable//逆向工程建表
public class RefundLog {
	@FieldInfo(length="32",isNull=FieldIsNull.NOT_NULL,detail="id")
	private String id;
	@FieldInfo(length="2",detail="退款类型@PaySign值")
	private String type;
	@FieldInfo(length="2",detail="退款状态(1.已提交，2.以通过，3.未通过，4.已退款)")
	private String state;
	@FieldInfo(length="255",detail="驳回原因")
	private String rejectMsg;
	@FieldInfo(length="30",detail="退款金额")
	private String money;
	@FieldInfo(length="32",detail="退款订单号")
	private String orderNo;
	@FieldInfo(length="32",detail="退款订单id")
	private String orderId;
	@FieldInfo(length="32",detail="发起退款日期")
	private String createDate;
	@FieldInfo(length="32",detail="退款完成日期")
	private String completeDate;
	@FieldInfo(length="255",detail="退款原因")
	private String refundMsg;
	@FieldInfo(length="2",detail="退款渠道（0-支付宝/1-微信/2-余额支付）")
	private String refundType;
	@FieldInfo(length="1024",detail="退款回调消息")
	private String callbackMsg;
	@FieldInfo(length="32",detail="支付日志id")
	private String paylogId;
	@FieldInfo(length="32",detail="用户id")
	private String memberId;
}
