package application;
public class MyArrayList<T> {
    
    private Object[] array;
    private int size;
    private int capacity;
    
    public MyArrayList() {
        this.capacity = 16;
        this.array = new Object[capacity];
        this.size = 0;
    }
    
    public MyArrayList(int initialCapacity) {
        this.capacity = initialCapacity;
        this.array = new Object[capacity];
        this.size = 0;
    }
    
    
    public void add(T element) {
        if (size == capacity) {
            resize();
        }
        
        array[size] = element;
        size++;
    }
    
  
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        return (T) array[index];
    }
//    
//    
//    public void set(int index, T element) {
//        if (index < 0 || index >= size) {
//            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
//        }
//        
//        array[index] = element;
//    }
//    
   
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        T removed = (T) array[index];
        
        // Shift elements left
        for (int i = index; i < size - 1; i++) {
            array[i] = array[i + 1];
        }
        
        size--;
        array[size] = null;
        
        return removed;
    }
    
    
    public int size() {
        return size;
    }
    
    
    public boolean isEmpty() {
        return size == 0;
    }
    
  
    public void clear() {
        size = 0;
        for (int i = 0; i < array.length; i++) {
            array[i] = null;
        }
    }
    
    
    public boolean contains(T element) {
        for (int i = 0; i < size; i++) {
            if (array[i] == null && element == null) {
                return true;
            }
            if (array[i] != null && array[i].equals(element)) {
                return true;
            }
        }
        return false;
    }
    
   
    private void resize() {
        capacity *= 2;
        Object[] newArray = new Object[capacity];
        
        for (int i = 0; i < size; i++) {
            newArray[i] = array[i];
        }
        
        array = newArray;
    }
    
   
    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(array[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        
        return sb.toString();
    }
}