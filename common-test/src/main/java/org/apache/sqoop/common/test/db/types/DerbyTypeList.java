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
package org.apache.sqoop.common.test.db.types;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Source: https://db.apache.org/derby/docs/10.7/ref/crefsqlj31068.html
 */
public class DerbyTypeList extends DatabaseTypeList {
  public DerbyTypeList() {
    super();

    // Numeric types
    add(DatabaseType.builder("SMALLINT")
      .addExample("-32768", Integer.valueOf(-32768), "-32768")
      .addExample(    "-1",     Integer.valueOf(-1),     "-1")
      .addExample(     "0",      Integer.valueOf(0),      "0")
      .addExample(     "1",      Integer.valueOf(1),      "1")
      .addExample( "32767",  Integer.valueOf(32767),  "32767")
      .build());
    add(DatabaseType.builder("INT")
      .addExample("-2147483648", Integer.valueOf(-2147483648), "-2147483648")
      .addExample(         "-1",          Integer.valueOf(-1),          "-1")
      .addExample(          "0",          Integer.valueOf(0),           "0")
      .addExample(          "1",          Integer.valueOf(1),           "1")
      .addExample( "2147483647",  Integer.valueOf(2147483647),  "2147483647")
      .build());
    add(DatabaseType.builder("BIGINT")
      .addExample("-9223372036854775808", Long.valueOf(-9223372036854775808L), "-9223372036854775808")
      .addExample(                  "-1",                   Long.valueOf(-1L),                   "-1")
      .addExample(                   "0",                    Long.valueOf(0L),                    "0")
      .addExample(                   "1",                    Long.valueOf(1L),                    "1")
      .addExample( "9223372036854775807",  Long.valueOf(9223372036854775807L),  "9223372036854775807")
      .build());

    // Floating points
    add(DatabaseType.builder("REAL")
      .addExample("CAST(-3.402E+38 AS REAL)", Float.valueOf(-3.402E+38f),  "-3.402E38")
      .addExample( "CAST(3.402E+38 AS REAL)",  Float.valueOf(3.402E+38f),   "3.402E38")
      .addExample(                       "0",          Float.valueOf(0f),        "0.0")
      .addExample( "CAST(1.175E-37 AS REAL)",  Float.valueOf(1.175E-37f),  "1.175E-37")
      .addExample("CAST(-1.175E-37 AS REAL)", Float.valueOf(-1.175E-37f), "-1.175E-37")
      .build());
    add(DatabaseType.builder("DOUBLE")
      .addExample("-1.79769E+308", Double.valueOf(-1.79769E+308), "-1.79769E308")
      .addExample( "1.79769E+308",  Double.valueOf(1.79769E+308),  "1.79769E308")
      .addExample(            "0",             Double.valueOf(0),          "0.0")
      .addExample(   "2.225E-307",    Double.valueOf(2.225E-307),   "2.225E-307")
      .addExample(  "-2.225E-307",   Double.valueOf(-2.225E-307),  "-2.225E-307")
      .build());

    // Fixed point
    add(DatabaseType.builder("DECIMAL(5, 2)")
      .addExample("-999.99", BigDecimal.valueOf(-999.99).setScale(2, RoundingMode.CEILING), "-999.99")
      .addExample( "-999.9",  BigDecimal.valueOf(-999.9).setScale(2,   RoundingMode.FLOOR), "-999.90")
      .addExample(  "-99.9",   BigDecimal.valueOf(-99.9).setScale(2, RoundingMode.CEILING),  "-99.90")
      .addExample(   "-9.9",    BigDecimal.valueOf(-9.9).setScale(2, RoundingMode.CEILING),   "-9.90")
      .addExample(     "-9",      BigDecimal.valueOf(-9).setScale(2, RoundingMode.CEILING),   "-9.00")
      .addExample(      "0",       BigDecimal.valueOf(0).setScale(2, RoundingMode.CEILING),    "0.00")
      .addExample(      "9",       BigDecimal.valueOf(9).setScale(2, RoundingMode.CEILING),    "9.00")
      .addExample(    "9.9",     BigDecimal.valueOf(9.9).setScale(2,   RoundingMode.FLOOR),    "9.90")
      .addExample(   "99.9",    BigDecimal.valueOf(99.9).setScale(2,   RoundingMode.FLOOR),   "99.90")
      .addExample(  "999.9",   BigDecimal.valueOf(999.9).setScale(2, RoundingMode.CEILING),  "999.90")
      .addExample( "999.99",  BigDecimal.valueOf(999.99).setScale(2,   RoundingMode.FLOOR),  "999.99")
      .build());

    // Boolean
    add(DatabaseType.builder("BOOLEAN")
      .addExample( "true",  Boolean.TRUE,  "true")
      .addExample("false", Boolean.FALSE, "false")
      .build());

    // String types
    add(DatabaseType.builder("VARCHAR(5)")
      .addExample("'A'", "A", "'A'")
      .addExample("''", "", "''")
      .addExample("''''", "'", "'\\\''")
      .addExample("'\"'", "\"", "'\\\"'")
      .build());
    add(DatabaseType.builder("CHAR(5)")
      .addExample("'A'", "A    ", "'A    '")
      .addExample( "''", "     ", "'     '")
      .build());

    // Date
    add(DatabaseType.builder("DATE")
      .addExample("'1970-01-01'", new java.sql.Date(70, 0, 1), "'1970-01-01'")
      .addExample("'2000-2-2'", new java.sql.Date(100, 1, 2), "'2000-02-02'")
      .build());

    // BLOB
    // CLOB
    // Time
    // Timestamp
    // XML?
  }
}
