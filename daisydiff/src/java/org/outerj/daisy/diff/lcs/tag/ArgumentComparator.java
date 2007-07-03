package org.outerj.daisy.diff.lcs.tag;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.rangedifferencer.IRangeComparator;
import org.outerj.daisy.diff.lcs.rangecomparator.Atom;
import org.outerj.daisy.diff.lcs.rangecomparator.IAtomSplitter;
import org.outerj.daisy.diff.lcs.rangecomparator.TagComparator;

public class ArgumentComparator implements IAtomSplitter {

	private List<Atom> atoms = new ArrayList<Atom>(5);
	
	public ArgumentComparator(String s) {
		generateAtoms(s);
	}
	
	private void generateAtoms(String s) {
		if(atoms.size()>0)
			throw new IllegalStateException("Atoms can only be generated once");
		
		StringBuilder currentWord = new StringBuilder(30);
		
		for(int i=0;i<s.length();i++){
			char c = s.charAt(i);
			
			if(c=='<' || c=='>'){	
				if(currentWord.length()>0){
					atoms.add(new TextAtom(currentWord.toString()));
					currentWord.setLength(0);
				}
				atoms.add(new TextAtom(""+c));
				currentWord.setLength(0);
			}else if(DelimiterAtom.isValidDelimiter(""+c)){
				//a delimiter
				if(currentWord.length()>0){
					atoms.add(new TextAtom(currentWord.toString()));
					currentWord.setLength(0);
				}
				atoms.add(new DelimiterAtom(c));
			}else{
				currentWord.append(c);
			}
		}
		if(currentWord.length()>0){
			atoms.add(new TextAtom(currentWord.toString()));
			currentWord.setLength(0);
		}
	}

	public Atom getAtom(int i){
		if(i<0  || i>=atoms.size())
			throw new IndexOutOfBoundsException();
		return atoms.get(i);
	}
	

	public int getRangeCount() {
		return atoms.size();
	}

	public boolean rangesEqual(int thisIndex, IRangeComparator other,
			int otherIndex) {
		ArgumentComparator tc2;
		try {
			tc2 = (ArgumentComparator)other;
		} catch (ClassCastException e) {
			return false;
		}
		return tc2.getAtom(otherIndex).equalsIdentifier(getAtom(thisIndex));
	}

	public boolean skipRangeComparison(int length, int maxLength,
			IRangeComparator other) {
		return false;
	}
	
	public String substring(int startAtom, int endAtom) {
        if (startAtom == endAtom) {
        	return "";
        } else {
            StringBuilder result = new StringBuilder();
            for (int i = startAtom; i < endAtom; i++) {
                result.append(atoms.get(i).getFullText());
            }
            return result.toString();
        }
    }
	
	public String substring(int startAtom) {
		return substring(startAtom, atoms.size());
    }

}
