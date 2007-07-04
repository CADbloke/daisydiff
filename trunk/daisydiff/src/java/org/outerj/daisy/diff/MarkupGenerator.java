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
package org.outerj.daisy.diff;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Outputs the diff result as HTML elements to a SAX ContentHandler. The
 * startDocument and endDocument events are not generated by this class.
 */
public class MarkupGenerator {
    private ContentHandler consumer;

    public MarkupGenerator(ContentHandler consumer) throws SAXException {
        this.consumer = consumer;
    }

    public void addClearPart(String text) throws Exception {
        addBasicText(text);
    }

    private boolean bclosed = false;

    private void addBasicText(String text) throws SAXException {
        char[] c = text.toCharArray();

        AttributesImpl attrs = new AttributesImpl();

        for (int i = 0; i < c.length; i++) {
            switch (c[i]) {
            case '\n':
                consumer.startElement("", "br", "br", attrs);
                consumer.endElement("", "br", "br");
                consumer.characters("\n".toCharArray(), 0, "\n".length());
                break;
            case '<':
                if (bclosed == false) {
                    AttributesImpl fattrs = new AttributesImpl();
                    fattrs.addAttribute("", "size", "size", "CDATA", "3");
                    fattrs.addAttribute("", "face", "face", "CDATA",
                            "MonoSpace");
                    consumer.startElement("", "font", "font", fattrs);

                    bclosed = true;
                } else
                    throw new IllegalArgumentException(
                            "This is not well formed HTML");
                consumer.characters("<".toCharArray(), 0, "<".length());
                break;
            case '>':
                consumer.characters(">".toCharArray(), 0, ">".length());

                if (bclosed == true) {
                    consumer.endElement("", "font", "font");
                    bclosed = false;
                } else
                    throw new IllegalArgumentException(
                            "This is not well formed HTML");
                break;
            default:
                consumer.characters(c, i, 1);
            }
        }

        // String[] split = text.split("\n");
        // if(split.length>0)
        // consumer.characters(split[0].toCharArray(), 0, split[0].length());
        //    	
        // AttributesImpl attrs = new AttributesImpl();
        // for(int i=1;i<split.length;i++){
        // consumer.startElement("", "br", "br", attrs);
        // consumer.endElement("", "br", "br");
        //	        
        // String newline = "\n";
        // consumer.characters(newline.toCharArray(), 0, newline.length());
        //	        
        // String[] split2 = split[i].split("<");
        // if(split2.length>0)
        // consumer.characters(split2[0].toCharArray(), 0, split2[0].length());
        //	    	
        // for(int j=1;j<split2.length;j++){
        // consumer.startElement("", "b", "b", attrs);
        //		        
        // String tag = "<";
        // consumer.characters(tag.toCharArray(), 0, tag.length());
        //		        
        // String[] split3 = split2[j].split(">");
        // if(split3.length>0)
        // consumer.characters(split3[0].toCharArray(), 0, split3[0].length());
        //		    	
        // for(int k=1;k<split3.length;k++){
        // String tagend = ">";
        //
        // consumer.characters(tagend.toCharArray(), 0, tagend.length());
        // consumer.endElement("", "b", "b");
        // consumer.characters(split3[k].toCharArray(), 0, split3[k].length());
        //			        
        //			        
        // }

        // consumer.characters(split2[j].toCharArray(), 0, split2[j].length());
        // }
        // }
        // AttributesImpl attrs = new AttributesImpl();
        //    	
        // BufferedReader reader = new BufferedReader(new StringReader(text));
        // try {
        // String line = reader.readLine();
        // while (line != null) {
        // consumer.characters(line.toCharArray(), 0, line.length());
        //            	
        // consumer.startElement("", "br", "br", attrs);
        // consumer.endElement("", "br", "br");
        //                
        // line = "\n";
        // consumer.characters(line.toCharArray(), 0, line.length());
        //                
        // line = reader.readLine();
        // }
        // } catch (IOException e) {
        // throw new RuntimeException("Unexpected: got exception while reading
        // from String object.", e);
        // }

    }

    public void addRemovedPart(String text) throws Exception {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "color", "color", "CDATA", "red");
        // attrs.addAttribute("", "size", "size", "CDATA", "2");

        consumer.startElement("", "font", "font", attrs);
        consumer.startElement("", "strike", "strike", new AttributesImpl());
        addBasicText(text);
        consumer.endElement("", "strike", "strike");
        consumer.endElement("", "font", "font");
    }

    public void addAddedPart(String text) throws Exception {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "color", "color", "CDATA", "green");

        consumer.startElement("", "font", "font", attrs);
        addBasicText(text);
        consumer.endElement("", "font", "font");
    }

    public void printInfo() throws Exception {
        AttributesImpl attrs = new AttributesImpl();
        consumer.startElement("", "u", "u", attrs);
        String text = "Begin Diff Markup:";
        consumer.characters(text.toCharArray(), 0, text.length());
        consumer.endElement("", "u", "u");
        consumer.startElement("", "br", "br", attrs);
        consumer.endElement("", "br", "br");
    }

    public void addSeperator(String s) throws SAXException {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "color", "color", "CDATA", "orange");

        consumer.startElement("", "font", "font", attrs);
        addBasicText("" + s);
        consumer.endElement("", "font", "font");
    }
}