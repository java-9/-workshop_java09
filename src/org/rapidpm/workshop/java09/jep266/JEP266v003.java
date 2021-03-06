package org.rapidpm.workshop.java09.jep266;


import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.IntStream;

/**
 * Copyright (C) 2010 RapidPM
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by RapidPM - Team on 23.11.16.
 */
public class JEP266v003 {


  public static void main(String[] args) throws InterruptedException {

    try (SubmissionPublisher pup = new SubmissionPublisher<Integer>()) {

      IntToStringProcessor processor = new IntToStringProcessor();
      pup.subscribe(processor);
      processor.subscribe(new TestSubscriber("Sub1"));

      IntStream.range(1, 10)
              .forEach(pup::submit);

      System.out.println("published all the numbers");
      Thread.sleep(100 + pup.estimateMaximumLag());
    }
  }



  public static class IntToStringProcessor extends SubmissionPublisher<String> implements Flow.Processor<Integer, String> {

    private Flow.Subscription subscription;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
      this.subscription = subscription;
      this.subscription.request(1);
    }

    @Override
    public void onNext(Integer item) {
      submit(String.valueOf(item));
      subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {

    }
  }

  public static class TestSubscriber implements Flow.Subscriber<String> {

    private final String name;

    public TestSubscriber(String name) {
      this.name = name;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
      subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(String item) {
      System.out.println(String.format("%s says: Hello %s", name, item));
      System.out.flush();
    }

    @Override
    public void onError(Throwable throwable) {
      System.err.print(throwable);
    }

    @Override
    public void onComplete() {
      // nothing to do here
    }
  }

}
