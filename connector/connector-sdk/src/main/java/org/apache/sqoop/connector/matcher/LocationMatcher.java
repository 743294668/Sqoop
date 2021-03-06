/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sqoop.connector.matcher;

import java.io.Serializable;

import org.apache.sqoop.classification.InterfaceAudience;
import org.apache.sqoop.classification.InterfaceStability;
import org.apache.sqoop.schema.Schema;
import org.apache.sqoop.schema.type.Column;

/**
 * Convert data according to FROM schema to data according to TO schema. This is
 * done based on column location, Data in first column in FROM goes into first
 * column in TO, etc., if TO schema has more fields and they are "nullable",
 * their values will be set to null. If TO schema has extra non-null fields, we
 * will throw an exception.
 */
@InterfaceAudience.Private
@InterfaceStability.Unstable
public class LocationMatcher extends Matcher implements Serializable {

  public LocationMatcher(Schema from, Schema to) {
    super(from, to);
  }

  @Override
  public Object[] getMatchingData(Object[] fields) {
    if (getToSchema().isEmpty()) {
      // No destination schema found. No need to convert anything.
      return fields;
    }

    Object[] out = new Object[getToSchema().getColumnsCount()];
    int i = 0;

    for (Column col : getToSchema().getColumnsList()) {
      if (i < fields.length) {
        out[i] = fields[i];
      }
      // We ran out of fields before we ran out of schema
      else {
        tryFillNullInArrayForUnexpectedColumn(col, out, i);
      }
      i++;
    }
    return out;
  }

}