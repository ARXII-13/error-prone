---
title: NumericEquality
summary: Numeric comparison using reference equality instead of value equality
layout: bugpattern
tags: ''
severity: ERROR
---

<!--
*** AUTO-GENERATED, DO NOT MODIFY ***
To make changes, edit the @BugPattern annotation or the explanation in docs/bugpattern.
-->


## The problem
Numbers are compared for reference equality/inequality using == or != instead of
for value equality using .equals()

## Suppression
Suppress false positives by adding the suppression annotation `@SuppressWarnings("NumericEquality")` to the enclosing element.

----------

### Positive examples
__NumericEqualityPositiveCases.java__

{% highlight java %}
/*
 * Copyright 2012 The Error Prone Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.errorprone.bugpatterns.testdata;

/** @author scottjohnson@google.com (Scott Johnson) */
public class NumericEqualityPositiveCases {

  public boolean testIntegers(Integer x, Integer y) {
    boolean retVal;

    // BUG: Diagnostic contains: Objects.equals(x, y)
    retVal = (x == y);

    // BUG: Diagnostic contains: !Objects.equals(x, y)
    retVal = (x != y);
    final Integer constValue = Integer.valueOf(1000);

    // BUG: Diagnostic contains: Objects.equals(x, constValue)
    retVal = (x == constValue);

    // BUG: Diagnostic contains: !Objects.equals(x, constValue)
    retVal = (x != constValue);

    return retVal;
  }

  public boolean testLongs(Long x, Long y) {
    boolean retVal;

    // BUG: Diagnostic contains: Objects.equals(x, y)
    retVal = (x == y);

    // BUG: Diagnostic contains: !Objects.equals(x, y)
    retVal = (x != y);
    final Long constValue = Long.valueOf(1000L);

    // BUG: Diagnostic contains: Objects.equals(x, constValue)
    retVal = (x == constValue);

    // BUG: Diagnostic contains: !Objects.equals(x, constValue)
    retVal = (x != constValue);

    return retVal;
  }

  public boolean testMixed(Integer x, Number y) {
    boolean retVal;

    // BUG: Diagnostic contains: Objects.equals(x, y)
    retVal = (x == y);

    // BUG: Diagnostic contains: !Objects.equals(x, y)
    retVal = (x != y);
    final Number constValue = Long.valueOf(1000L);

    // BUG: Diagnostic contains: Objects.equals(x, constValue)
    retVal = (x == constValue);

    // BUG: Diagnostic contains: !Objects.equals(x, constValue)
    retVal = (x != constValue);

    return retVal;
  }
}
{% endhighlight %}

### Negative examples
__NumericEqualityNegativeCases.java__

{% highlight java %}
/*
 * Copyright 2012 The Error Prone Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.errorprone.bugpatterns.testdata;

/** @author scottjohnson@google.com (Scott Johnsson) */
public class NumericEqualityNegativeCases {

  public static final Integer NULLINT = null;

  public boolean testEquality(Integer x, Integer y) {
    boolean retVal;

    retVal = x.equals(y);
    retVal = (x == null);
    retVal = (x != null);
    retVal = (null == x);
    retVal = (null != x);
    retVal = x == 1000;
    retVal = x + y == y + x;
    retVal = x == NULLINT;
    retVal = NULLINT == x;

    return retVal;
  }

  @SuppressWarnings("NumericEquality")
  public boolean testSuppressWarnings(Integer x, Integer y) {
    boolean retVal;

    retVal = (x != y);
    retVal = (x == y);

    return retVal;
  }

  public boolean testComparisons(Integer x, Integer y) {
    boolean retVal;

    retVal = x <= y;
    retVal = x < y;
    retVal = x >= y;
    retVal = x > y;

    return retVal;
  }

  public boolean testUnboxing(Integer x, int y) {
    boolean retVal;

    retVal = (x == y);
    retVal = (x != y);

    final int constValue = 1000;
    retVal = (x == constValue);
    retVal = (x != constValue);

    return retVal;
  }
}
{% endhighlight %}

