package org.sagebionetworks.repo.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sagebionetworks.repo.model.Dataset;
import org.sagebionetworks.repo.model.DatastoreException;
import org.sagebionetworks.repo.model.EntityHeader;
import org.sagebionetworks.repo.model.Layer;
import org.sagebionetworks.repo.model.LayerTypeNames;
import org.sagebionetworks.repo.model.InvalidModelException;
import org.sagebionetworks.repo.model.LocationData;
import org.sagebionetworks.repo.model.LocationTypeNames;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.Project;
import org.sagebionetworks.repo.model.Reference;
import org.sagebionetworks.repo.model.Step;
import org.sagebionetworks.repo.model.UnauthorizedException;
import org.sagebionetworks.repo.model.UserInfo;
import org.sagebionetworks.repo.web.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class GenericEntityControllerImpleAutowiredTest {
	
	@Autowired
	GenericEntityController entityController;
	
	@Autowired
	public UserProvider testUserProvider;
	
	
	List<String> toDelete = null;
	
	private int totalEntities = 10;
	private int layers = 5;
	private int locations = 2;
	
	private String userName;
	private UserInfo userInfo;
	
	HttpServletRequest mockRequest;

	
	@Before
	public void before() throws DatastoreException, InvalidModelException, NotFoundException, UnauthorizedException{
		assertNotNull(entityController);
		assertNotNull(testUserProvider);
		// Map test objects to their urls
		// Make sure we have a valid user.
		userInfo = testUserProvider.getTestAdminUserInfo();
		UserInfo.validateUserInfo(userInfo);
		userName = userInfo.getUser().getUserId();
		mockRequest = Mockito.mock(HttpServletRequest.class);
		when(mockRequest.getServletPath()).thenReturn("/repo/v1");
		
		toDelete = new ArrayList<String>();
		// Create a project to hold the datasets
		Project project = new Project();
		project.setName("projectRoot");
		project = entityController.createEntity(userName, project, mockRequest);
		assertNotNull(project);

		
		// Create some datasetst.
		for(int i=0; i<totalEntities; i++){
			Dataset ds = createForTest(i);
			ds.setParentId(project.getId());
			ds = entityController.createEntity(userName, ds, mockRequest);
			for(int layer=0; layer<layers; layer++){
				Layer inLayer = createLayerForTest(i*10+layer);
				inLayer.setParentId(ds.getId());
				inLayer.setMd5("b960413cf33e1333b2b709319c29870d");
				List<LocationData> locationDatas = new ArrayList<LocationData>();
				inLayer.setLocations(locationDatas);
				for(int loc=0; loc<locations; loc++){
					LocationData loca = createLayerLocatoinsForTest(i*10+layer*10+loc);
					locationDatas.add(loca);
				}
				inLayer = entityController.createEntity(userName, inLayer, mockRequest);
			}
			toDelete.add(ds.getId());
		}
		toDelete.add(project.getId());
	}
	
	private Dataset createForTest(int i){
		Dataset ds = new Dataset();
		ds.setName("someName"+i);
		ds.setDescription("someDesc"+i);
		ds.setCreatedBy("magic"+i);
		ds.setCreatedOn(new Date(1001));
		ds.setAnnotations("someAnnoUrl"+1);
		ds.setHasClinicalData(false);
		ds.setHasExpressionData(true);
		ds.setHasGeneticData(true);
		ds.setLayers("someLayerUrl"+i);
		ds.setReleaseDate(new Date(15689));
		ds.setStatus("someStatus"+i);
		ds.setUri("someUri"+i);
		return ds;
	}
	
	private Layer createLayerForTest(int i) throws InvalidModelException{
		Layer layer = new Layer();
		layer.setName("layerName"+i);
		layer.setDescription("layerDesc"+i);
		layer.setCreatedOn(new Date(1001));
		layer.setType(LayerTypeNames.G);
		return layer;
	}
	
	private LocationData createLayerLocatoinsForTest(int i) throws InvalidModelException{
		LocationData locationData = new LocationData();
		locationData.setPath("a/very/long/path/"+i);
		locationData.setType(LocationTypeNames.awsebs);
		return locationData;
	}
	
	@After
	public void after(){
		if(entityController != null && toDelete != null){
			for(String id: toDelete){
				try{
					entityController.deleteEntity(userName, id);
				}catch(Exception e){}
			}
		}
	}
	
	@Test
	public void testQuery() throws DatastoreException, NotFoundException, UnauthorizedException{
		// Basic query
		PaginatedResults<Dataset> paginated = entityController.getEntities(userName, new PaginatedParameters(1,100, null, true), mockRequest, Dataset.class);
		assertNotNull(paginated);
		assertNotNull(paginated.getPaging());
		List<Dataset> results = paginated.getResults();
		assertNotNull(results);
		assertEquals(totalEntities, results.size());
		// Check the urls for each object
		for(Dataset ds: results){
			UrlHelpers.validateAllUrls(ds);
			// Each dataset should also have a genetic layer
			assertFalse(ds.getHasClinicalData());
			assertFalse(ds.getHasExpressionData());
			assertTrue(ds.getHasGeneticData());
		}
		// Sorted
		paginated = entityController.getEntities(userName, new PaginatedParameters(1, 3, "name", true), mockRequest, Dataset.class);
		results = paginated.getResults();
		assertNotNull(results);
		assertEquals(3, results.size());
		assertNotNull(results.get(2));
		assertEquals("someName2", results.get(2).getName());
	}
	
	@Test 
	public void testGetChildrenOfType() throws DatastoreException, NotFoundException, UnauthorizedException{
		String datasetOneId = toDelete.get(0);
		List<Layer> list = entityController.getEntityChildrenOfType(userName, datasetOneId, Layer.class, mockRequest);
		assertNotNull(list);
		assertEquals(layers, list.size());
		Layer lastLayer = list.get(layers -1);
		assertNotNull(lastLayer);
		// Check the urls for each object
		for(Layer layer: list){
			// Check all of the urls
			UrlHelpers.validateAllUrls(layer);
			// Now get the locations.
			assertNotNull(layer.getLocations());
			assertEquals(locations, layer.getLocations().size());
		}
	}
	
	@Test 
	public void testGetChildrenOfTypePaginated() throws DatastoreException, NotFoundException, UnauthorizedException{
		String datasetOneId = toDelete.get(0);
		PaginatedResults<Layer> resutls = entityController.getEntityChildrenOfTypePaginated(userName, datasetOneId, Layer.class, new PaginatedParameters(), mockRequest);
		assertNotNull(resutls);
		assertEquals(layers, resutls.getTotalNumberOfResults());
		List<Layer> list = resutls.getResults();
		assertNotNull(list);
		assertEquals(layers, list.size());
		Layer lastLayer = list.get(layers -1);
		assertNotNull(lastLayer);
		// Now get the locations.
		assertNotNull(lastLayer.getLocations());
		assertEquals(locations, lastLayer.getLocations().size());
	}
	
	@Test
	public void testGetReferences() throws Exception {
		// get an entity
		String id1 = toDelete.get(0);
		// verify that nothing refers to it
		PaginatedResults<EntityHeader> ehs = entityController.getEntityReferences(userName, id1, null, null, null, mockRequest);
		assertEquals(0, ehs.getTotalNumberOfResults());
		// make another entity refer to the first one
		Step step = new Step();
		Reference ref = new Reference();
		ref.setTargetId(id1);
		Set<Reference> refs = new HashSet<Reference>();
		refs.add(ref);
		step.setInput(refs);
		step = entityController.createEntity(userName, step, mockRequest);
		toDelete.add(step.getId());
		// verify that the Step can be retrieved via its reference
		ehs = entityController.getEntityReferences(userName, id1, null, null, null, mockRequest);
		assertEquals(1, ehs.getTotalNumberOfResults());
		assertEquals(step.getId(), ehs.getResults().iterator().next().getId());
	}

}
