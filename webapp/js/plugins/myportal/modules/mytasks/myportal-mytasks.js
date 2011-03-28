/**
 * Change the status of the mytask
 * @param checkbox the checkbox
 * @param action the action
 * @param id_widget the ID widget
 * @param id_mytask the ID mytask
 * @return void
 */
function changeMyTaskStatus( checkbox, action, id_widget, id_mytask ) {
	$.ajax( { 
		type: 'POST',
		url : 'jsp/site/plugins/myportal/modules/mytasks/DoChangeMyTaskStatus.jsp',
		data : 'action=' + action + '&id_widget=' + id_widget + "&id_mytask=" + id_mytask,
		error : function( msg ) {
			alert( 'Une erreur AJAX est survenue.' );
		},
		success : function( data ) {
			if ( $( checkbox ).parent(  ).children( 'span' ).hasClass( 'mytasks-done' ) ) {
				$( checkbox ).parent(  ).children( 'span' ).removeClass( 'mytasks-done' );
			} else {
				$( checkbox ).parent(  ).children( 'span' ).addClass( 'mytasks-done' );
			}
		}
	} );
}
