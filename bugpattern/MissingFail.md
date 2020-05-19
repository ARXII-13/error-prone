---
title: MissingFail
summary: Not calling fail() when expecting an exception masks bugs
layout: bugpattern
tags: ''
severity: WARNING
---

<!--
*** AUTO-GENERATED, DO NOT MODIFY ***
To make changes, edit the @BugPattern annotation or the explanation in docs/bugpattern.
-->


_Alternate names: missing-fail_

## The problem
When testing for exceptions in junit, it is easy to forget the call to `fail()`:

```java
try {
  someOperationThatShouldThrow();
  // forget to call Assert.fail()
} catch (SomeException expected) {
  assertThat(expected).hasMessage("Operation failed");
}
```

Without the call to `fail()`, the test is broken: it will pass if the exception
is never thrown *or* if the exception is thrown with the expected message.

If the try/catch block is defensive and the exception may not always be thrown,
then the exception should be named 'tolerated'.

## Detection

This checker uses heuristics that identify as many occurrences of the problem as
possible while keeping the false positive rate low (low single-digit
percentages).

## Heuristics

Five methods were explored to detect missing `fail()` calls, triggering if no
`fail()` is used in a `try/catch` statement within a JUnit test class:

*   Cases in which the caught exception is called "expected".
*   Cases in which there is a call to an `assert*()` method in the catch block.
*   Cases in which "expected" shows up in a comment inside the `catch` block.
*   Cases in which the `catch` block is empty.
*   Cases in which the `try` block contains only a single statement.

Only the first three yield useful results and also required some more refinement
to reduce false positives. In addition, the checker does not trigger on comments
in the `catch` block due to implementation complexity.

To reduce false positives, no match is found if any of the following is true:

*   Any method with `fail` in its name is present in either catch or try block.
*   A `throw` statement or synonym (`assertTrue(false)`, etc.) is present in
    either `catch` or `try` block.
*   The occurrence happens inside a `setUp`, `tearDown`, `@Before`, `@After`,
    `suite` or`main` method.
*   The method returns from the `try` or `catch` block or immediately after.
*   The exception caught is of type `InterruptedException`, `AssertionError`,
    `junit.framework.AssertionFailedError` or `Throwable`.
*   The occurrence is inside a loop.
*   The try block contains a `while(true)` loop.
*   The `try` or `catch` block contains a `continue;` statement.
*   The `try/catch` statement also contains a `finally` statement.
*   A logging call is present in the `catch` block.

In addition, for occurrences which matched because they have a call to an
`assert*()` method in the catch block, no match is found if any of the following
characteristics are present:

