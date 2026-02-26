package application;

import java.io.*;

// Time: O(n log n)
public class HuffmanCompressor {

	private static final int ALL_CHARS = 256;

	private HuffmanTree tree;  
	private int[] frequencies; // تكرار كل حرف
	private int totalChars; // نعرف متى نوقف فك الضغط (عدد جميع الحروف في الملف)
	private int uniqueChars; // عدد الحروف المختلفة
	private String serializedTree; // "01A01B1C"
	private String[] huffmanCodes; // الأكواد "01011" لكل حرف

	public HuffmanCompressor() {
		this.tree = new HuffmanTree();
		this.frequencies = new int[ALL_CHARS];
	}

	// Compress file

	public CompressionResult compress(File inputFile, File outputFile) throws IOException {
		countFrequencies(inputFile); // كل حرف كم تكرر

		CharTree root = tree.buildTree(frequencies); // بناء الشجرة عن طريق جمع العقدتين الاصغر كتردد ووو

		// Step 3: Generate Huffman codes - O(n)
		huffmanCodes = tree.generateCodes(root); // if left -> 0 || if right -> 1 للشجرة الكاملة
		MyArrayList<Boolean>[] boolCodes = tree.convertCodesToBoolean(huffmanCodes); // BitStreamWriter write Boolean bits , NOT String 


		serializedTree = tree.convertHuffToString(root);  // "01A01B1C"
		writeCompressedFile(inputFile, outputFile, boolCodes); 

		return calculateStatistics(inputFile, outputFile);
	}

	private void countFrequencies(File inputFile) throws IOException { // تقرأ الملف Byte-by-Byte // وتحسب عدد مرات ظهور
																		// كل حرف.
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputFile));

		frequencies = new int[ALL_CHARS];
		totalChars = 0;
		uniqueChars = 0;

		byte[] buffer = new byte[1];
		while (in.available() != 0) {
			in.read(buffer); // fetch one byte
			int charIndex = buffer[0] & 0xFF; // تحويل البايت إلى رقم 0–255

			if (frequencies[charIndex] == 0) {
				uniqueChars++;
			}
			frequencies[charIndex]++;
			totalChars++;
		}

		in.close();
	}

	// [extension][uniqueChars][totalChars][treeBitsCount][treePadding][treeSirelized][data][dataPadding]
	private void writeCompressedFile(File inputFile, File outputFile, MyArrayList<Boolean>[] boolCodes)
			throws IOException {

		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
		BitStreamWriter writer = new BitStreamWriter(out);

		// WRITE HEADER SECTION
		String extension = getFileExtension(inputFile.getName());
		writer.writeString(extension); // 3 bytes

		writer.writeInt(uniqueChars); // عدد الحروف المختلفة
		writer.writeInt(totalChars); // عدد كل الحروف في الملف الأصلي

	
		int treeBitCount = 0;  // صافي بدون البادينغ

		for (int i = 0; i < serializedTree.length(); i++) { // count tree bit // serializedTree = 01A1D01E
		    char c = serializedTree.charAt(i);

		    if (c == '1') {
		        treeBitCount += 1 + 8; // marker + char
		        i++;
		    } else {
		        treeBitCount += 1; // internal node
		    }
		}
		
		writer.writeInt(treeBitCount);
//		لأن الشجرة تُكتب على شكل bits،
//		لكن التخزين يتم على مستوى bytes.
//		لذلك:
//		آخر byte فيه padding
//		لازم أعرف كم bit فعلي للشجرة
//		بدون treeBitCount ما بقدر أفرق بين:
//		نهاية الشجرة
//		بداية البيانات


		int treePadding = (treeBitCount % 8 == 0) ? 0 : (8 - (treeBitCount % 8));
		writer.writeInt(treePadding);
		
															//serializedTree:وصفة لفك الترميز بتقلي وين الاوراق
													 	   // وين العقد كل حرف على اي ورقة وليست بيانات حقيقية
													       // serializedTree = 001a1b01c1d 
		for (int i = 0; i < serializedTree.length(); i++) {
		    char c = serializedTree.charAt(i);

		    if (c == '1') {
		        writer.writeBit(true);
		        i++;

		        byte nextChar = (byte) serializedTree.charAt(i);
		        String binary = String.format("%8s",
		                Integer.toBinaryString(nextChar & 0xFF)).replace(' ', '0');

		        for (int j = 0; j < 8; j++) {
		            writer.writeBit(binary.charAt(j) == '1'); // // 'a' = 97 --> 01100001
		        }
		    } else {
		        writer.writeBit(false);
		    }
		}
		writer.padToCompleteByte(); // 00011101 0101101 ..
		// END HEADER	
																// نقرأ الملف الأصلي Byte Byte
																// ونحوّل كل Byte إلى كود Huffman
		// DATA SECTION											// ونكتب البتات مباشرة بدون تخزين في الذاكرة
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputFile)); // فتح الملف الأصلي للقراءة
		byte[] buffer = new byte[1];

		while (in.available() != 0) {
			in.read(buffer);
			int charIndex = buffer[0] & 0xFF; // // نقرأ حرف واحد (Byte). من 0 إلى 255

			if (boolCodes[charIndex] != null) {
				for (int i = 0; i < boolCodes[charIndex].size(); i++) { // 'a' → [true, false, true]
					writer.writeBit(boolCodes[charIndex].get(i));
				}
			}
		}
		// الملف المضغوط : 10100 <-- ab

		writer.padToCompleteByte();
		in.close();
		writer.close();
	}



	private String getFileExtension(String filename) {
		int dotIndex = filename.lastIndexOf('.'); // عدد الحروف قبل اخر نقطه في اسم الملف
		if (dotIndex > 0 && dotIndex < filename.length() - 1) { // بتاكد اذا النقطة ما كانت اول حرف وبتاكد انه مش بعد
																// النقطة فاضي
			String ext = filename.substring(dotIndex + 1); // اقتطعت اخر حروف بعد النقطة
			if (ext.length() > 3) {
				ext = ext.substring(0, 3); // ناخذ اول 3 حروف
			}
			while (ext.length() < 3) { // اذا اقل بضيف سبيس
				ext += " ";
			}
			return ext;
		}

		return "txt"; // هذا في حال ما كان اي امتداد للملف بحطله اشي افتراضي تكست
	}

	public int[] getFrequencies() {
		return frequencies;
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private CompressionResult calculateStatistics(File inputFile, File outputFile) {
		long inputSize = inputFile.length();
		long outputSize = outputFile.length();
		double ratio = 100-((outputSize * 100.0) / inputSize);
		
		CompressionResult res = new CompressionResult(inputFile.getName(), inputSize, outputSize, ratio, uniqueChars, totalChars,
				frequencies, huffmanCodes);
		return res ; 
	}

	// -------------------------------------------------------------------------------------------------------------//
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// claaaaaaaaaaaasssssssssssss newwwwwwwwwwwwwwwwwwwwww (result statistics ) ;

	public static class CompressionResult {
		public final String filename;
		public final long inputSize;
		public final long outputSize;
		public final double compressionRatio;
		public final int uniqueChars;
		public final int totalChars;
		public final int[] frequencies;
		public final String[] huffmanCodes;

		public CompressionResult(String filename, long inputSize, long outputSize, double ratio, int uniqueChars,
				int totalChars, int[] frequencies, String[] codes) {
			this.filename = filename;
			this.inputSize = inputSize;
			this.outputSize = outputSize;
			this.compressionRatio = ratio;
			this.uniqueChars = uniqueChars;
			this.totalChars = totalChars;
			this.frequencies = frequencies;
			this.huffmanCodes = codes;
		}
	}
}

