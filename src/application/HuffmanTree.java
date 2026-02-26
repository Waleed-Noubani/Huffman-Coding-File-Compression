package application;

import java.io.IOException;

public class HuffmanTree {
    
    private static final int ALL_CHARS = 256;
    
//      Time: O(n log n), Space: O(n)
     
    public CharTree buildTree(int[] frequencies) {
        MyPriorityQueue pq = new MyPriorityQueue();
        
        // Add all characters with frequency > 0 // تعبئة المين هييب
        for (char i = 0; i < ALL_CHARS; i++) {
            if (frequencies[i] > 0) {
                pq.add(new CharTree(i, frequencies[i]));
            }
        }
        
        // حالة خاصة : لو كان الملف يحتوي على حرف واحد فقط بضيفله من عندي عقدة
        if (pq.size() == 1) {
            CharTree right = pq.remove();    //  '\0' هو null character :حرف قيمته صفر (0)، هو حرف موجود في ASCII لكنه ليس حرفًا مطبوعًا    ما بظهر نص
            CharTree parent = new CharTree('\0', right.getFreq(), new CharTree('\0'), right); //Internal Nodes (عقد داخلية) هذه العقد لا تمثل حرفًا
            pq.add(parent);
        }
        
        while (pq.size() > 1) {        // أسااااااس الشجرة والمشروع
            CharTree left = pq.remove();
            CharTree right = pq.remove();
            CharTree parent = new CharTree('\0', left.getFreq() + right.getFreq(), left, right);
            pq.add(parent);
        }
        
        return pq.remove();
    }
    
    
    public String[] generateCodes(CharTree root) { //الأكواد لكل حرف بناء على الشجرة
        String[] codes = new String[ALL_CHARS];
        generateCodesRecursive(root, codes, "");
        return codes;
    }
    
    
    private void generateCodesRecursive(CharTree node, String[] codes, String code) { // الذهاب لليسار → نضيف "0"
        if (node.isLeaf()) {                                                         // الذهاب لليمين → نضيف "1"
            codes[node.getC()] = code;                                              // عند الوصول إلى Leaf = وجدنا كود الحرف
        } else {
            if (node.getLeft() != null) {
                generateCodesRecursive(node.getLeft(), codes, code + '0');
            }
            if (node.getRight() != null) {
                generateCodesRecursive(node.getRight(), codes, code + '1');
            }
        }
    }
    
  //  فك الضغط لا يعرف الشجرة لازم نرسل وصفة الشجرة مع الملف المضغوط 
    public String convertHuffToString(CharTree root) {      // تحوّل شجرة الهوفمان إلى
                                                     //	String حتى نخزنها داخل ملف الضغط
        MyStack<CharTree> stack = new MyStack<>();   // هذا مهم جدًا حتى نقدر نفك الضغط لاحقًا.
        stack.push(root);
        StringBuilder result = new StringBuilder();
        
        while (!stack.isEmpty()) {
            CharTree node = stack.pop();
            
            if (node.isLeaf()) {
                result.append('1');
                result.append(node.getC());
            } else {
                result.append('0'); //internal node
            }
            
            if (node.getRight() != null) {   
                stack.push(node.getRight());
            }
            if (node.getLeft() != null) {
                stack.push(node.getLeft());  // preorder: node → left → right
            }
        }
        
        return result.toString();  // "01A01B1C"
    }
    
  //  تأخذ الـ  :لفك الضغطString (وتُعيد بناء شجرة هوفمان الأصلية )  المشفّر من الملف المضغوط
   
    public CharTree convertStringToHuff(String string, int addBits) { //addBits = عدد البتات المضافة لإكمال آخر Byte .

        CharTree root = new CharTree('\0'); // root : internal node
        
        StringBuilder cleaned = new StringBuilder();
        for (int i = 0; i < string.length() - addBits; ) {
            if (string.charAt(i) == '0') {
                cleaned.append('0');
                i++;
            } else {
                cleaned.append('1');
                i++;
                if (i + 8 <= string.length() - addBits) {
                    int charCode = Integer.parseInt(string.substring(i, i + 8), 2); // 2 : نظام ثنائي
                    cleaned.append((char) (charCode & 0xFF));
                    i += 8;
                }
            }
        } 
      //string =101000001011000010..
//        إذا كان البت الحالي = '1'
//        		 هذا leaf node : لكن بعده مباشرة يوجد 8 بتات تمثل الحرف
//        		مثال:
//        		1 01000001 = يعني leaf بالحرف 'A'
//        		الكود يعمل التالي:
//
//        		يأخذ 01000001
//        		يحولها إلى int
//        		ثم إلى char
//        		ويضيف '1' + 'A' إلى cleaned
        
          // calanced = 1A1B0

        
        // Rebuild tree using stack اعادة البناء للشجرة
        MyStack<CharTree> stack = new MyStack<>();
        stack.push(root);
        
        for (int i = 0; i < cleaned.length(); i++) {
            CharTree current = stack.isEmpty() ? new CharTree('\0') : stack.pop();
            
            if (cleaned.charAt(i) == '0') {
                // Internal node
                current.setRight(new CharTree('\0'));
                current.setLeft(new CharTree('\0'));
                stack.push(current.getRight());
                stack.push(current.getLeft());
                
            } else {  // Leaf node  
                if (i + 1 < cleaned.length()) {
                    current.setC(cleaned.charAt(i + 1));
                    i++;
                }
            }
        }
        
        return root;
    }
    
            // BitStreamWriter write Boolean bits , NOT String 
    public MyArrayList<Boolean>[] convertCodesToBoolean(String[] codes) { // تحويل الكود من "0101" إلى [false, true, false, true]
        MyArrayList<Boolean>[] boolCodes = new MyArrayList[ALL_CHARS];    // لأن BitWriter يعمل على Boolean وليس String.
        
        for (int i = 0; i < codes.length; i++) {
            if (codes[i] != null) {
                boolCodes[i] = new MyArrayList<Boolean>();
                for (char bit : codes[i].toCharArray()) {
                    boolCodes[i].add(bit == '1');
                }
            }
        }
        
        return boolCodes;  // boolCodes['A'] = [false, true, false]
    }
}