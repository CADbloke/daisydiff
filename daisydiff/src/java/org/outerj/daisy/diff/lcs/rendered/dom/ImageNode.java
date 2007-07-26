/*
 * Copyright 2007 Outerthought bvba and Schaubroeck nv
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
package org.outerj.daisy.diff.lcs.rendered.dom;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class ImageNode extends TextNode {

    private AttributesImpl attributes;

    public ImageNode(TagNode parent, Attributes attrs) {
        super(parent, "<img>"+attrs.getValue("src").toLowerCase()+"</img>");
        this.attributes = new AttributesImpl(attrs);
    }
    
    public boolean isSameText(Object other) {
        if (other == null)
            return false;

        ImageNode otherImageNode;
        try {
            otherImageNode = (ImageNode) other;
        } catch (ClassCastException e) {
            return false;
        }
        return getText().equalsIgnoreCase(
                otherImageNode.getText());
    }

    public AttributesImpl getAttributes() {
        return attributes;
    }
    
}
