package com.smallzhi.clingservice.dlna.service;

import org.fourthline.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import com.iii360.sup.common.utl.LogManager;
import com.smallzhi.clingservice.modle.ContentNode;
import com.smallzhi.clingservice.modle.ContentTree;

public class MyContentDirectoryService extends AbstractContentDirectoryService {
	private final String NULL_STRING = "";

	@Override
	public BrowseResult browse(String objectID, BrowseFlag browseFlag, String filter,
			long firstResult, long maxResults, SortCriterion[] orderby)
			throws ContentDirectoryException {
		LogManager.e( "ContentDirectoryService---browse()--objectID is " + objectID);
		try {
			DIDLContent didl = new DIDLContent();
			ContentNode contentNode = ContentTree.getNode(objectID);			

			if (contentNode == null)
				return new BrowseResult(NULL_STRING, 0, 0);

			if (contentNode.isItem()) {
				didl.addItem(contentNode.getItem());
				return new BrowseResult(new DIDLParser().generate(didl), 1, 1); 
			} else {
				if (browseFlag == BrowseFlag.METADATA) {
					didl.addContainer(contentNode.getContainer());					
					return new BrowseResult(new DIDLParser().generate(didl), 1, 1);
				} else {
					for (Container container : contentNode.getContainer().getContainers()) {
						didl.addContainer(container);						
					}
					for (Item item : contentNode.getContainer().getItems()) {
						didl.addItem(item);						
					}
					return new BrowseResult(new DIDLParser().generate(didl),
							contentNode.getContainer().getChildCount(),
							contentNode.getContainer().getChildCount());
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ContentDirectoryException(
					ContentDirectoryErrorCode.CANNOT_PROCESS, ex.toString());
		}
	}

}
