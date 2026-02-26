package application;

public class MyStack<T> {
    
    private Object[] stack;
    private int top;
    private int capacity;
    
    public MyStack() {
        this.capacity = 16;
        this.stack = new Object[capacity];
        this.top = -1;
    }
    
    public MyStack(int initialCapacity) {
        this.capacity = initialCapacity;
        this.stack = new Object[capacity];
        this.top = -1;
    }
 
    public void push(T element) {
        if (top == capacity - 1) {
            resize();
        }
        
        top++;
        stack[top] = element;
    }
    
  
    public T pop() {
        if (isEmpty()) {
            throw new RuntimeException("Stack is empty");
        }
        
        T element = (T) stack[top];
        stack[top] = null;
        top--;
        
        return element;
    }
    
  
   
    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("Stack is empty");
        }
        
        return (T) stack[top];
    }
    

    public boolean isEmpty() {
        return top == -1;
    }
    

    public int size() {
        return top + 1;
    }

    private void resize() {
        capacity *= 2;
        Object[] newStack = new Object[capacity];
        
        for (int i = 0; i <= top; i++) {
            newStack[i] = stack[i];
        }
        
        stack = newStack;
    }
    
    public void clear() {
        top = -1;
        for (int i = 0; i < stack.length; i++) {
            stack[i] = null;
        }
    }
}