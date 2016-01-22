package br.com.infox.epp.modeler.rest.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.token.AccessTokenAuthentication;
import br.com.infox.core.token.TokenRequester;
import br.com.infox.epp.modeler.rest.manager.BpmnRestManager;
import br.com.infox.epp.modeler.rest.model.Bpmn;
import br.com.infox.ibpm.process.definition.variable.VariableType;

@Path("/bpmn")
public class BpmnRestService {
	
	@Inject
	private BpmnRestManager bpmnManager;
	@Inject
	private InfoxMessages infoxMessages;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@AccessTokenAuthentication(TokenRequester.BPMN_MODELER)
	public List<Bpmn> listBpmn() {
		return bpmnManager.listBpmn();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@AccessTokenAuthentication(TokenRequester.BPMN_MODELER)
	public Bpmn getBpmn(@PathParam("id") Integer id) {
		return bpmnManager.getBpmn(id);
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@AccessTokenAuthentication(TokenRequester.BPMN_MODELER)
	public Response updateBpmn(@PathParam("id") Integer id, String bpmnObj) {
		bpmnManager.updateBpmn(new Gson().fromJson(bpmnObj, Bpmn.class));
		return Response.noContent().build();
	}
	
	@GET
	@Path("/form-field-type")
	@Produces(MediaType.APPLICATION_JSON)
	@AccessTokenAuthentication(TokenRequester.BPMN_MODELER)
	public Response getFormFieldTypes() {
		JsonArray formFieldTypes = new JsonArray();
		List<VariableType> variableTypes = Arrays.asList(VariableType.values());
		Collections.sort(variableTypes, new Comparator<VariableType>() {
			@Override
			public int compare(VariableType o1, VariableType o2) {
				return infoxMessages.get(o1.getLabel()).compareTo(infoxMessages.get(o2.getLabel()));
			}
		});
		for (VariableType variableType : variableTypes) {
			if (variableType.name().equals("NULL")) {
				continue;
			}
			JsonObject type = new JsonObject();
			type.add("name", new JsonPrimitive(variableType.name()));
			type.add("label", new JsonPrimitive(infoxMessages.get(variableType.getLabel())));
			formFieldTypes.add(type);
		}
		return Response.ok(new Gson().toJson(formFieldTypes)).build();
	}
}
