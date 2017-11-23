package top.starrysea.object.dto;

public class Orders extends Entity {

	private String orderId;
	private Work work;
	private String orderNum;
	private String orderName;
	private Area orderArea;
	private String orderAddress;
	private short orderStatus;
	private String orderExpressnum;
	private long orderTime;

	private Orders(Builder builder) {
		this.orderId = builder.orderId;
		this.work=builder.work;
		this.orderNum = builder.orderNum;
		this.orderName = builder.orderName;
		this.orderArea = builder.orderArea;
		this.orderAddress = builder.orderAddress;
		this.orderStatus = builder.orderStatus;
		this.orderExpressnum = builder.orderExpressnum;
		this.orderTime=builder.orderTime;
	}

	public static class Builder implements IBuilder<Orders> {

		private String orderId;
		private Work work;
		private String orderNum;
		private String orderName;
		private Area orderArea;
		private String orderAddress;
		private short orderStatus;
		private String orderExpressnum;
		private long orderTime;

		public Builder orderId(String orderId) {
			this.orderId = orderId;
			return this;
		}
		
		public Builder work(Work work) {
			this.work=work;
			return this;
		}

		public Builder orderNum(String orderNum) {
			this.orderNum = orderNum;
			return this;
		}

		public Builder orderName(String orderName) {
			this.orderName = orderName;
			return this;
		}

		public Builder orderArea(Area area) {
			this.orderArea = area;
			return this;
		}

		public Builder orderAddress(String orderAddress) {
			this.orderAddress = orderAddress;
			return this;
		}

		public Builder orderStatus(short orderStatus) {
			this.orderStatus = orderStatus;
			return this;
		}

		public Builder orderExpressnum(String orderExpressnum) {
			this.orderExpressnum = orderExpressnum;
			return this;
		}
		
		public Builder orderTime(long orderTime) {
			this.orderTime=orderTime;
			return this;
		}

		@Override
		public Orders build() {
			return new Orders(this);
		}

	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getOrderName() {
		return orderName;
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}

	public Area getOrderArea() {
		return orderArea;
	}

	public void setOrderArea(Area orderArea) {
		this.orderArea = orderArea;
	}

	public String getOrderAddress() {
		return orderAddress;
	}

	public void setOrderAddress(String orderAddress) {
		this.orderAddress = orderAddress;
	}

	public short getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(short orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOrderExpressnum() {
		return orderExpressnum;
	}

	public void setOrderExpressnum(String orderExpressnum) {
		this.orderExpressnum = orderExpressnum;
	}

	public long getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(long orderTime) {
		this.orderTime = orderTime;
	}

	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}

}