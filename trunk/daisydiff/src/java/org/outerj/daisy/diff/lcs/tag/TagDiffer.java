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
package org.outerj.daisy.diff.lcs.tag;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.compare.rangedifferencer.PublicRangeDifference;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.compare.rangedifferencer.RangeDifferencer;

public class TagDiffer {

    private HtmlSaxDiffOutput markup;

    public TagDiffer(HtmlSaxDiffOutput markup) {
        this.markup = markup;
    }

    public void parseNoChange(int beginLeft, int endLeft, int beginRight,
            int endRight, IAtomSplitter leftComparator,
            IAtomSplitter rightComparator) throws Exception {

        StringBuilder sb = new StringBuilder();

        while (beginLeft < endLeft) {

            while (beginLeft < endLeft
                    && !rightComparator.getAtom(beginRight)
                            .hasInternalIdentifiers()
                    && !leftComparator.getAtom(beginLeft)
                            .hasInternalIdentifiers()) {
                sb.append(rightComparator.getAtom(beginRight).getFullText());
                beginRight++;
                beginLeft++;
            }

            if (sb.length() > 0) {
                markup.addClearPart(sb.toString());
                sb.setLength(0);
            }

            if (beginLeft < endLeft) {

                IAtomSplitter leftComparator2 = new ArgumentComparator(
                        leftComparator.getAtom(beginLeft).getFullText());
                IAtomSplitter rightComparator2 = new ArgumentComparator(
                        rightComparator.getAtom(beginRight).getFullText());

                RangeDifference[] differences2 = RangeDifferencer
                        .findDifferences(leftComparator2, rightComparator2);
                List<PublicRangeDifference> pdifferences2 = preProcess(
                        differences2, 1);

                int rightAtom2 = 0;
                for (int j = 0; j < pdifferences2.size(); j++) {
                    if (rightAtom2 < pdifferences2.get(j).rightStart()) {
                        markup.addClearPart(rightComparator2.substring(
                                rightAtom2, pdifferences2.get(j).rightStart()));
                    }
                    if (pdifferences2.get(j).leftLength() > 0) {
                        markup.addRemovedPart(leftComparator2.substring(
                                pdifferences2.get(j).leftStart(), pdifferences2
                                        .get(j).leftEnd()));
                    }

                    if (pdifferences2.get(j).leftLength() > 0
                            && pdifferences2.get(j).rightLength() > 0) {
                        markup.addSeperator("»");
                    }

                    if (pdifferences2.get(j).rightLength() > 0) {
                        markup.addAddedPart(rightComparator2.substring(
                                pdifferences2.get(j).rightStart(),
                                pdifferences2.get(j).rightEnd()));
                    }

                    rightAtom2 = pdifferences2.get(j).rightEnd();

                }
                if (rightAtom2 < rightComparator2.getRangeCount())
                    markup.addClearPart(rightComparator2.substring(rightAtom2));
                beginLeft++;
                beginRight++;
            }

        }

    }

    public void parseNewDiff(IAtomSplitter leftComparator,
            IAtomSplitter rightComparator) throws Exception {

        RangeDifference[] differences = RangeDifferencer.findDifferences(
                leftComparator, rightComparator);

        List<PublicRangeDifference> pdifferences = preProcess(differences,
                leftComparator);

        int rightAtom = 0;
        int leftAtom = 0;

        for (int i = 0; i < pdifferences.size(); i++) {

            parseNoChange(leftAtom, pdifferences.get(i).leftStart(), rightAtom,
                    pdifferences.get(i).rightStart(), leftComparator,
                    rightComparator);

            String leftString = leftComparator.substring(pdifferences.get(i)
                    .leftStart(), pdifferences.get(i).leftEnd());
            String rightString = rightComparator.substring(pdifferences.get(i)
                    .rightStart(), pdifferences.get(i).rightEnd());

            if (pdifferences.get(i).leftLength() > 0)
                markup.addRemovedPart(leftString);

            if (leftString.replace(" ", "").replaceAll("\n", "").replace("\r",
                    "").replace("\t", "").length() > 0
                    && rightString.replace(" ", "").replaceAll("\n", "")
                            .replace("\r", "").replace("\t", "").length() > 0)
                markup.addSeperator("|");

            if (pdifferences.get(i).rightLength() > 0)
                markup.addAddedPart(rightString);

            rightAtom = pdifferences.get(i).rightEnd();
            leftAtom = pdifferences.get(i).leftEnd();

        }
        if (rightAtom < rightComparator.getRangeCount())
            parseNoChange(leftAtom, leftComparator.getRangeCount(), rightAtom,
                    rightComparator.getRangeCount(), leftComparator,
                    rightComparator);

    }

