package com.pmease.commons.antlr.codeassist;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class AnyTokenElementSpec extends ElementSpec {

	private static final long serialVersionUID = 1L;

	public AnyTokenElementSpec(CodeAssist codeAssist, String label, Multiplicity multiplicity) {
		super(codeAssist, label, multiplicity);
	}

	@Override
	public List<ElementSuggestion> doSuggestFirst(Node parent, String matchWith, AssistStream stream) {
		return new ArrayList<ElementSuggestion>();
	}

	@Override
	public CaretMove skipMandatories(String content, int offset) {
		return new CaretMove(offset, true);
	}

	@Override
	public List<String> getMandatories() {
		return new ArrayList<>();
	}

	@Override
	protected boolean matchOnce(AssistStream stream) {
		if (!stream.isEof())
			stream.increaseIndex();
		return true;
	}

	@Override
	protected List<TokenNode> getPartialMatchesOnce(AssistStream stream, Node parent) {
		Preconditions.checkArgument(!stream.isEof());
		
		Token token = stream.getCurrentToken();
		stream.increaseIndex();
		return Lists.newArrayList(new TokenNode(this, parent, token));
	}

}