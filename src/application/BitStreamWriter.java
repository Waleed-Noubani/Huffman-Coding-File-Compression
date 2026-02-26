package application;

import java.io.BufferedOutputStream;
import java.io.IOException;

//الكمبيوتر ما بحفظ "bit" لوحده
//هو يحفظ Byte = 8 bits
//فلو أعطيناه 1 بت → مستحيل يكتب إشي
//لازم نجمع "8 بت" في buffer
//ولما يصيروا 8 → نكتب byte كامل مرة واحدة.
public class BitStreamWriter {   // لأن Huffman ينتج bits بطول متغير

	private BufferedOutputStream out; // يجمع Bytes في داخلي buffer // يكتبهم دفعة واحدة لما يمتلئ
	private int buffer; // يخزن البتات قبل ما يصيروا byte
	private int bitsInBuffer; // كم بت موجود الآن

	public BitStreamWriter(BufferedOutputStream out) {
		this.out = out;
		this.buffer = 0;
		this.bitsInBuffer = 0;
	}

	public void writeBit(boolean bit) throws IOException {
		buffer <<= 1; // add new bit from right // شيفت ليفت
		if (bit) {
			buffer = buffer + 1;
		}

		bitsInBuffer++;

		if (bitsInBuffer == 8) {
			out.write((byte) buffer); // اكتب هذا الـ Byte (الذي جمعناه من 8 بتات) في الملف المضغوط.
			buffer = 0;
			bitsInBuffer = 0;
		}
	}

	public void writeString(String str) throws IOException {
		for (int i = 0; i < str.length(); i++) {
			out.write((byte) str.charAt(i));
		}
	}

	public void writeInt(int value) throws IOException {
		for (int i = 0; i < 4; i++) {
			out.write((byte) value);
			value >>= 8; // ازح القيمة 8 بتات لليمين ، Byte ، بمعنى: احذف أول
		} // واطّلع على البايت اللي بعده
	}

	public boolean padToCompleteByte() throws IOException { //  writeBit(kk);
		if (bitsInBuffer == 0) {
			return false;
		}

		int addBits = 8 - bitsInBuffer; // اضافة البتات المكملة للبايت مثلا ضل عندي 2 بيت باقي بروح بعبي الباقي اصفار
		for (int i = 0; i < addBits; i++) {
			writeBit(false);
		}

		return true;
	}

	public void close() throws IOException {
		if (out != null) {
			out.flush(); // برسل البيانات للقرص
			out.close();
		}
	}
}