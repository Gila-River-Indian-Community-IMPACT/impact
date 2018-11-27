/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package us.oh.state.epa.stars2.webcommon.tag;

import org.apache.myfaces.shared_impl.taglib.UIComponentTagBase;

/**
 * @author Manfred Geiler (latest modification by $Author: kbradley $)
 * @version $Revision: 1.3 $ $Date: 2008/01/09 20:57:15 $
 */
public class SelectItemsTag extends UIComponentTagBase implements java.io.Serializable {
    // private static final Log log = LogFactory.getLog(SelectItemsTag.class);

    public final String getComponentType() {
        return "us.oh.state.epa.stars2.webcommon.component.SelectItems";
    }

    public final String getRendererType() {
        return null;
    }
}
