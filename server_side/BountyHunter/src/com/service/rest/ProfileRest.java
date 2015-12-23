package com.service.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONException;

import com.service.rest.imp.ProfileRestImp;


@Path("/profile")
public class ProfileRest {
	
	ProfileRestImp profileRestImp = new ProfileRestImp();
	
	@POST
	@Path("{userid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createProfile (
		@PathParam("userid") String userId,
		String profileStr
		) throws JSONException {
		System.out.println("in post profile");
		return profileRestImp.createProfile(userId, profileStr);
	}
	
	@PUT
	@Path("{userid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateProfile (
			@PathParam("userid") String userId,
			String profileStr
			) throws JSONException {
		return profileRestImp.updateProfile(userId, profileStr);
	}
	
	@GET
	@Path("{userid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProfile(
		@PathParam("userid") String userId
		) throws JSONException {
		return profileRestImp.getProfile(userId);
	}
	
	@POST
	@Path("register")
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(
		@QueryParam("userId") String userId,
		@QueryParam("password") String password) throws JSONException {
		return profileRestImp.register(userId, password);
	}
	
	@POST
	@Path("login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(
		@QueryParam("userId") String userId,
		@QueryParam("password") String accessToken
		) throws JSONException {
		return profileRestImp.login(userId, accessToken);
	}
	
	@DELETE
	@Path("delete/{userid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUser(
		@PathParam("userid") String userId
	) {
		return profileRestImp.deleteUser(userId);
	}
}