---
title: StringBuilderInitWithChar
summary: StringBuilder does not have a char constructor; this invokes the int constructor.
layout: bugpattern
tags: ''
severity: ERROR
---

<!--
*** AUTO-GENERATED, DO NOT MODIFY ***
To make changes, edit the @BugPattern annotation or the explanation in docs/bugpattern.
-->


## The problem
StringBuilder does not have a char constructor, so instead this code creates a
StringBuilder with initial size equal to the code point of the specified char.

## Suppression
Suppress false positives by adding the suppression annotation `@SuppressWarnings("StringBuilderInitWithChar")` to the enclosing element.


----------

### Positive examples
__StringBuilderInitWithCharPositiveCases.java__

{% highlight java %}
/*
 * Copyright 2014 The Error Prone Authors.
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

/** @author lowasser@google.com (Louis Wasserman) */
public class StringBuilderInitWithCharPositiveCases {
  {
    // BUG: Diagnostic contains: new StringBuilder("a")
    new StringBuilder('a');
    // BUG: Diagnostic contains: new StringBuilder("\"")
    new StringBuilder('"');
    char c = 'c';
    // BUG: Diagnostic contains: new StringBuilder().append(c)
    new StringBuilder(c);
  }
}
{% endhighlight %}

### Negative examples
__StringBuilderInitWithCharNegativeCases.java__

{% highlight java %}
/*
 * Copyright 2014 The Error Prone Authors.
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

/** @author lowasser@google.com (Louis Wasserman) */
public class StringBuilderInitWithCharNegativeCases {
  {
    new StringBuilder("a");
    new StringBuilder(5);
    new StringBuilder();
  }
}
{% endhighlight %}

