package com.mobile_test.services;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;

import com.mobile_test.models.Test;
import com.mobile_test.providers.TestProvider;

@Path("/test")
public class TestService {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("update")
    public Response modify(Test test) {
    	try {
			TestProvider.updateTest(test);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError().build();
		}
    	return Response.ok("").build();
    }
	
	@DELETE
	@Path("delete")
    public Response delete(@QueryParam("id") int id) {
		try {
			TestProvider.deleteTestByID(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError().build();
		}
        return Response.ok("").build();
    }
	
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("create")
    public Response insert(/*@QueryParam("data")*/ String data) {
    	try {
			TestProvider.insertTest(data);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError().build();
		}
    	return Response.ok("").build();
    }
	
	@GET
    @Path("read")
	@Produces(MediaType.APPLICATION_JSON)
    public Test get(@QueryParam("id") int id) {
		try {
			return TestProvider.getTestByID(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
}
