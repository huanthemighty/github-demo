package com.huanthemighty;

import autovalue.shaded.com.google.common.common.base.Optional;
import java.util.HashMap;

/*
 * {@link HashMap#values()}
 */
public class Test {

  static class Super {
    private final String name;

    public Super(String name) {
      this.name = name;
    }

    public String name() {
      return "super";
    }

    public final String toString() {
      return Optional.of(name()).or(super.toString());
    }
  }

  static class Sub extends Super {
    @Override
    public String name() {
      return "sub";
    }

    public Sub(String name) {
      super(name);
    }
  }

  public static void main(String[] args) {
    Sub sub = new Sub("");
    System.out.println(sub);
  }
}
