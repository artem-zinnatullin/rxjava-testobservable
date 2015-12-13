package com.artemzin.rxjava.testobservable;

import rx.Observable;
import rx.Subscriber;

public class TestObservable<T> extends Observable<T> {

  public static <T> TestObservable<T> from(final Observable<T> observable) {
    return new TestObservable<T>(new OnSubscribe<T>() {
      @Override public void call(Subscriber<? super T> subscriber) {
        observable.subscribe(subscriber);
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  protected TestObservable(OnSubscribe<T> f) {
    super(f);
  }

  /**
   * Asserts that source {@link Observable} emits error.
   * If source {@link Observable} won't emit error or error won't be equal to expected one then
   * this Operator will emit {@link AssertionError}.
   *
   * @param expectedError expected error that should be emitted by source {@link Observable}.
   *                      Operator will compare them via {@link Throwable#equals(Object)} call.
   * @return {@link TestObservable}.
   */
  public TestObservable<T> expectError(final Throwable expectedError) {
    return new TestObservable<T>(new OnSubscribe<T>() {
      @Override public void call(final Subscriber<? super T> subscriber) {
        TestObservable.this.subscribe(new Subscriber<T>() {
          @Override public void onCompleted() {
            subscriber.onError(new AssertionError("No errors were thrown by the source Observable"));
          }

          @Override public void onError(Throwable e) {
            if (expectedError.equals(e)) {
              subscriber.onCompleted();
            } else {
              AssertionError assertionError = new AssertionError("Expected exception is not equals to actual one. Expected = " + expectedError + ", actual = " + e);
              assertionError.initCause(e);
              subscriber.onError(assertionError);
            }
          }

          @Override public void onNext(T t) {
            subscriber.onNext(t);
          }
        });
      }
    });
  }
}
