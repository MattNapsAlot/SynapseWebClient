package org.sagebionetworks.repo.model.jdo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.repo.model.AccessControlList;
import org.sagebionetworks.repo.model.AccessControlListDAO;
import org.sagebionetworks.repo.model.AuthorizationConstants.ACCESS_TYPE;
import org.sagebionetworks.repo.model.DatastoreException;
import org.sagebionetworks.repo.model.InvalidModelException;
import org.sagebionetworks.repo.model.Node;
import org.sagebionetworks.repo.model.NodeDAO;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.ResourceAccess;
import org.sagebionetworks.repo.model.UserGroup;
import org.sagebionetworks.repo.model.UserGroupDAO;
import org.sagebionetworks.repo.web.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This test is for http://sagebionetworks.jira.com/browse/PLFM-543.
 * 
 * @author jmhill
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:jdomodels-test-context.xml" })
public class JDOAccessControlListDAOScaleTest {

	@Autowired
	private AccessControlListDAO accessControlListDAO;

	@Autowired
	private NodeDAO nodeDAO;

	@Autowired
	private UserGroupDAO userGroupDAO;

	private UserGroup userGroup;
	private String userId;

	private List<String> toDelete;

	@Before
	public void before() throws NotFoundException, DatastoreException,
			InvalidModelException {

		toDelete = new ArrayList<String>();

		// Create a user
		userGroup = new UserGroup();
		userGroup.setName("aTestUser@sagebase.org");
		userGroup.setCreationDate(new Date());
		userGroup.setIndividual(true);
		userId = userGroupDAO.create(userGroup);
		// Create 100 projects root project
		for (int i = 0; i < 200; i++) {
			Node node = new Node();
			node.setName("foo");
			node.setCreatedOn(new Date());
			node.setCreatedBy("me");
			node.setModifiedOn(new Date());
			node.setModifiedBy("metoo");
			node.setNodeType(ObjectType.project.name());
			String nodeId = nodeDAO.createNew(node);
			assertNotNull(nodeId);
			toDelete.add(nodeId);

			// Create an ACL for each node
			// Create an ACL for this node
			AccessControlList acl = new AccessControlList();
			acl.setId(nodeId);
			acl.setCreatedBy("someDude");
			acl.setCreationDate(new Date(System.currentTimeMillis()));
			acl.setModifiedBy(acl.getCreatedBy());
			acl.setModifiedOn(acl.getCreationDate());
			acl.setResourceAccess(new HashSet<ResourceAccess>());
			ResourceAccess ra = new ResourceAccess();
			ra.setGroupName(userGroup.getName());

			// Add each type
			Set<ACCESS_TYPE> types = new HashSet<ACCESS_TYPE>();
			for (ACCESS_TYPE type : ACCESS_TYPE.values()) {
				types.add(type);
			}
			ra.setAccessType(types);
			acl.getResourceAccess().add(ra);
			accessControlListDAO.create(acl);
		}

	}

	@After
	public void after() {
		// Delete all nodes created
		if (nodeDAO != null && toDelete != null) {
			for (String id : toDelete) {
				try {
					nodeDAO.delete(id);
				} catch (NotFoundException e) {
				}
			}
		}
		if (userId != null && userGroupDAO != null) {
			try {
				userGroupDAO.delete(userId);
			} catch (Exception e) {
			}
		}
	}

	@Test
	public void testTime() throws DatastoreException{
		// Time the can access methods
		ArrayList<UserGroup> groups = new ArrayList<UserGroup>();
		groups.add(userGroup);
		System.out.println("Number of base projects: \t"+toDelete.size());
		for(ACCESS_TYPE type: ACCESS_TYPE.values()){
			long start = System.nanoTime();
			boolean canAccess = accessControlListDAO.canAccess(groups, toDelete.get(0), type);
			long end = System.nanoTime();
			long elpaseMs = (end-start)/1000000;
			assertTrue(canAccess);
			System.out.println("Time for \t"+type.name()+"\t"+elpaseMs+"\tms");
			assertTrue("Since accessControlListDAO.canAccess() is called everywhere, it cannot take more than 100 ms to run!",elpaseMs < 100);
		}
	}
}