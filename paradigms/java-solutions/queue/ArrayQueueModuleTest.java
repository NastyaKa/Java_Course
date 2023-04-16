package queue;

public class ArrayQueueModuleTest {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            ArrayQueueModule.enqueue(i);
        }
        for (int i = 11; i < 21; i++) {
            ArrayQueueModule.push(i);
        }
        for (int i = 0; i < 4; i++) {
            ArrayQueueModule.dequeue();
        }
        for (int i = 0; i < 16; i++) {
            ArrayQueueModule.remove();
        }
        for (int i = 10; i < 15; i++) {
            ArrayQueueModule.enqueue(i);
        }
        ArrayQueueModule.remove();
        System.out.println(ArrayQueueModule.indexOf(13));

        while (!ArrayQueueModule.isEmpty()) {
            System.out.println(ArrayQueueModule.size() + " " + ArrayQueueModule.dequeue());
        }
    }
}
