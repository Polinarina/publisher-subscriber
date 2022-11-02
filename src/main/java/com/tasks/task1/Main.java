package com.tasks.task1;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Integer[] a = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        CustomPublisher publisher = new CustomPublisher<Integer>(a);

        CustomSubscriber subscriber =  new CustomSubscriber<Integer>();

        publisher.subscribe(subscriber);
    }

}
