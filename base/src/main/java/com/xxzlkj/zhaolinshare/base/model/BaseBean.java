package com.xxzlkj.zhaolinshare.base.model;

import java.io.Serializable;

/**
 *  基础的bean
 * 
 * @author zhangrq
 * 
 */
public class BaseBean implements Serializable {


	/**
	 * code : 400
	 * message : 未注册
	 * data : []
	 */

	private String code;
	private String message;
	// 1:显示对话框
	private int dialog;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getDialog() {
		return dialog;
	}

	public void setDialog(int dialog) {
		this.dialog = dialog;
	}
}
