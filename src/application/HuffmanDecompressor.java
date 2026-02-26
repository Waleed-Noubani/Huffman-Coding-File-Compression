//[HEADER]
//[extension][uniqueChars][totalChars][treeBitCount][treePadding][tree][data]

//[DATA]
//├─ Huffman bits (compressed data)
//├─ data padding

package application;

import java.io.*;

public class HuffmanDecompressor {

	private HuffmanTree tree;

	public HuffmanDecompressor() {
		this.tree = new HuffmanTree();
	}

	public void decompress(File inputFile, File outputFile) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputFile)); // فتح الملف

		// Read file extension
		byte[] extBytes = new byte[3];
		in.read(extBytes);
		String extension = new String(extBytes).trim();

		// Read uniqueChars + totalChars 4 + 4 = 8 Byte
		byte[] uniqueBytes = new byte[4];
		in.read(uniqueBytes);
		int uniqueChars = readInt(uniqueBytes);

		byte[] totalBytes = new byte[4];
		in.read(totalBytes);
		int totalChars = readInt(totalBytes);
		
		//  Read treeBitCount
		byte[] treeBitBytes = new byte[4];
		in.read(treeBitBytes);
		int treeBitCount = readInt(treeBitBytes); // كم bit فعلي للشجرة
												//وأين تنتهي الشجرة وتبدأ البيانات

		//Read treePadding 
		byte[] treePadBytes = new byte[4];
		in.read(treePadBytes);
		int treePadding = readInt(treePadBytes);

		//  عدد البايتات الفعلية للشجرة 
		int treeBytes = (treeBitCount + treePadding) / 8;

		//  اقرأ الشجرة 
		StringBuilder treeBuilder = new StringBuilder(); // نقرأ كل byte -- نحوله إلى 8 bits--نجمعهم بسلسلة واحدة--
		for (int i = 0; i < treeBytes; i++) {
			int b = in.read();
			if (b == -1)
				break;

			String byteBinary = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
			// b = 110 ->> after replace b = 00000110

			treeBuilder.append(byteBinary);
		}

		String serializedTree = treeBuilder.toString(); // 00000011 00110110 ..

		CharTree root = tree.convertStringToHuff(serializedTree, treePadding); // إعادة بناء الشجرة

		// DATA SECTION
		String outputPath = outputFile.getParent() + File.separator // (/) // مسار الإخراج النهائي
				+ outputFile.getName().substring(0, outputFile.getName().lastIndexOf('.')) + "." + extension;

		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputPath));
		StringBuilder bitBuffer = new StringBuilder();
		int charsCount = 0;

		while (charsCount < totalChars) {
			int nextByte = in.read(); // Read next byte

			// Check end of file
			if (nextByte == -1) {
				break;
			}

			// تحويل byte → bits 5 ->> 00000101
			String byteBinary = String.format("%8s", Integer.toBinaryString(nextByte & 0xFF)).replace(' ', '0');
			bitBuffer.append(byteBinary);

			CharTree current = root; // عشان الف عالشجرة
			int i = 0;
			while (i < bitBuffer.length() && charsCount < totalChars) {

				if (bitBuffer.charAt(i) == '0') {
					current = current.getLeft();
				} else {
					current = current.getRight();
				}
				if (current == null) {
					break;
				}

				if (current.isLeaf()) {
					out.write(current.getC()); // نكتب الحرف
					charsCount++; // نزيد الحروف

					bitBuffer.delete(0, i + 1); // نحذف البتات المستخدمة
					i = 0;
					current = root;
				} else { // if not leaf
					i++;
				}
			}
		}

		out.flush();
		out.close();
		in.close();
	}

	private int readInt(byte[] bytes) { // Read 4 bytes as integer
		if (bytes == null || bytes.length != 4)
			return 0;

		int value = 0;
		value += (bytes[3] & 0x000000FF) << 24; // (xxx[y] & 0x000000FF) : تحوّل البايت من signed إلى unsigned
												// 00000001 (int) :الآن نزيحه 24 خانة لليسار:
												// after shift lift 24 : 00000001 00000000 00000000 00000000
		value += (bytes[2] & 0x000000FF) << 16;
		value += (bytes[1] & 0x000000FF) << 8;
		value += (bytes[0] & 0x000000FF);

		return value;
	}

	// Validate if file can be decompressed
	public boolean canDecompress(String filename) {
		return filename.toLowerCase().endsWith(".huff");
	}
}

//1️⃣ قراءة الهيدر
//
//أول إشي بقرأ الهيدر بنفس الترتيب:
//
//الامتداد
//
//عدد الحروف
//
//عدد بتات الشجرة
//
//padding
//
//ليش؟
//
//لأن الهيدر هو خريطة فك الضغط.
//
//2️⃣ قراءة الشجرة
//
//بحسب عدد البتات الحقيقي
//بقرأ الشجرة كـ bits
//وأعيد بنائها باستخدام نفس القواعد:
//
//0 → عقدة
//
//1 + 8 bits → ورقة
//
//3️⃣ فك البيانات
//
//أبدأ أقرأ البيانات bit-bit
//وأمشي على الشجرة:
//
//0 → يسار
//
//1 → يمين
//
//لما أوصل ورقة:
//
//أكتب الحرف
//
//أرجع للجذر.
//
//4️⃣ متى أوقف؟
//
//لما أفك totalChars حرف
//مش بناءً على EOF
//عشان أتجاهل padding.

