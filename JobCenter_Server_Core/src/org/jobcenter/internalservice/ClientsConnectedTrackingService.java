package org.jobcenter.internalservice;

import java.util.List;

import org.jobcenter.constants.ClientStatusUpdateTypeEnum;
import org.jobcenter.nondbdto.ClientConnectedDTO;
import org.jobcenter.nondbdto.ClientIdentifierDTO;

/**
 * This Singleton holds all the clients that have connected.  The clients are purged off after a time
 *
 */
public interface ClientsConnectedTrackingService {

	/**
	 * @return
	 */
	public List<ClientConnectedDTO> retrieveClientsConnectedList();
	
	/**
	 * @param clientConnectedDTO
	 */
	public void addClient( ClientConnectedDTO clientConnectedDTO );
	
	/**
	 * @param clientIdentifierDTO
	 * @param updateType
	 */
	public void updateClientStatusAndInfo( ClientIdentifierDTO clientIdentifierDTO, ClientStatusUpdateTypeEnum updateType );

	/**
	 * @param clientIdentifierDTO
	 */
	public void updateClientGetJobStartTimestamp( ClientIdentifierDTO clientIdentifierDTO );

	/**
	 * @param clientIdentifierDTO
	 */
	public void updateClientGetJobEndTimestamp( ClientIdentifierDTO clientIdentifierDTO );
	
	/**
	 * @param current_GetJobProcessingTime
	 * @param clientIdentifierDTO
	 */
	public void updateClient_Tracking_GetJobMaxProcessingTimeSinceLastGUIQuery( long current_GetJobProcessingTime, ClientIdentifierDTO clientIdentifierDTO );
}
