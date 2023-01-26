import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final int LENGTH = 100_000;
    private static final ArrayBlockingQueue<String> blockingDequeToA = new ArrayBlockingQueue<>(100);
    private static final ArrayBlockingQueue<String> blockingDequeToB = new ArrayBlockingQueue<>(100);
    private static final ArrayBlockingQueue<String> blockingDequeToC = new ArrayBlockingQueue<>(100);

    private static AtomicInteger countA = new AtomicInteger(0);
    private static AtomicInteger countB = new AtomicInteger(0);
    private static AtomicInteger countC = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                String text = generateText("abc", LENGTH);
                try {
                    blockingDequeToA.put(text);
                    blockingDequeToB.put(text);
                    blockingDequeToC.put(text);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();

        Thread threadA = new Thread(() -> {
            countA = countSymbol(blockingDequeToA, 'a');
        });
        Thread threadB = new Thread(() -> {
            countB = countSymbol(blockingDequeToB, 'b');
        });
        Thread threadC = new Thread(() -> {
            countC = countSymbol(blockingDequeToC, 'c');
        });
        threadA.start();
        threadB.start();
        threadC.start();

        threadA.join();
        threadB.join();
        threadC.join();


        System.out.println("Самое максимальное количество символов 'a' = " + countA.get());
        System.out.println("Самое максимальное количество символов 'b' = " + countB.get());
        System.out.println("Самое максимальное количество символов 'c' = " + countC.get());
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static AtomicInteger countSymbol(ArrayBlockingQueue<String> blockingDeque, char c) {
        AtomicInteger resultValue = new AtomicInteger(0);
        for (int i = 0; i < 10_000; i++) {
            try {
                AtomicInteger tmpValue = new AtomicInteger(0);
                String text = blockingDeque.take();
                for (int j = 0; j < text.length(); j++) {
                    if (text.charAt(j) == c) {
                        tmpValue.getAndIncrement();
                    }
                }
                if (tmpValue.get() > resultValue.get()) {
                    resultValue = tmpValue;
                }
            } catch (InterruptedException e) {
                return resultValue;
            }
        }
        return resultValue;
    }

}
