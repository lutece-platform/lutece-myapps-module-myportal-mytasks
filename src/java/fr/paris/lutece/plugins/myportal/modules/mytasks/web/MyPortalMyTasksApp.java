/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.myportal.modules.mytasks.web;

import fr.paris.lutece.plugins.myportal.service.MyPortalPlugin;
import fr.paris.lutece.plugins.myportal.service.WidgetContentService;
import fr.paris.lutece.plugins.myportal.util.auth.MyPortalUser;
import fr.paris.lutece.plugins.mytasks.business.MyTask;
import fr.paris.lutece.plugins.mytasks.service.MyTasksService;
import fr.paris.lutece.plugins.mytasks.web.MyTasksApp;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides a simple implementation of an XPage
 */
public class MyPortalMyTasksApp extends MyTasksApp
{
    // CONSTANTS
    private static final String BEAN_MYPORTAL_WIDGET_CONTENT_SERVICE = "myportal.widgetContentService";
    private static final String USER_ANONYMOUS = "Anonymous";

    // TEMPLATES
    private static final String TEMPLATE_ADD_MYTASK_PAGE = "skin/plugins/myportal/modules/mytasks/add_mytask.html";
    private static final String TEMPLATE_EDIT_MYTASK_PAGE = "skin/plugins/myportal/modules/mytasks/update_mytask.html";

    // PARAMETERS
    private static final String PARAMETER_ACTION = "action";
    private static final String PARAMETER_ID_MYTASK = "id_mytask";
    private static final String PARAMETER_ID_WIDGET = "id_widget";
    private static final String PARAMETER_MYTASK_URL_RETURN = "mytasks_url_return";
    private static final String PARAMETER_MYPORTAL_URL_RETURN = "myportal_url_return";

    // MARKS
    private static final String MARK_MYTASK = "mytask";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_MYTASKS_URL_RETURN = "mytasks_url_return";
    private static final String MARK_MYPORTAL_URL_RETURN = "myportal_url_return";
    private static final String MARK_ID_WIDGET = "id_widget";

    // ACTIONS
    private static final String ACTION_ADD_MYTASK = "add_mytask";
    private static final String ACTION_UPDATE_MYTASK = "update_mytask";
    private static final String ACTION_DO_CHANGE_MYTASK_STATUS = "do_change_mytask_status";

    // PROPERTIES
    private static final String PROPERTY_PAGE_PATH = "mytasks.mytasks.pagePathLabel";
    private static final String PROPERTY_ADD_MYTASK_PAGE_TITLE = "mytasks.add_mytask.pageTitle";
    private static final String PROPERTY_UPDATE_MYTASK_PAGE_TITLE = "mytasks.update_mytask.pageTitle";
    private MyTasksService _myTasksService = MyTasksService.getInstance(  );
    private WidgetContentService _widgetContentService = (WidgetContentService) SpringContextService.getPluginBean( MyPortalPlugin.PLUGIN_NAME,
            BEAN_MYPORTAL_WIDGET_CONTENT_SERVICE );

