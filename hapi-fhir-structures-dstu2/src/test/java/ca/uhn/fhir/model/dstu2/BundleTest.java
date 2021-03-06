package ca.uhn.fhir.model.dstu2;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Bundle.Link;

public class BundleTest {

	@Test
	public void testGetLink() {
		
		Bundle b = new Bundle();
		Link link = b.getLink(Bundle.LINK_NEXT);
		
		assertNull(link);
		
		Link link2 = b.getLinkOrCreate(Bundle.LINK_NEXT);
		link = b.getLink(Bundle.LINK_NEXT);
		
		assertNotNull(link);
		assertNotNull(link2);
		assertSame(link, link2);
	}
	
}
