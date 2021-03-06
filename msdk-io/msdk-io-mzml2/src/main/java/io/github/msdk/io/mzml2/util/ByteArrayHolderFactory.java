/*
 * Copyright 2016 Dmitry Avtonomov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.msdk.io.mzml2.util;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * <p>ByteArrayHolderFactory class.</p>
 *
 * @author Dmitry Avtonomov
 * @version $Id: $Id
 */
public class ByteArrayHolderFactory extends BasePooledObjectFactory<ByteArrayHolder> {
  protected volatile int defaultSize = 8192; // default to 8192

  /**
   * <p>Constructor for ByteArrayHolderFactory.</p>
   */
  public ByteArrayHolderFactory() {}

  /**
   * <p>Constructor for ByteArrayHolderFactory.</p>
   *
   * @param defaultSize a int.
   */
  public ByteArrayHolderFactory(int defaultSize) {
    this.defaultSize = defaultSize;
  }

  /**
   * <p>Getter for the field <code>defaultSize</code>.</p>
   *
   * @return a int.
   */
  public int getDefaultSize() {
    return defaultSize;
  }

  /**
   * Sets the default size of byte buffers, produced by this factory.
   *
   * @param defaultSize new default size
   */
  public synchronized void setDefaultSize(int defaultSize) {
    this.defaultSize = defaultSize;
  }

  /**
   * Updates the default byte buffer size, if the new value is larger, than the current default.
   *
   * @param defaultSize new default size
   */
  public synchronized void setDefaultSizeMax(int defaultSize) {
    if (this.defaultSize < defaultSize) {
      this.defaultSize = defaultSize;
    }
  }

  /** {@inheritDoc} */
  @Override
  public ByteArrayHolder create() throws Exception {
    return new ByteArrayHolder(getDefaultSize());
  }

  /** {@inheritDoc} */
  @Override
  public PooledObject<ByteArrayHolder> wrap(ByteArrayHolder obj) {
    return new DefaultPooledObject<>(obj);
  }

  /** {@inheritDoc} */
  @Override
  public void passivateObject(PooledObject<ByteArrayHolder> p) throws Exception {
    ByteArrayHolder bah = p.getObject();
    if (bah == null)
      return;
    bah.reset();
  }

  /** {@inheritDoc} */
  @Override
  public void activateObject(PooledObject<ByteArrayHolder> p) throws Exception {
    ByteArrayHolder bah = p.getObject();
    if (bah == null)
      return;
    bah.reset();
  }
}
