package FrequencyOfOperations;

import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            synchronized (sizeToFreq) {
                while (!Thread.interrupted()) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        System.out.println("Конец программы");
                    }
                    int maxValueKey = getMaxValueKey();
                    System.out.println("Самое частое количество повторений на данный момент " + maxValueKey +
                            " (встретилось " + sizeToFreq.get(maxValueKey) + " раз)");
                }
            }
        });
        thread.start();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Thread th = new Thread(() -> {
                int countR = 0;
                int freq;
                char[] chars = generateRoute("RLRFR", 100).toCharArray();
                for (char ch : chars) {
                    if (ch == 'R') {
                        countR++;
                    }
                }
                System.out.println("Количество повторений в этом потоке = " + countR);
                synchronized (sizeToFreq) {
                    if (sizeToFreq.containsKey(countR)) {
                        freq = sizeToFreq.get(countR);
                        freq++;
                    } else {
                        freq = 1;
                    }
                    sizeToFreq.put(countR, freq);
                    sizeToFreq.notify();
                }
            });
            threads.add(th);
            th.start();
        }
        for (var th : threads) {
            th.join();
        }
        thread.interrupt();
        int maxValueKey = getMaxValueKey();
        System.out.println("Самое частое количество повторений " + maxValueKey + " (встретилось "
                + sizeToFreq.get(maxValueKey) + " раз)");
        sizeToFreq.remove(maxValueKey);
        System.out.println("Другие размеры:");
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            System.out.println("- " + entry.getKey() + " (" + entry.getValue().toString() + " раз)");
        }

    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static int getMaxValueKey() {
        return sizeToFreq.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .get().getKey();
    }
}
