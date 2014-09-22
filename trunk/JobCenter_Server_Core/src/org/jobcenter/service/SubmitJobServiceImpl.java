package org.jobcenter.service;


import java.util.Map;

import org.apache.log4j.Logger;
import org.jobcenter.constants.DBConstantsServerCore;
import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.dao.JobTypeDAO;
import org.jobcenter.dao.RequestTypeDAO;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.JobType;
import org.jobcenter.dto.RequestTypeDTO;
import org.jobcenter.internalservice.ClientNodeNameCheck;
import org.jobcenter.internalservice.SubmitJobInternalService;
import org.jobcenter.jdbc.JobJDBCDAO;
import org.jobcenter.request.SubmitJobRequest;
import org.jobcenter.response.BaseResponse;
import org.jobcenter.response.SubmitJobResponse;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    WARNING   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

//The only way to get proper roll backs ( managed by Spring ) is to only use un-checked exceptions.
//
//The best way to make sure there are no checked exceptions is to have no "throws" on any of the methods.


//@Transactional causes Spring to surround calls to methods in this class with a database transaction.
//        Spring will roll back the transaction if a un-checked exception ( extended from RuntimeException ) is thrown.
//                 Otherwise it commits the transaction.


/**
*
*
*/
@Transactional ( propagation = Propagation.REQUIRED, readOnly = false )

public class SubmitJobServiceImpl implements SubmitJobService {

	private static Logger log = Logger.getLogger(SubmitJobServiceImpl.class);


	//  Service

	private ClientNodeNameCheck clientNodeNameCheck;
	private SubmitJobInternalService submitJobInternalService;

	public ClientNodeNameCheck getClientNodeNameCheck() {
		return clientNodeNameCheck;
	}
	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
		this.clientNodeNameCheck = clientNodeNameCheck;
	}

	public SubmitJobInternalService getSubmitJobInternalService() {
		return submitJobInternalService;
	}
	public void setSubmitJobInternalService(
			SubmitJobInternalService submitJobInternalService) {
		this.submitJobInternalService = submitJobInternalService;
	}


	/**
	 * @param jobRequest
	 * @param remoteHost
	 * @return
	 */
	public SubmitJobResponse submitJob( SubmitJobRequest submitJobRequest, String remoteHost )
	{
		final String method = "submitJob";


		if ( submitJobRequest == null ) {

			log.error( method + "  IllegalArgument: submitJobRequest == null");

			throw new IllegalArgumentException( "submitJobRequest == null" );
		}

		if ( log.isDebugEnabled() ) {

			log.debug( "SubmitJobServiceImpl::submitJob  submitJobRequest.getNodeName() = |" + submitJobRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}

		SubmitJobResponse submitJobResponse = new SubmitJobResponse();

		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( submitJobResponse, submitJobRequest.getNodeName(), remoteHost ) ) {

			return submitJobResponse;
		}


		///////////

		//  validate request type name
		

		RequestTypeDTO requestTypeDTO = submitJobInternalService.validateRequestTypeNameRequestId( submitJobRequest.getRequestTypeName(), submitJobRequest.getRequestId(), submitJobResponse );
		
		if ( requestTypeDTO == null ) {
			
			return submitJobResponse;
		}


		//////////////
		
		JobType jobType = submitJobInternalService.validateJobTypeName( submitJobRequest.getJobTypeName(), submitJobRequest.getRequestId(), submitJobResponse );
		
		if ( jobType == null ) {
			
			return submitJobResponse;
		}
		
		
		if ( ! submitJobInternalService.validateRequiredExecutionThreads( submitJobRequest.getRequiredExecutionThreads(), jobType.getMaxRequiredExecutionThreads(), submitJobResponse ) ) {
			
			return submitJobResponse;
		}
		

		
		Integer requestId = submitJobRequest.getRequestId();

		if ( requestId == null ) {

			requestId = submitJobInternalService.insertRequest( requestTypeDTO );
		}

		
		Job job = submitJobInternalService.createJobFromSubmitJobRequest( submitJobRequest, jobType, requestId );

		submitJobInternalService.insertJob( job );

		submitJobResponse.setRequestId( requestId );

		return submitJobResponse;
	}
}
