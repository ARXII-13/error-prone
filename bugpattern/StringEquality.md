---
title: StringEquality
summary: String comparison using reference equality instead of value equality
layout: bugpattern
tags: ''
severity: WARNING
---

<!--
*** AUTO-GENERATED, DO NOT MODIFY ***
To make changes, edit the @BugPattern annotation or the explanation in docs/bugpattern.
-->


## The problem
Strings are compared for reference equality/inequality using == or !=instead of
for value equality using .equals()

## Suppression
Suppress false positives by adding the suppression annotation `@SuppressWarnings("StringEquality")` to the enclosing element.

----------

### Positive examples
__StringEqualityPositiveCases.java__

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

/** @author ptoomey@google.com (Patrick Toomey) */
public class StringEqualityPositiveCases {

  public boolean testEquality(String x, String y) {
    boolean retVal;

    // BUG: Diagnostic contains: Objects.equals(x, y)
    retVal = (x == y);
    // BUG: Diagnostic contains: !Objects.equals(x, y)
    retVal = (x != y);
    // BUG: Diagnostic contains: (x + y).equals(y + x)
    retVal = (x + y == y + x);
    // BUG: Diagnostic contains: !(x + y).equals(y + x)
    retVal = (x + y != y + x);
    // BUG: Diagnostic contains: (x + "str").equals(y + "str")
    retVal = (x + "str" == y + "str");
    // BUG: Diagnostic contains: !(x + "str").equals(y + "str")
    retVal = (x + "str" != y + "str");
    // BUG: Diagnostic contains: "str".equals(x)
    retVal = ("str" == x);
    // BUG: Diagnostic contains: "str".equals(x)
    retVal = (x == "str");
    // BUG: Diagnostic contains: "str2".equals("str")
    retVal = ("str2" == "str");
    final String constValue = "str";
    // BUG: Diagnostic contains: constValue.equals(x)
    retVal = (x == constValue);
    // BUG: Diagnostic contains: !constValue.equals(x)
    retVal = (x != constValue);
    // BUG: Diagnostic contains: (x + y + constValue).equals(x + y)
    retVal = (x + y + constValue == x + y);
    final String constValue2 = "str2";
    // BUG: Diagnostic contains: (constValue + constValue2).equals(x)
    retVal = (constValue + constValue2 == x);
    // BUG: Diagnostic contains: (constValue + constValue2).equals(x)
    retVal = (x == constValue + constValue2);
    // BUG: Diagnostic contains: "".equals(x)
    retVal = ("" == x);

    return retVal;
  }
}
{% endhighlight %}

### Negative examples
__StringEqualityNegativeCases.java__

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

/** @author ptoomey@google.com (Patrick Toomey) */
public class StringEqualityNegativeCases {

  public boolean testEquality(String x, String y) {
    boolean retVal;

    retVal = x.equals(y);
    retVal = (x == null);
    retVal = (x != null);
    retVal = (null == x);
    retVal = (null != x);

    return retVal;
  }

  @SuppressWarnings("StringEquality")
  public boolean testSuppressWarnings(String x, String y) {
    boolean retVal;

    retVal = (x != y);
    retVal = (x == y);

    return retVal;
  }
}
{% endhighlight %}

