/*
 * Copyright 2004 Outerthought bvba and Schaubroeck nv
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
package org.outerj.daisy.diff.lcs.block;

import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.compare.rangedifferencer.RangeDifferencer;
import org.outerj.daisy.diff.lcs.rangecomparator.BlockComparator;

public class BlockDiffer {
	
	private BlockDiffParser parser;
	
	public BlockDiffer(BlockDiffParser parser){
		this.parser=parser;
	}
	
	public void diff(BlockComparator leftComparator, BlockComparator rightComparator) throws Exception{
		
		RangeDifference[] differences = RangeDifferencer.findDifferences(leftComparator, rightComparator);
	    
		parser.parseNewDiff(leftComparator, rightComparator, differences);
	}
	
}
