package com.tasks.task2;


import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/**
 * Using project reactor
 *
 */


public class Reactor {
    public static void main(String[] args) {
        Flux flux = Flux.range(0, 100);
        flux.subscribe(new Subscriber<Integer>() {
            private Subscription subscription;
            private List<Integer> items = new ArrayList<>();
            private int counter;


            @Override
            public void onSubscribe(Subscription subscription) {
                if (subscription == null) throw new NullPointerException();
                this.subscription = subscription;
                subscription.request(3);

            }
            //3 items are requested each time
            @Override
            public void onNext(Integer integer) {
                if (integer == null) throw  new NullPointerException();
                counter++;
                items.add(integer);
                System.out.println(integer);
                if (counter%3==0){
                    subscription.request(3);
                }
            }


            @Override
            public void onError(Throwable throwable) {
                System.out.println("Some error");
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("Complete");
            }
        });


    }
}
