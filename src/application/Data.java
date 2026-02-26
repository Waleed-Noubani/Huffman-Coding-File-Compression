package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Data {

	private SimpleStringProperty c1;     //JavaFX يدعم data binding، يعني أي تغيير في البيانات ينعكس مباشرة على الواجهة (TableView, TextField, Label...).
	private SimpleIntegerProperty c3;   // SimpleStringProperty بدل String. SimpleIntegerProperty بدل int.( هذه ليست فقط متغيرات، بل كائنات قابلة للمراقبة.)
	private SimpleStringProperty c2;

	public Data(String c1, int c3, String c2) {
		this.c1 = new SimpleStringProperty(c1);
		this.c2 = new SimpleStringProperty(c2);
		this.c3 = new SimpleIntegerProperty(c3);

	}

	public SimpleStringProperty getC1() {
		return c1;
	}

	public void setC1(SimpleStringProperty c1) {
		this.c1 = c1;
	}

	public SimpleIntegerProperty getC3() {
		return c3;
	}

	public void setC3(SimpleIntegerProperty c3) {
		this.c3 = c3;
	}

	public SimpleStringProperty getC2() {
		return c2;
	}

	public void setC2(SimpleStringProperty c2) {
		this.c2 = c2;
	}

}