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
package org.outerj.daisy.diff.lcs.html.helper;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.rangedifferencer.IRangeComparator;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.compare.rangedifferencer.RangeDifferencer;
import org.outerj.daisy.diff.lcs.html.dom.Node;
import org.outerj.daisy.diff.lcs.html.dom.TagNode;
import org.outerj.daisy.diff.lcs.html.dom.TextNode;
/**
 * A comparator that compares only the elements of text inside a given tag.
 */
public class TextOnlyComparator implements IRangeComparator {

    private List<TextNode> leafs=new ArrayList<TextNode>();

    public TextOnlyComparator(TagNode tree) {
        addRecursive(tree);
    }

    private void addRecursive(TagNode tree) {
        for(Node child:tree){
            if (child instanceof TagNode) {
                TagNode tagnode = (TagNode) child;
                addRecursive(tagnode);
            }else if (child instanceof TextNode) {
                TextNode textnode = (TextNode) child;
                leafs.add(textnode);
            }
        }
    }

    public int getRangeCount() {
        return leafs.size();
    }

    public boolean rangesEqual(int owni, IRangeComparator otherComp, int otheri) {
        TextOnlyComparator other;
        try {
            other = (TextOnlyComparator) otherComp;
        } catch (ClassCastException e) {
            return false;
        }

        return getLeaf(owni).isSameText(other.getLeaf(otheri));
    }

    private TextNode getLeaf(int owni) {
       return leafs.get(owni);
    }

    public boolean skipRangeComparison(int arg0, int arg1, IRangeComparator arg2) {
        return false;
    }

    public double getMatchRatio(TextOnlyComparator other) {
        RangeDifference[] differences = RangeDifferencer.findDifferences(other,
                this);

        int distanceOther = 0;
        for (RangeDifference d : differences) {
            distanceOther += d.leftLength();
        }
        
        int distanceThis = 0;
        for (RangeDifference d : differences) {
            distanceThis += d.rightLength();
        }
        
        
        return ((0.0+distanceOther)/other.getRangeCount()+(0.0+distanceThis)/getRangeCount())/2;
    }

}
