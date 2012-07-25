/*
 * Copyright 2012 Google Inc. All Rights Reserved.
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

package com.google.errorprone.bugpatterns;

import java.lang.String;

/**
 *
 * @author eaftan@google.com (Eddie Aftandilian)
 */
public class CovariantEqualsPositiveCase3 {

  boolean isInVersion;
  String whitelist;

  public boolean equals(CovariantEqualsPositiveCase3 that) {   //BUG("Did you mean '@Override'")
    return ((this.isInVersion == that.isInVersion) &&
            this.whitelist.equals(that.whitelist));
  }

}
