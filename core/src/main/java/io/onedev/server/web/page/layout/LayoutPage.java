package io.onedev.server.web.page.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import io.onedev.launcher.loader.AppLoader;
import io.onedev.launcher.loader.Plugin;
import io.onedev.server.OneDev;
import io.onedev.server.manager.SettingManager;
import io.onedev.server.model.User;
import io.onedev.server.security.SecurityUtils;
import io.onedev.server.web.component.link.ViewStateAwarePageLink;
import io.onedev.server.web.component.user.avatar.UserAvatar;
import io.onedev.server.web.page.admin.AdministrationPage;
import io.onedev.server.web.page.admin.systemsetting.SystemSettingPage;
import io.onedev.server.web.page.base.BasePage;
import io.onedev.server.web.page.project.ProjectListPage;
import io.onedev.server.web.page.project.ProjectPage;
import io.onedev.server.web.page.security.LoginPage;
import io.onedev.server.web.page.security.LogoutPage;
import io.onedev.server.web.page.security.RegisterPage;
import io.onedev.server.web.page.user.UserListPage;
import io.onedev.server.web.page.user.UserPage;
import io.onedev.server.web.page.user.UserProfilePage;

@SuppressWarnings("serial")
public abstract class LayoutPage extends BasePage {
	
	public LayoutPage() {
	}
	
	public LayoutPage(PageParameters params) {
		super(params);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		WebMarkupContainer projectsContainer = new WebMarkupContainer("navProjects");
		projectsContainer.add(new ViewStateAwarePageLink<Void>("link", ProjectListPage.class));
		if (getPage() instanceof ProjectListPage || getPage() instanceof ProjectPage) 
			projectsContainer.add(AttributeAppender.append("class", "active"));
		add(projectsContainer);
		
		RepeatingView contributionsView = new RepeatingView("navContributions");		
		List<MainNavContribution> contributions = new ArrayList<>();
		for (MainNavContribution contribution: OneDev.getExtensions(MainNavContribution.class)) {
			if (contribution.isAuthorized())
				contributions.add(contribution);
		}
		Collections.sort(contributions, new Comparator<MainNavContribution>() {

			@Override
			public int compare(MainNavContribution o1, MainNavContribution o2) {
				return o1.getOrder() - o2.getOrder();
			}
			
		});
		for (MainNavContribution contribution: contributions) {
			WebMarkupContainer contributionContainer = new WebMarkupContainer(contributionsView.newChildId());
			Link<Void> link = new ViewStateAwarePageLink<Void>("link", contribution.getPageClass());
			link.add(new Label("label", contribution.getLabel()));
			contributionContainer.add(link);
		}
		add(contributionsView);
		
		WebMarkupContainer usersContainer = new WebMarkupContainer("navUsers");
		usersContainer.add(new ViewStateAwarePageLink<Void>("link", UserListPage.class));
		if (getPage() instanceof UserListPage || getPage() instanceof UserPage) 
			usersContainer.add(AttributeAppender.append("class", "active"));
		usersContainer.setVisible(SecurityUtils.isAdministrator());
		add(usersContainer);
		
		WebMarkupContainer administrationContainer = new WebMarkupContainer("navAdministration");
		administrationContainer.add(new ViewStateAwarePageLink<Void>("link", SystemSettingPage.class));
		if (getPage() instanceof AdministrationPage) 
			administrationContainer.add(AttributeAppender.append("class", "active"));
		administrationContainer.setVisible(SecurityUtils.isAdministrator());
		add(administrationContainer);
		
		Plugin product = AppLoader.getProduct();
		add(new Label("productVersion", product.getVersion()));
		add(new ExternalLink("docLink", OneDev.getInstance().getDocLink() + "/readme.md"));
		
		WebMarkupContainer notSignedInContainer = new WebMarkupContainer("notSignedIn");
		notSignedInContainer.add(new Link<Void>("signIn") {

			@Override
			public void onClick() {
				throw new RestartResponseAtInterceptPageException(LoginPage.class);
			}
			
		});
		
		User loginUser = getLoginUser();
		boolean enableSelfRegister = OneDev.getInstance(SettingManager.class).getSecuritySetting().isEnableSelfRegister();
		notSignedInContainer.add(new ViewStateAwarePageLink<Void>("signUp", RegisterPage.class).setVisible(enableSelfRegister));
		notSignedInContainer.setVisible(loginUser == null);
		add(notSignedInContainer);
		
		WebMarkupContainer signedInContainer = new WebMarkupContainer("signedIn");
		signedInContainer.add(new UserAvatar("avatar", loginUser));
		signedInContainer.add(new Label("userNameExpanded", loginUser!=null?loginUser.getDisplayName():""));
		signedInContainer.add(new Label("userNameCollapsed", loginUser!=null?loginUser.getDisplayName():""));
		signedInContainer.add(new ViewStateAwarePageLink<Void>("profile", UserProfilePage.class, 
				loginUser!=null?UserProfilePage.paramsOf(loginUser):new PageParameters()));
		signedInContainer.add(new ViewStateAwarePageLink<Void>("signOut", LogoutPage.class));
		signedInContainer.setVisible(loginUser != null);
		add(signedInContainer);
	}

	@Override
	protected boolean isPermitted() {
		return getLoginUser() != null || OneDev.getInstance(SettingManager.class).getSecuritySetting().isEnableAnonymousAccess();
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new LayoutResourceReference()));
	}

}