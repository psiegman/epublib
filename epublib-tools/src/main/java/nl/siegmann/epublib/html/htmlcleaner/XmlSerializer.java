package nl.siegmann.epublib.html.htmlcleaner;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.CommentNode;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.EndTagToken;
import org.htmlcleaner.TagNode;

public class XmlSerializer {
	
	protected CleanerProperties props;

	public XmlSerializer(CleanerProperties props) {
		this.props = props;
    }

	
    public void writeXml(TagNode tagNode, XMLStreamWriter writer) throws XMLStreamException {
//        if ( !props.isOmitXmlDeclaration() ) {
//            String declaration = "<?xml version=\"1.0\"";
//            if (charset != null) {
//                declaration += " encoding=\"" + charset + "\"";
//            }
//            declaration += "?>";
//            writer.write(declaration + "\n");
//		}
		
//		if ( !props.isOmitDoctypeDeclaration() ) {
//			DoctypeToken doctypeToken = tagNode.getDocType();
//			if ( doctypeToken != null ) {
//				doctypeToken.serialize(this, writer);
//			}
//		}
//		
		serialize(tagNode, writer);

        writer.flush();
    }
		
    protected void serializeOpenTag(TagNode tagNode, XMLStreamWriter writer) throws XMLStreamException {
        String tagName = tagNode.getName();
        
        writer.writeStartElement(tagName);
        Map tagAtttributes = tagNode.getAttributes();
        for(Iterator it = tagAtttributes.entrySet().iterator();it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String attName = (String) entry.getKey();
            String attValue = (String) entry.getValue();
            
            if ( !props.isNamespacesAware() && ("xmlns".equals(attName) || attName.startsWith("xmlns:")) ) {
            	continue;
            }
            writer.writeAttribute(attName, attValue);
        }
    }

    protected void serializeEmptyTag(TagNode tagNode, XMLStreamWriter writer) throws XMLStreamException {
        String tagName = tagNode.getName();
        
        writer.writeEmptyElement(tagName);
        Map tagAtttributes = tagNode.getAttributes();
        for(Iterator it = tagAtttributes.entrySet().iterator();it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String attName = (String) entry.getKey();
            String attValue = (String) entry.getValue();
            
            if ( !props.isNamespacesAware() && ("xmlns".equals(attName) || attName.startsWith("xmlns:")) ) {
            	continue;
            }
            writer.writeAttribute(attName, attValue);
        }
    }
    
    protected void serializeEndTag(TagNode tagNode, XMLStreamWriter writer) throws XMLStreamException {
    	writer.writeEndElement();
    }


    protected void serialize(TagNode tagNode, XMLStreamWriter writer) throws XMLStreamException {
    	if(tagNode.getChildren().isEmpty()) {
    		serializeEmptyTag(tagNode, writer);
    	} else {
	    	serializeOpenTag(tagNode, writer);
	
	        List tagChildren = tagNode.getChildren();
	        for(Iterator childrenIt = tagChildren.iterator(); childrenIt.hasNext(); ) {
	            Object item = childrenIt.next();
	            if (item != null) {
	            	serializeToken(item, writer);
	            }
	        }
	        serializeEndTag(tagNode, writer);
    	}
    }


	private void serializeToken(Object item, XMLStreamWriter writer) throws XMLStreamException {
        if ( item instanceof ContentNode ) {
            writer.writeCharacters(((ContentNode) item).getContent().toString());
        } else if(item instanceof CommentNode) {
			writer.writeComment(((CommentNode) item).getContent().toString());
        } else if(item instanceof EndTagToken) {
//        	writer.writeEndElement();
        } else if(item instanceof TagNode) {
        	serialize((TagNode) item, writer);
        }
	}	
}