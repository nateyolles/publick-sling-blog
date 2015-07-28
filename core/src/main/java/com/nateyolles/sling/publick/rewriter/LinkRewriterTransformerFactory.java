package com.nateyolles.sling.publick.rewriter;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.rewriter.ProcessingComponentConfiguration;
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.rewriter.Transformer;
import org.apache.sling.rewriter.TransformerFactory;
import org.osgi.service.component.ComponentContext;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Link rewriter to remove "/content" from the path. For example, links to
 * "/content/blog/2015/02/blog-post.html" will become "/blog/2015/02/blog-post.html".
 */
@Component
@Service
@Property(name="pipeline.type", value="linkrewriter", propertyPrivate=true)
public class LinkRewriterTransformerFactory implements TransformerFactory {

    public Transformer createTransformer() {
        return new LinkRewriterTransformer();
    }

    @Activate
    protected void activate(Map<String, Object> properties) {
    }

    @Deactivate
    protected void deactivate(ComponentContext ctx) {
    }

    private class LinkRewriterTransformer implements Transformer {
        private static final String CONTENT_PATH = "/content";
        private static final String HREF_ATTRIBUTE = "href";

        private ContentHandler contentHandler;

        public void characters(char[] ch, int start, int length) throws SAXException {
            contentHandler.characters(ch, start, length);
        }

        public void dispose() {
        }

        public void endDocument() throws SAXException {
            contentHandler.endDocument();
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            contentHandler.endElement(uri, localName, qName);
        }

        public void endPrefixMapping(String prefix) throws SAXException {
            contentHandler.endPrefixMapping(prefix);
        }

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            contentHandler.ignorableWhitespace(ch, start, length);
        }

        public void init(ProcessingContext context, ProcessingComponentConfiguration config) throws IOException {
        }

        public void processingInstruction(String target, String data) throws SAXException {
            contentHandler.processingInstruction(target, data);
        }

        public void setContentHandler(ContentHandler handler) {
            this.contentHandler = handler;
        }

        public void setDocumentLocator(Locator locator) {
            contentHandler.setDocumentLocator(locator);
        }

        public void skippedEntity(String name) throws SAXException {
            contentHandler.skippedEntity(name);
        }

        public void startDocument() throws SAXException {
            contentHandler.startDocument();
        }

        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            final AttributesImpl attributes = new AttributesImpl(atts);
            final String href = attributes.getValue(HREF_ATTRIBUTE);

            if (href != null && href.startsWith("/")) {

                for (int i = 0; i < attributes.getLength(); i++) {
                    if (HREF_ATTRIBUTE.equalsIgnoreCase(attributes.getQName(i))) {
                        attributes.setValue(i, rewritePath(href));
                        break;
                    }
                }
            }

            contentHandler.startElement(uri, localName, qName, attributes);
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            contentHandler.startPrefixMapping(prefix, uri);
        }

        /**
         * Rewrite link to remove content folder.
         *
         * @param href The link path.
         * @return The link path without the content folder.
         */
        private String rewritePath(final String href) {
            if (StringUtils.isNotEmpty(href) && href.startsWith(CONTENT_PATH)) {
                return href.replaceFirst(CONTENT_PATH, StringUtils.EMPTY);
            }

            return href;
        }
    }
}