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
package org.apache.iotdb.confignode.physical.sys;

import org.apache.iotdb.common.rpc.thrift.TDataNodeLocation;
import org.apache.iotdb.commons.utils.ThriftCommonsSerDeUtils;
import org.apache.iotdb.confignode.physical.PhysicalPlan;
import org.apache.iotdb.confignode.physical.PhysicalPlanType;

import java.nio.ByteBuffer;
import java.util.Objects;

public class RegisterDataNodePlan extends PhysicalPlan {

  private TDataNodeLocation location;

  public RegisterDataNodePlan() {
    super(PhysicalPlanType.RegisterDataNode);
  }

  public RegisterDataNodePlan(TDataNodeLocation location) {
    this();
    this.location = location;
  }

  public TDataNodeLocation getLocation() {
    return location;
  }

  @Override
  protected void serializeImpl(ByteBuffer buffer) {
    buffer.putInt(PhysicalPlanType.RegisterDataNode.ordinal());
    ThriftCommonsSerDeUtils.writeTDataNodeLocation(location, buffer);
  }

  @Override
  protected void deserializeImpl(ByteBuffer buffer) {
    location = ThriftCommonsSerDeUtils.readTDataNodeLocation(buffer);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RegisterDataNodePlan plan = (RegisterDataNodePlan) o;
    return location.equals(plan.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(location);
  }
}