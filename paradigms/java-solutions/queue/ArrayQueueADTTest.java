package queue;

public class ArrayQueueADTTest {
    public static void main(String[] args) {
        ArrayQueueADT q1 = ArrayQueueADT.create();
        ArrayQueueADT q2 = ArrayQueueADT.create();
        for (int i = 0; i < 10; i++) {
            ArrayQueueADT.enqueue(q1, "q_1_" + i);
            ArrayQueueADT.enqueue(q2, "q_2_" + i);
        }
        for (int i = 0; i < 4; i++) {
            ArrayQueueADT.dequeue(q1);
            ArrayQueueADT.dequeue(q2);
        }
        for (int i = 10; i < 15; i++) {
            ArrayQueueADT.enqueue(q1, "q_1_" + i);
            ArrayQueueADT.enqueue(q2, "q_2_" + i);
        }
        dumpQueue(q1);
        dumpQueue(q2);
    }

    public static void dumpQueue(ArrayQueueADT queue) {
        while (!ArrayQueueADT.isEmpty(queue)) {
            System.out.println(ArrayQueueADT.size(queue) + " " + ArrayQueueADT.dequeue(queue));
        }
        System.out.println();
    }
}
