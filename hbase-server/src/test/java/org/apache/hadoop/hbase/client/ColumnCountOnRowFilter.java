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
package org.apache.hadoop.hbase.client;

import java.io.IOException;
import java.util.Objects;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.classification.InterfaceAudience;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.filter.FilterBase;
import org.apache.hadoop.hbase.util.Bytes;

@InterfaceAudience.Private
public final class ColumnCountOnRowFilter extends FilterBase {

  private final int limit;

  private int count = 0;

  public ColumnCountOnRowFilter(int limit) {
    this.limit = limit;
  }

  @Override
  public ReturnCode filterKeyValue(Cell v) throws IOException {
    count++;
    return count > limit ? ReturnCode.NEXT_ROW : ReturnCode.INCLUDE;
  }

  @Override
  public void reset() throws IOException {
    this.count = 0;
  }

  @Override
  public byte[] toByteArray() throws IOException {
    return Bytes.toBytes(limit);
  }

  public static ColumnCountOnRowFilter parseFrom(byte[] bytes) throws DeserializationException {
    return new ColumnCountOnRowFilter(Bytes.toInt(bytes));
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ColumnCountOnRowFilter)) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    ColumnCountOnRowFilter f = (ColumnCountOnRowFilter) obj;
    return this.limit == f.limit;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.limit);
  }
}
