package example;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parse XML file to fill a CSCourseDB.
 * <a href="https://docs.oracle.com/javase/tutorial/jaxp/sax/parsing.html">SAX parser tutorial</a>.
 *
 * @author Mo
 */
public class CSCourseDBHandler extends DefaultHandler {
    private final CSCourseDB db;
    private String activeState = "";
    private CSCourseDB.Course lastCourse;
    private String from;
    private String to;

    CSCourseDBHandler(CSCourseDB db) {
        this.db = db;
        this.lastCourse = null;
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (qName.equals("course")) {
            activeState = "course";
            CSCourseDB.Course c = new CSCourseDB.Course(attributes.getValue("id"),
                    attributes.getValue("division"));
            db.addCourse(c);
            lastCourse = c;
        } else if (qName.equals("req")) {
            activeState = "req";
            lastCourse = null;
        } else if (activeState.equals("req") && qName.equals("from")) {
            from = attributes.getValue("ref");
        } else if (activeState.equals("req") && qName.equals("to")) {
            to = attributes.getValue("ref");
        } else if (activeState.equals("course") && qName.equals("tag")) {
            lastCourse.extraInfo.put(attributes.getValue("k"), attributes.getValue("v"));
        }
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("req")) {
            this.db.addPrereq(from, to);
            from = null;
            to = null;
        }
    }
}
