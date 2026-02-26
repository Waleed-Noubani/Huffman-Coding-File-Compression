package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {

	private HuffmanController controller;

	@Override
	public void start(Stage primaryStage) {
		try {
			controller = new HuffmanController(primaryStage);
			Pane root = createMainLayout();
			root.getStyleClass().add("main-pane");

			Scene scene = new Scene(root, 1300, 800);

			scene.getStylesheets().add(this.getClass().getResource("application.css").toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.setTitle("HUFFMAN PROJECT");
			primaryStage.setResizable(true);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
			showError("Error starting application", e.getMessage());
		}
	}

	private Pane createMainLayout() {
		Pane root = new Pane();
		root.getStyleClass().add("main-pane");

		Label titleLabel = new Label("HUFFMAN PROJECT"); /////////
		titleLabel.getStyleClass().add("title-label");
		titleLabel.setLayoutX(20);
		titleLabel.setLayoutY(15);

		VBox buttons = createButton(); ////////
		buttons.setLayoutX(60);
		buttons.setLayoutY(100);

		// Statistics Area
		TextArea statsArea = controller.getStatsArea(); ////////////
		statsArea.setEditable(false);
		statsArea.setWrapText(true);
		statsArea.getStyleClass().add("stats-area");
		statsArea.setPrefSize(900, 120);
		statsArea.setLayoutX(280);
		statsArea.setLayoutY(90);

		TableView<Data> table = createHuffmanTable(); /////////////
		table.setLayoutX(280);
		table.setLayoutY(230);
		table.setPrefSize(900, 500);

		root.getChildren().addAll(titleLabel, buttons, statsArea, table);

		return root;
	}

	private VBox createButton() {
		VBox vbox = new VBox(25);
		vbox.getStyleClass().add("button-panel");

		Button selectBtn = createStyledButton("📁 SELECT FILE");
		selectBtn.setOnAction(e -> controller.selectFile());

		Button encodeBtn = createStyledButton("🔒 ENCODE");
		encodeBtn.setOnAction(e -> controller.compressFile());

		Button statsBtn = createStyledButton("📊 STATISTICS");
		statsBtn.setOnAction(e -> controller.showStatistics());

		Button decodeBtn = createStyledButton("🔓 DECODE");
		decodeBtn.setOnAction(e -> controller.decompressFile());

		vbox.getChildren().addAll(selectBtn, encodeBtn, statsBtn, decodeBtn);

		return vbox;
	}

	private Button createStyledButton(String text) {
		Button btn = new Button(text);
		btn.setPrefSize(200, 50);
		btn.getStyleClass().add("custom-button");
		return btn;
	}

	private TableView<Data> createHuffmanTable() {
		TableView<Data> table = controller.getTableView();
		table.setPrefSize(900, 500);
		table.getStyleClass().add("custom-table");

		TableColumn<Data, String> charCol = new TableColumn<>("CHARECTER");
		charCol.setPrefWidth(200);
		charCol.setCellValueFactory(cellData -> cellData.getValue().getC1());

		TableColumn<Data, Integer> freqCol = new TableColumn<>("FREQUENCY");
		freqCol.setPrefWidth(250);
		freqCol.setCellValueFactory(cellData -> cellData.getValue().getC3().asObject());

		TableColumn<Data, String> codeCol = new TableColumn<>("HUFFMAN CODE");
		codeCol.setPrefWidth(430);
		codeCol.setCellValueFactory(cellData -> cellData.getValue().getC2());
		codeCol.getStyleClass().add("code-column");

		table.getColumns().addAll(charCol, freqCol, codeCol);

		return table;
	}

	private void showError(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
