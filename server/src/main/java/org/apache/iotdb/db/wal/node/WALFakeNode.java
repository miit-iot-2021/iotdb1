/*
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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.iotdb.db.wal.node;

import org.apache.iotdb.consensus.common.request.IConsensusRequest;
import org.apache.iotdb.db.engine.memtable.IMemTable;
import org.apache.iotdb.db.mpp.plan.planner.plan.node.write.InsertRowNode;
import org.apache.iotdb.db.mpp.plan.planner.plan.node.write.InsertTabletNode;
import org.apache.iotdb.db.qp.physical.crud.DeletePlan;
import org.apache.iotdb.db.qp.physical.crud.InsertRowPlan;
import org.apache.iotdb.db.qp.physical.crud.InsertTabletPlan;
import org.apache.iotdb.db.wal.exception.WALException;
import org.apache.iotdb.db.wal.utils.listener.WALFlushListener;

import java.util.List;

/** This class provides fake wal node when wal is disabled or exception happens. */
public class WALFakeNode implements IWALNode {
  private final WALFlushListener.Status status;
  private final Exception cause;

  private WALFakeNode(WALFlushListener.Status status) {
    this(status, null);
  }

  public WALFakeNode(WALFlushListener.Status status, Exception cause) {
    this.status = status;
    this.cause = cause;
  }

  @Override
  public WALFlushListener log(int memTableId, InsertRowPlan insertRowPlan) {
    return getResult();
  }

  @Override
  public WALFlushListener log(int memTableId, InsertRowNode insertRowNode) {
    return getResult();
  }

  @Override
  public WALFlushListener log(
      int memTableId, InsertTabletPlan insertTabletPlan, int start, int end) {
    return getResult();
  }

  @Override
  public WALFlushListener log(
      int memTableId, InsertTabletNode insertTabletNode, int start, int end) {
    return getResult();
  }

  @Override
  public WALFlushListener log(int memTableId, DeletePlan deletePlan) {
    return getResult();
  }

  private WALFlushListener getResult() {
    WALFlushListener walFlushListener = new WALFlushListener(false);
    switch (status) {
      case SUCCESS:
        walFlushListener.succeed();
        break;
      case FAILURE:
        walFlushListener.fail(cause);
        break;
    }
    return walFlushListener;
  }

  @Override
  public void onMemTableFlushStarted(IMemTable memTable) {
    // do nothing
  }

  @Override
  public void onMemTableFlushed(IMemTable memTable) {
    // do nothing
  }

  @Override
  public void onMemTableCreated(IMemTable memTable, String targetTsFile) {
    // do nothing
  }

  @Override
  public IConsensusRequest getReq(long index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<IConsensusRequest> getReqs(long startIndex, int num) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ReqIterator getReqIterator(long startIndex) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() {
    // do nothing
  }

  public static WALFakeNode getFailureInstance(Exception e) {
    return new WALFakeNode(
        WALFlushListener.Status.FAILURE,
        new WALException("Cannot write wal into a fake node. ", e));
  }

  public static WALFakeNode getSuccessInstance() {
    return WALFakeNodeHolder.SUCCESS_INSTANCE;
  }

  private static class WALFakeNodeHolder {
    private static final WALFakeNode SUCCESS_INSTANCE =
        new WALFakeNode(WALFlushListener.Status.SUCCESS);
  }
}
