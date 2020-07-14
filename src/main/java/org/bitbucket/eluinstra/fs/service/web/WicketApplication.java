/**
 * Copyright 2020 E.Luinstra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitbucket.eluinstra.fs.service.web;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.bitbucket.eluinstra.fs.service.web.menu.MenuItem;
import org.bitbucket.eluinstra.fs.service.web.menu.MenuLinkItem;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class WicketApplication extends WebApplication
{
	@NonNull
	Integer maxItemsPerPage;
	List<MenuItem> menuItems = new ArrayList<>();
	
	public WicketApplication(@NonNull Integer maxItemsPerPage)
	{
		this.maxItemsPerPage = maxItemsPerPage;
		val home = new MenuLinkItem("0","home",getHomePage());
		menuItems.add(home);
		
		val user = new MenuItem("1","userService");
		menuItems.add(user);

		val file = new MenuItem("2","fileService");
		menuItems.add(file);

		val configuration = new MenuItem("3","configuration");
		new MenuLinkItem(configuration,"1","serviceProperties",org.bitbucket.eluinstra.fs.service.web.configuration.PropertiesPage.class);
		menuItems.add(configuration);

		val extensionProviders = ExtensionProvider.get();
		if (extensionProviders.size() > 0)
		{
			val extensions = new MenuItem("5","extensions");
			menuItems.add(extensions);
			val i = new AtomicInteger(1);
			extensionProviders.forEach(p ->
			{
				val epmi = new MenuItem(extensions,String.valueOf(i.getAndIncrement()),p.getName());
				p.getMenuItems(epmi);
			});
		}

		val about = new MenuLinkItem("6","about",org.bitbucket.eluinstra.fs.service.web.AboutPage.class);
		menuItems.add(about);
	}
	
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return HomePage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();
		getDebugSettings().setDevelopmentUtilitiesEnabled(true);
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));
		getJavaScriptLibrarySettings().setJQueryReference(new JavaScriptResourceReference(HomePage.class,"../../../../../../js/jquery-min.js"));
		getRequestCycleListeners().add(new IRequestCycleListener()
		{
			@Override
			public IRequestHandler onException(final RequestCycle cycle, final Exception e)
			{
				return new RenderPageRequestHandler(new PageProvider(new ErrorPage(e)));
			}
		});
		mountPage("/404",PageNotFoundPage.class); 
	}
	
	public static WicketApplication get()
	{
		return (WicketApplication)WebApplication.get();
	}
}
