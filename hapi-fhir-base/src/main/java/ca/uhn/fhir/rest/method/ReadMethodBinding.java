package ca.uhn.fhir.rest.method;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu.valueset.RestfulOperationSystemEnum;
import ca.uhn.fhir.model.dstu.valueset.RestfulOperationTypeEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.client.GetClientInvocation;
import ca.uhn.fhir.rest.method.SearchMethodBinding.RequestType;
import ca.uhn.fhir.rest.server.Constants;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.Util;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;

public class ReadMethodBinding extends BaseMethodBinding {
	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(ReadMethodBinding.class);
	
	private Method myMethod;
	private Integer myIdIndex;
	private Integer myVersionIdIndex;
	private int myParameterCount;

	public ReadMethodBinding(MethodReturnTypeEnum theMethodReturnType, Class<? extends IResource> theAnnotatedResourceType, Method theMethod) {
		super(theMethodReturnType, theAnnotatedResourceType);
		
		Validate.notNull(theMethod, "Method must not be null");
		
		Integer idIndex = Util.findReadIdParameterIndex(theMethod);
		Integer versionIdIndex = Util.findReadVersionIdParameterIndex(theMethod);

		myMethod = theMethod;
		myIdIndex = idIndex;
		myVersionIdIndex = versionIdIndex;
		myParameterCount = myMethod.getParameterTypes().length;
		
		Class<?>[] parameterTypes = theMethod.getParameterTypes();
		if (!IdDt.class.equals(parameterTypes[myIdIndex])) {
			throw new ConfigurationException("ID parameter must be of type: " + IdDt.class.getCanonicalName() + " - Found: "+parameterTypes[myIdIndex]);
		}
		if (myVersionIdIndex != null && !IdDt.class.equals(parameterTypes[myVersionIdIndex])) {
			throw new ConfigurationException("Version ID parameter must be of type: " + IdDt.class.getCanonicalName()+ " - Found: "+parameterTypes[myVersionIdIndex]);
		}

	}
	
	public boolean isVread() {
		return myVersionIdIndex != null;
	}

	@Override
	public boolean matches(Request theRequest) {
		if (!theRequest.getResourceName().equals(getResourceName())) {
			return false;
		}
		if (theRequest.getParameterNames().isEmpty() == false) {
			return false;
		}
		if ((theRequest.getVersion() == null) != (myVersionIdIndex == null)) {
			return false;
		}
		if (theRequest.getId() == null) {
			return false;
		}
		if (theRequest.getRequestType() != RequestType.GET) {
			ourLog.trace("Method {} doesn't match because request type is not GET: {}", theRequest.getId(), theRequest.getRequestType());
			return false;
		}
		return true;
	}

	@Override
	public ReturnTypeEnum getReturnType() {
		return ReturnTypeEnum.RESOURCE;
	}

	@Override
	public List<IResource> invokeServer(IResourceProvider theResourceProvider, IdDt theId, IdDt theVersionId, Map<String, String[]> theParameterValues) throws InvalidRequestException,
			InternalErrorException {
		Object[] params = new Object[myParameterCount];
		params[myIdIndex] = theId;
		if (myVersionIdIndex != null) {
			params[myVersionIdIndex] = theVersionId;
		}
		
		Object response;
		try {
			response = myMethod.invoke(theResourceProvider, params);
		} catch (Exception e) {
			throw new InternalErrorException("Failed to call access method",e);
		}
		
		return toResourceList(response);
	}

	@Override
	public GetClientInvocation invokeClient(Object[] theArgs) {
		String id = ((IdDt)theArgs[myIdIndex]).getValue();
		if (myVersionIdIndex == null) {
			return new GetClientInvocation(getResourceName(), id);
		}else {
			String vid = ((IdDt)theArgs[myVersionIdIndex]).getValue();
			return new GetClientInvocation(getResourceName(), id, Constants.URL_TOKEN_HISTORY, vid);
		}
	}

	@Override
	public RestfulOperationTypeEnum getResourceOperationType() {
		return isVread() ? RestfulOperationTypeEnum.VREAD : RestfulOperationTypeEnum.READ;
	}

	@Override
	public RestfulOperationSystemEnum getSystemOperationType() {
		return null;
	}

}