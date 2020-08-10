---
title: InjectedConstructorAnnotations
summary: Injected constructors cannot be optional nor have binding annotations
layout: bugpattern
tags: ''
severity: WARNING
---

<!--
*** AUTO-GENERATED, DO NOT MODIFY ***
To make changes, edit the @BugPattern annotation or the explanation in docs/bugpattern.
-->


## The problem
The constructor is annotated with @Inject(optional=true), or it is annotated
with @Inject and a binding annotation. This will cause a Guice runtime error.

See [https://code.google.com/p/google-guice/wiki/InjectionPoints] for details.

## Suppression
Suppress false positives by adding the suppression annotation `@SuppressWarnings("InjectedConstructorAnnotations")` to the enclosing element.

----------

### Positive examples
__InjectedConstructorAnnotationsPositiveCases.java__

{% highlight java %}
/*
 * Copyright 2013 The Error Prone Authors.
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

package com.google.errorprone.bugpatterns.inject.testdata;

import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;

/** A positive test case for InjectedConstructorAnnotation. */
public class InjectedConstructorAnnotationsPositiveCases {

  /** A binding annotation. */
  @BindingAnnotation
  public @interface TestBindingAnnotation {
  }

  /** A class with an optionally injected constructor. */
  public class TestClass1 {
    // BUG: Diagnostic contains: @Inject public TestClass1
    @Inject(optional = true) public TestClass1() {}
  }

  /** A class with an injected constructor that has a binding annotation. */
  public class TestClass2 {
    // BUG: Diagnostic contains: @Inject public TestClass2
    @TestBindingAnnotation @Inject public TestClass2() {}
  }

  /** A class whose constructor is optionally injected and has a binding annotation. */
  public class TestClass3 {
    // BUG: Diagnostic contains: @Inject public TestClass3
    @TestBindingAnnotation @Inject(optional = true) public TestClass3() {}
  }
}
{% endhighlight %}

### Negative examples
__InjectedConstructorAnnotationsNegativeCases.java__

{% highlight java %}
/*
 * Copyright 2013 The Error Prone Authors.
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

package com.google.errorprone.bugpatterns.inject.testdata;

import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;

/** A negative test case for InjectedConstructorAnnotation. */
public class InjectedConstructorAnnotationsNegativeCases {

  private @interface TestAnnotation {}

  @BindingAnnotation
  private @interface TestBindingAnnotation {}

  /** A class with a constructor that has no annotations. */
  public class TestClass1 {
    public TestClass1() {}
  }

  /** A class with a constructor that has a binding Annotation. */
  public class TestClass2 {
    @TestBindingAnnotation
    public TestClass2() {}
  }

  /** A class with an injected constructor. */
  public class TestClass3 {
    @Inject
    public TestClass3() {}
  }

  /** A class with an injected constructor that has a non-binding annotation. */
  public class TestClass4 {
    @Inject
    @TestAnnotation
    public TestClass4() {}
  }
}
{% endhighlight %}

