package com.pmease.gitplex.web.component.diff.blob;

import org.apache.wicket.ajax.AjaxRequestTarget;

import com.pmease.gitplex.core.entity.CodeComment;
import com.pmease.gitplex.web.component.diff.revision.DiffMark;

public interface CommentAware {
	
	void onCommentDeleted(AjaxRequestTarget target, CodeComment comment);
	
	void onCommentClosed(AjaxRequestTarget target, CodeComment comment);

	void onCommentAdded(AjaxRequestTarget target, CodeComment comment);
	
	void mark(AjaxRequestTarget target, DiffMark mark);
	
	void clearMark(AjaxRequestTarget target);
}
