/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.datastore;

import org.sonatype.nexus.common.stateguard.StateGuardLifecycleSupport;
import org.sonatype.nexus.datastore.api.DataAccess;
import org.sonatype.nexus.datastore.api.DataSession;
import org.sonatype.nexus.datastore.api.DataSessionSupplier;
import org.sonatype.nexus.transaction.TransactionalStore;

import com.google.inject.TypeLiteral;
import org.eclipse.sisu.inject.TypeArguments;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.nexus.datastore.api.DataStore.access;
import static org.sonatype.nexus.datastore.api.DataStoreManager.CONFIG_DATASTORE_NAME;

/**
 * Support class for transactional domain stores backed by the config data store.
 *
 * @since 3.next
 */
public abstract class ConfigStoreSupport<T extends DataAccess>
    extends StateGuardLifecycleSupport
    implements TransactionalStore<DataSession<?>>
{
  private final DataSessionSupplier sessionSupplier;

  private final Class<T> daoClass;

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public ConfigStoreSupport(final DataSessionSupplier sessionSupplier) {
    this.sessionSupplier = checkNotNull(sessionSupplier);
    TypeLiteral<?> superType = TypeLiteral.get(getClass()).getSupertype(ConfigStoreSupport.class);
    this.daoClass = (Class) TypeArguments.get(superType, 0).getRawType();
  }

  protected T dao() {
    return access(daoClass);
  }

  @Override
  public DataSession<?> openSession() {
    return sessionSupplier.openSession(CONFIG_DATASTORE_NAME);
  }
}
