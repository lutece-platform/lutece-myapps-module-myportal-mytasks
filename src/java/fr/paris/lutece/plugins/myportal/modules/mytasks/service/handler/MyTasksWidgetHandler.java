/*
 * Copyright (c) 2002-2011, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.myportal.modules.mytasks.service.handler;

import fr.paris.lutece.plugins.myportal.business.Widget;
import fr.paris.lutece.plugins.myportal.service.handler.WidgetHandler;
import fr.paris.lutece.plugins.mytasks.business.MyTask;
import fr.paris.lutece.plugins.mytasks.service.MyTasksService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * MyTasks Widget Handler
 *
 */
public class MyTasksWidgetHandler implements WidgetHandler
{
    private static final String NAME = "mytasks";
    private static final String DESCRIPTION = "MyTasks Widget";

    // TEMPLATES
    private static final String TEMPLATE_WIDGET_MYTASKS = "skin/plugins/myportal/modules/mytasks/widget_mytasks.html";

    // MARKS
    private static final String MARK_MYTASKS_LIST = "mytasks_list";
    private static final String MARK_MYTASK_URL_RETURN = "mytasks_url_return";
    private static final String MARK_MYPORTAL_URL_RETURN = "myportal_url_return";
    private static final String MARK_ID_WIDGET = "id_widget";

    // PROPERTIES
    private static final String PROPERTY_MYTASKS_URL_RETURN = "myportal-mytasks.urlReturn.mytasks";
    private static final String PROPERTY_MYPORTAL_URL_RETURN = "myportal-mytasks.urlReturn.myportal";

    /**
     * {@inheritDoc }
     */
    public String renderWidget( Widget widget, LuteceUser user, HttpServletRequest request )
    {
        List<MyTask> listMyTasks = MyTasksService.getInstance(  ).getMyTasksList( user );
        Locale locale = request.getLocale(  );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_MYTASKS_LIST, listMyTasks );
        model.put( MARK_MYTASK_URL_RETURN, AppPropertiesService.getProperty( PROPERTY_MYTASKS_URL_RETURN ) );
        model.put( MARK_MYPORTAL_URL_RETURN, AppPropertiesService.getProperty( PROPERTY_MYPORTAL_URL_RETURN ) );
        model.put( MARK_ID_WIDGET, widget.getIdWidget(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_WIDGET_MYTASKS, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc }
     */
    public String getName(  )
    {
        return NAME;
    }

    /**
     * {@inheritDoc }
     */
    public String getDescription(  )
    {
        return DESCRIPTION;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCustomizable(  )
    {
        return true;
    }
}
