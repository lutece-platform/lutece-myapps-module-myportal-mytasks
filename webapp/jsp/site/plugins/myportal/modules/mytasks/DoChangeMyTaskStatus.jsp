<jsp:useBean id="myPortalMyTasksApp" scope="request" class="fr.paris.lutece.plugins.myportal.modules.mytasks.web.MyPortalMyTasksApp" />

<%
	myPortalMyTasksApp.doActionMyTask( request );
%>
