package org.jobcenter.webservice;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.jobcenter.constants.WebServiceURLConstants;
import org.jobcenter.exception.RecordNotUpdatedException;
import org.jobcenter.request.*;
import org.jobcenter.response.*;
import org.jobcenter.service.*;

import com.sun.jersey.spi.inject.Inject;
import com.sun.jersey.spi.resource.Singleton;



/**
 *
 *
 */
@Produces("application/xml")

@Path( WebServiceURLConstants.SUBMIT_JOB )

//  Jersey specific annotation
@Singleton
public class SubmitJobWebService {

	private static Logger log = Logger.getLogger(SubmitJobWebService.class);


	@Inject
	private SubmitJobService submitJobService;


	/**
	 * @return
	 */
	@POST
	@Consumes("application/xml")
	public SubmitJobResponse submitJob( SubmitJobRequest submitJobRequest, @Context HttpServletRequest request ) {



		if ( log.isInfoEnabled() ) {

			log.info( "submitJob: getNodeName(): " + submitJobRequest.getNodeName()  );
		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();

		int errorCode = JobResponse.ERROR_CODE_NO_ERRORS;


		try {
			SubmitJobResponse submitJobResponse = submitJobService.submitJob( submitJobRequest, remoteHost );

			return submitJobResponse;

		} catch (RecordNotUpdatedException e) {

			log.error( "submitJob Failed: RecordNotUpdatedException: Exception: JobWebService:: updateJobStatus:   getNodeName(): " + submitJobRequest.getNodeName() + ", Exception: " + e.toString() , e );

			errorCode = JobResponse.ERROR_CODE_DATABASE_NOT_UPDATED;

		} catch (Throwable e) {

			log.error( "submitJob Failed: Exception: JobWebService:: updateJobStatus:   getNodeName(): " + submitJobRequest.getNodeName() + ", Exception: " + e.toString() , e );

			errorCode = JobResponse.ERROR_CODE_GENERAL_ERROR;
		}

		SubmitJobResponse submitJobResponse = new SubmitJobResponse();

		submitJobResponse.setErrorResponse( true );

		submitJobResponse.setErrorCode( errorCode );

		return submitJobResponse;
	}

}