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
import org.apache.sqoop.connector.hdfs.configuration.ToFormat;
import org.apache.sqoop.model.MDriverConfig;
import org.apache.sqoop.model.MLink;
import org.apache.sqoop.model.MJob;
import org.apache.sqoop.test.infrastructure.Infrastructure;
import org.apache.sqoop.test.infrastructure.SqoopTestCase;
import org.apache.sqoop.test.infrastructure.providers.DatabaseInfrastructureProvider;
import org.apache.sqoop.test.infrastructure.providers.HadoopInfrastructureProvider;
import org.apache.sqoop.test.infrastructure.providers.KdcInfrastructureProvider;
import org.apache.sqoop.test.infrastructure.providers.SqoopInfrastructureProvider;
import org.apache.sqoop.test.utils.ParametrizedUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

/**
 *
 */
@Test(groups = "slow")
@Infrastructure(dependencies = {KdcInfrastructureProvider.class, HadoopInfrastructureProvider.class, SqoopInfrastructureProvider.class, DatabaseInfrastructureProvider.class})
public class PartitionerTest extends SqoopTestCase {

  /**
   * Columns that we will use as partition column with maximal number of
   * partitions that can be created for such column.
   */
  public static Object[] COLUMNS = new Object [][] {
    {"id",           13},
    {"code_name",    13},
    {"version",      13},
    {"release_date", 13},
  };

  /**
   * Number of extractors that we will use to transfer the table.
   */
  public static Object [] EXTRACTORS = new Object[] {
    3, 5, 10, 13,
  };

  private String partitionColumn;
  private int maxOutputFiles;
  private int extractors;

  @Factory(dataProvider="partitioner-integration-test")
  public PartitionerTest(String partitionColumn, int expectedOutputFiles, int extractors) {
    this.partitionColumn = partitionColumn;
    this.maxOutputFiles = expectedOutputFiles;
    this.extractors = extractors;
  }

  @Override
  public String getTestName() {
    return "PartitionerTest-" + this.partitionColumn + "-" + this.maxOutputFiles + "-" + this.extractors;
  }

  @DataProvider(name="partitioner-integration-test", parallel=true)
  public static Object[][] data() {
    return Iterables.toArray(ParametrizedUtils.crossProduct(COLUMNS, EXTRACTORS), Object[].class);
  }

  @Test
  public void testSplitter() throws Exception {
    createAndLoadTableUbuntuReleases();

    // RDBMS link
    MLink rdbmsLink = getClient().createLink("generic-jdbc-connector");
    fillRdbmsLinkConfig(rdbmsLink);
    saveLink(rdbmsLink);

    // HDFS link
    MLink hdfsLink = getClient().createLink("hdfs-connector");
    fillHdfsLink(hdfsLink);
    saveLink(hdfsLink);

    // Job creation
    MJob job = getClient().createJob(rdbmsLink.getName(), hdfsLink.getName());

    // set the rdbms "FROM" config
    fillRdbmsFromConfig(job, partitionColumn);

    // fill hdfs "TO" config
    fillHdfsToConfig(job, ToFormat.TEXT_FILE);

    // set driver config
    MDriverConfig driverConfig = job.getDriverConfig();
    driverConfig.getIntegerInput("throttlingConfig.numExtractors").setValue(extractors);

    saveJob(job);

    executeJob(job);

    // Assert correct output
    assertToFiles((extractors > maxOutputFiles) ? maxOutputFiles : extractors);
    assertTo(
      "1,'Warty Warthog',4.10,'2004-10-20 00:00:00.000'",
      "2,'Hoary Hedgehog',5.04,'2005-04-08 00:00:00.000'",
      "3,'Breezy Badger',5.10,'2005-10-13 00:00:00.000'",
      "4,'Dapper Drake',6.06,'2006-06-01 00:00:00.000'",
      "5,'Edgy Eft',6.10,'2006-10-26 00:00:00.000'",
      "6,'Feisty Fawn',7.04,'2007-04-19 00:00:00.000'",
      "7,'Gutsy Gibbon',7.10,'2007-10-18 00:00:00.000'",
      "8,'Hardy Heron',8.04,'2008-04-24 00:00:00.000'",
      "9,'Intrepid Ibex',8.10,'2008-10-18 00:00:00.000'",
      "10,'Jaunty Jackalope',9.04,'2009-04-23 00:00:00.000'",
      "11,'Karmic Koala',9.10,'2009-10-29 00:00:00.000'",
      "12,'Lucid Lynx',10.04,'2010-04-29 00:00:00.000'",
      "13,'Maverick Meerkat',10.10,'2010-10-10 00:00:00.000'",
      "14,'Natty Narwhal',11.04,'2011-04-28 00:00:00.000'",
      "15,'Oneiric Ocelot',11.10,'2011-10-10 00:00:00.000'",
      "16,'Precise Pangolin',12.04,'2012-04-26 00:00:00.000'",
      "17,'Quantal Quetzal',12.10,'2012-10-18 00:00:00.000'",
      "18,'Raring Ringtail',13.04,'2013-04-25 00:00:00.000'",
      "19,'Saucy Salamander',13.10,'2013-10-17 00:00:00.000'"
    );

    // Clean up testing table
    dropTable();
  }
}
