import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class ExamplePatternPCWithWaitAndNotify {
    public static void main(String[] args) throws InterruptedException {
        ProducerConsumer pc = new ProducerConsumer();
        // создание потоков
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pc.produce();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pc.consume();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        // запуск потоков
        thread1.start();
        thread2.start();

        // ожидание выпонения потоков
        thread1.join();
        thread2.join();
    }
}

class ProducerConsumer {
    // не потокобезопасная очередь, но с помощью блоков синхронизации
    // и методов wait() и notify() она станет потокобезопасной
    private Queue<Integer> queue = new LinkedList<>();
    // задание константы ограничения очереди
    private final int LIMIT = 10;
    // задание объекта синхронизации
    private final Object lock = new Object();
    public void produce() throws InterruptedException {
        int value = 0;
        while (true) {
            synchronized (lock) {
                // при переполнении очереди wait() передаёт окно lock другому потоку
                while (queue.size() == LIMIT) {
                    lock.wait();
                }
                // помещение в очередь нового элемента
                queue.offer(value++);
                // идёт оповещение другого потока о появлении новых элементов в очереди
                lock.notify();
            }

        }
    }
    public void consume() throws InterruptedException {
        while (true) {
            synchronized (lock) {
                // при окончании очереди wait() передаёт окно lock другому потоку
                while (queue.size() == 0) {
                    lock.wait();
                }
                // метод poll() освобождает свободные места в очереди
                int value = queue.poll();
                System.out.println(value);
                System.out.println("Queue size is " + queue.size());
                // оповещение другого потока об освобождении очереди
                lock.notify();
            }
            Thread.sleep(1000); // торможение consume() для поспевания produce() заполнять очередь
        }
    }
}