package com.xianguo.pay.entity;

import com.xianguo.hotmapper.annotation.FieldInfo;
import com.xianguo.hotmapper.annotation.ReTable;
import com.xianguo.hotmapper.annotation.Table;
import com.xianguo.hotmapper.enums.FieldIsNull;
import com.xianguo.hotmapper.enums.FieldType;

import lombok.Data;

/**
 * 支付记录表
 * @author 鲜果
 * @date 2019年3月15日
 *
 */
@Data
@Table("m_pay_log")//表名
@ReTable//逆向工程建表
public class PayLog {

	/**
	 * 主键
	 */
	@FieldInfo(length="32",isNull=FieldIsNull.NOT_NULL,detail="主键")
	private String id;
	
	/**
	 * 订单编号
	 */
	@FieldInfo(length="32",detail="订单编号")
	private String orderNo;
	
	/**
	 * 会员编号
	 */
	@FieldInfo(length="32",detail="支付用户id")
	private String memberId;
	
	/**
	 * 支付类型(0：支付宝，1：微信，2：余额支付)
	 */
	@FieldInfo(length="3",detail="支付类型@PaySign的值")
	private String payType;
	
	/**
	 * 支付金额(分)
	 */
	@FieldInfo(length="30",detail="支付金额(分)")
	private String payMoney;
	
	/**
	 * 支付时间
	 */
	@FieldInfo(length="50",detail="支付时间")
	private String payTime;
	
	/**
	 * 支付状态 0-待支付 1-成功 2-失败
	 */
	@FieldInfo(length="2",detail="支付状态(0-待支付 1-成功 2-失败)")
	private String payState;
	
	/**
	 * 回调时间
	 */
	@FieldInfo(length="50",detail="回调时间")
	private String notifyTime;
	
	/**
	 * 回调内容
	 */
	@FieldInfo(type=FieldType.TEXT,length="",detail="回调内容")
	private String notifyContent;

	/**
	 * 支付账号
	 */
	@FieldInfo(length="200",detail="支付账号")
	private String payAccount;
	
	/**
	 * 支付流水号
	 */
	@FieldInfo(length="128",detail="支付流水号，第三方机构返回的流水号，余额支付是自己系统生成的流水号")
	private String paySerialCode;
	
	/**
	 * 对账状态：0:未对账 1:已对账
	 */
	@FieldInfo(length="1",detail="对账状态(0:未对账 1:已对账)")
	private String syncState;
	
	/**
	 * 订单id
	 */
	@FieldInfo(length="32",detail="订单id")
	private String orderId;
}
