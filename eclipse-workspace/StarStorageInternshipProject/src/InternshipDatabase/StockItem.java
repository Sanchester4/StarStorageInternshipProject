package InternshipDatabase;

public class StockItem {
	private String name;
	private String category;
	private long quantity;
	private long price;
	private long maxQuantity;

	public StockItem(String category2, String name2, long quantity2, long price2, long maxQuantity2) {
		this.name = name2;
		this.category = category2;
		this.quantity = quantity2;
		this.price = price2;
		this.maxQuantity = maxQuantity2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public long getMaxQuantity() {
		return maxQuantity;
	}

	public void setMaxQuantity(long maxQuantity) {
		this.maxQuantity = maxQuantity;
	}
}
