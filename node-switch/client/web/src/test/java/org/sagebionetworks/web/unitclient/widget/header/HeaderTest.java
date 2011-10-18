package org.sagebionetworks.web.unitclient.widget.header;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sagebionetworks.web.client.widget.header.Header;
import org.sagebionetworks.web.client.widget.header.HeaderView;

public class HeaderTest {
		
	Header header;
	HeaderView mockView;
	
	@Before
	public void setup(){		
		mockView = Mockito.mock(HeaderView.class);		
		header = new Header(mockView);
		
		verify(mockView).setPresenter(header);
	}
	
	@Test
	public void testAsWidget(){
		header.asWidget();
	}
	
}