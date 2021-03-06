package org.sagebionetworks.web.client.widget.portlet;

import org.sagebionetworks.web.client.widget.entity.DOMUtil;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.google.gwt.user.client.Element;

@Deprecated
public class SynapsePortlet extends Portlet {

	public SynapsePortlet(String title, boolean isTop, boolean isTitle) {
		String headTag = "";
		String headTagClose = "";
		if(isTitle) {
			headTag = isTop ? "<h2 class=\"top\">" : "<h2>";
			headTagClose = "</h2>";			
		} else {
			headTag = isTop ? "<h3 class=\"top\">" : "<h3>";
			headTagClose = "</h3>";
		}
		Html header = new Html(headTag + title + headTagClose);
		header.setAutoHeight(true);
	    add(header);
	    
		setStyleName("");
		setHeaderVisible(false);
		setBorders(false);
		setFrame(false);
		setBodyStyleName(""); // TODO : create a style for SynapsePortlet?
		setBodyStyle("background-color: #ffffff");
		
	}
	
	public SynapsePortlet(String title) {
		this(title, false, false);
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		// remove all GXT ContentPanel border styles
		String[] classNames = new String[] { "x-panel-ml", "x-panel-mr",
				"x-panel-bl", "x-panel-br", "x-panel-bc", "x-panel-mc",
				"x-panel-body", "x-panel-tl", "x-panel-tr",
				"x-panel-body-noheader" };			
		DOMUtil.removeStyles(classNames, this.getElement());			
	}

}
