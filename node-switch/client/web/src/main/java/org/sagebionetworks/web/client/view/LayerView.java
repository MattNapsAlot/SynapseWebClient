package org.sagebionetworks.web.client.view;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sagebionetworks.web.shared.FileDownload;
import org.sagebionetworks.web.shared.LicenseAgreement;
import org.sagebionetworks.web.shared.TableResults;

import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.IsWidget;



/**
 * Defines the communication between the view and presenter for a view of a single datasets.
 * 
 * @author jmhill
 *
 */
public interface LayerView extends IsWidget {
	
	/**
	 * This how the view communicates with the presenter.
	 * @param presenter
	 */
	public void setPresenter(Presenter presenter);
	
	/**
	 * Clears out instances of values already present
	 */
	public void clear();
	
	/**
	 * The view pops-up an error dialog.
	 * @param message
	 */
	public void showErrorMessage(String message);
	
	/**
	 * Sets the values to display in the view
	 * @param processingFacility
	 * @param qcByDisplay
	 * @param qcByUrl
	 * @param qcAnalysisDisplay
	 * @param qcAnalysisUrl
	 * @param qcDate
	 * @param overviewText
	 * @param nDataRowsShown
	 * @param totalDataRows
	 * @param privacyLevel
	 */
	public void setLayerDetails(String layerName,								
								String processingFacility, 
								String qcByDisplay,
								String qcByUrl, 
								String qcAnalysisDisplay, 
								String qcAnalysisUrl,
								Date qcDate, 
								String overviewText, 
								int nDataRowsShown,
								int totalDataRows, 
								String privacyLevel,
								String datasetLink,
								String platform);
	
	/**
	 * require the view to show the license agreement
	 * @param requireLicense
	 */
	public void requireLicenseAcceptance(boolean requireLicense);
	
	/**
	 * the license agreement to be shown
	 * @param agreement
	 */
	public void setLicenseAgreement(LicenseAgreement agreement);
	
	/**
	 * Set the list of files available via the whole dataset download
	 * @param downloads
	 */
	public void setLicensedDownloads(List<FileDownload> downloads);
	
	/**
	 * Disables the downloading of files
	 * @param disable
	 */
	public void disableLicensedDownloads(boolean disable);

	
	/**
	 * Shows a download modal for this layer
	 */
	public void showDownload();
	
	/**
	 * This sets the data to be shown in the preview table
	 * @param preview
	 * @param columnDisplayOrder
	 * @param columnUnits 
	 * @param columnDescriptions 
	 */
	public void setLayerPreviewTable(TableResults preview, String[] columnDisplayOrder, Map<String, String> columnDescriptions, Map<String, String> columnUnits);
	
	/**
	 * Defines the communication with the presenter.
	 *
	 */
	public interface Presenter {

		public void licenseAccepted();
		
	}

}