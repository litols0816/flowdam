/*
 * Copyright 2014 University of Lancaster
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.leafgraph.flowdam.proxy;

/**
 * Describe the type of channel.
 */
public enum ProxyChannelType {
    /** Channel type is a switch. */
    SWITCH,
    /** Channel type is a controller. */
    CONTROLLER,
    /** Channel type doesn't really exist, sent to proxy. */
    PROXY
}
