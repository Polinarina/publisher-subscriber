package com.tasks.task1;

import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Example of simple ArrayPublisher class
 * @param <T> - item Type
 */
public class CustomPublisher<T> implements Flow.Publisher<T> {

    private final T[] array;
    private AtomicBoolean completed;

    public CustomPublisher(T[]array) {
        this.array = array;
        completed = new AtomicBoolean(false);
    }


    @Override
    public void subscribe(Flow.Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new Flow.Subscription() {

            private int index;
            private AtomicLong requested = new AtomicLong(0);
            private volatile boolean canceled;


            @Override
            public void request(long n) {
                if (n <= 0) {
                    return;
                }
                long currentRequested;
                while (true){
                    currentRequested = requested.get();
                    if(currentRequested == Long.MAX_VALUE){
                        return;
                    }
                    n = currentRequested + n;
                    if (n <=0){
                        n = Long.MAX_VALUE;
                    }
                    if (requested.compareAndSet(currentRequested, n)){
                        break;
                    }
                }
                if (currentRequested > 0){
                    return;
                }

                if (n > (array.length-index)){
                    fastSend();
                }else {
                    slowSend(n);
                }
            }

            void fastSend(){
                int index = this.index;

                for (; index < array.length; index++){
                    if (canceled){
                        return;
                    }
                    T item = array[index];
                    if (item == null){
                        subscriber.onError(new NullPointerException());
                        return;
                    }
                    subscriber.onNext(item);
                }
                if (canceled){
                    return;
                }
                subscriber.onComplete();
            }

            void slowSend(long n){
                int index = this.index;
                int sent = 0;
                while (true){
                    for (; sent < n && index < array.length; sent++, index++){
                        if (canceled){
                            return;
                        }
                        T item = array[index];
                        if (item == null){
                            subscriber.onError(new NullPointerException());
                            return;
                        }
                        subscriber.onNext(item);
                    }
                    if (canceled){
                        return;
                    }
                    if (index == array.length){
                        subscriber.onComplete();
                        return;
                    }
                    n = requested.get();
                    if (n == sent){
                        this.index = index;
                        n = requested.addAndGet(-sent);
                        if (n == 0){
                            return;
                        }
                    }
                    sent =0;
                }
            }


            @Override
            public void cancel() {
                canceled = true;
            }
        });

    }
}
