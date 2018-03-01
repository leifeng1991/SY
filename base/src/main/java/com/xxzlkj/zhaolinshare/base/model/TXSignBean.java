package com.xxzlkj.zhaolinshare.base.model;

/**
 *  获取腾讯的token
 * 
 * @author zhangrq
 * 
 */
public class TXSignBean extends BaseBean{


	/**
	 * data : {"sign":"tV6QyBLEx6MJ0e9wqfJz2GuVw2xhPTEwMDI5MTIxJmI9emhhb2xpbiZrPUFLSURKVFNLeVFvZVF4SlFNMnViT3A1NnFLWVZDZGpjdTJRWSZlPTE0ODk2NTk0MjEmdD0xNDg5NjU4NDIyJnI9MTExODczODU4MyZ1PTAmZj0=","url":"http://web.image.myqcloud.com/photos/v2/10029121/zhaolin/0/sample1489658422357328"}
	 */

	private DataBean data=new DataBean();

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	public static class DataBean {
		/**
		 * sign : tV6QyBLEx6MJ0e9wqfJz2GuVw2xhPTEwMDI5MTIxJmI9emhhb2xpbiZrPUFLSURKVFNLeVFvZVF4SlFNMnViT3A1NnFLWVZDZGpjdTJRWSZlPTE0ODk2NTk0MjEmdD0xNDg5NjU4NDIyJnI9MTExODczODU4MyZ1PTAmZj0=
		 * url : http://web.image.myqcloud.com/photos/v2/10029121/zhaolin/0/sample1489658422357328
		 */

		private String sign;
		private String url;

		public String getSign() {
			return sign;
		}

		public void setSign(String sign) {
			this.sign = sign;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}
}
