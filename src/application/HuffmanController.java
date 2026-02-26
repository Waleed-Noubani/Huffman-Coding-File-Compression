package application;

import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class HuffmanController {
	private static final int ALL_CHARS = 256;

	private Stage stage;
	private TextArea statsArea;
	private TableView<Data> tableView;

	private File selectedFile;

	private HuffmanCompressor compressor;
	private HuffmanDecompressor decompressor;
	//private HuffmanTree tree;
	// Statistics
	private HuffmanCompressor.CompressionResult lastResult; // (Inner / Nested Class)
	private String[] huffmanCodes;

	public HuffmanController(Stage stage) {
		this.stage = stage;
		this.statsArea = new TextArea();
		this.tableView = new TableView<>();

	}

	private HuffmanCompressor getCompressor() {
		if (compressor == null) {
			compressor = new HuffmanCompressor();
		}
		return compressor;
	}

	private HuffmanDecompressor getDecompressor() {
		if (decompressor == null) {
			decompressor = new HuffmanDecompressor();
		}
		return decompressor;
	}

//	private HuffmanTree getTreeBuilder() {
//		if (tree == null) {
//			tree = new HuffmanTree();
//		}
//		return tree;
//	}

	public TextArea getStatsArea() {
		return statsArea;
	}

	public TableView<Data> getTableView() {
		return tableView;
	}

	public void selectFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose a file");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"),
				new FileChooser.ExtensionFilter("Huffman Files", "*.huff"));
		selectedFile = fileChooser.showOpenDialog(stage);

		if (selectedFile != null) {
			showAlert(Alert.AlertType.INFORMATION, "File Selected", "Selected: " + selectedFile.getName());
		}
	}

	// Compress selected file
	public void compressFile() {
		if (!validateFileSelection()) {
			return;
		}

		if (selectedFile.getName().toLowerCase().endsWith(".huff")) {
			showAlert(Alert.AlertType.ERROR, "Invalid File", "Cannot compress .huff files");
			return;
		}

		try {
			String outputPath = selectedFile.getPath().substring(0, selectedFile.getPath().lastIndexOf('.')) + ".huff";
			File outputFile = new File(outputPath);

			lastResult = getCompressor().compress(selectedFile, outputFile);
			huffmanCodes = lastResult.huffmanCodes;  //////////////////////////////////////////////////////////////////////////////

			showAlert(Alert.AlertType.INFORMATION, "Success",
					"Compression completed!\n" + "Input: " + lastResult.inputSize + " bytes\n" + "Output: "
							+ lastResult.outputSize + " bytes\n" + "Ratio: "
							+ String.format("%.2f", lastResult.compressionRatio) + "%\n" + "Saved to: " + outputPath);

		} catch (Exception e) {
			showAlert(Alert.AlertType.ERROR, "Compression Error", "Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Decompress selected file
	public void decompressFile() {
		if (!validateFileSelection()) {
			return;
		}

		if (!getDecompressor().canDecompress(selectedFile.getName())) {
			showAlert(Alert.AlertType.ERROR, "Invalid File", "Can only decompress .huff files!");
			return;
		}

		try {
			getDecompressor().decompress(selectedFile, selectedFile);

			String outputPath = selectedFile.getPath().substring(0, selectedFile.getPath().lastIndexOf('.'));

			showAlert(Alert.AlertType.INFORMATION, "Success",
					"Decompression completed!\n" + "Output file: " + outputPath);

		} catch (Exception e) {
			showAlert(Alert.AlertType.ERROR, "Decompression Error", "Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void showStatistics() {
		if (lastResult == null) {
			showAlert(Alert.AlertType.WARNING, "No Data", "Please compress a file first!");
			return;
		}

		try {             // Update text area
			statsArea.clear();
			statsArea.appendText("     HUFFMAN CODING STATISTICS       \n");
			statsArea.appendText("═══════════════════════════════════════\n\n");
			statsArea.appendText("File name: " + lastResult.filename + "\n");
			statsArea.appendText("Input size: " + lastResult.inputSize + " bytes\n");
			statsArea.appendText("Output size: " + lastResult.outputSize + " bytes\n");
			statsArea.appendText("Compression ratio: " + String.format("%.2f", lastResult.compressionRatio) + "%\n");
			statsArea.appendText("Unique characters: " + lastResult.uniqueChars + "\n");
			statsArea.appendText("Total characters: " + lastResult.totalChars + "\n");

			// Update table
			ObservableList<Data> tableData = FXCollections.observableArrayList();

			for (int i = 0; i < ALL_CHARS; i++) {
				if (lastResult.frequencies[i] > 0) {
					String charDisplay = formatCharacter((char) (i & 0xFF), i);  // تنسيق الحرف
					String code = (huffmanCodes != null && huffmanCodes[i] != null) ? huffmanCodes[i] : ""; //  جلب الكود

					tableData.add(new Data(charDisplay, lastResult.frequencies[i], code));
				}
			}

			tableView.setItems(tableData);

		} catch (Exception e) {
			showAlert(Alert.AlertType.ERROR, "Display Error", "Error displaying statistics: " + e.getMessage());
		}
	}

	private String formatCharacter(char ch, int code) {
		if (ch == '\n')
			return "\\n (newline)";
		if (ch == '\r')
			return "\\r (carriage return)";
		if (ch == '\t')
			return "\\t (tab)";
		if (ch == ' ')
			return "SPACE";
		if (Character.isISOControl(ch))
			return "[CTRL:" + code + "]";
		return Character.toString(ch);
	}

	private boolean validateFileSelection() {
		if (selectedFile == null) {
			showAlert(Alert.AlertType.ERROR, "No File", "Please select a file first!");
			return false;
		}
		return true;
	}

	private void showAlert(Alert.AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}

