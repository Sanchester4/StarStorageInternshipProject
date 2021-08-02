package InternshipDatabase;

import java.util.Vector;

public interface DatabaseInterface {
	
	public void addCategory(String category);

	public void addNewProd(String name, String category, int quantity, int price);

	public void removeProd(String name);

	public void replenishProdQuant(String name, int num);

	public void buyProd(String name, int num, String username);

	public void printAllProd();

	public void printProdCategory(String category);

	public void printCategory();

	public void printProdName(String name);

	public void fillStock(Vector<StockItem> item);

	public void freeDb();

	public void createDb();

	public void fillCategory(String category);

	public void readInput(String url);

	public void getTime();
}
