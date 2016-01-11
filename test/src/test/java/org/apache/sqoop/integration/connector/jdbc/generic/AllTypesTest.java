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
package org.apache.sqoop.integration.connector.jdbc.generic;

import com.google.common.collect.Iterables;
import org.apache.sqoop.common.test.db.DatabaseProvider;
import org.apache.sqoop.common.test.db.DatabaseProviderFactory;
import org.apache.sqoop.common.test.db.types.DatabaseType;
import org.apache.sqoop.common.test.db.types.ExampleValue;
import org.apache.sqoop.connector.hdfs.configuration.ToFormat;
import org.apache.sqoop.model.MConfigList;
import org.apache.sqoop.model.MDriverConfig;
import org.apache.sqoop.model.MJob;
import org.apache.sqoop.model.MLink;
import org.apache.sqoop.test.infrastructure.Infrastructure;
import org.apache.sqoop.test.infrastructure.SqoopTestCase;
import org.apache.sqoop.test.infrastructure.providers.DatabaseInfrastructureProvider;
import org.apache.sqoop.test.infrastructure.providers.HadoopInfrastructureProvider;
import org.apache.sqoop.test.infrastructure.providers.KdcInfrastructureProvider;
import org.apache.sqoop.test.infrastructure.providers.SqoopInfrastructureProvider;
import org.apache.sqoop.test.utils.ParametrizedUtils;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Test transfer of all supported data types.
 */
@Test(groups = "slow")
@Infrastructure(dependencies = {KdcInfrastructureProvider.class, HadoopInfrastructureProvider.class, SqoopInfrastructureProvider.class, DatabaseInfrastructureProvider.class})
public class AllTypesTest extends SqoopTestCase {

  private static String testName;

  private DatabaseType type;

  @DataProvider(name="all-types-test", parallel=false)
  public static Object[][] data(ITestContext context) throws Exception {
    testName = context.getName();

    DatabaseProvider provider = DatabaseProviderFactory.getProvider(System.getProperties());
    return Iterables.toArray(ParametrizedUtils.toArrayOfArrays(provider.getDatabaseTypes().getAllTypes()), Object[].class);
  }

  @Factory(dataProvider="all-types-test")
  public AllTypesTest(DatabaseType type) {
    this.type = type;
  }

  @Override
  public String getTestName() {
    if (methodName == null) {
      return testName;
    } else {
      return methodName + "[" + type.name + "]";
    }
  }

  @Test
  public void testFrom() throws Exception {
    createTable("id",
      "id", "INT",
      "value", type.name
    );

    int i = 1;
    for(ExampleValue value: type.values) {
      insertRow(i++, value.getObjectValue());
    }

    // RDBMS link
    MLink rdbmsConnection = getClient().createLink("generic-jdbc-connector");
    fillRdbmsLinkConfig(rdbmsConnection);
    saveLink(rdbmsConnection);

    // HDFS link
    MLink hdfsConnection = getClient().createLink("hdfs-connector");
    fillHdfsLink(hdfsConnection);
    saveLink(hdfsConnection);

    // Job creation
    MJob job = getClient().createJob(rdbmsConnection.getName(), hdfsConnection.getName());

    // Fill rdbms "FROM" config
    fillRdbmsFromConfig(job, "id");
    MConfigList fromConfig = job.getFromJobConfig();
    List<String> columns = new java.util.LinkedList<>();
    columns.add("value");
    fromConfig.getListInput("fromJobConfig.columnList").setValue(columns);

    // Fill the hdfs "TO" config
    fillHdfsToConfig(job, ToFormat.TEXT_FILE);

    // driver config
    MDriverConfig driverConfig = job.getDriverConfig();
    driverConfig.getIntegerInput("throttlingConfig.numExtractors").setValue(1);

    saveJob(job);
    executeJob(job);

    // Assert correct output
    assertTo(type.escapedStringValues());

    // Clean up testing table
    dropTable();
  }

  @Test
  public void testTo() throws Exception {
    createTable(null,
      "value", type.name
    );

    createFromFile("input-0001", type.escapedStringValues());

    // RDBMS link
    MLink rdbmsLink = getClient().createLink("generic-jdbc-connector");
    fillRdbmsLinkConfig(rdbmsLink);
    saveLink(rdbmsLink);

    // HDFS link
    MLink hdfsLink = getClient().createLink("hdfs-connector");
    fillHdfsLink(hdfsLink);
    saveLink(hdfsLink);

    // Job creation
    MJob job = getClient().createJob(hdfsLink.getName(), rdbmsLink.getName());
    fillHdfsFromConfig(job);

    // Set the rdbms "TO" config here
    fillRdbmsToConfig(job);

    // Driver config
    MDriverConfig driverConfig = job.getDriverConfig();
    driverConfig.getIntegerInput("throttlingConfig.numExtractors").setValue(1);

    saveJob(job);
    executeJob(job);
    dumpTable();

    assertEquals(type.values.size(), rowCount());
    for(ExampleValue value : type.values) {
      assertRow(
        new Object[] {"value", value.getObjectValue()},
        value.getObjectValue());
    }

    // Clean up testing table
    dropTable();
  }
}
