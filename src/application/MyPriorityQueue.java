package application;
//4. الخلاصة المختصرة جداً
//Min-Heap مستخدم في Huffman لأنه:
//يضمن استخراج أقل ترددين بسرعة.
//وهذا أساسي لبناء شجرة Huffman.
//ويعمل بإدراج من الأسفل لأعلى وإزالة من الأعلى لأسفل.
//الرووت دائماً هو أصغر عنصر في الهيب.
public class MyPriorityQueue {
    
    private CharTree[] heap;
    private int size;
    private int capacity;
    
    public MyPriorityQueue() {
        this.capacity = 16; // initial capacity
        this.heap = new CharTree[capacity];
        this.size = 0;
    }
    
    public MyPriorityQueue(int initialCapacity) {
        this.capacity = initialCapacity;
        this.heap = new CharTree[capacity];
        this.size = 0;
    }
    
  
    public void add(CharTree element) {
        if (element == null) {
            throw new NullPointerException("Cannot add null element");
        }
        
        if (size == capacity) {
            resize();
        }
        
        heap[size] = element;
        heapifyUp(size);
        size++;
    }
    
    public CharTree remove() {
        if (size == 0) {
            return null;
        }
        
        CharTree min = heap[0];
        size--;
        heap[0] = heap[size];
        heap[size] = null;
        
        if (size > 0) {
            heapifyDown(0);
        }
        
        return min;
    }
    
   // Get minimum element without removing
    public CharTree peek() {
        if (size == 0) {
            return null;
        }
        return heap[0];
    }
    

    public int size() {
        return size;
    }
    

    public boolean isEmpty() {
        return size == 0;
    }
    
    
    private void heapifyUp(int index) {
        int current = index;
        
        while (current > 0) {
            int parent = (current - 1) / 2;
            
            if (heap[current].compareTo(heap[parent]) >= 0) {
                break;
            }
            
            // Swap
            CharTree temp = heap[current];
            heap[current] = heap[parent];
            heap[parent] = temp;
            
            current = parent;
        }
    }
    
   
    private void heapifyDown(int index) {
        int current = index;
        
        while (true) {
            int left = 2 * current + 1;
            int right = 2 * current + 2;
            int smallest = current;
            
            if (left < size && heap[left].compareTo(heap[smallest]) < 0) {
                smallest = left;
            }
            
            if (right < size && heap[right].compareTo(heap[smallest]) < 0) {
                smallest = right;
            }
            
            if (smallest == current) {
                break;
            }
            
            // Swap
            CharTree temp = heap[current];
            heap[current] = heap[smallest];
            heap[smallest] = temp;
            
            current = smallest;
        }
    }
    
    private void resize() {
        capacity *= 2;
        CharTree[] newHeap = new CharTree[capacity];
        
        for (int i = 0; i < size; i++) {
            newHeap[i] = heap[i];
        }
        
        heap = newHeap;
    }
    
  
    public void clear() {
        size = 0;
        for (int i = 0; i < heap.length; i++) {
            heap[i] = null;
        }
    }
}