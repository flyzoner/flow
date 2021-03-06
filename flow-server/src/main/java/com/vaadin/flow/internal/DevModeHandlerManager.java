/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.internal;

import java.util.Optional;
import java.util.Set;

import com.vaadin.flow.di.Lookup;
import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.startup.VaadinInitializerException;

/**
 * Provides API to access to the {@link DevModeHandler} instance by a
 * {@link VaadinService}.
 * <p>
 * For internal use only. May be renamed or removed in a future release.
 *
 * @author Vaadin Ltd
 * @since
 *
 */
public interface DevModeHandlerManager {

    /**
     * A dev mode handler implementation is interested in certain annotations to
     * be be scanned from the class path and passed to the
     * {@link #initDevModeHandler(Set, VaadinContext)} initializer.
     *
     * @return an array of types the dev mode handler is interested id.
     */
    Class<?>[] getHandlesTypes();

    /**
     * Starts up a new {@link DevModeHandler}.
     *
     * @param classes
     *            classes to check for npm- and js modules
     * @param context
     *            servlet context we are running in
     *
     * @throws VaadinInitializerException
     *             if dev mode can't be initialized
     */
    void initDevModeHandler(Set<Class<?>> classes, VaadinContext context)
            throws VaadinInitializerException;

    /**
     * Returns a {@link DevModeHandler} instance for the given {@code service}.
     *
     * @return a {@link DevModeHandler} instance
     */
    DevModeHandler getDevModeHandler();

    /**
     * Returns whether {@link DevModeHandler} has been already started or not.
     *
     * @param context
     *            The {@link VaadinContext}, not <code>null</code>
     * @return <code>true</code> if {@link DevModeHandler} has already been
     *         started, <code>false</code> - otherwise
     */
    boolean isDevModeAlreadyStarted(VaadinContext context);

    /**
     * Create a {@link DevModeHandler} if factory available.
     *
     * @param service
     *            a Vaadin service
     * @return an {@link Optional} containing a {@link DevModeHandler} instance
     *         or <code>EMPTY</code> if disabled
     */
    static Optional<DevModeHandler> getDevModeHandler(VaadinService service) {
        VaadinContext context = service.getContext();
        return Optional.ofNullable(context)
                .map(ctx -> ctx.getAttribute(Lookup.class))
                .map(lu -> lu.lookup(DevModeHandlerManager.class))
                .flatMap(dmha -> Optional.ofNullable(dmha.getDevModeHandler()));
    }
}