    /**
     * Returns the content of the page myportal.
     * @param request The http request
     * @param nMode The current mode
     * @param plugin The plugin object
     * @return the {@link XPage}
     * @throws SiteMessageException Message displayed if an exception occurs
     * @throws UserNotSignedException exception if user not connected
     */
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin )
        throws SiteMessageException, UserNotSignedException
    {
        XPage page = null;

        String strAction = request.getParameter( PARAMETER_ACTION );
        String strIdWidget = request.getParameter( PARAMETER_ID_WIDGET );

        if ( StringUtils.isNotBlank( strIdWidget ) && StringUtils.isNumeric( strIdWidget ) )
        {
            if ( StringUtils.isNotBlank( strAction ) )
            {
                if ( ACTION_ADD_MYTASK.equals( strAction ) )
                {
                    page = getAddMyTaskPage( request );
                }
                else if ( ACTION_UPDATE_MYTASK.equals( strAction ) )
                {
                    page = getUpdateMyTaskPage( request );
                }
                else
                {
                    doActionMyTask( request );
                }
            }

            if ( page == null )
            {
                page = super.getPage( request, nMode, plugin );
            }
        }
        else
        {
            SiteMessageService.setMessage( request, Messages.MANDATORY_FIELDS, SiteMessage.TYPE_ERROR );
        }

        return page;
    }

    /**
     * Get the AddMyTask Page
     * @param request {@link HttpServletRequest}
     * @return the {@link XPage}
     * @throws SiteMessageException Message displayed if an exception occurs
     * @throws UserNotSignedException exception if user not connected
     */
    private XPage getAddMyTaskPage( HttpServletRequest request )
        throws SiteMessageException, UserNotSignedException
    {
        XPage page = new XPage(  );
        // Check if the user is indeed connected
        getUser( request );

        String strIdWidget = request.getParameter( PARAMETER_ID_WIDGET );
        String strMyPortalMyTasksUrlReturn = request.getParameter( PARAMETER_MYTASK_URL_RETURN );
        String strMyPortalUrlReturn = request.getParameter( PARAMETER_MYPORTAL_URL_RETURN );

        if ( StringUtils.isBlank( strMyPortalMyTasksUrlReturn ) )
        {
            strMyPortalMyTasksUrlReturn = StringUtils.EMPTY;
        }

        if ( StringUtils.isBlank( strMyPortalUrlReturn ) )
        {
            strMyPortalUrlReturn = StringUtils.EMPTY;
        }

        if ( StringUtils.isNotBlank( strIdWidget ) && StringUtils.isNumeric( strIdWidget ) )
        {
            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_LOCALE, request.getLocale(  ) );
            model.put( MARK_MYTASKS_URL_RETURN, strMyPortalMyTasksUrlReturn );
            model.put( MARK_MYPORTAL_URL_RETURN, strMyPortalUrlReturn );
            model.put( MARK_ID_WIDGET, strIdWidget );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_ADD_MYTASK_PAGE, request.getLocale(  ),
                    model );

            page.setTitle( I18nService.getLocalizedString( PROPERTY_ADD_MYTASK_PAGE_TITLE, request.getLocale(  ) ) );
            page.setPathLabel( I18nService.getLocalizedString( PROPERTY_PAGE_PATH, request.getLocale(  ) ) );
            page.setContent( template.getHtml(  ) );
        }
        else
        {
            SiteMessageService.setMessage( request, Messages.MANDATORY_FIELDS, SiteMessage.TYPE_ERROR );
        }

        return page;
    }

    /**
     * Get the update MyTask page
     * @param request {@link HttpServletRequest}
     * @return the {@link XPage}
     * @throws UserNotSignedException exception if user not connected
     * @throws SiteMessageException Message displayed if an exception occurs
     */
    private XPage getUpdateMyTaskPage( HttpServletRequest request )
        throws SiteMessageException, UserNotSignedException
    {
        XPage page = new XPage(  );
        // Check if the user is indeed connected
        getUser( request );

        String strIdWidget = request.getParameter( PARAMETER_ID_WIDGET );
        String strIdMyTask = request.getParameter( PARAMETER_ID_MYTASK );
        String strMyPortalMyTasksUrlReturn = request.getParameter( PARAMETER_MYTASK_URL_RETURN );
        String strMyPortalUrlReturn = request.getParameter( PARAMETER_MYPORTAL_URL_RETURN );

        if ( StringUtils.isBlank( strMyPortalMyTasksUrlReturn ) )
        {
            strMyPortalMyTasksUrlReturn = StringUtils.EMPTY;
        }

        if ( StringUtils.isBlank( strMyPortalUrlReturn ) )
        {
            strMyPortalUrlReturn = StringUtils.EMPTY;
        }

        if ( StringUtils.isNotBlank( strIdWidget ) && StringUtils.isNumeric( strIdWidget ) &&
                StringUtils.isNotBlank( strIdMyTask ) )
        {
            int nIdMyTask = Integer.parseInt( strIdMyTask );
            MyTask myTask = _myTasksService.getMyTask( nIdMyTask );

            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_MYTASK, myTask );
            model.put( MARK_LOCALE, request.getLocale(  ) );
            model.put( MARK_MYTASKS_URL_RETURN, strMyPortalMyTasksUrlReturn );
            model.put( MARK_MYPORTAL_URL_RETURN, strMyPortalUrlReturn );
            model.put( MARK_ID_WIDGET, strIdWidget );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_EDIT_MYTASK_PAGE, request.getLocale(  ),
                    model );

            page.setTitle( I18nService.getLocalizedString( PROPERTY_UPDATE_MYTASK_PAGE_TITLE, request.getLocale(  ) ) );
            page.setPathLabel( I18nService.getLocalizedString( PROPERTY_PAGE_PATH, request.getLocale(  ) ) );
            page.setContent( template.getHtml(  ) );
        }
        else
        {
            SiteMessageService.setMessage( request, Messages.MANDATORY_FIELDS, SiteMessage.TYPE_ERROR );
        }

        return page;
    }

    /**
     * Do action MyTask
     * @param request {@link HttpServletRequest
     * @return the url return from the parameter
     * @throws UserNotSignedException exception if user not connected
     * @throws SiteMessageException Message displayed if an exception occurs
     */
    public String doActionMyTask( HttpServletRequest request )
        throws SiteMessageException, UserNotSignedException
    {
        String strUrl = StringUtils.EMPTY;
        String strIdWidget = request.getParameter( PARAMETER_ID_WIDGET );

        if ( StringUtils.isNotBlank( strIdWidget ) && StringUtils.isNumeric( strIdWidget ) )
        {
            int nIdWidget = Integer.parseInt( strIdWidget );
            String strAction = request.getParameter( PARAMETER_ACTION );

            // Action specific to the module : change status of only one task
            if ( StringUtils.isNotBlank( strAction ) && ACTION_DO_CHANGE_MYTASK_STATUS.equals( strAction ) )
            {
                String strIdMyTask = request.getParameter( PARAMETER_ID_MYTASK );

                if ( StringUtils.isNotBlank( strIdMyTask ) && StringUtils.isNumeric( strIdMyTask ) )
                {
                    _widgetContentService.removeCache( nIdWidget, getUser( request ) );

                    int nIdMyTask = Integer.parseInt( strIdMyTask );
                    MyTask myTask = _myTasksService.getMyTask( nIdMyTask );
                    myTask.setDone( !myTask.isDone(  ) );
                    _myTasksService.doUpdateMyTask( myTask, getUser( request ) );
                }
            }
            else
            {
                _widgetContentService.removeCache( nIdWidget, getUser( request ) );
                strUrl = super.doActionMyTask( request );
            }
        }
        else
        {
            SiteMessageService.setMessage( request, Messages.MANDATORY_FIELDS, SiteMessage.TYPE_ERROR );
        }

        return strUrl;
    }

    /**
     * Gets the user from the request
     * @param request The HTTP user
     * @return The Lutece User
     */
    public LuteceUser getUser( HttpServletRequest request )
    {
        LuteceUser user = null;

        if ( SecurityService.isAuthenticationEnable(  ) )
        {
            try
            {
                user = SecurityService.getInstance(  ).getRemoteUser( request );
            }
            catch ( UserNotSignedException ue )
            {
                AppLogService.error( ue.getMessage(  ), ue );
            }
        }

        if ( user == null )
        {
            user = new MyPortalUser( USER_ANONYMOUS );
        }

        return user;
    }
}
