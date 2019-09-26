---
title: EqualsReference
summary: == must be used in equals method to check equality to itself or an infinite loop will occur.
layout: bugpattern
tags: ''
severity: ERROR
---

<!--
*** AUTO-GENERATED, DO NOT MODIFY ***
To make changes, edit the @BugPattern annotation or the explanation in docs/bugpattern.
-->

## The problem
.equals() to the same object will result in infinite recursion

## Suppression
Suppress false positives by adding the suppression annotation `@SuppressWarnings("EqualsReference")` to the enclosing element.

----------

### Positive examples
__EqualsReferencePositiveCases.java__

{% highlight java %}
/*
 * Copyright 2017 The Error Prone Authors.
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

/** Created by mariasam on 6/22/17. */
public class EqualsReferencePositiveCases {

  @Override
  // BUG: Diagnostic contains: ==
  public boolean equals(Object o) {
    System.out.println(this.equals(o));
    return true;
  }

  class EqualsInElse {
    @Override
    // BUG: Diagnostic contains: ==
    public boolean equals(Object o) {
      System.out.println(o == this);
      return this.equals(o);
    }
  }

  class FinalObject {
    @Override
    // BUG: Diagnostic contains: ==
    public boolean equals(final Object object) {
      return this.equals(object);
    }
  }

  class NoThis {
    @Override
    // BUG: Diagnostic contains: ==
    public boolean equals(Object o) {
      return equals(o);
    }
  }
}
{% endhighlight %}

### Negative examples
__EqualsReferenceNegativeCases.java__

{% highlight java %}
/*
 * Copyright 2017 The Error Prone Authors.
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

/** Created by mariasam on 6/23/17. */
public class EqualsReferenceNegativeCases {

  @Override
  public boolean equals(Object o) {
    return (this == o);
  }

  class OtherEquals {
    @Override
    public boolean equals(Object o) {
      if (o.equals("hi")) {
        return true;
      } else {
        return o == this;
      }
    }
  }

  class EqualsThisLast {
    @Override
    public boolean equals(Object o) {
      if (o instanceof EqualsThisLast) {
        return true;
      }
      return o.equals(this);
    }
  }

  class Foo {
    @Override
    public boolean equals(Object o) {
      return o instanceof Foo && this.equals((Foo) o);
    }

    public boolean equals(Foo o) {
      return true;
    }
  }

  class OtherEqualsMethod {
    @Override
    public boolean equals(Object o) {
      return equals((String) o);
    }

    public boolean equals(String o) {
      return true;
    }
  }

  class CodeBase {
    public CodeBase(Object o) {}

    public boolean equals(Object obj) {
      CodeBase other = (CodeBase) obj;
      return equals(new CodeBase(other.getValue()));
    }

    public Object getValue() {
      return null;
    }
  }
}
{% endhighlight %}