//1️⃣ قراءة الملف وحساب التكرارات
//
//أول خطوة بقرأ الملف byte-byte
//وبحسب كم مرة كل byte تكرر.
//ليش؟
//لأن Huffman يعتمد على التردد
//الحرف اللي يتكرر أكثر يأخذ كود أقصر.
//
//2️⃣ بناء شجرة Huffman
//
//بستخدم Priority Queue
//كل عقدة تمثل حرف + تردده
//بدمج أقل عقدتين ترددًا
//وبكرر لحد ما يتكوّن root واحد.
//
//ليش؟
//
//هذا يضمن أقل متوسط طول للكود
//وهو أساس خوارزمية Huffman.
//
//
//بمر على الشجرة:
//
//يسار = 0
//
//يمين = 1
//
//كل حرف يأخذ مسار من الجذر للورقة.
//
//النتيجة؟
//
//كود ثنائي مختلف لكل حرف
//بدون prefix ambiguity.
/////////////////////////////////////////////////////////////////////
//4️⃣ تسلسل الشجرة (Serialized Tree)
//
//بما إن فك الضغط ما عنده الشجرة
//لازم أخزنها داخل الملف المضغوط.
//
//خزنتها باستخدام preorder:
//
//0 = عقدة داخلية
//
//1 + 8 bits = ورقة + حرف
//
//ليش؟
//
//حتى أقدر أعيد بناء نفس الشجرة 100%.
//
//5️⃣ بناء الـ Header
//
//الهيدر يحتوي معلومات ثابتة:
//
//امتداد الملف
//
//عدد الحروف الكلي
//
//عدد بتات الشجرة
//
//padding الخاص بالشجرة
//
//ليش الهيدر؟
//
//عشان فك الضغط يعرف:
//
//متى تنتهي الشجرة
//
//متى تبدأ البيانات
//
//ومتى يوقف.
//
//6️⃣ /////////////////////////////////////////// ضغط البيانات
//
//أرجع أقرأ الملف مرة ثانية
//كل byte أستبدله بكود Huffman الخاص فيه
//وأكتبه bit-bit في الملف.
//إذا ضل bits أقل من 8
//بضيف padding.



