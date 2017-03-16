/*
 * Copyright 2000-2017 Vaadin Ltd.
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
package com.vaadin.client.hummingbird.binding;

import java.util.function.Supplier;

import com.vaadin.client.hummingbird.StateNode;
import com.vaadin.client.hummingbird.collection.JsArray;
import com.vaadin.client.hummingbird.nodefeature.NodeList;
import com.vaadin.hummingbird.shared.NodeFeatures;

import elemental.dom.Element;
import elemental.events.EventRemover;

/**
 * Binds and updates server object able to send notifications to the server.
 *
 * @author Vaadin Ltd
 */
public class ServerEventHandlerBinder {

    private ServerEventHandlerBinder() {
        // Only static methods
    }

    /**
     * Registers all the server event handler names found in the
     * {@link NodeFeatures#PUBLISHED_SERVER_EVENT_HANDLERS} feature in the state
     * node as <code>serverObject.&lt;methodName&gt;</code>. Additionally
     * listens to changes in the feature and updates <code>$server</code>
     * accordingly.
     *
     * @param element
     *            the element to update
     * @param node
     *            the state node containing the feature
     * @return a handle which can be used to remove the listener for the feature
     */
    public static EventRemover bindServerEventHandlerNames(Element element,
            StateNode node) {
        return bindServerEventHandlerNames(() -> ServerEventObject.get(element),
                node, NodeFeatures.PUBLISHED_SERVER_EVENT_HANDLERS);
    }

    /**
     * Registers all the server event handler names found in the feature with
     * the {@code featureId} in the {@link ServerEventObject} {@code object}.
     * Additionally listens to changes in the feature and updates server event
     * object accordingly.
     *
     * @param objectProvider
     *            the provider of the event object to update
     * @param node
     *            the state node containing the feature
     * @param featureId
     *            the feature id which contains event handler methods
     * @return a handle which can be used to remove the listener for the feature
     */
    public static EventRemover bindServerEventHandlerNames(
            Supplier<ServerEventObject> objectProvider, StateNode node,
            int featureId) {
        NodeList serverEventHandlerNamesList = node.getList(featureId);

        if (serverEventHandlerNamesList.length() > 0) {
            ServerEventObject object = objectProvider.get();

            for (int i = 0; i < serverEventHandlerNamesList.length(); i++) {
                String serverEventHandlerName = (String) serverEventHandlerNamesList
                        .get(i);
                object.defineMethod(serverEventHandlerName, node);
            }
        }

        return serverEventHandlerNamesList.addSpliceListener(e -> {
            ServerEventObject serverObject = objectProvider.get();

            JsArray<?> remove = e.getRemove();
            for (int i = 0; i < remove.length(); i++) {
                serverObject.removeMethod((String) remove.get(i));
            }

            JsArray<?> add = e.getAdd();
            for (int i = 0; i < add.length(); i++) {
                serverObject.defineMethod((String) add.get(i), node);
            }
        });
    }
}
