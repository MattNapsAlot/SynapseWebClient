package org.sagebionetworks.web.shared.exceptions;

public class ForbiddenException extends RestServiceException {
	
	private static final long serialVersionUID = 1L;

	public ForbiddenException() {
		super();
	}

	public ForbiddenException(String message) {
		super(message);
	}

}