    private List<PublicRangeDifference> preProcess(
            RangeDifference[] differences, IAtomSplitter leftComparator) {

        List<PublicRangeDifference> newRanges = new LinkedList<PublicRangeDifference>();

        for (int i = 0; i < differences.length; i++) {

            int leftStart = differences[i].leftStart();
            int leftEnd = differences[i].leftEnd();
            int rightStart = differences[i].rightStart();
            int rightEnd = differences[i].rightEnd();
            int kind = differences[i].kind();
            int temp = leftEnd;
            boolean connecting = true;

            while (connecting && i + 1 < differences.length
                    && differences[i + 1].kind() == kind) {

                int bridgelength = 0;

                int nbtokens = Math.max((leftEnd - leftStart),
                        (rightEnd - rightStart));
                // System.out.println("#tokens: "+nbtokens);
                if (nbtokens > 5) {
                    if (nbtokens > 10) {
                        bridgelength = 3;
                    } else
                        bridgelength = 2;
                }
                // System.out.println("bridgelength= "+bridgelength);
                while ((leftComparator.getAtom(temp) instanceof DelimiterAtom || (bridgelength-- > 0))
                        && temp < differences[i + 1].leftStart()) {
                    // System.out.println("bridgelength= "+bridgelength);

                    temp++;
                }
                if (temp == differences[i + 1].leftStart()) {
                    leftEnd = differences[i + 1].leftEnd();
                    rightEnd = differences[i + 1].rightEnd();
                    temp = leftEnd;
                    i++;
                } else {
                    // System.out.println("bridge stopped at token "+
                    // leftComparator.getAtom(temp).getFullText().replaceAll("
                    // ", "_"));
                    // System.out.println(leftComparator.getAtom(temp)
                    // instanceof DelimiterAtom);
                    // System.out.println(bridgelength+1>0);
                    // System.out.println("bridgelength was "+bridgelength+1);
                    connecting = false;
                    if (!(leftComparator.getAtom(temp) instanceof DelimiterAtom)) {
                        if (leftComparator.getAtom(temp).getFullText().equals(
                                " "))
                            throw new IllegalStateException(
                                    "space found aiaiai");
                    }
                }
            }
            newRanges.add(new PublicRangeDifference(kind, rightStart, rightEnd
                    - rightStart, leftStart, leftEnd - leftStart));
        }

        return newRanges;
    }

    private List<PublicRangeDifference> preProcess(
            RangeDifference[] differences, int span) {

        List<PublicRangeDifference> newRanges = new LinkedList<PublicRangeDifference>();

        for (int i = 0; i < differences.length; i++) {

            int leftStart = differences[i].leftStart();
            int leftEnd = differences[i].leftEnd();
            int rightStart = differences[i].rightStart();
            int rightEnd = differences[i].rightEnd();
            int kind = differences[i].kind();

            while (i + 1 < differences.length
                    && differences[i + 1].kind() == kind
                    && differences[i + 1].leftStart() <= leftEnd + span
                    && differences[i + 1].rightStart() <= rightEnd + span) {
                leftEnd = differences[i + 1].leftEnd();
                rightEnd = differences[i + 1].rightEnd();
                i++;
            }

            newRanges.add(new PublicRangeDifference(kind, rightStart, rightEnd
                    - rightStart, leftStart, leftEnd - leftStart));
        }

        return newRanges;
    }

}
