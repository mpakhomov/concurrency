package com.mpakhomov.jcip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author mpakhomov
 */
public class UnsafeSequence {
    private long value;

    public long nextValue() {
        return value++;
    }

    public static void main(String[] args) {
        UnsafeSequence unsafeSequence = new UnsafeSequence();
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        final int numberOfIterations = 1000;
        List<Long> list1 = new ArrayList<>(numberOfIterations);
        List<Long> list2 = new ArrayList<>(numberOfIterations);

        Runnable r1 = () -> {
            for (int i = 0; i < numberOfIterations; i++) {
                list1.add(unsafeSequence.nextValue());
//                System.out.println("Thread " + Thread.currentThread() + " value = " + unsafeSequence.nextValue());
//                try { Thread.sleep(ThreadLocalRandom.current().nextInt(1000)); } catch (InterruptedException e) {}
            }
        };
        Runnable r2 = () -> {
            for (int i = 0; i < numberOfIterations; i++) {
                list2.add(unsafeSequence.nextValue());
//                System.out.println("Thread " + Thread.currentThread() + " value = " + unsafeSequence.nextValue());
//                try { Thread.sleep(ThreadLocalRandom.current().nextInt(1000)); } catch (InterruptedException e) {}
            }
        };

        executorService.submit(r1);
        executorService.submit(r2);

        executorService.shutdownNow();
        try {
            executorService.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Long> mergedList = new ArrayList<>(numberOfIterations * 2);
        mergedList.addAll(list1);
        mergedList.addAll(list2);

        Set<Long> uniques = mergedList.stream().distinct().collect(Collectors.toSet());

        Map<Long, Integer> occurences = new HashMap<>();
        for (Long l : mergedList) {
            if (occurences.get(l) == null) {
                occurences.put(l, 1);
            } else {
                int value = occurences.get(l);
                occurences.put(l, ++value);
            }
        }

        Map<Long, Integer> duplicates = new TreeMap<>();
        for (Map.Entry<Long, Integer> entry : occurences.entrySet()) {
            if (entry.getValue() > 1) {
                duplicates.put(entry.getKey(), entry.getValue());
            }
        }


        System.out.println("nextValue() was called: " + numberOfIterations * 2 );
        System.out.println("Number of unique values: " + uniques.size());
        System.out.println("Number of duplicates = " + duplicates.size());
        System.out.println("Duplicates: " + duplicates);

        System.out.println("Main thread finished");
    }
}
