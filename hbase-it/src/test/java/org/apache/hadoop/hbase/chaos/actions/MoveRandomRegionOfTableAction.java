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

package org.apache.hadoop.hbase.chaos.actions;

import java.util.List;

import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.chaos.monkies.PolicyBasedChaosMonkey;
import org.apache.hadoop.hbase.client.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Action that tries to move a random region of a table.
*/
public class MoveRandomRegionOfTableAction extends Action {
  private static final Logger LOG =
      LoggerFactory.getLogger(MoveRandomRegionOfTableAction.class);
  private final long sleepTime;
  private final TableName tableName;

  public MoveRandomRegionOfTableAction(TableName tableName) {
    this(-1, tableName);
  }

  public MoveRandomRegionOfTableAction(long sleepTime, TableName tableName) {
    this.sleepTime = sleepTime;
    this.tableName = tableName;
  }

  @Override
  public void perform() throws Exception {
    if (sleepTime > 0) {
      Thread.sleep(sleepTime);
    }

    HBaseTestingUtility util = context.getHBaseIntegrationTestingUtility();
    Admin admin = util.getHBaseAdmin();

    LOG.info("Performing action: Move random region of table " + tableName);
    List<HRegionInfo> regions = admin.getTableRegions(tableName);
    if (regions == null || regions.isEmpty()) {
      LOG.info("Table " + tableName + " doesn't have regions to move");
      return;
    }

    HRegionInfo region = PolicyBasedChaosMonkey.selectRandomItem(
      regions.toArray(new HRegionInfo[regions.size()]));
    LOG.debug("Unassigning region " + region.getRegionNameAsString());
    admin.unassign(region.getRegionName(), false);
    if (sleepTime > 0) {
      Thread.sleep(sleepTime);
    }
  }
}