*   A field assignment in the catch block.
*   A call to `assertTrue/False(boolean variable or field)` in the catch block.
*   The last statement in the `try` block is an `assert*()` (that is not a noop:
    `assertFalse(false)`, `assertTrue(true))` or `Mockito.verify()` call.

## Suppression
Suppress false positives by adding the suppression annotation `@SuppressWarnings("MissingFail")` to the enclosing element.


----------

### Positive examples
__MissingFailPositiveCases.java__

{% highlight java %}
/*
 * Copyright 2015 The Error Prone Authors.
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

import junit.framework.TestCase;
import org.junit.Assert;
import org.mockito.Mockito;

/** Test cases for missing fail */
public class MissingFailPositiveCases extends TestCase {

  private boolean foo = true;

  public void expectedException_emptyCatch() {
    try {
      // BUG: Diagnostic contains: fail()
      dummyMethod();
    } catch (Exception expected) {
    }
  }

  public void catchAssert() {
    try {
      // BUG: Diagnostic contains: fail()
      dummyMethod();
    } catch (Exception e) {
      assertDummy();
    }
  }

  public void catchVerify() {
    try {
      // BUG: Diagnostic contains: fail()
      dummyMethod();
    } catch (Exception e) {
      verifyDummy();
    }
  }

  public void expectedException_throwOutsideTryTree() throws Exception {
    try {
      // BUG: Diagnostic contains: fail()
      dummyMethod();
    } catch (Exception expected) {
    }
    throw new Exception();
  }

  public void expectedException_assertLastCall() throws Exception {
    try {
      dummyMethod();
      // BUG: Diagnostic contains: fail()
      assertDummy();
    } catch (Exception expected) {
    }
    throw new Exception();
  }

  public void expectedException_fieldAssignmentInCatch() {
    try {
      // BUG: Diagnostic contains: fail()
      dummyMethod();
    } catch (Exception expected) {
      foo = true;
    }
  }

  public void catchAssert_noopAssertLastCall() {
    try {
      dummyMethod();
      // BUG: Diagnostic contains: fail()
      Assert.assertTrue(true);
    } catch (Exception e) {
      assertDummy();
    }
  }

  public void assertInCatch_verifyNotLastStatement() {
    try {
      Mockito.verify(new Dummy()).dummy();
      // BUG: Diagnostic contains: fail()
      dummyMethod();
    } catch (Exception e) {
      assertDummy();
    }
  }

  public void assertInCatch_verifyInCatch() {
    try {
      // BUG: Diagnostic contains: fail()
      dummyMethod();
    } catch (Exception e) {
      assertDummy();
      Mockito.verify(new Dummy()).dummy();
    }
  }

  public void expectedException_logInTry() {
    try {
      new Logger().log();
      // BUG: Diagnostic contains: fail()
      dummyMethod();
    } catch (Exception expected) {
      foo = true;
    }
  }

  /** Sameple inner class. */
  public static class Inner {
    public void expectedException_emptyCatch() {
      try {
        // BUG: Diagnostic contains: fail()
        dummyMethod();
      } catch (Exception expected) {
      }
    }
  }

  private static class Dummy {

    String dummy() {
      return "";
    }
  }

  private static class Logger {

    void log() {};

    void info() {};
  }

  private static void dummyMethod() {}

  private static void assertDummy() {}

  private static void verifyDummy() {}
}
{% endhighlight %}

__MissingFailPositiveCases2.java__

{% highlight java %}
/*
 * Copyright 2015 The Error Prone Authors.
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

// unused import to make sure we don't introduce an import conflict.

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Test cases for missing fail */
@RunWith(JUnit4.class)
public class MissingFailPositiveCases2 {

  @Test
  public void expectedException() {
    try {
      // BUG: Diagnostic contains: fail()
      dummyMethod();
    } catch (Exception expected) {
    }
  }

  public void expectedException_helperMethod() {
    try {
      // BUG: Diagnostic contains: fail()
      dummyMethod();
    } catch (Exception expected) {
    }
  }

  private static void dummyMethod() {}
}
{% endhighlight %}

__MissingFailPositiveCases3.java__

{% highlight java %}
/*
 * Copyright 2015 The Error Prone Authors.
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

import junit.framework.TestCase;

/** Examples of an inner test case. */
public class MissingFailPositiveCases3 {

  /** Sample inner class. */
  public static class Inner extends TestCase {

    public void expectedException_emptyCatch() {
      try {
        // BUG: Diagnostic contains: fail()
        dummyMethod();
      } catch (Exception expected) {
      }
    }

    public void catchAssert() {
      try {
        // BUG: Diagnostic contains: fail()
        dummyMethod();
      } catch (Exception e) {
        assertDummy();
      }
    }
  }

  private static void dummyMethod() {}

  private static void assertDummy() {}
}
{% endhighlight %}

### Negative examples
__MissingFailNegativeCases.java__

{% highlight java %}
/*
 * Copyright 2015 The Error Prone Authors.
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

import java.util.HashMap;
import java.util.Map;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mockito;

/** Test cases for missing fail */
public class MissingFailNegativeCases extends TestCase {

  private static final Logger logger = new Logger();
  private static final Logger log = new Logger();
  private static final Logger thingThatLogs = new Logger();

  private boolean foo = true;

  public void expectedException_withFail() {
    try {
      dummyMethod();
      Assert.fail();
    } catch (Exception expected) {
    }
  }

  @SuppressWarnings("deprecation") // Need to recognize a framework call but don't want a warning.
  public void expectedException_withFrameworkFail() {
    try {
      dummyMethod();
      junit.framework.Assert.fail();
    } catch (Exception expected) {
    }
  }

  public void expectedException_withStaticFail() {
    try {
      dummyMethod();
      fail();
    } catch (Exception expected) {
    }
  }

  public void expectedException_returnInTry() {
    try {
      dummyMethod();
      return;
    } catch (Exception expected) {
    }
  }

  public void expectedException_returnInCatch() {
    try {
      dummyMethod();
    } catch (Exception expected) {
      return;
    }
  }

  public void expectedException_returnAfterCatch() {
    try {
      dummyMethod();
    } catch (Exception expected) {
    }
    return;
  }

  public void expectedException_throwInCatch() throws Exception {
    try {
      dummyMethod();
    } catch (Exception expected) {
      throw new Exception();
    }
  }

  public void expectedException_throwInTry() throws Exception {
    boolean foo = false;
    try {
      if (foo) {
        throw new Exception();
      }
      dummyMethod();
    } catch (Exception expected) {
    }
  }

  public void expectedException_throwSynonymInCatch() throws Exception {
    try {
      dummyMethod();
    } catch (Exception expected) {
      Assert.assertFalse(true);
    }
  }

  public void assertInCatch_testCaseThrowSynonymInCatch() throws Exception {
    try {
      dummyMethod();
    } catch (Exception e) {
      assertFalse(true);
    }
  }

  public void expectedException_throwSynonymInTry() throws Exception {
    boolean foo = false;
    try {
      if (foo) {
        Assert.assertFalse(true);
      }
      dummyMethod();
    } catch (Exception expected) {
    }
  }

  public void expectedException_assertTrueFalse() throws Exception {
    try {
      dummyMethod();
    } catch (Exception expected) {
      Assert.assertTrue(false);
    }
  }

  public void expectedException_assertTrueFalseWithMessage() throws Exception {
    try {
      dummyMethod();
    } catch (Exception expected) {
      Assert.assertTrue("This should never happen", false);
    }
  }

  public void expectedException_testCaseAssertTrueFalseWithMessage() throws Exception {
    try {
      dummyMethod();
    } catch (Exception expected) {
      assertTrue("This should never happen", false);
    }
  }

  public void assertInCatch_assertTrueFalseWithMessage() throws Exception {
    try {
      dummyMethod();
    } catch (Exception e) {
      Assert.assertTrue("This should never happen", false);
    }
  }

  public void expectedException_assertBoxedTrueFalse() throws Exception {
    try {
      dummyMethod();
    } catch (Exception expected) {
      Assert.assertTrue(Boolean.FALSE);
    }
  }

  public void expectedException_assertUnequal() throws Exception {
    try {
      dummyMethod();
    } catch (Exception expected) {
      Assert.assertEquals(1, 2);
    }
  }

  public void expectedException_testCaseAssertUnequal() throws Exception {
    try {
      dummyMethod();
    } catch (Exception expected) {
      assertEquals(1, 2);
    }
  }

  public void expectedException_assertFalse() throws Exception {
    try {
      dummyMethod();
    } catch (Exception expected) {
      assert (false);
    }
  }

  @Before
  public void expectedException_beforeAnnotation() throws Exception {
    try {
      dummyMethod();
    } catch (Exception expected) {
    }
  }

  @After
  public void expectedException_afterAnnotation() throws Exception {
    try {
      dummyMethod();
    } catch (Exception expected) {
    }
  }

  // Don't match setUp methods.
  public void setUp() throws Exception {
    try {
      dummyMethod();
    } catch (Exception expected) {
    }
  }

  // Don't match tearDown methods.
  public void tearDown() throws Exception {
    try {
      dummyMethod();
    } catch (Exception expected) {
    }
  }

  // Don't match main methods.
  public static void main(String[] args) throws Exception {
    try {
      dummyMethod();
    } catch (Exception expected) {
    }
  }

  // Don't match suite methods.
  public static Test suite() throws Exception {
    try {
      dummyMethod();
    } catch (Exception expected) {
    }
    int x; // Don't return right after catch so as not to trigger that exclusion.
    return null;
  }

  public void expectedException_interruptedException() throws Exception {
    try {
      dummyMethod();
    } catch (InterruptedException expected) {
    }
  }

  public void expectedException_assertionError() throws Exception {
    try {
      dummyMethod();
    } catch (AssertionError expected) {
    }
  }

  public void expectedException_assertionFailedError() throws Exception {
    try {
      dummyMethod();
    } catch (AssertionFailedError expected) {
    }
  }

  public void expectedException_throwable() throws Exception {
    try {
      dummyMethod();
    } catch (Throwable expected) {
    }
  }

  public void testExpectedException_loopInTestMethod() throws Exception {
    for (int i = 0; i < 2; i++) {
      try {
        dummyMethod();
      } catch (Exception expected) {
      }
    }
  }

  public void expectedException_loopInHelperMethod() throws Exception {
    for (int i = 0; i < 2; i++) {
      try {
        dummyMethod();
      } catch (Exception expected) {
      }
    }
  }

  public static Map<String, String> assertInCatch_loopInHelperMethod(String... strings) {
    Map<String, String> map = new HashMap<>();
    for (String s : strings) {
      try {
        map.put(s, s);
      } catch (Exception e) {
        Assert.assertTrue(s.contains("foo"));
      }
    }
    return map;
  }

  // prefixed with "test" but private - not a test method.
  private void testExpectedException_loopInPrivateTestHelperMethod() throws Exception {
    for (int i = 0; i < 2; i++) {
      try {
        dummyMethod();
      } catch (Exception expected) {
      }
    }
  }

  // prefixed with "test" but returns - not a test method.
  public String testExpectedException_loopInReturningTestHelperMethod() throws Exception {
    for (int i = 0; i < 2; i++) {
      try {
        dummyMethod();
      } catch (Exception expected) {
      }
    }
    return "";
  }

  // Prefixed with "test" to not trigger loop in helper method exclusion.
  public void testExpectedException_continueInCatch() throws Exception {
    for (int i = 0; i < 2; i++) {
      try {
        dummyMethod();
      } catch (Exception expected) {
        continue;
      }
    }
  }

  // Prefixed with "test" to not trigger loop in helper method exclusion.
  public void testExpectedException_continueInTry() throws Exception {
    for (int i = 0; i < 2; i++) {
      try {
        dummyMethod();
        continue;
      } catch (Exception expected) {
      }
    }
  }

  public void expectedException_finally() {
    try {
      dummyMethod();
    } catch (Exception expected) {
    } finally {
    }
  }

  public void expectedException_logInCatch() {
    try {
      dummyMethod();
    } catch (Exception expected) {
      thingThatLogs.log();
    }
  }

  public void expectedException_loggerCallInCatch() {
    try {
      dummyMethod();
    } catch (Exception expected) {
      logger.info();
    }
  }

  public void expectedException_logCallInCatch() {
    try {
      dummyMethod();
    } catch (Exception expected) {
      log.info();
    }
  }

  public void assertInCatch_assertLastCallInTry() {
    try {
      dummyMethod();
      assertDummy();
    } catch (Exception e) {
      assertDummy();
    }
  }

  public void assertInCatch_fieldAssignmentInCatch() {
    try {
      dummyMethod();
    } catch (Exception e) {
      assertDummy();
      foo = true;
    }
  }

  public void assertInCatch_assertOnFieldInCatch() {
    try {
      dummyMethod();
    } catch (Exception e) {
      Assert.assertTrue(foo);
    }
  }

  public void assertInCatch_assertOnVariableInCatch() {
    boolean bar = false;
    try {
      dummyMethod();
    } catch (Exception e) {
      Assert.assertTrue(bar);
    }
  }

  public void assertInCatch_verifyBeforeCatch() {
    try {
      dummyMethod();
      Mockito.verify(new Dummy()).dummy();
    } catch (Exception e) {
      assertDummy();
    }
  }

  public void assertInCatch_noopAssertInCatch() {
    try {
      dummyMethod();
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  public void expectedException_failInCatch() {
    try {
      dummyMethod();
    } catch (Exception expected) {
      Assert.fail();
    }
  }

  public void expectedException_whileTrue() {
    try {
      while (true) {
        dummyMethod();
      }
    } catch (Exception expected) {
    }
  }

  public void expectedException_customFail() {
    try {
      dummyMethod();
      specialFail();
    } catch (Exception expected) {
    }
  }

  private static void dummyMethod() throws InterruptedException {}

  private static void assertDummy() {}

  private static void specialFail() {}

  private static class Logger {

    void log() {};

    void info() {};
  }

  private static class Dummy {

    String dummy() {
      return "";
    }
  }
}
{% endhighlight %}

__MissingFailNegativeCases2.java__

{% highlight java %}
/*
 * Copyright 2015 The Error Prone Authors.
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

/** Test cases for missing fail in a non-test class */
public class MissingFailNegativeCases2 {

  public void expectedException_emptyCatch() {
    try {
      dummyMethod();
    } catch (Exception expected) {
    }
  }

  public void catchAssert() {
    try {
      dummyMethod();
    } catch (Exception e) {
      assertDummy();
    }
  }

  private static void dummyMethod() {}

  private static void assertDummy() {}
}
{% endhighlight %}

