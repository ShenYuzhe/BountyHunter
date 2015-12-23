package com.service.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;

import com.service.rest.imp.TaskRestImp;

@Path("/task")
public class TaskRest {
	 @Context
	 private HttpServletRequest request;
	 @Context
	 private HttpServletResponse response;
	 
	 TaskRestImp taskRestImp = new TaskRestImp();
	
	@GET
	@Path("/nearby")
	@Produces(MediaType.APPLICATION_JSON)
	public Response queryNearbyTasks(
		@QueryParam("lat") String lat,
		@QueryParam("lng") String lng,
		@QueryParam("radius") String radius
		) throws JSONException {
		return taskRestImp.taskNearby(lat, lng, radius);
	}
	
	@GET
	@Path("/recommend")
	@Produces(MediaType.APPLICATION_JSON)
	public Response queryRecommendTasks(
		@QueryParam("userid") String userId,
		@QueryParam("lat") String lat,
		@QueryParam("lng") String lng
		) throws JSONException {
		String taskNum = "10";
		String radius = "20000";
		return taskRestImp.recommendTask(userId, lat, lng, radius, taskNum);
	}
	
	@POST
	@Path("/apply")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response acceptTask(
		String requestStr
		) throws JSONException {
		return taskRestImp.acceptTask(requestStr);
	}
	
	@POST
	@Path("/confirm")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response confirmTask(
		String requestStr
		) throws JSONException {
		return taskRestImp.confirmTask(requestStr);
	}
	
	@POST
	@Path("/reject")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response rejectHunter(
		String requestStr
		) throws JSONException {
		return taskRestImp.rejectHunter(requestStr);
	}
	
	@POST
	@Path("/finish")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response finishTask(
		String requestStr
		) throws JSONException {
		return taskRestImp.finishTask(requestStr);
	}
	
	@POST
	@Path("/postreward")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postTask (
		String requestStr
		) throws JSONException {
		return taskRestImp.postTask(requestStr);
	}
	
	@GET
	@Path("/posterHistory/{userid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHunters(
		@PathParam("userid") String userId
		) throws JSONException {
		return taskRestImp.getHunterList(userId);
	}
	
	@GET
	@Path("/hunterHistory/{userid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStatus(
		@PathParam("userid") String userId
		) throws JSONException {
		return taskRestImp.getTaskStatus(userId);
	}
	
	@POST
	@Path("/cancelPoster")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response cancePoster(String taskStr) throws JSONException {
		return taskRestImp.cancelTaskPoster(taskStr);
	}
	
	@POST
	@Path("/cancelHunter")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response canceHunter(String taskStr) throws JSONException {
		return taskRestImp.cancelTaskHunter(taskStr);
	}
	
	@POST
	@Path("/updatePosterPrice")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatePosterPrice(String updateRequest) throws JSONException {
		return taskRestImp.updatePricePoster(updateRequest);
	}
	
	@POST
	@Path("/updateHunterPrice")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateHunterPrice(String updateRequest) throws JSONException {
		return taskRestImp.updatePriceHunter(updateRequest);
	}
}
