package com.tasks.task1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;

/**
 * Example of simple Subscriber class
 * @param <T> - item Type
 */
public class CustomSubscriber<T> implements Flow.Subscriber<T> {

    private List items;
    private boolean complete = false;

    private Flow.Subscription subscription;

    public CustomSubscriber() {
        items = new ArrayList();
    }

    @Override
    public void onSubscribe(final Flow.Subscription subscription) {
        if(subscription == null) throw new NullPointerException();
        if (this.subscription != null){
            subscription.cancel();
        }else{
            this.subscription = subscription;
        }

        subscription.request(1);
    }

    @Override
    public void onNext(final T item) {
        if(item == null){
            throw new NullPointerException();
        }
        System.out.println(item);
        items.add(item);
        if(!complete){
            subscription.request(1);
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
}
